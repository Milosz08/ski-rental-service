/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.rent;

import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto;
import pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.RentDetailsResDto;
import pl.polsl.skirentalservice.dto.rent.SellerRentRecordResDto;
import pl.polsl.skirentalservice.entity.RentEntity;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.util.RentStatus;

import java.util.List;
import java.util.Optional;

public interface IRentDao {
    Optional<RentReturnDetailsResDto> findRentReturnDetails(Object rentId, Object employerId);
    Optional<RentDetailsResDto> findRentDetails(Object rentId, Object employerId, String roleAlias);

    boolean checkIfRentIsFromEmployer(Object rentId, Object employerId);
    boolean checkIfIssuerExist(Object issuer);
    void updateRentStatus(RentStatus rentStatus, Object rentId);

    Long findAllRentsCount(FilterDataDto filterData);
    Long findAllRentsFromEmployerCount(FilterDataDto filterData, Long employerId);

    List<RentEntity> findAllRentsBaseCustomerId(Object customerId);
    List<OwnerRentRecordResDto> findAllPageableRents(PageableDto pageableDto);
    List<SellerRentRecordResDto> findAllPageableRentsFromEmployer(PageableDto pageableDto, Object employerId);
}
