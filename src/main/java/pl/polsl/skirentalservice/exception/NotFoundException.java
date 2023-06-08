/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: NotFoundException.java
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

package pl.polsl.skirentalservice.exception;

import org.slf4j.Logger;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.UserRole;
import pl.polsl.skirentalservice.dto.login.LoginFormReqDto;
import pl.polsl.skirentalservice.dto.change_password.RequestToChangePasswordReqDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class NotFoundException {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String userId) {
            super("Użytkownik z podanym ID <strong>#" + userId + "</strong> nie istnieje w systemie.");
        }

        public UserNotFoundException(RequestToChangePasswordReqDto reqDto, Logger logger) {
            super("Użytkownik z podanymi danymi logowania nie istnieje w systemie.");
            logger.warn("Attempt to change password for non existing account. Login data: {}", reqDto);
        }

        public UserNotFoundException(LoginFormReqDto reqDto, Logger logger) {
            super("Użytkownik z podanymi danymi logowania nie istnieje w systemie.");
            logger.warn("Attempt to login on non existing account. Login data: {}", reqDto);
        }

        public UserNotFoundException(UserRole role) {
            super(StringUtils.capitalize(role.getName()) + " nie istnieje w systemie.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentTypeNotFoundException extends RuntimeException {
        public EquipmentTypeNotFoundException() {
            super("Wybrany typ sprzętu narciarskiego nie istnieje w systemie.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentBrandNotFoundException extends RuntimeException {
        public EquipmentBrandNotFoundException() {
            super("Wybrana marka sprzętu narciarskiego nie istnieje w systemie.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentColorNotFoundException extends RuntimeException {
        public EquipmentColorNotFoundException() {
            super("Wybrany kolor sprzętu narciarskiego nie istnieje w systemie.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentNotFoundException extends RuntimeException {
        public EquipmentNotFoundException(String eqId) {
            super("Wybrany sprzęt narciarski na podstawie ID <strong>#" + eqId + "</strong> nie istnieje w systemie.");
        }

        public EquipmentNotFoundException() {
            super("Nie znaleziono żadnych sprzętów narciarskich w wyszukiwanym zapytaniu.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class EquipmentInCartNotFoundException extends RuntimeException {
        public EquipmentInCartNotFoundException() {
            super("Wybrany sprzęt na podstawie identyfikatora nie istnieje na liście z zestawieniami.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class AnyEquipmentsInCartNotFoundException extends RuntimeException {
        public AnyEquipmentsInCartNotFoundException() {
            super("W celu złożenia nowego wypożyczenia należy dodać do niego przynajmniej jeden sprzęt.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class RentNotFoundException extends RuntimeException {
        public RentNotFoundException() {
            super("Szukane wypożyczenie nie istnieje w systemie, bądź zostało z niego usunięte.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class ReturnNotFoundException extends RuntimeException {
        public ReturnNotFoundException() {
            super("Szukany zwrot wypożyczenia nie istnieje w systemie, bądź zostało z niego usunięty.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class NonEquipmentsBeforeRentCreatingException extends RuntimeException {
        public NonEquipmentsBeforeRentCreatingException() {
            super("Aby móc stworzyć nowe wypożyczenie, na liście przedmiotów musi być przynajmniej jeden element w" +
                "ilości co najmniej jednej sztuki.");
        }
    }
}
