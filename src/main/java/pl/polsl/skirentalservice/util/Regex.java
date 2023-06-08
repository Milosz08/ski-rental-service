/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: Regex.java
 * Last modified: 2/10/23, 7:59 PM
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Regex {
    public static final String LOGIN_CHANGE_EMAIL       = "^[a-z0-9]{5,80}$";
    public static final String LOGIN_EMAIL              = "^[a-z0-9@.]{5,80}$";
    public static final String PASSWORD_REQ             = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*]).{8,25}$";
    public static final String PASSWORD_AVLB            = "^[a-zA-Z0-9#?!@$%^&*]{8,25}$";
    public static final String NAME_SURNAME             = "^[a-zA-ZżźćńółęąśŻŹĆĄŚĘŁÓŃ -']{3,30}$";
    public static final String PHONE_NUMBER             = "^[0-9]{3}( |)[0-9]{3}( |)[0-9]{3}$";
    public static final String DATE                     = "^[0-9]{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";
    public static final String STREET                   = "^[a-zA-Z0-9ąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\- ]{2,50}$";
    public static final String BUILDING_NR              = "^([0-9]+(?:[a-z]{0,1})){1,5}$";
    public static final String APARTMENT_NR             = "^([0-9]+(?:[a-z]{0,1})){0,5}$";
    public static final String CITY                     = "^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\- ]{2,70}$";
    public static final String POSTAL_CODE              = "^[0-9]{2}-[0-9]{3}$";
    public static final String DEF_SELECT_VALUE         = "\\b(?!none\\b)\\w+";
    public static final String POS_NUMBER_INT           = "^(?:[1-9][0-9]{3}|[1-9][0-9]{2}|[1-9][0-9]|[1-9])$";
    public static final String DECIMAL_2_ROUND          = "^$|[0-9]{0,4}(?:[.,][0-9]{1,2})?";
    public static final String DATE_TIME                = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}$";
    public static final String TAX                      = "^[1-9][0-9]?$|^100$";
}
