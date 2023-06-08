/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ReturnDao.java
 * Last modified: 3/12/23, 11:01 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.dao.return_deliv;

import lombok.RequiredArgsConstructor;

import org.hibernate.Session;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto;
import pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnAlreadyExistPayloadDto;
import pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public class ReturnDao implements IReturnDao {

    private final Session session;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<ReturnAlreadyExistPayloadDto> findReturnExistDocument(Object rentId) {
        final String jpqlFindReturn = """
            SELECT new pl.polsl.skirentalservice.dto.deliv_return.ReturnAlreadyExistPayloadDto(
                re.id, re.issuedIdentifier
            ) FROM RentReturnEntity re INNER JOIN re.rent r WHERE r.id = :rid
        """;
        final ReturnAlreadyExistPayloadDto returnAlreadyExist = session
            .createQuery(jpqlFindReturn, ReturnAlreadyExistPayloadDto.class)
            .setParameter("rid", rentId)
            .getSingleResultOrNull();
        if (Objects.isNull(returnAlreadyExist)) return Optional.empty();
        return Optional.of(returnAlreadyExist);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<ReturnRentDetailsResDto> findReturnDetails(Object returnId, Object employerId, String roleAlias) {
        final String jpqlFindReturnDetails = """
            SELECT new pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto(
                rr.id, rr.issuedIdentifier, r.issuedIdentifier, rr.issuedDateTime, rr.description, r.tax,
                rr.totalPrice, CAST((r.tax / 100) * rr.totalPrice + rr.totalPrice AS bigdecimal), rr.totalDepositPrice,
                CAST((r.tax / 100) * rr.totalDepositPrice + rr.totalDepositPrice AS bigdecimal),
                CONCAT(d.firstName, ' ', d.lastName), d.pesel, d.bornDate, CONCAT('+', d.phoneAreaCode,
                SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' ',
                SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate), d.emailAddress,
                d.gender, CONCAT(a.postalCode, ' ', a.city),
                CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))
            ) FROM RentReturnEntity rr
            LEFT OUTER JOIN rr.rent r
            LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c LEFT OUTER JOIN c.userDetails d
            LEFT OUTER JOIN c.locationAddress a
            WHERE rr.id = :rid AND (e.id = :eid OR :ralias = 'K')
        """;
        final ReturnRentDetailsResDto returnDetails = session
            .createQuery(jpqlFindReturnDetails, ReturnRentDetailsResDto.class)
            .setParameter("rid", returnId)
            .setParameter("eid", employerId)
            .setParameter("ralias", roleAlias)
            .getSingleResultOrNull();
        if (Objects.isNull(returnDetails)) return Optional.empty();
        return Optional.of(returnDetails);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Long findAllReturnsCount(FilterDataDto filterData) {
        String jpqlTotalReturnsCount = """
            SELECT COUNT(r.id) FROM RentReturnEntity r
            INNER JOIN r.rent rd INNER JOIN rd.employer e INNER JOIN e.userDetails ed
            WHERE :searchColumn LIKE :search
        """;
        jpqlTotalReturnsCount = jpqlTotalReturnsCount.replace(":searchColumn", filterData.getSearchColumn());
        return session.createQuery(jpqlTotalReturnsCount, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Long findAllReturnsFromEmployerCount(FilterDataDto filterData, Object employerId) {
        String jpqlTotalReturnsCount = """
            SELECT COUNT(r.id) FROM RentReturnEntity r
            INNER JOIN r.rent rd INNER JOIN rd.employer e
            WHERE e.id = :eid AND :searchColumn LIKE :search
        """;
        jpqlTotalReturnsCount = jpqlTotalReturnsCount.replace(":searchColumn", filterData.getSearchColumn());
        return session.createQuery(jpqlTotalReturnsCount, Long.class)
            .setParameter("search", "%" + filterData.getSearchText() + "%")
            .setParameter("eid", employerId)
            .getSingleResult();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<OwnerRentReturnRecordResDto> findAllPageableReturnsRecords(PageableDto pageableDto) {
        String jpqlFindAlReturns = """
            SELECT new pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto(
                r.id, r.issuedIdentifier, r.issuedDateTime, r.totalPrice,
                CAST((rd.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal), rd.id, rd.issuedIdentifier,
                e.id, CONCAT(ed.firstName, ' ', ed.lastName)
            ) FROM RentReturnEntity r
            INNER JOIN r.rent rd INNER JOIN rd.employer e INNER JOIN e.userDetails ed
            WHERE :searchColumn LIKE :search
            ORDER BY :sortedColumn
        """;
        jpqlFindAlReturns = jpqlFindAlReturns
            .replace(":searchColumn", pageableDto.filterData().getSearchColumn())
            .replace(":sortedColumn", pageableDto.sorterData().getJpql());
        return session
            .createQuery(jpqlFindAlReturns, OwnerRentReturnRecordResDto.class)
            .setParameter("search", "%" + pageableDto.filterData().getSearchText() + "%")
            .setFirstResult((pageableDto.page() - 1) * pageableDto.total())
            .setMaxResults(pageableDto.total())
            .getResultList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<SellerRentReturnRecordResDto> findAllPageableReturnsFromEmployerRecords(PageableDto pageableDto, Object employerId) {
        String jpqlFindAlReturnsConnectedWithEmployer = """
            SELECT new pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto(
                r.id, r.issuedIdentifier, r.issuedDateTime, r.totalPrice,
                CAST((rd.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal), rd.id, rd.issuedIdentifier
            ) FROM RentReturnEntity r
            INNER JOIN r.rent rd INNER JOIN rd.employer e
            WHERE e.id = :eid AND :searchColumn LIKE :search
            ORDER BY :sortedColumn
        """;
        jpqlFindAlReturnsConnectedWithEmployer = jpqlFindAlReturnsConnectedWithEmployer
            .replace(":searchColumn", pageableDto.filterData().getSearchColumn())
            .replace(":sortedColumn", pageableDto.sorterData().getJpql());
        return session
            .createQuery(jpqlFindAlReturnsConnectedWithEmployer, SellerRentReturnRecordResDto.class)
            .setParameter("search", "%" + pageableDto.filterData().getSearchText() + "%")
            .setParameter("eid", employerId)
            .setFirstResult((pageableDto.page() - 1) * pageableDto.total())
            .setMaxResults(pageableDto.total())
            .getResultList();
    }
}
