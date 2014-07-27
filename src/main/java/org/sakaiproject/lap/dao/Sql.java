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
package org.sakaiproject.lap.dao;

import org.sakaiproject.lap.Constants;

/**
 * Handles all the SQL strings
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
public class Sql {

    /**
     * SQL string to get event data
     * Optionally filter by site title, start date, end date
     * 
     * @param hasStartDate has a start date?
     * @param hasEndDate has an end date?
     * @return the SQL string
     */
    public static String getSqlActivity(boolean hasStartDate, boolean hasEndDate) {
        String sql = "SELECT " +
                "suim.EID " + Constants.CSV_HEADER_ACTIVITY_1 + "," +
                "ss.SITE_ID " + Constants.CSV_HEADER_ACTIVITY_2 + "," +
                "se.EVENT " + Constants.CSV_HEADER_ACTIVITY_3 + "," +
                "se.EVENT_DATE " + Constants.CSV_HEADER_ACTIVITY_1 + " " +
            "FROM " +
                "SAKAI_EVENT se " +
            "LEFT JOIN " +
                "SAKAI_SESSION sn " +
                    "ON se.SESSION_ID = sn.SESSION_ID " +
            "LEFT JOIN " +
                "SAKAI_SITE ss " +
                    "ON se.CONTEXT = ss.SITE_ID " +
            "LEFT JOIN " +
                "SAKAI_USER_ID_MAP suim " +
                    "ON sn.SESSION_USER = suim.USER_ID " +
            "WHERE " +
                "ss.TITLE LIKE ? AND " +
                "se.SESSION_ID NOT LIKE '~%'";

        if (hasStartDate) {
            sql += " AND se.EVENT_DATE >= ?";
        }

        if (hasEndDate) {
            sql += " AND se.EVENT_DATE <= ?";
        }

        return sql;
    }

    /**
     * SQL string to get grade data
     * 
     * @return the SQL string
     */
    public static String getSqlGrades() {
        String sql = "SELECT " +
                "suim.EID " + Constants.CSV_HEADER_GRADES_1 + "," +
                "ss.SITE_ID " + Constants.CSV_HEADER_GRADES_2 + "," +
                "go.NAME " + Constants.CSV_HEADER_GRADES_3 + "," +
                "c.NAME " + Constants.CSV_HEADER_GRADES_4 + "," +
                "go.POINTS_POSSIBLE " + Constants.CSV_HEADER_GRADES_5 + "," +
                "gr.POINTS_EARNED " + Constants.CSV_HEADER_GRADES_6 + "," +
                "c.WEIGHT " + Constants.CSV_HEADER_GRADES_7 + "," +
                "gr.DATE_RECORDED " + Constants.CSV_HEADER_GRADES_8 + " " +
            "FROM " +
                "GB_GRADABLE_OBJECT_T go " +
            "LEFT JOIN " +
                "GB_CATEGORY_T c " +
                    "ON go.CATEGORY_ID = c.ID " +
            "LEFT JOIN " +
                "GB_GRADE_RECORD_T gr " +
                    "ON go.ID = gr.GRADABLE_OBJECT_ID " +
            "LEFT JOIN " +
                "GB_GRADEBOOK_T g " +
                    "ON g.ID = go.GRADEBOOK_ID " +
            "LEFT JOIN " +
                "SAKAI_SITE ss " +
                    "ON g.GRADEBOOK_UID = ss.SITE_ID " +
            "LEFT JOIN " +
                "SAKAI_USER_ID_MAP suim " +
                    "ON gr.STUDENT_ID = suim.USER_ID " +
            "WHERE " +
                "ss.TITLE LIKE ? AND " +
                "go.OBJECT_TYPE_ID = 1 AND " +
                "go.NOT_COUNTED <> 1 AND " +
                "(c.REMOVED IS NULL OR c.REMOVED <> 1) AND " +
                "(go.REMOVED IS NULL OR go.REMOVED <> 1) AND " +
                "(gr.STUDENT_ID IS NOT NULL AND gr.STUDENT_ID <> '') " +
            "ORDER BY " +
                "go.DUE_DATE ASC, " +
                "go.NAME ASC, " +
                "gr.STUDENT_ID ASC";

        return sql;
    }

}
