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
package nl.knaw.dans.dccd.common.web.validate;

/*
 * Note by pboon: was in eof project package nl.knaw.dans.easy.web.wicketutil
 */

import java.util.Arrays;
import java.util.Collection;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.easymock.EasyMock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for the password policy validator.
 *
 * @author Herman Suijs
 */
@RunWith(Parameterized.class)
public class PasswordPolicyValidatorTest
{
    /**
     * Password to test.
     */
    private final String  password;

    /**
     * Validity of the tested password.
     */
    private final boolean valid;

    /**
     * Create a validation test with parameterized passwords.
     *
     * @param password to test
     * @param valid true if password is valid
     */
    public PasswordPolicyValidatorTest(String password, boolean valid)
    {
        super();
        this.password = password;
        this.valid = valid;

    }

    /**
     * Return a collection of passwords to test.
     *
     * @return collection of passwords
     */
    @Parameters
    public static Collection< Object[] > getValidPasswords()
    {
        return Arrays.asList(new Object[][] { {"test1ValidPa$$", true}, {"an0th3rP@ssword", true}, {"invalid", false}, {"invalidpassword", false},
                {"invalidPassword", false}, {"1nvalid", false}, {"noDig1Ts", false}, {"n0Sp3cialChars", false}, {"N0LOWERCASE#", false},
                {"n0uppercase&", false}});

    }

    /**
     * Test valid passwords.
     */
    @Test
    public void testValidPasswords()
    {
        IValidatable validatable = EasyMock.createMock(IValidatable.class);

        EasyMock.expect(validatable.getValue()).andReturn(this.password).anyTimes();

        if (!this.valid)
        {
            validatable.error((IValidationError) EasyMock.anyObject());
            EasyMock.expectLastCall().anyTimes();
        }
        EasyMock.replay(validatable);

        PasswordPolicyValidator policy = PasswordPolicyValidator.getInstance();
        policy.validate(validatable);

        EasyMock.verify(validatable);

    }

    /**
     * Test invalid passwords.
     */
    public void testInvalidPasswords()
    {
        IValidatable validatable = EasyMock.createMock(IValidatable.class);

        EasyMock.expect(validatable.getValue()).andReturn("testPassword@").anyTimes();

        EasyMock.replay(validatable);

        PasswordPolicyValidator policy = PasswordPolicyValidator.getInstance();
        policy.validate(validatable);

        EasyMock.verify(validatable);
    }

}
