/*******************************************************************************
 * Copyright 2015 DANS - Data Archiving and Networked Services
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package nl.knaw.dans.dccd.web.authn;

/*
 * Note by pboon: was part of eof project package nl.knaw.dans.easy.web.common
 * These strings are method names of the User class.
 * Having these properties in a separate class is usefull,
 * because these property names (for get/set methods) are used in several forms
 * Renaming or adding of methods only has to be done here!
 */

/**
 * Useful properties of the class {@link User}.
 *
 * @author ecco Mar 11, 2009
 */
public interface UserProperties
{

    public static final String   TELEPHONE              = "telephone";
    public static final String   COUNTRY                = "country";
    public static final String   CITY                   = "city";
    public static final String   POSTALCODE             = "postalCode";
    public static final String   ADDRESS                = "address";
    public static final String   FUNCTION               = "function";
    public static final String   DEPARTMENT             = "department";
    public static final String   ORGANIZATION           = "organization";
    public static final String   EMAIL                  = "email";
    public static final String   DISPLAYNAME            = "displayName";
    public static final String   COMMONNAME             = "commonName";
    public static final String   SURNAME                = "surname";
    public static final String   PREFIXES               = "prefixes";
    public static final String   FIRSTNAME              = "firstname";
    public static final String   INITIALS               = "initials";
    public static final String   TITLE                  = "title";
    public static final String   USER_ID                = "userId";
    public static final String   STATE                  = "state";
    public static final String   ROLES                  = "roles";
    public static final String   GROUP_IDS              = "groupIds";
    public static final String   DISPLAYROLES           = "displayRoles";
    public static final String   DISPLAYGROUPS          = "displayGroups";
    public static final String   DAI         			= "digitalAuthorIdentifier";

    public static final String[] ALL_PROPERTIES         = {USER_ID, COMMONNAME, DISPLAYNAME, TITLE, INITIALS,
            FIRSTNAME, PREFIXES, SURNAME, ORGANIZATION, DEPARTMENT, FUNCTION, ADDRESS, POSTALCODE, CITY, COUNTRY,
            EMAIL, TELEPHONE, STATE, DISPLAYROLES, DISPLAYGROUPS, DAI};

    /**
     * Minimum length of a user id.
     */
    public static final int      MINIMUM_USER_ID_LENGTH = 5;
    
    /**
     * Allowed characters for the user id
     * letters lowercase [a-z] and uppercase[A-Z] and digits [0-9]
     */
    public static final String	USER_ID_ALLOWED_CHARS_PATTERN = "([A-Za-z0-9]+)";

}
