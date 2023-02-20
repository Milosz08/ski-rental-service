/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RentDao.java
 *  Last modified: 20/02/2023, 18:57
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.rent;

import lombok.RequiredArgsConstructor;

import java.util.*;
import org.hibernate.Session;

import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.entity.RentEntity;
import pl.polsl.skirentalservice.paging.sorter.SorterDataDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto;

import static java.util.Optional.*;
import static java.util.Objects.isNull;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class RentDao implements IRentDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<RentReturnDetailsResDto> findRentReturnDetails(Object rentId, Object employerId) {
        final String jpqlFindRent =
            "SELECT new pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto(" +
                "r.issuedIdentifier, r.rentDateTime, r.tax," +
                "r.totalPrice, CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal)," +
                "r.totalDepositPrice, CAST((r.tax / 100) * r.totalDepositPrice + r.totalDepositPrice AS bigdecimal)" +
            ") FROM RentEntity r INNER JOIN r.employer e " +
            "WHERE r.id = :rentid AND e.id = :eid";
        final RentReturnDetailsResDto rentDetails = session.createQuery(jpqlFindRent, RentReturnDetailsResDto.class)
            .setParameter("rentid", rentId)
            .setParameter("eid", employerId)
            .getSingleResultOrNull();
        if (isNull(rentDetails)) return empty();
        return of(rentDetails);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<RentDetailsResDto> findRentDetails(Object rentId, Object employerId, String roleAlias) {
        final String jpqlFindRentDetails =
            "SELECT new pl.polsl.skirentalservice.dto.rent.RentDetailsResDto(" +
                "r.id, r.issuedIdentifier, r.issuedDateTime, r.rentDateTime, r.returnDateTime, " +
                "IFNULL(r.description, '<i>Brak danych</i>'), " +
                "r.tax, r.status, r.totalPrice, CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal)," +
                "r.totalDepositPrice, CAST((r.tax / 100) * r.totalDepositPrice + r.totalDepositPrice AS bigdecimal)," +
                "CONCAT(d.firstName, ' ', d.lastName), d.pesel, d.bornDate, CONCAT('+', d.phoneAreaCode," +
                "SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                "SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate), d.emailAddress," +
                "d.gender, CONCAT(a.postalCode, ' ', a.city)," +
                "CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))" +
            ") FROM RentEntity r " +
            "LEFT OUTER JOIN r.employer e INNER JOIN e.role rl " +
            "LEFT OUTER JOIN r.customer c LEFT OUTER JOIN c.userDetails d LEFT OUTER JOIN c.locationAddress a " +
            "WHERE r.id = :rid AND (e.id = :eid OR :ralias = 'K')";
        final RentDetailsResDto equipmentDetails = session.createQuery(jpqlFindRentDetails, RentDetailsResDto.class)
            .setParameter("rid", rentId)
            .setParameter("eid", employerId)
            .setParameter("ralias", roleAlias)
            .getSingleResultOrNull();
        if (isNull(equipmentDetails)) return empty();
        return of(equipmentDetails);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void updateRentStatus(RentStatus rentStatus, Object rentId) {
        final String jpqlUpdateRentStatus = "UPDATE RentEntity r SET r.status = :rst WHERE r.id = :rentid";
        session.createMutationQuery(jpqlUpdateRentStatus)
            .setParameter("rst", rentStatus)
            .setParameter("rentid", rentId)
            .executeUpdate();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Long findAllRentsCount(FilterDataDto filterData) {
        final String jpqlTotalRentsCount =
            "SELECT COUNT(r.id) FROM RentEntity r " +
            "LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c " +
            "LEFT OUTER JOIN c.userDetails d LEFT OUTER JOIN e.userDetails ed " +
            "WHERE " + filterData.getSearchColumn() + " LIKE :search";
        return session.createQuery(jpqlTotalRentsCount, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Long findAllRentsFromEmployerCount(FilterDataDto filterData, Long employerId) {
        final String jpqlTotalRentsCount =
            "SELECT COUNT(r.id) FROM RentEntity r " +
            "LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c LEFT OUTER JOIN c.userDetails d " +
            "WHERE e.id = :eid AND " + filterData.getSearchColumn() + " LIKE :search";
        return session.createQuery(jpqlTotalRentsCount, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .setParameter("eid", employerId)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<RentEntity> findAllRentsBaseCustomerId(Object customerId) {
        final String jpqlFindAllRents = "SELECT r FROM RentEntity r INNER JOIN r.customer c WHERE c.id = :cid";
        return session.createQuery(jpqlFindAllRents, RentEntity.class)
            .setParameter("cid", customerId)
            .getResultList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<OwnerRentRecordResDto> findAllPageableRents(
        FilterDataDto filterData, SorterDataDto sorterData, int page, int total
    ) {
        final String jpqlFindAllRents =
            "SELECT new pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto(" +
                "r.id, r.issuedIdentifier, r.issuedDateTime, r.status, r.totalPrice," +
                "CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal)," +
                "IFNULL(CONCAT(d.firstName, ' ', d.lastName), '<i>klient usunięty</i>'), c.id," +
                "IFNULL(CONCAT(ed.firstName, ' ', ed.lastName), '<i>pracownik usunięty</i>'), ed.id" +
            ") FROM RentEntity r " +
            "LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c " +
            "LEFT OUTER JOIN c.userDetails d LEFT OUTER JOIN e.userDetails ed " +
            "WHERE " + filterData.getSearchColumn() + " LIKE :search " +
            "ORDER BY " + sorterData.getJpql();
        return session.createQuery(jpqlFindAllRents, OwnerRentRecordResDto.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .setFirstResult((page - 1) * total)
            .setMaxResults(total)
            .getResultList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<SellerRentRecordResDto> findAllPageableRentsFromEmployer(
        FilterDataDto filterData, SorterDataDto sorterData, Long employerId, int page, int total
    ) {
        final String jpqlFindAllRentsConnectedWithEmployer =
            "SELECT new pl.polsl.skirentalservice.dto.rent.SellerRentRecordResDto(" +
                "r.id, r.issuedIdentifier, r.issuedDateTime, r.status, r.totalPrice," +
                "CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal)," +
                "IFNULL(CONCAT(d.firstName, ' ', d.lastName), '<i>klient usunięty</i>'), c.id" +
            ") FROM RentEntity r " +
            "INNER JOIN r.employer e LEFT OUTER JOIN r.customer c LEFT OUTER JOIN c.userDetails d " +
            "WHERE e.id = :eid AND " + filterData.getSearchColumn() + " LIKE :search " +
            "ORDER BY " + sorterData.getJpql();
        return session.createQuery(jpqlFindAllRentsConnectedWithEmployer, SellerRentRecordResDto.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .setParameter("eid", employerId)
            .setFirstResult((page - 1) * total)
            .setMaxResults(total)
            .getResultList();
    }
}
