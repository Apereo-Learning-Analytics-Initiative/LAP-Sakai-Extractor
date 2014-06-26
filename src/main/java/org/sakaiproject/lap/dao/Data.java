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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.lap.Constants;
import org.sakaiproject.lap.service.CsvService;
import org.sakaiproject.lap.service.FileService;

public class Data extends Db {

    private final Log log = LogFactory.getLog(Data.class);

    /**
     * Create the usage.csv file, store it on file system
     * 
     * @param criteria the site title search term to use
     * @param startDate the date to start the inclusion of data
     * @param endDate the date to end the inclusion of data
     * @param directory the name of the date-specific directory to store the .csv file
     * @return true, if creation and storage successful
     */
    public boolean prepareUsageCsv(String criteria, String startDate, String endDate, String directory) {
        boolean hasStartDate = StringUtils.isNotBlank(startDate);
        boolean hasEndDate = StringUtils.isNotBlank(endDate);
        boolean success = false;
        PreparedStatement preparedStatement = null;

        try {
            String sql = queries.getSqlEvents(hasStartDate, hasEndDate);

            preparedStatement = createPreparedStatement(preparedStatement, sql);
            preparedStatement.setString(1, "%" + criteria + "%");

            if (hasStartDate) {
                startDate += Constants.DATE_START_TIME;
                preparedStatement.setString(2, startDate);
            }

            if (hasEndDate) {
                endDate += Constants.DATE_END_TIME;
                if (hasStartDate) {
                    preparedStatement.setString(3, endDate);
                } else {
                    preparedStatement.setString(2, endDate);
                }
            }

            success = saveToFile(preparedStatement, directory, Constants.CSV_FILE_USAGE);
        } catch (Exception e) {
            log.error("Error preparing usage csv: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return success;
    }

    /**
     * Create the grades.csv file, store it on file system
     * 
     * @param criteria the site title search term to use
     * @param directory the name of the date-specific directory to store the .csv file
     * @return true, if creation and storage successful
     */
    public boolean prepareGradesCsv(String criteria, String directory) {
        boolean success = false;
        PreparedStatement preparedStatement = null;

        try {
            String sql = queries.getSqlGrades();

            preparedStatement = createPreparedStatement(preparedStatement, sql);
            preparedStatement.setString(1, "%" + criteria + "%");

            success = saveToFile(preparedStatement, directory, Constants.CSV_FILE_GRADES);
        } catch (Exception e) {
            log.error("Error preparing grades csv: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return success;
    }

    /**
     * Create the courses.csv file, store it on file system
     * 
     * @param criteria the site title search term to use
     * @param directory the name of the date-specific directory to store the .csv file
     * @return true, if creation and storage successful
     */
    public boolean prepareCoursesCsv(String criteria, String directory) {
        boolean success = false;
        PreparedStatement preparedStatement = null;

        try {
            String sql = queries.getSqlCourses();

            /*preparedStatement = createPreparedStatement(preparedStatement, sql);
            preparedStatement.setString(1, "%" + criteria + "%");

            success = saveToFile(preparedStatement, directory, Constants.CSV_FILE_COURSES);*/
        } catch (Exception e) {
            log.error("Error preparing courses csv: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return success;
    }

    /**
     * Create the students.csv file, store it on file system
     * 
     * @param criteria the site title search term to use
     * @param directory the name of the date-specific directory to store the .csv file
     * @return true, if creation and storage successful
     */
    public boolean prepareStudentsCsv(String criteria, String directory) {
        boolean success = false;
        PreparedStatement preparedStatement = null;

        try {
            String sql = queries.getSqlStudents();

            /*preparedStatement = createPreparedStatement(preparedStatement, sql);
            preparedStatement.setString(1, "%" + criteria + "%");

            success = saveToFile(preparedStatement, directory, Constants.CSV_FILE_STUDENTS);*/
        } catch (Exception e) {
            log.error("Error preparing students csv: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return success;
    }

    private boolean saveToFile(PreparedStatement preparedStatement, String directory, String fileName) throws Exception {
        ResultSet results = executePreparedStatement(preparedStatement);
        ResultSetMetaData metadata = results.getMetaData();
        int numberOfColumns = metadata.getColumnCount();

        // header row
        List<String> header = new ArrayList<String>();
        for (int i = 1;i <=numberOfColumns;i++) {
            header.add(metadata.getColumnLabel(i));
        }
        String csvData = csvService.setAsCsvRow(header);

        // data rows
        while (results.next()) {
            List<String> row = new ArrayList<String>();
            for (int i = 1;i <=numberOfColumns;i++) {
                row.add(results.getString(i));
            }
            csvData += csvService.setAsCsvRow(row);
        }

        boolean success = fileService.saveStringToFile(csvData, directory, fileName);

        return success;
    }

    public String getCsvData(String datedDirectory, String fileName) {
        String csvData = fileService.readFileIntoString(datedDirectory, fileName);

        return csvData;
    }

    public Map<String, String> getDirectoryListing() {
        List<String> directoryNames = fileService.parseDirectory();
        Map<String, String> directories = new LinkedHashMap<String, String>(directoryNames.size());

        for (String directoryName : directoryNames) {
            try {
                Date date = Constants.FORMAT_FILE_NAME.parse(directoryName);
                String formattedDate = Constants.FORMAT_DROP_DOWN.format(date);
                directories.put(directoryName, formattedDate);
            } catch (Exception e) {
                log.error("Error parsing directory name: " + e, e);
                directories.put(directoryName, directoryName);
            }
        }

        return directories;
    }

    public String getLatestRunDate() {
        Map<String, String> directoryListing = getDirectoryListing();

        return getLatestRunDate(directoryListing);
    }

    public String getLatestRunDate(Map<String, String> directoryListing) {
        if (directoryListing == null || directoryListing.isEmpty()) {
            return "Never";
        }

        String firstDirectory = directoryListing.keySet().iterator().next();
        String latestRundate = directoryListing.get(firstDirectory);

        return latestRundate;
    }

    public String getNextScheduledRunDate() {
        String nextScheduledRunDate = "";
        String[] scheduledRunTimes = ServerConfigurationService.getStrings("lap.data.generation.times");
        if (ArrayUtils.isEmpty(scheduledRunTimes)) {
            scheduledRunTimes = Constants.DEFAULT_DATA_GENERATION_TIMES;
        }

        try {
            List<String> scheduledDateStrings = new ArrayList<String>(scheduledRunTimes.length);
            
            Date currentTime = new Date();
            String currentTimeDateOnly = Constants.FORMAT_DATE_ONLY.format(currentTime);
            String currentTimeString = Constants.FORMAT_FILE_NAME.format(currentTime);

            scheduledDateStrings.add(currentTimeString);
            for (String runTime : scheduledRunTimes) {
                scheduledDateStrings.add(currentTimeDateOnly + "_" + runTime.replaceAll(":", ""));
            }

            Collections.sort(scheduledDateStrings, new DateComparator());

            int position = scheduledDateStrings.indexOf(currentTimeString);
            if (position < scheduledDateStrings.size() - 1) {
                Date nextDate = Constants.FORMAT_FILE_NAME.parse(scheduledDateStrings.get(position + 1));
                nextScheduledRunDate = Constants.FORMAT_DROP_DOWN.format(nextDate);
            } else {
                String firstTime = scheduledDateStrings.get(0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Constants.FORMAT_FILE_NAME.parse(firstTime));
                calendar.add(Calendar.DATE, 1);
                nextScheduledRunDate = Constants.FORMAT_DROP_DOWN.format(calendar.getTime());
            }
        } catch (Exception e) {
            log.error("Error getting scheduled run time: " + e, e);
        }

        return nextScheduledRunDate;
    }

    private CsvService csvService;
    public void setCsvService(CsvService csvService) {
        this.csvService = csvService;
    }

    private FileService fileService;
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    private Queries queries;
    public void setQueries(Queries queries) {
        this.queries = queries;
    }

    /**
     * Compare directory names by converting name to date
     */
    public class DateComparator implements Comparator<String> {
        @Override
        public int compare(String arg0, String arg1) {
            try {
                Date date1 = Constants.FORMAT_FILE_NAME.parse(arg0);
                Date date2 = Constants.FORMAT_FILE_NAME.parse(arg1);
                return date1.compareTo(date2);
            } catch (ParseException e) {
                log.error("Error comparing dates: " + e, e);
                return 0;
            }
        }
    }
}
