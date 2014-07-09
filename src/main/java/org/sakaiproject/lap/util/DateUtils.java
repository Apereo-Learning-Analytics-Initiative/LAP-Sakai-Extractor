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
package org.sakaiproject.lap.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.lap.service.DateService;

public class DateUtils {

    private final static Log log = LogFactory.getLog(DateUtils.class);

    public final static String DATE_FORMAT_FILE_NAME = "yyyyMMdd_HHmmss";
    public final static String DATE_FORMAT_FILE_NAME_DATE_ONLY = "yyyyMMdd";
    public final static String DATE_FORMAT_DROPDOWN = "MMMM dd, yyyy HH:mm:ss";
    public final static String DATE_FORMAT_TIME_ONLY = "HH:mm:ss";
    public final static String DATE_FORMAT_DATE_TIME = "yyyyMMdd HH:mm:ss";
    public final static String DATE_FORMAT_DATE_TIME_MYSQL = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_START_TIME = " 00:00:00";
    public final static String DATE_END_TIME = " 23:59:59";
    
    public final static SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat(DATE_FORMAT_DATE_TIME, Locale.ENGLISH);
    public final static SimpleDateFormat SDF_DATE_TIME_MYSQL = new SimpleDateFormat(DATE_FORMAT_DATE_TIME_MYSQL, Locale.ENGLISH);
    public final static SimpleDateFormat SDF_DATE_ONLY = new SimpleDateFormat(DATE_FORMAT_FILE_NAME_DATE_ONLY, Locale.ENGLISH);
    public final static SimpleDateFormat SDF_TIME_ONLY = new SimpleDateFormat(DATE_FORMAT_TIME_ONLY, Locale.ENGLISH);
    public final static SimpleDateFormat SDF_FILE_NAME = new SimpleDateFormat(DATE_FORMAT_FILE_NAME, Locale.ENGLISH);
    public final static SimpleDateFormat SDF_DISPLAY = new SimpleDateFormat(DATE_FORMAT_DROPDOWN, Locale.ENGLISH);

    /**
     * Transforms a MySQL-style date string (yyyy-MM-dd HH:mm:ss) to a java.sql.Timestamp
     * 
     * @param dateString the MySQL-style date formatted string (yyyy-MM-dd HH:mm:ss)
     * @return the java.sql.Timestamp object, or null if an error occurred
     */
    public static Timestamp dateStringToSqlTimestamp(String dateString) {
         Timestamp sqlTimestamp = null;

        try {
            Date date = SDF_DATE_TIME_MYSQL.parse(dateString);

            sqlTimestamp = new Timestamp(date.getTime());
        } catch (Exception e) {
            log.error("Error parsing dateString to java.sql.Timestamp. Error: " + e, e);
        }

        return sqlTimestamp;
    }

    /**
     * Compare dates for sorting latest to earliest
     */
    public static class DateComparatorLatestToEarliest implements Comparator<String> {
        @Override
        public int compare(String arg0, String arg1) {
            DateService dateService = new DateService();
            Date date1 = dateService.parseDirectoryToDateTime(arg0);
            Date date2 = dateService.parseDirectoryToDateTime(arg1);

            return date2.compareTo(date1);
        }
    }

    /**
     * Compare dates for sorting earliest to latest date
     */
    public static class DateComparatorEarliestToLatest implements Comparator<String> {
        @Override
        public int compare(String arg0, String arg1) {
            DateService dateService = new DateService();
            Date date1 = dateService.parseDirectoryToDateTime(arg0);
            Date date2 = dateService.parseDirectoryToDateTime(arg1);

            return date1.compareTo(date2);
        }
    }

}
