/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RequestToChangePasswordResDto.java
 *  Last modified: 20/01/2023, 06:33
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.change_password;

import lombok.Data;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class RequestToChangePasswordResDto {
    private FormValueInfoTupleDto loginOrEmail;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public RequestToChangePasswordResDto(ValidatorBean validator, RequestToChangePasswordReqDto reqDto) {
        this.loginOrEmail = validator.validateField(reqDto, "loginOrEmail", reqDto.getLoginOrEmail());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail=" + loginOrEmail +
            '}';
    }
}
