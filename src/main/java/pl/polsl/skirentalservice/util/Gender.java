/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: Gender.java
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

package pl.polsl.skirentalservice.util;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("mężczyzna", 'M'),
    FEMALE("kobieta", 'K'),
    UNISEX("unisex", 'U');

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private final char alias;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FormSelectTupleDto convertToTuple(Gender gender) {
        return new FormSelectTupleDto(name.equals(gender.name), String.valueOf(alias), name);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Gender findByAlias(String alias) {
        return Arrays.stream(Gender.values()).filter(g -> g.alias == alias.charAt(0)).findFirst().orElse(Gender.MALE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<FormSelectTupleDto> getGenders() {
        return List.of(
            new FormSelectTupleDto(true, String.valueOf(MALE.alias), MALE.name),
            new FormSelectTupleDto(false, String.valueOf(FEMALE.alias), FEMALE.name)
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<FormSelectTupleDto> getSelectedGender(Gender gender) {
        return List.of(MALE.convertToTuple(gender), FEMALE.convertToTuple(gender));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<FormSelectTupleDto> getSelectedGenderWithUnisex(Gender gender) {
        return List.of(MALE.convertToTuple(gender), FEMALE.convertToTuple(gender), UNISEX.convertToTuple(gender));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<FormSelectTupleDto> getGendersWithUnisex() {
        final List<FormSelectTupleDto> genders = new ArrayList<>(getGenders());
        genders.add(new FormSelectTupleDto(false, String.valueOf(UNISEX.alias), UNISEX.name));
        return genders;
    }
}
