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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.lap.Constants;
import org.sakaiproject.lap.service.DateService;
import org.sakaiproject.lap.service.FileService;
import org.sakaiproject.lap.util.CsvUtils;
import org.sakaiproject.lap.util.DateUtils;
import org.sakaiproject.lap.util.ExtractorUtils;
import org.sakaiproject.lap.util.FileUtils;

/**
 * General data-gathering methods, from database and other operations
 * Sends data out to REST endpoints and views
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
public class Data extends Database {

    private final Log log = LogFactory.getLog(getClass());

    /**
     * Convenience method for the activity method
     * @param directory the name of the date-specific directory to store the .csv file
     * @param isManualExtraction is this from a manual extraction?
     * @return true, if creation and storage successful
     */
    public boolean prepareActivityCsv(String directory, boolean isManualExtraction) {
        return prepareActivityCsv("", "", "", directory, isManualExtraction);
    }

    /**
     * Create the activity.csv file, store it on file system
     * 
     * @param criteria the site title search term to use
     * @param startDate the date to start the inclusion of data
     * @param endDate the date to end the inclusion of data
     * @param directory the name of the date-specific directory to store the .csv file
     * @param isManualExtraction is this from a manual extraction?
     * @return true, if creation and storage successful
     */
    public boolean prepareActivityCsv(String criteria, String startDate, String endDate, String directory, boolean isManualExtraction) {
        boolean hasStartDate = StringUtils.isNotBlank(startDate);
        boolean hasEndDate = StringUtils.isNotBlank(endDate);
        boolean success = false;
        PreparedStatement preparedStatement = null;

        try {
            String query = sql.getSqlActivity(hasStartDate, hasEndDate);

            preparedStatement = createPreparedStatement(preparedStatement, query);
            preparedStatement.setString(1, "%" + criteria + "%");

            if (hasStartDate) {
                java.sql.Timestamp sqlStartTime = DateUtils.dateStringToSqlTimestamp(startDate);
                preparedStatement.setTimestamp(2, sqlStartTime);
            }

            if (hasEndDate) {
                java.sql.Timestamp sqlEndTime = DateUtils.dateStringToSqlTimestamp(endDate);

                if (hasStartDate) {
                    preparedStatement.setTimestamp(3, sqlEndTime);
                } else {
                    preparedStatement.setTimestamp(2, sqlEndTime);
                }
            }

            success = saveResultsToFile(preparedStatement, directory, Constants.CSV_FILE_ACTIVITY, isManualExtraction);

            if (success) {
                log.info("LAP-Extractor :: Activity extraction created successfully for criteria: " + criteria + ", startDate: " + startDate + ", endDate: " + endDate);
            } else {
                log.info("LAP-Extractor :: Activity extraction UNSUCCESSFUL for criteria: " + criteria + ", startDate: " + startDate + ", endDate: " + endDate);
            }
        } catch (Exception e) {
            log.error("Error preparing activity extraction: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return success;
    }

    /**
     * Convenience method for the grades method
     * @param directory the name of the date-specific directory to store the .csv file
     * @param isManualExtraction is this from a manual extraction?
     * @return true, if creation and storage successful
     */
    public boolean prepareGradesCsv(String directory, boolean isManualExtraction) {
        return prepareGradesCsv("", directory, isManualExtraction);
    }

    /**
     * Create the grades.csv file, store it on file system
     * 
     * @param criteria the site title search term to use
     * @param directory the name of the date-specific directory to store the .csv file
     * @param isManualExtraction is this from a manual extraction?
     * @return true, if creation and storage successful
     */
    public boolean prepareGradesCsv(String criteria, String directory, boolean isManualExtraction) {
        boolean success = false;
        PreparedStatement preparedStatement = null;

        try {
            String query = sql.getSqlGrades();

            preparedStatement = createPreparedStatement(preparedStatement, query);
            preparedStatement.setString(1, "%" + criteria + "%");

            success = saveResultsToFile(preparedStatement, directory, Constants.CSV_FILE_GRADES, isManualExtraction);

            if (success) {
                log.info("LAP-Extractor :: Grade extraction created successfully for criteria: " + criteria);
            } else {
                log.info("LAP-Extractor :: Grade extraction UNSUCCESSFUL for criteria: " + criteria);
            }
        } catch (Exception e) {
            log.error("Error preparing grades csv: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return success;
    }

    /**
     * Processes the results from a query
     * 1. Executes the prepared statement
     * 2. Creates CSV strings
     * 3. Saves CSV string to a file
     * 
     * @param preparedStatement the prepared statement
     * @param directory the directory to save the file in
     * @param fileName the name of the file
     * @param isManualExtraction is this from a manual extraction?
     * @return true, if successful operations
     * @throws Exception on errors
     */
    private boolean saveResultsToFile(PreparedStatement preparedStatement, String directory, String fileName, boolean isManualExtraction) throws Exception {
        ResultSet results = executePreparedStatement(preparedStatement);
        ResultSetMetaData metadata = results.getMetaData();
        int numberOfColumns = metadata.getColumnCount();

        // header row
        List<String> header = new ArrayList<String>();
        for (int i = 1;i <=numberOfColumns;i++) {
            header.add(metadata.getColumnLabel(i));
        }
        String csvData = CsvUtils.setAsCsvRow(header);

        // data rows
        while (results.next()) {
            List<String> row = new ArrayList<String>();
            for (int i = 1;i <=numberOfColumns;i++) {
                row.add(results.getString(i));
            }
            csvData += CsvUtils.setAsCsvRow(row);
        }

        boolean success = fileService.saveStringToFile(csvData, directory, fileName, isManualExtraction);

        return success;
    }

    /**
     * Retrieves a CSV file's contents as a string
     * 
     * @param directory the directory containing the file
     * @param fileName the name of the file
     * @return the CSV data as a string
     */
    public String getCsvDataString(String directory, String fileName) {
        String csvData = fileService.readFileIntoString(directory, fileName);

        return csvData;
    }

    /**
     * Retrieves the listing of sub-directories in the configured storage path
     * 
     * @return HashMap of the listings (e.g. "yyyyMMdd_HHmmss" => "MMM dd, yyyy HH:mm:ss ({EXTRACTION TYPE})")
     */
    public Map<String, Map<String, String>> getExtractionDates() {
        String directory = fileService.getStoragePath();

        return getExtractionDates(directory);
    }

    /**
     * Retrieves the listing of sub-directories in the given directory
     * 
     * @return HashMap of the listings (e.g. "yyyyMMdd_HHmmss" => "MMM dd, yyyy HH:mm:ss ({EXTRACTION TYPE})")
     */
    public Map<String, Map<String, String>> getExtractionDates(String directory) {
        if (StringUtils.isBlank(directory)) {
            directory = fileService.getStoragePath();
        }

        List<String> directoryNames = FileUtils.parseDirectory(directory, "");
        Map<String, Map<String, String>> directories = new LinkedHashMap<String, Map<String, String>>(directoryNames.size());

        for (String directoryName : directoryNames) {
            Map<String, String> dateMap = new HashMap<String, String>(2);
            try {
                Date date = dateService.parseDirectoryToDateTime(directoryName);

                String dateTime = DateUtils.SDF_DATE_TIME_MYSQL.format(date);
                dateMap.put(Constants.REST_MAP_KEY_DATE_TIME, dateTime);

                String displayDate = DateUtils.SDF_DISPLAY.format(date);
                String extractionType = ExtractorUtils.calculateExtractionType(directoryName);
                if (StringUtils.isNotBlank(extractionType)) {
                    displayDate += " (" + extractionType + ")";
                }

                dateMap.put(Constants.REST_MAP_KEY_DISPLAY_DATE, displayDate);
                directories.put(directoryName, dateMap);
            } catch (Exception e) {
                log.error("Error parsing directory name: " + e, e);

                dateMap.put(Constants.REST_MAP_KEY_DATE_TIME, directoryName);
                dateMap.put(Constants.REST_MAP_KEY_DISPLAY_DATE, directoryName);
                directories.put(directoryName, dateMap);
            }
        }

        return directories;
    }

    /**
     * Gets the last data extraction date from the listing of directories
     * 
     * @param directoryListing the directory listing map
     * @return the last extraction date
     */
    public Map<String, Map<String, String>> getLatestExtractionDate() {
        Map<String, Map<String, String>> extractionDates = getExtractionDates();

        return dateService.getLatestExtractionDate(extractionDates);
    }

    /**
     * Gets the next scheduled extraction date
     * 
     * @return the next extraction date
     */
    public String getNextScheduledExtractionDate() {
        return dateService.getNextScheduledExtractionDate();
    }

    /**
     * Gets a listing of all the extraction dates
     * 
     * @return the list of all extraction dates
     */
    public Map<String, String> getAllExtractionDates() {
        return dateService.getAllExtractionDates();
    }

    /**
     * Get the list of all available files for downloading
     * Note: this is to rid the constants map of the serialVersionUID
     * so the map can be serialized and sent through a REST call
     * 
     * @return a mapping of the files (e.g. "activity" => "activity.csv")
     */
    public Map<String, String> getAvailableFiles() {
        Map<String, String> availableFiles = new LinkedHashMap<String, String>();

        availableFiles.putAll(Constants.AVAILABLE_FILE_LISTING);

        return availableFiles;
    }

    private DateService dateService;
    public void setDateService(DateService dateService) {
        this.dateService = dateService;
    }

    private FileService fileService;
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    private Sql sql;
    public void setSql(Sql sql) {
        this.sql = sql;
    }

}
