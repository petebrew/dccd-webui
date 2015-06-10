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
package nl.knaw.dans.dccd.web.search.years;

/**
 * Labels for the years dating system
 */
public enum YearSuffix {
    AD("AD"), // Anno Domini (after Christ)
    BC("BC"), // Before Christ (there is no year 0)
    BP("BP"); // Before Present (1950)
    private final String value;
    YearSuffix(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static YearSuffix fromValue(String v) {
        for (YearSuffix c: YearSuffix.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}