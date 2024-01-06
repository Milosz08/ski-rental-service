/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertType {
    INFO("alert-success"),
    WARN("alert-warning"),
    ERROR("alert-danger"),
    ;

    private final String cssClass;
}
