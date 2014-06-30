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
package org.sakaiproject.lap.dao;

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
    public String getSqlActivity(boolean hasStartDate, boolean hasEndDate) {
        String sql = "SELECT " +
                "suim.EID ALTERNATIVE_ID," +
                "ss.SITE_ID COURSE_ID," +
                "se.EVENT EVENT," +
                "se.EVENT_DATE EVENT_DATE " +
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
                "ss.TITLE LIKE ?";

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
    public String getSqlGrades() {
        String sql = "SELECT " +
                "suim.EID ALTERNATIVE_ID," +
                "ss.SITE_ID COURSE_ID," +
                "go.NAME GRADABLE_OBJECT," +
                "c.NAME CATEGORY," +
                "go.POINTS_POSSIBLE MAX_POINTS," +
                "gr.POINTS_EARNED EARNED_POINTS," +
                "c.WEIGHT WEIGHT," +
                "gr.DATE_RECORDED GRADE_DATE " +
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
                "(c.REMOVED IS NULL or c.REMOVED <> 1) AND " +
                "(go.REMOVED IS NULL or go.REMOVED <> 1) " +
            "ORDER BY " +
                "go.DUE_DATE ASC, " +
                "go.NAME ASC, " +
                "gr.STUDENT_ID ASC";

        return sql;
    }

}
