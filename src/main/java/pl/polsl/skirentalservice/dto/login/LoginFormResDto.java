/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: LoginFormResDto.java
 *  Last modified: 20/01/2023, 06:34
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.login;

import lombok.Data;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class LoginFormResDto {
    private FormValueInfoTupleDto loginOrEmail;
    private FormValueInfoTupleDto password;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LoginFormResDto(ValidatorSingleton validator, LoginFormReqDto reqDto) {
        this.loginOrEmail = validator.validateField(reqDto, "loginOrEmail", reqDto.getLoginOrEmail());
        this.password = validator.validateField(reqDto, "password", reqDto.getPassword());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "loginOrEmail=" + loginOrEmail +
            ", password=" + password +
            '}';
    }
}
