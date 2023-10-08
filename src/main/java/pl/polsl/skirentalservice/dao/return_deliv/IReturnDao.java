/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.return_deliv;

import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnAlreadyExistPayloadDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto;
import pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;

import java.util.List;
import java.util.Optional;

public interface IReturnDao {
    Optional<ReturnAlreadyExistPayloadDto> findReturnExistDocument(Object rentId);
    Optional<ReturnRentDetailsResDto> findReturnDetails(Object returnId, Object employerId, String roleAlias);

    Long findAllReturnsCount(FilterDataDto filterData);
    Long findAllReturnsFromEmployerCount(FilterDataDto filterData, Object employerId);

    List<OwnerRentReturnRecordResDto> findAllPageableReturnsRecords(PageableDto pageableDto);
    List<SellerRentReturnRecordResDto> findAllPageableReturnsFromEmployerRecords(PageableDto pageableDto, Object employerId);
}
