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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.lap.Constants;
import org.sakaiproject.lap.service.ComparatorService;
import org.sakaiproject.lap.service.CsvService;
import org.sakaiproject.lap.service.DateService;
import org.sakaiproject.lap.service.FileService;

public class Data extends Db {

    private final Log log = LogFactory.getLog(getClass());

    public boolean prepareUsageCsv(String directory) {
        return prepareUsageCsv("", "", "", directory);
    }

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
                startDate += DateService.DATE_START_TIME;
                preparedStatement.setString(2, startDate);
            }

            if (hasEndDate) {
                endDate += DateService.DATE_END_TIME;
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

    public boolean prepareGradesCsv(String directory) {
        return prepareGradesCsv("", directory);
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

    public boolean prepareCoursesCsv(String directory) {
        return prepareCoursesCsv("", directory);
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

    public boolean prepareStudentsCsv(String directory) {
        return prepareStudentsCsv("", directory);
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
                Date date = DateService.SDF_FILE_NAME.parse(directoryName);
                String displayDate = DateService.SDF_DISPLAY.format(date);
                directories.put(directoryName, displayDate);
            } catch (Exception e) {
                log.error("Error parsing directory name: " + e, e);
                directories.put(directoryName, directoryName);
            }
        }

        return directories;
    }

    public String getLatestRunDate() {
        Map<String, String> directoryListing = getDirectoryListing();

        return dateService.getLatestRunDate(directoryListing);
    }

    public String getNextScheduledRunDate() {
        return dateService.getNextScheduledRunDate();
    }

    private CsvService csvService;
    public void setCsvService(CsvService csvService) {
        this.csvService = csvService;
    }

    private DateService dateService;
    public void setDateService(DateService dateService) {
        this.dateService = dateService;
    }

    private FileService fileService;
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    private Queries queries;
    public void setQueries(Queries queries) {
        this.queries = queries;
    }

}
