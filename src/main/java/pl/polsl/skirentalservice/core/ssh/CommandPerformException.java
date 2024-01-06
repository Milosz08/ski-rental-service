/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.ssh;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandPerformException extends RuntimeException {
    public CommandPerformException(String message, String errMsg) {
        super(message + " Spróbuj ponownie później.");
        log.error("Unable to perform SSH command. Command details: {}. ERR Cause by: {}", message, errMsg);
    }
}
