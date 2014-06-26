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
package org.sakaiproject.lap;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {

    public final static String DEFAULT_CSV_STORAGE_DIRECTORY = "lap-data/";
    public final static String CSV_FILE_COURSES = "courses.csv";
    public final static String CSV_FILE_GRADES = "grades.csv";
    public final static String CSV_FILE_STUDENTS = "students.csv";
    public final static String CSV_FILE_USAGE = "usage.csv";

    public final static String DATE_FORMAT_FILE_NAME = "yyyyMMdd_HHmmss";
    public final static String DATE_FORMAT_FILE_NAME_DATE_ONLY = "yyyyMMdd";
    public final static String DATE_FORMAT_DROPDOWN = "MMMM dd, yyyy HH:mm:ss";
    public final static String DATE_START_TIME = " 00:00:00";
    public final static String DATE_END_TIME = " 23:59:59";
    public final static SimpleDateFormat FORMAT_DATE_ONLY = new SimpleDateFormat(Constants.DATE_FORMAT_FILE_NAME_DATE_ONLY, Locale.ENGLISH);
    public final static SimpleDateFormat FORMAT_FILE_NAME = new SimpleDateFormat(Constants.DATE_FORMAT_FILE_NAME, Locale.ENGLISH);
    public final static SimpleDateFormat FORMAT_DROP_DOWN = new SimpleDateFormat(Constants.DATE_FORMAT_DROPDOWN, Locale.ENGLISH);

    public final static String MIME_TYPE_CSV = "text/csv";
    public final static String ENCODING_UTF8 = "UTF-8";

    /**
     * Default times for automatic generation of data reports (defaults to GMT midnight and noon each day)
     */
    public final static String[] DEFAULT_DATA_GENERATION_TIMES = new String[] {"00:00:00", "12:00:00"};
}
