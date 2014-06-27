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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.lap.Constants;

public class DateService {

    private final Log log = LogFactory.getLog(getClass());

    private String currentDay;
    private Map<String, List<Date>> remainingTimes = new HashMap<String, List<Date>>();
    private ArrayList<String> scheduledRunTimes;

    public void init() {
        processCurrentDay(new Date());
        processScheduledRunTimes();
    }

    public void processCurrentDay(Date date) {
        if (!StringUtils.equalsIgnoreCase(currentDay, SDF_DATE_ONLY.format(date))) {
            currentDay = SDF_DATE_ONLY.format(date);

            processRemainingTimes(date);
        }
    }

    private void processScheduledRunTimes() {
        // get run dates from config
        String[] runTimes = ServerConfigurationService.getStrings("lap.data.generation.times");
        if (ArrayUtils.isEmpty(runTimes)) {
            runTimes = Constants.DEFAULT_DATA_GENERATION_TIMES;
        }

        scheduledRunTimes = new ArrayList<String>(runTimes.length);
        for (String runTime : runTimes) {
            scheduledRunTimes.add(runTime);
        }
        Collections.sort(scheduledRunTimes);
    }

    private void processRemainingTimes(Date date) {
        try {
            List<Date> dates = new ArrayList<Date>();
            String dateString = SDF_DATE_ONLY.format(date);

            if (!remainingTimes.containsKey(dateString)) {
                remainingTimes.put(dateString, new ArrayList<Date>());
            }

            if (scheduledRunTimes == null) {
                processScheduledRunTimes();
            }

            for (String s : scheduledRunTimes) {
                Date scheduledDate = SDF_DATE_TIME.parse(dateString + " " + s);
                // add the date to the schedule if it's after the current date or is next day
                if (!StringUtils.equalsIgnoreCase(currentDay, dateString) || scheduledDate.after(date)) {
                    dates.add(scheduledDate);
                }
            }
            remainingTimes.put(dateString, dates);
        } catch (Exception e) {
            log.error("Error parsing scheduled dates. Error: " + e, e);
        }
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
        String nextScheduledRunDate = "Never";
        List<Date> times = remainingTimes.get(currentDay);

        if (times != null) {
            if (times.size() > 0) {
                nextScheduledRunDate = SDF_DISPLAY.format(times.get(0));
            } else {
                // we are after the last time scheduled for today, get the next day's times
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, 1);
                Date nextDay = calendar.getTime();
                String nextDayString = DateService.SDF_DATE_ONLY.format(nextDay);
                processRemainingTimes(nextDay);
                List<Date> nextDayTimes = remainingTimes.get(nextDayString);
                nextScheduledRunDate = DateService.SDF_DISPLAY.format(nextDayTimes.get(0));
            }
        }

        return nextScheduledRunDate;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(String currentDay) {
        this.currentDay = currentDay;
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

    public void setScheduledRunTimes(ArrayList<String> scheduledRunTimes) {
        this.scheduledRunTimes = scheduledRunTimes;
    }

    /*
     * Static items
     */
    public final static String DATE_FORMAT_FILE_NAME = "yyyyMMdd_HHmmss";
    public final static String DATE_FORMAT_FILE_NAME_DATE_ONLY = "yyyyMMdd";
    public final static String DATE_FORMAT_DROPDOWN = "MMMM dd, yyyy HH:mm:ss";
    public final static String DATE_FORMAT_TIME_ONLY = "HH:mm:ss";
    public final static String DATE_FORMAT_DATE_TIME = "yyyyMMdd HH:mm:ss";
    public final static String DATE_START_TIME = " 00:00:00";
    public final static String DATE_END_TIME = " 23:59:59";
    
    public final static SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat(DATE_FORMAT_DATE_TIME, Locale.ENGLISH);
    public final static SimpleDateFormat SDF_DATE_ONLY = new SimpleDateFormat(DATE_FORMAT_FILE_NAME_DATE_ONLY, Locale.ENGLISH);
    public final static SimpleDateFormat SDF_TIME_ONLY = new SimpleDateFormat(DATE_FORMAT_TIME_ONLY, Locale.ENGLISH);
    public final static SimpleDateFormat SDF_FILE_NAME = new SimpleDateFormat(DATE_FORMAT_FILE_NAME, Locale.ENGLISH);
    public final static SimpleDateFormat SDF_DISPLAY = new SimpleDateFormat(DATE_FORMAT_DROPDOWN, Locale.ENGLISH);

}
