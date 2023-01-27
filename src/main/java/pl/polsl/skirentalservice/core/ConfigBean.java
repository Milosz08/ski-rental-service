/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ConfigBean.java
 *  Last modified: 21/01/2023, 03:39
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core;

import lombok.Getter;
import jakarta.ejb.*;

import java.nio.file.*;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@Startup
@Singleton(name = "ConfigFactoryBean")
public class ConfigBean {

    private final String systemVersion;
    private final int circaDateYears;
    private final String defPageTitle;
    private final String uploadsDir;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ConfigBean() throws IOException {
        this.systemVersion = defaultIfEmpty(getClass().getPackage().getImplementationVersion(), "DEVELOPMENT");
        this.circaDateYears = 18;
        this.defPageTitle = "SkiRent System";
        final Path path = Paths.get(System.getProperty("jboss.server.data.dir") + "/ski-rental-service");
        Files.createDirectories(path);
        this.uploadsDir = path.toString();
    }
}
