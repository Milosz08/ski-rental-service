/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: AttributeModalResDto.java
 *  Last modified: 24/01/2023, 21:51
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.attribute;

import lombok.Data;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@NoArgsConstructor
public class AttributeModalResDto {
    private FormValueInfoTupleDto name;
    private AlertTupleDto alert;
    private String immediatelyShow;
    private NavCanvasState activeFirstPage = new NavCanvasState(true);
    private NavCanvasState activeSecondPage = new NavCanvasState(false);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AttributeModalResDto(ValidatorSingleton validator, AttributeModalReqDto reqDto, AlertTupleDto alert) {
        this.name = validator.validateField(reqDto, "name", reqDto.getName());
        this.alert = alert;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setModalImmediatelyOpen(boolean isImmediatelyOpen) {
        this.immediatelyShow = isImmediatelyOpen ? "open" : "close";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", alert=" + alert +
            '}';
    }
}
