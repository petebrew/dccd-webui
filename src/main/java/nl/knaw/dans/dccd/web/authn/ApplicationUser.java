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
/**
 *
 */
package nl.knaw.dans.dccd.web.authn;

import java.io.Serializable;

import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.DccdUserImpl;

import org.apache.commons.lang.builder.HashCodeBuilder;

/*
 * Note by pboon: copied from the eof project: nl.knaw.dans.easy.web.common;
 * Wraps the User(Impl), for use on an input form, like the when Registrating a new User
 *
 * Changed UserImpl to DccdUserImpl in constructor,
 * but maybe we should make it generic <T extends User> ?
 */

/**
 * ApplicationUser that is maintained within the session.
 *
 * @author Herman Suijs
 */
public class ApplicationUser implements Serializable
{

    /**
     * Property name.
     */
    public static final String USER_ID           = "userId";

    /**
     * Property name.
     */
    public static final String TITLE             = "title";

    /**
     * Property name.
     */
    public static final String INITIALS          = "initials";

    /**
     * Property name.
     */
    public static final String FIRSTNAME         = "firstname";

    /**
     * Property name.
     */
    public static final String PREFIXES          = "prefixes";

    /**
     * Property name.
     */
    public static final String SURNAME           = "surname";

    /**
     * Property name.
     */
    public static final String PASSWORD          = "password";

    /**
     * Property name.
     */
    public static final String EMAIL             = "email";

    /**
     * Property name.
     */
    public static final String CONFIRM_PASSWORD  = "confirmPassword";

    /**
     * Property name.
     */
    public static final String CONFIRM_EMAIL     = "confirmEmail";

    /**
     * Property name.
     */
    public static final String ACCEPT_CONDITIONS = "acceptConditions";

    /**
     * Property name.
     */
    public static final String ORGANIZATION = "organization";

    /**
     * Property name.
     */
    public static final String DEPARTMENT = "department";

    /**
     * Property name.
     */
    public static final String FUNCTION = "function";

    /**
     * Property name.
     */
    public static final String ADDRESS = "address";

    /**
     * Property name.
     */
    public static final String POSTALCODE = "postalCode";

    /**
     * Property name.
     */
    public static final String CITY = "city";

    /**
     * Property name.
     */
    public static final String COUNTRY = "country";

    /**
     * Property name.
     */
    public static final String TELEPHONE = "telephone";


    /**
     * Serial version uid.
     */
    private static final long  serialVersionUID  = -6040242842409608573L;

    /**
     * Password only available when entered in screen.
     */
    private String             password;

    /**
     * Password confirmation in screens.
     */
    private String             confirmPassword;

    /**
     * Email confirmation in screens.
     */
    private String             confirmEmail;


    /**
     * Wrapped user model object.
     */
    private final DccdUser         businessUser;

    /**
     * Default constructor.
     */
    public ApplicationUser()
    {
        this(new DccdUserImpl());//UserImpl());
    }

    /**
     * Return wrapped business user.
     *
     * @return business user.
     */
    public DccdUser getBusinessUser()
    {
        return this.businessUser;
    }

    /**
     * Constructor used with model user.
     *
     * @param user
     *        model user.
     */
    public ApplicationUser(final DccdUser user)
    {
        this.businessUser = user;
    }

    public String getDigitalAuthorIdentifier() {
		return businessUser.getDigitalAuthorIdentifier();
	}

	public void setDigitalAuthorIdentifier(String digitalAuthorIdentifier) {
		businessUser.setDigitalAuthorIdentifier(digitalAuthorIdentifier);
	}

    /**
     * Get email.
     *
     * @return email.
     */
    public String getEmail()
    {
        return this.businessUser.getEmail();
    }



	/**
     * Get user.
     *
     * @return user
     */
    public String getUserId()
    {
        return this.businessUser.getId();
    }

    /**
     * Set email.
     *
     * @param email
     *        email
     */
    public void setEmail(final String email)
    {
        this.businessUser.setEmail(email);
    }

    /**
     * Set password.
     *
     * @param password
     *        password
     */
    public void setPassword(final String password)
    {
        this.businessUser.setPassword(password);
        this.password = password;
    }

    /**
     * Get password only when entered in screen.
     *
     * @return password.
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * Set user.
     *
     * @param userId
     *        user
     */
    public void setUserId(final String userId)
    {
        this.businessUser.setId(userId);
    }

    public void setTitle(String title)
    {
        this.businessUser.setTitle(title);
    }

    public String getTitle()
    {
        return businessUser.getTitle();
    }

    public void setInitials(String initials)
    {
        businessUser.setInitials(initials);
    }

    public String getInitials()
    {
        return businessUser.getInitials();
    }

    public void setFirstname(String firstname)
    {
        businessUser.setFirstname(firstname);
    }

    public String getFirstname()
    {
        return businessUser.getFirstname();
    }

    public void setPrefixes(String prefixes)
    {
        businessUser.setPrefixes(prefixes);
    }

    public String getPrefixes()
    {
        return businessUser.getPrefixes();
    }

    public void setSurname(String surname)
    {
        businessUser.setSurname(surname);
    }

    public String getSurname()
    {
        return businessUser.getSurname();
    }

    /**
	 * @return
	 * @see nl.knaw.dans.common.lang.user.Person#getAddress()
	 */
	public String getAddress() {
		return businessUser.getAddress();
	}

