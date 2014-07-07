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
package org.sakaiproject.lap.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.lap.Constants;
import org.sakaiproject.lap.util.DateUtils;
import org.sakaiproject.lap.util.ExtractorUtils;
import org.sakaiproject.lap.util.FileUtils;

/**
 * Handles all needed date functionality
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
public class DateService {

    private final Log log = LogFactory.getLog(getClass());

    private String currentDay;
    private Map<String, List<Date>> remainingTimes = new HashMap<String, List<Date>>();
    private ArrayList<String> scheduledRunTimes;
    private String lastScheduledExtractionDate = null;

    public void init() {
        processCurrentDay(new Date());
        processScheduledRunTimes();
        processLastScheduledExtractionDate();
    }

    /**
     * Sets the day string to the current one
     * format: yyyyMMdd
     * 
     * @param date the current date object
     */
    public void processCurrentDay(Date date) {
        if (date == null) {
            date = new Date();
        }

        if (!StringUtils.equalsIgnoreCase(currentDay, DateUtils.SDF_DATE_ONLY.format(date))) {
            currentDay = DateUtils.SDF_DATE_ONLY.format(date);

            processRemainingTimes(date);
        }
    }

    /**
     * Sets all the configured run times from sakai.properties
     * Defaults to coded times, if none specified in sakai.properties
     */
    private void processScheduledRunTimes() {
        // get run dates from config
        String[] runTimes = ServerConfigurationService.getStrings("lap.data.extraction.times");
        // get hard-coded auto-extraction times
        if (ArrayUtils.isEmpty(runTimes)) {
            runTimes = Constants.DEFAULT_DATA_EXTRACTION_TIMES;
        }

        if (runTimes != null) {
            scheduledRunTimes = new ArrayList<String>(runTimes.length);

            for (String runTime : runTimes) {
                scheduledRunTimes.add(runTime);
            }

            Collections.sort(scheduledRunTimes);
        } else {
            scheduledRunTimes = new ArrayList<String>(0);
        }
    }

    /**
     * Sets all the remaining future times for the given date
     * 
     * @param date the date to set times
     */
    private void processRemainingTimes(Date date) {
        if (scheduledRunTimes == null) {
            processScheduledRunTimes();
        }
        List<Date> dates = new ArrayList<Date>(scheduledRunTimes.size());
        String dateString = DateUtils.SDF_DATE_ONLY.format(date);

        try {
            for (String s : scheduledRunTimes) {
                Date scheduledDate = DateUtils.SDF_DATE_TIME.parse(dateString + " " + s);

                // add the date to the schedule if it's after the current date or is next day
                if (!StringUtils.equalsIgnoreCase(currentDay, dateString) || scheduledDate.after(date)) {
                    dates.add(scheduledDate);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing scheduled dates. Error: " + e, e);
        } finally {
            remainingTimes.put(dateString, dates);
        }
    }

    /**
     * Gets the last data extraction date from the listing of directories
     * 
     * @param directoryListing the directory listing map
     * @return the last extraction date
     */
    public String getLatestExtractionDate(Map<String, String> directoryListing) {
        if (directoryListing == null || directoryListing.isEmpty()) {
            return Constants.DEFAULT_NO_TIME;
        }

        String firstDirectory = directoryListing.keySet().iterator().next();
        String latestRunDate = directoryListing.get(firstDirectory);

        return latestRunDate;
    }

    /**
     * Gets the next scheduled extraction date
     * If none available for current date, calculates the next day's times
     * 
     * @return the next extraction date
     */
    public String getNextScheduledExtractionDate() {
        String nextScheduledExtractionDate = Constants.DEFAULT_NO_TIME;
        List<Date> times = remainingTimes.get(currentDay);

        if (times != null) {
            if (times.size() > 0) {
                nextScheduledExtractionDate = DateUtils.SDF_DISPLAY.format(times.get(0));
            } else {
                // we are after the last time scheduled for today, get the next day's times
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, 1);
                Date nextDay = calendar.getTime();
                String nextDayString = DateUtils.SDF_DATE_ONLY.format(nextDay);
                processRemainingTimes(nextDay);
                List<Date> nextDayTimes = remainingTimes.get(nextDayString);
                nextScheduledExtractionDate = DateUtils.SDF_DISPLAY.format(nextDayTimes.get(0));
            }
        }

        return nextScheduledExtractionDate;
    }

    /**
     * Gets a listing of all the extraction dates
     * 
     * @return the list of all extraction dates
     */
    public Map<String, String> getAllExtractionDates() {
        String directory = fileService.getStoragePath();
        List<String> directoryNames = FileUtils.parseDirectory(directory, "");
        Map<String, String> extractionDates = new HashMap<String, String>(directoryNames.size());

        for (String directoryName : directoryNames) {
            try {
                Date date = parseDirectoryToDateTime(directoryName);
                String dateTime = DateUtils.SDF_DISPLAY.format(date);
                String displayDateTime = dateTime;
                String extractionType = ExtractorUtils.calculateExtractionType(directoryName);
                if (StringUtils.isNotBlank(extractionType)) {
                    displayDateTime += " (" + extractionType + ")";
                }
                extractionDates.put(dateTime, displayDateTime);
            } catch (Exception e) {
                log.error("Error parsing directory name: " + e, e);
                extractionDates.put(directoryName, directoryName);
            }
        }

        return extractionDates;
    }

    /**
     * Finds the last directory created by a scheduled extraction
     */
    private void processLastScheduledExtractionDate() {
        String directory = fileService.getStoragePath();
        List<String> scheduledDirectories = FileUtils.parseDirectory(directory, Constants.EXTRACTION_TYPE_EXTENSION_SCHEDULED);

        if (scheduledDirectories.size() > 0) {
            lastScheduledExtractionDate = scheduledDirectories.get(0);
        }
    }

    /**
     * Parses a dated directory name to get the date object
     * e.g. yyyyMMdd_HHmmss_A => date object
     * 
     * @param directory the directory name
     * @return the date object
     */
    public Date parseDirectoryToDateTime(String directory) {
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException(directory);
        }

        if (directory.length() > 15) {
            directory = StringUtils.substring(directory, 0, 15);
        }

        Date date = null;
        try {
            date = DateUtils.SDF_FILE_NAME.parse(directory);
        } catch (Exception e) {
            log.error("Error parsing directory string to date. Error: " + e, e);
        }

        return date;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public Map<String, List<Date>> getRemainingTimes() {
        return remainingTimes;
    }

    public void setRemainingTimes(Map<String, List<Date>> remainingTimes) {
        this.remainingTimes = remainingTimes;
    }

    public ArrayList<String> getScheduledRunTimes() {
        return scheduledRunTimes;
    }

    public String getLastScheduledExtractionDate() {
        return lastScheduledExtractionDate;
    }

    public void setLastScheduledExtractionDate(String lastScheduledExtractionDate) {
        this.lastScheduledExtractionDate = lastScheduledExtractionDate;
    }

    private FileService fileService;
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

}
