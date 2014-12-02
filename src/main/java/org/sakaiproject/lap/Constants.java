/**
 * Copyright 2014 Sakaiproject Licensed under the
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User-definable constants
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
public class Constants {

    public final static String DEFAULT_CSV_STORAGE_DIRECTORY = "lap-data" + System.getProperty("file.separator");
    public final static String ACTION_ACTIVITY = "activity";
    public final static String ACTION_GRADES = "grades";
    public final static String CSV_FILE_ACTIVITY = "activity.csv";
    public final static String CSV_FILE_GRADES = "grades.csv";
    public final static Map<String, String> AVAILABLE_FILE_LISTING = new LinkedHashMap<String, String>(2) {
        private static final long serialVersionUID = 1L;

        {
            put(ACTION_ACTIVITY, CSV_FILE_ACTIVITY);
            put(ACTION_GRADES, CSV_FILE_GRADES);
        }
    };

    /*
     * Activity CSV headers
     */
    public final static String CSV_HEADER_ACTIVITY_1 = "ALTERNATIVE_ID";
    public final static String CSV_HEADER_ACTIVITY_2 = "COURSE_ID";
    public final static String CSV_HEADER_ACTIVITY_3 = "EVENT";
    public final static String CSV_HEADER_ACTIVITY_4 = "EVENT_DATE";

    /*
     * Grades CSV headers
     */
    public final static String CSV_HEADER_GRADES_1 = "ALTERNATIVE_ID";
    public final static String CSV_HEADER_GRADES_2 = "COURSE_ID";
    public final static String CSV_HEADER_GRADES_3 = "GRADABLE_OBJECT";
    public final static String CSV_HEADER_GRADES_4 = "CATEGORY";
    public final static String CSV_HEADER_GRADES_5 = "MAX_POINTS";
    public final static String CSV_HEADER_GRADES_6 = "EARNED_POINTS";
    public final static String CSV_HEADER_GRADES_7 = "WEIGHT";
    public final static String CSV_HEADER_GRADES_8 = "GRADE_DATE";

    public final static String MIME_TYPE_CSV = "text/csv";
    public final static String ENCODING_UTF8 = "UTF-8";

    public final static String DEFAULT_NO_TIME = "Never";

    /**
     * Default times for scheduled extraction of data (defaults to midnight and noon each day)
     */
    public final static String[] DEFAULT_DATA_EXTRACTION_TIMES = new String[] {"00:00:00", "12:00:00"};

    /**
     * Default interval to check to see if the scheduled extraction should run (default is 60 seconds)
     */
    public final static long DEFAULT_DATA_EXTRACTION_CHECK_INTERVAL = 1000L * 60L;

    public final static String EXTRACTION_TYPE_EXTENSION_SCHEDULED = "_S";
    public final static String EXTRACTION_TYPE_NAME_SCHEDULED = "scheduled";
    public final static String EXTRACTION_TYPE_EXTENSION_MANUAL = "_M";
    public final static String EXTRACTION_TYPE_NAME_MANUAL = "manual";

    public final static Map<String, String> EXTRACTION_TYPE_MAP = new HashMap<String, String>(2) {
        private static final long serialVersionUID = 1L;

        {
            put(EXTRACTION_TYPE_EXTENSION_MANUAL, EXTRACTION_TYPE_NAME_MANUAL);
            put(EXTRACTION_TYPE_EXTENSION_SCHEDULED, EXTRACTION_TYPE_NAME_SCHEDULED);
        }
    };

    /*
     * REST endpoint response map keys
     */
    public final static String REST_MAP_KEY_DISPLAY_DATE = "displayDate";
    public final static String REST_MAP_KEY_DATE_TIME = "dateTime";
    public final static String REST_MAP_KEY_LATEST_EXTRACTION_DATE = "latestExtractionDate";
    public final static String REST_MAP_KEY_NEXT_EXTRACTION_DATE = "nextExtractionDate";
    public final static String REST_MAP_KEY_ALL_EXTRACTION_DATES = "allExtractionDates";
    public final static String REST_MAP_KEY_AVAILABLE_FILES = "availableFiles";
    public final static String REST_MAP_KEY_VALID_FILE_SYSTEM = "validFileSystem";

}
