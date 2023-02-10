/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: AddEditCustomerResDto.java
 *  Last modified: 22/01/2023, 08:26
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.customer;

import lombok.*;
import java.util.List;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.core.ValidatorBean;

import static pl.polsl.skirentalservice.util.Gender.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class AddEditCustomerResDto {
    private FormValueInfoTupleDto firstName;
    private FormValueInfoTupleDto lastName;
    private FormValueInfoTupleDto pesel;
    private FormValueInfoTupleDto phoneNumber;
    private FormValueInfoTupleDto bornDate;
    private FormValueInfoTupleDto emailAddress;
    private FormValueInfoTupleDto street;
    private FormValueInfoTupleDto buildingNr;
    private FormValueInfoTupleDto apartmentNr;
    private FormValueInfoTupleDto city;
    private FormValueInfoTupleDto postalCode;
    private List<FormSelectTupleDto> genders = Gender.getGenders();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AddEditCustomerResDto(ValidatorBean validator, AddEditCustomerReqDto reqDto) {
        this.firstName = validator.validateField(reqDto, "firstName", reqDto.getFirstName());
        this.lastName = validator.validateField(reqDto, "lastName", reqDto.getLastName());
        this.pesel = validator.validateField(reqDto, "pesel", reqDto.getPesel());
        this.phoneNumber = validator.validateField(reqDto, "phoneNumber", reqDto.getPhoneNumber());
        this.bornDate = validator.validateField(reqDto, "bornDate", reqDto.getBornDate());
        this.emailAddress = validator.validateField(reqDto, "emailAddress", reqDto.getEmailAddress());
        this.street = validator.validateField(reqDto, "street", reqDto.getStreet());
        this.buildingNr = validator.validateField(reqDto, "buildingNr", reqDto.getBuildingNr());
        this.apartmentNr = validator.validateField(reqDto, "apartmentNr", reqDto.getApartmentNr());
        this.city = validator.validateField(reqDto, "city", reqDto.getCity());
        this.postalCode = validator.validateField(reqDto, "postalCode", reqDto.getPostalCode());
        this.genders = List.of(MALE.convertToTuple(reqDto.getGender()), FEMALE.convertToTuple(reqDto.getGender()));
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
            ", emailAddress=" + emailAddress +
            ", street=" + street +
            ", buildingNr=" + buildingNr +
            ", apartmentNr=" + apartmentNr +
            ", city=" + city +
            ", postalCode=" + postalCode +
            '}';
    }
}
