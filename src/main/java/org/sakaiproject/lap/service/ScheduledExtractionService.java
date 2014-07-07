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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.lap.Constants;
import org.sakaiproject.lap.dao.Data;
import org.sakaiproject.lap.util.DateUtils;
import org.sakaiproject.lap.util.FileUtils;

/**
 * Handles the scheduled data extraction via thread
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
public class ScheduledExtractionService implements Runnable {

    final protected Log log = LogFactory.getLog(getClass());

    private boolean threadStop;
    private Thread thread = null;
    private boolean isManualExtraction = false;

    public void init() {
        boolean scheduledExtractionEnabled = ServerConfigurationService.getBoolean("lap.scheduled.extraction.enabled", true);

        if (scheduledExtractionEnabled) {
            List<String> scheduledRunTimes = dateService.getScheduledRunTimes();

            // only run if scheduled times exist
            if (!scheduledRunTimes.isEmpty()) {
                start();
            } else {
                log.error("Cannot setup scheduled extraction of data. No times are configured.");
            }
        }
    }

    public void destroy() {
        stop();
    }

    /**
     * Start the thread
     */
    protected void start() {
        threadStop = false;

        thread = new Thread(this, getClass().getName());
        thread.setDaemon(true);
        thread.start();

        log.info("Scheduled data extraction thread started.");
    }

    /**
     * Stop the thread.
     */
    protected void stop() {
        if (thread == null) return;

        // signal the thread to stop
        threadStop = true;

        // wake up the thread
        thread.interrupt();

        thread = null;

        log.info("Scheduled data extraction thread stopped.");
    }

    @Override
    public void run() {
        while ((!threadStop) && (!Thread.currentThread().isInterrupted())) {
            log.info("Thread running! " + new Date());

            if (isTimeToRun()) {
                // create the file storage directory
                String directory = FileUtils.createDatedDirectoryName(isManualExtraction);

                // get the last scheduled extraction date for activity processing
                String lastScheduledExtractionDate = dateService.getLastScheduledExtractionDate();
                boolean activitySuccess = false;
                if (lastScheduledExtractionDate != null) {
                    // only extract activity from last scheduled extraction date
                    String startDate = "";

                    try {
                        Date lastDate = dateService.parseDirectoryToDateTime(lastScheduledExtractionDate);
                        startDate = DateUtils.SDF_DATE_TIME_MYSQL.format(lastDate);
                    } catch (Exception e) {
                        log.error("Error parsing last scheduled extraction date. Error: " + e, e);
                    }

                    activitySuccess = data.prepareActivityCsv("", startDate, "", directory, isManualExtraction);
                } else {
                    // extract all activity data
                    activitySuccess = data.prepareActivityCsv(directory, isManualExtraction);
                }

                boolean gradesSuccess = data.prepareGradesCsv(directory, isManualExtraction);

                if (activitySuccess) {
                    dateService.setLastScheduledExtractionDate(directory);
                }

                log.info("Data files created: activity: " + activitySuccess + ", grades: " + gradesSuccess);
            }

            try{
                // wait the configured period of time
                Thread.sleep(Constants.DEFAULT_DATA_EXTRACTION_CHECK_INTERVAL);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Calculates if the current time is past the scheduled time
     * 
     * @return true, if current time is past the scheduled time
     */
    private boolean isTimeToRun() {
        boolean isTimeToRun = false;
        Date currentDate = new Date();
        dateService.processCurrentDay(currentDate);

        // get the remaining day's times
        Map<String, List<Date>> currentDayRemainingTimes = dateService.getRemainingTimes();
        List<Date> remainingTimes = currentDayRemainingTimes.get(dateService.getCurrentDay());

        if (remainingTimes != null) {
            List<Date> datesToRemove = new ArrayList<Date>();
            for (Date remainingTime : remainingTimes) {
                // if this time is after the scheduled time, allow process to run and remove for remaining day's time listing
                if (currentDate.after(remainingTime)) {
                    isTimeToRun = true;
                    datesToRemove.add(remainingTime);
                }
            }

            remainingTimes.removeAll(datesToRemove);
            currentDayRemainingTimes.put(dateService.getCurrentDay(), remainingTimes);
            dateService.setRemainingTimes(currentDayRemainingTimes);
        }

        return isTimeToRun;
    }

    private Data data;
    public void setData(Data data) {
        this.data = data;
    }

    private DateService dateService;
    public void setDateService(DateService dateService) {
        this.dateService = dateService;
    }

}
