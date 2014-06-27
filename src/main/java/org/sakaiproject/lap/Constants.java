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

public class Constants {

    public final static String DEFAULT_CSV_STORAGE_DIRECTORY = "lap-data/";
    public final static String CSV_FILE_COURSES = "courses.csv";
    public final static String CSV_FILE_GRADES = "grades.csv";
    public final static String CSV_FILE_STUDENTS = "students.csv";
    public final static String CSV_FILE_USAGE = "usage.csv";

    public final static String MIME_TYPE_CSV = "text/csv";
    public final static String ENCODING_UTF8 = "UTF-8";

    /**
     * Default times for automatic generation of data reports (defaults to GMT midnight and noon each day)
     */
    public final static String[] DEFAULT_DATA_GENERATION_TIMES = new String[] {"00:00:00", "12:00:00"};

    /**
     * Default interval to check to see if the automatic generation should run (default is 60 seconds)
     */
    public final static int DEFAULT_DATA_GENERATION_CHECK_INTERVAL = 60;
}
