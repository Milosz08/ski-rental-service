/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ChangeForgottenPasswordReqDto.java
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

package pl.polsl.skirentalservice.dto.change_password;

import lombok.Data;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.Regex;
import pl.polsl.skirentalservice.core.IReqValidatePojo;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class ChangeForgottenPasswordReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole hasła nie może być puste.")
    @Pattern(regexp = Regex.PASSWORD_REQ, message = "Nieprawidłowa wartość/wartości w polu hasło.")
    private String password;

    @NotEmpty(message = "Pole powtórzonego hasła nie może być puste.")
    @Pattern(regexp = Regex.PASSWORD_REQ, message = "Nieprawidłowa wartość/wartości w polu powtórzonego hasła.")
    private String passwordRepeat;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ChangeForgottenPasswordReqDto(HttpServletRequest req) {
        this.password = StringUtils.trimToEmpty(req.getParameter("password"));
        this.passwordRepeat = StringUtils.trimToEmpty(req.getParameter("passwordRepeat"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "password='" + password +
            ", passwordRepeat='" + passwordRepeat +
            '}';
    }
}
