/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: AddEditEmployerResDto.java
 *  Last modified: 24/01/2023, 16:15
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.employer;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class AddEditEmployerResDto {
    private FormValueInfoTupleDto firstName;
    private FormValueInfoTupleDto lastName;
    private FormValueInfoTupleDto pesel;
    private FormValueInfoTupleDto phoneNumber;
    private FormValueInfoTupleDto bornDate;
    private FormValueInfoTupleDto hiredDate;
    private FormValueInfoTupleDto street;
    private FormValueInfoTupleDto buildingNr;
    private FormValueInfoTupleDto apartmentNr;
    private FormValueInfoTupleDto city;
    private FormValueInfoTupleDto postalCode;
    private List<FormSelectTupleDto> genders = Gender.getGenders();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditEmployerResDto(ValidatorSingleton validator, AddEditEmployerReqDto reqDto) {
        this.firstName = validator.validateField(reqDto, "firstName", reqDto.getFirstName());
        this.lastName = validator.validateField(reqDto, "lastName", reqDto.getLastName());
        this.pesel = validator.validateField(reqDto, "pesel", reqDto.getPesel());
        this.phoneNumber = validator.validateField(reqDto, "phoneNumber", reqDto.getPhoneNumber());
        this.bornDate = validator.validateField(reqDto, "bornDate", reqDto.getBornDate());
        this.hiredDate = validator.validateField(reqDto, "hiredDate", reqDto.getHiredDate());
        this.street = validator.validateField(reqDto, "street", reqDto.getStreet());
        this.buildingNr = validator.validateField(reqDto, "buildingNr", reqDto.getBuildingNr());
        this.apartmentNr = validator.validateField(reqDto, "apartmentNr", reqDto.getApartmentNr());
        this.city = validator.validateField(reqDto, "city", reqDto.getCity());
        this.postalCode = validator.validateField(reqDto, "postalCode", reqDto.getPostalCode());
        this.genders = Gender.getSelectedGender(reqDto.getGender());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "firstName=" + firstName +
            ", lastName=" + lastName +
            ", pesel=" + pesel +
            ", phoneNumber=" + phoneNumber +
            ", bornDate=" + bornDate +
            ", hiredDate=" + hiredDate +
            ", street=" + street +
            ", buildingNr=" + buildingNr +
            ", localeNr=" + apartmentNr +
            ", city=" + city +
            ", postalCode=" + postalCode +
            '}';
    }
}
