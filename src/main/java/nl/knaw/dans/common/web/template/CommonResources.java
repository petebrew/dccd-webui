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
package nl.knaw.dans.common.web.template;

/*
 * Note by pboon: copied from the eof project package:  nl.knaw.dans.easy.web.template;
 * For now, the resources are all about the User, but that might change
 */

/**
 * Keys for common resources, found on AbstractCommonPage*properties. These resources can be found throughout the
 * application and have a uniform translation. Please do not use common resources for resources that are specific to a
 * particular package or a particular page.
 *
 * @author ecco Mar 13, 2009
 */
public interface CommonResources
{

    public static final String USER_USER_ID          = "user.userId";
    public static final String USER_PASSWORD         = "user.password";
    public static final String USER_CONFIRM_PASSWORD = "user.confirmPassword";
    public static final String USER_EMAIL            = "user.email";
    public static final String USER_CONFIRM_EMAIL    = "user.confirmEmail";
    public static final String USER_TITLE            = "user.title";
    public static final String USER_INITIALS         = "user.initials";
    public static final String USER_FIRSTNAME        = "user.firstname";
    public static final String USER_PREFIXES         = "user.prefixes";
    public static final String USER_SURNAME          = "user.surname";
    public static final String USER_COMMONNAME       = "user.commonName";
    public static final String USER_DISPLAYNAME      = "user.displayName";
    public static final String USER_ORGANIZATION     = "user.organization";
    public static final String USER_DEPARTMENT       = "user.department";
    public static final String USER_FUNCTION         = "user.function";
    public static final String USER_ADDRESS          = "user.address";
    public static final String USER_POSTALCODE       = "user.postalCode";
    public static final String USER_CITY             = "user.city";
    public static final String USER_COUNTRY          = "user.country";
    public static final String USER_TELEPHONE        = "user.telephone";
    public static final String USER_STATE            = "user.state";
    public static final String USER_ROLES            = "user.roles";
    public static final String USER_WELCOME          = "user.welcome";
    public static final String USER_DAI				 = "user.digitalAuthorIdentifier";
}
