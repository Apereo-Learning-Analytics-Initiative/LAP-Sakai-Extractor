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

import java.util.List;

/**
 * Handles transforming data into CSV-specific strings
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
public class CsvUtils {

    /**
     * Formats a list of strings into a CSV-specific string
     * 
     * @param rowData a list of strings
     * @return the CSV-specific formatted string
     */
    public static String setAsCsvRow(List<String> rowData) {
        String csvRow = "";
        String csvSep = ",";
        StringBuilder sb = new StringBuilder();

        for (String r : rowData) {
            if (r == null) {
                r = "";
            }
            appendQuoted(sb, r);
            sb.append(csvSep);
        }

        if (sb.length() > 0) {
            csvRow = sb.toString();
            // remove last comma
            csvRow = csvRow.substring(0, csvRow.length() - 1);
            // add line break
            csvRow += "\n";
        }

        return csvRow;
    }

    /**
     * Add quotes to each CSV field
     * 
     * @param sb the string builder
     * @param toQuote the data string
     * @return the quoted string
     */
    private static StringBuilder appendQuoted(StringBuilder sb, String toQuote) {
        if ((toQuote.indexOf(',') >= 0) || (toQuote.indexOf('"') >= 0)) {
            String out = toQuote.replaceAll("\"", "\"\"");
            sb.append("\"").append(out).append("\"");
        } else {
            sb.append(toQuote);
        }

        return sb;
    }

}
