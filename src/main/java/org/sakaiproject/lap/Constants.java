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

import java.util.HashMap;
import java.util.Map;

/**
 * User-definable constants
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
public class Constants {

    public final static String DEFAULT_CSV_STORAGE_DIRECTORY = "lap-data/";
    public final static String CSV_FILE_ACTIVITY = "activity.csv";
    public final static String CSV_FILE_GRADES = "grades.csv";


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

    public final static Map<String, String> EXTRACTION_TYPE_MAP = new HashMap<String, String>() {
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

}
