/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.rent;

import lombok.Data;
import pl.polsl.skirentalservice.dto.PriceUnitsDto;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.util.RentStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Data
public class InMemoryRentDataDto {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Long customerId;
    private String customerFullName;
    private String issuedIdentifier;
    private String issuedDateTime;
    private String rentDateTime;
    private String returnDateTime;
    private RentStatus rentStatus;
    private String description;
    private String tax;
    private PriceUnitsDto priceUnits;
    private long days;
    private long hours;
    private int totalCount;
    private boolean allGood;
    private CustomerDetailsResDto customerDetails;
    private List<CartSingleEquipmentDataDto> equipments = new ArrayList<>();

    public InMemoryRentDataDto(Long customerId, String customerFullName) {
        this.customerId = customerId;
        this.customerFullName = customerFullName;
        this.issuedDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString().replace('T', ' ');
        this.rentStatus = RentStatus.OPENED;
        this.priceUnits = new PriceUnitsDto();
    }

    public LocalDateTime getParsedRentDateTime() {
        return LocalDateTime.parse(rentDateTime.replace('T', ' '), formatter);
    }

    public LocalDateTime getParsedReturnDateTime() {
        return LocalDateTime.parse(returnDateTime.replace('T', ' '), formatter);
    }

    @Override
    public String toString() {
        return "{" +
            "customerId=" + customerId +
            ", customerFullName='" + customerFullName +
            ", issuedIdentifier='" + issuedIdentifier +
            ", issuedDateTime='" + issuedDateTime +
            ", rentDateTime='" + rentDateTime +
            ", returnDateTime='" + returnDateTime +
            ", rentStatus=" + rentStatus +
            ", description='" + description +
            ", tax='" + tax +
            ", priceUnits=" + priceUnits +
            ", days=" + days +
            ", hours=" + hours +
            ", totalCount=" + totalCount +
            ", allGood=" + allGood +
            ", equipments=" + equipments +
            '}';
    }
}