	/**
	 * @return
	 * @see nl.knaw.dans.common.lang.user.Person#getCity()
	 */
	public String getCity() {
		return businessUser.getCity();
	}

	/**
	 * @return
	 * @see nl.knaw.dans.common.lang.user.Person#getCountry()
	 */
	public String getCountry() {
		return businessUser.getCountry();
	}

	/**
	 * @return
	 * @see nl.knaw.dans.common.lang.user.Person#getDepartment()
	 */
	public String getDepartment() {
		return businessUser.getDepartment();
	}

	/**
	 * @return
	 * @see nl.knaw.dans.common.lang.user.Person#getFunction()
	 */
	public String getFunction() {
		return businessUser.getFunction();
	}

	/**
	 * @return
	 * @see nl.knaw.dans.common.lang.user.Person#getOrganization()
	 */
	public String getOrganization() {
		return businessUser.getOrganization();
	}

	/**
	 * @return
	 * @see nl.knaw.dans.common.lang.user.Person#getPostalCode()
	 */
	public String getPostalCode() {
		return businessUser.getPostalCode();
	}

	/**
	 * @return
	 * @see nl.knaw.dans.common.lang.user.Person#getTelephone()
	 */
	public String getTelephone() {
		return businessUser.getTelephone();
	}

	/**
	 * @param arg0
	 * @see nl.knaw.dans.common.lang.user.Person#setAddress(java.lang.String)
	 */
	public void setAddress(String arg0) {
		businessUser.setAddress(arg0);
	}

	/**
	 * @param arg0
	 * @see nl.knaw.dans.common.lang.user.Person#setCity(java.lang.String)
	 */
	public void setCity(String arg0) {
		businessUser.setCity(arg0);
	}

	/**
	 * @param arg0
	 * @see nl.knaw.dans.common.lang.user.Person#setCountry(java.lang.String)
	 */
	public void setCountry(String arg0) {
		businessUser.setCountry(arg0);
	}

	/**
	 * @param arg0
	 * @see nl.knaw.dans.common.lang.user.Person#setDepartment(java.lang.String)
	 */
	public void setDepartment(String arg0) {
		businessUser.setDepartment(arg0);
	}

	/**
	 * @param arg0
	 * @see nl.knaw.dans.common.lang.user.Person#setFunction(java.lang.String)
	 */
	public void setFunction(String arg0) {
		businessUser.setFunction(arg0);
	}

	/**
	 * @param arg0
	 * @see nl.knaw.dans.common.lang.user.Person#setOrganization(java.lang.String)
	 */
	public void setOrganization(String arg0) {
		businessUser.setOrganization(arg0);
	}

	/**
	 * @param arg0
	 * @see nl.knaw.dans.common.lang.user.Person#setPostalCode(java.lang.String)
	 */
	public void setPostalCode(String arg0) {
		businessUser.setPostalCode(arg0);
	}

	/**
	 * @param arg0
	 * @see nl.knaw.dans.common.lang.user.Person#setTelephone(java.lang.String)
	 */
	public void setTelephone(String arg0) {
		businessUser.setTelephone(arg0);
	}

    /**
     * Get name.
     *
     * @return name
     */
    public String getCommonName()
    {
        return this.businessUser.getCommonName();
    }

    /**
     * Get confirm Password.
     *
     * @return confirm password
     */
    public String getConfirmPassword()
    {
        return this.confirmPassword;
    }

    /**
     * Set confirm password.
     *
     * @param confirmPassword
     *        password
     */
    public void setConfirmPassword(final String confirmPassword)
    {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Accepts the General DANS Conditions of Use at the time of registration.
     *
     * @return <code>true</code> if accepted, <code>false</code> otherwise
     */
    public boolean getAcceptConditions()
    {
        return businessUser.getAcceptConditionsOfUse();
    }

    /**
     * Accept the General DANS Conditions of Use at the time of registration.
     *
     * @param accept
     *        <code>true</code> if accepting the conditions of use, <code>false</code> otherwise
     */
    public void setAcceptConditions(boolean accept)
    {
        businessUser.setAcceptConditionsOfUse(accept);
    }

    /**
     * Get confirmation email address. This address is used in the registration procedure to confirm the entered email.
     *
     * @return confirmation email address.
     */
    public String getConfirmEmail()
    {
        return this.confirmEmail;
    }

    /**
     * Set confirmation email address. See registration.
     *
     * @param confirmEmail
     */
    public void setConfirmEmail(final String confirmEmail)
    {
        this.confirmEmail = confirmEmail;
    }

    /**
     * Test if equal.
     *
     * @param obj
     *        object to test
     * @return true if equal
     */
    @Override
    public boolean equals(final Object obj)
    {
        boolean equals = false;
        if (obj != null)
        {
            if (obj == this)
            {
                equals = true;
            }
            else
            {
                if (obj.getClass() == this.getClass())
                {
                    final ApplicationUser otherUser = (ApplicationUser) obj;
                    equals = this.getBusinessUser().equals(otherUser.getBusinessUser());
                }
            }
        }
        return equals;
    }

    /**
     * Return hashcode.
     *
     * @return hashcode.
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(3, 5).append(getBusinessUser()).toHashCode();
    }

    /**
     * String representation.
     *
     * @return string representation
     */
    @Override
    public String toString()
    {
        return "Application user: " + getUserId() + ", named: " + getCommonName();
    }

}
