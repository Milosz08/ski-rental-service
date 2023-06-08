/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MailRequestPayload.java
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

package pl.polsl.skirentalservice.core.mail;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Set;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@Builder
@AllArgsConstructor
public class MailRequestPayload {
    private String messageResponder;
    private String subject;
    private String templateName;
    private Map<String, Object> templateVars;
    private Set<String> attachmentsPaths;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "messageResoponder='" + messageResponder +
            ", subject='" + subject +
            ", templateName='" + templateName +
            ", templateVars=" + templateVars +
            ", attachmentsPaths=" + attachmentsPaths +
            '}';
    }
}
