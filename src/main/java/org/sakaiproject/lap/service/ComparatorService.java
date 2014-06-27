/**
 * Copyright 2008 Sakaiproject Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.sakaiproject.lap.service;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles all needed comparators for sorting data
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
public class ComparatorService {

    private final Log log = LogFactory.getLog(getClass());

    /**
     * Compare dates for sorting latest to earliest
     */
    public class DateComparatorLatestToEarliest implements Comparator<String> {
        @Override
        public int compare(String arg0, String arg1) {
            try {
                Date date1 = DateService.SDF_FILE_NAME.parse(arg0);
                Date date2 = DateService.SDF_FILE_NAME.parse(arg1);
                return date2.compareTo(date1);
            } catch (ParseException e) {
                log.error("Error comparing dates of files: " + e, e);
                return 0;
            }
        }
    }

    /**
     * Compare dates for sorting earliest to latest date
     */
    public class DateComparatorEarliestToLatest implements Comparator<String> {
        @Override
        public int compare(String arg0, String arg1) {
            try {
                Date date1 = DateService.SDF_FILE_NAME.parse(arg0);
                Date date2 = DateService.SDF_FILE_NAME.parse(arg1);
                return date1.compareTo(date2);
            } catch (ParseException e) {
                log.error("Error comparing dates: " + e, e);
                return 0;
            }
        }
    }

}
