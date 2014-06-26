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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.lap.Constants;
import org.sakaiproject.lap.dao.Data;

public class AutomaticGeneratorService implements Runnable {

    final protected Log log = LogFactory.getLog(getClass());

    private boolean threadStop;
    private Thread thread = null;
    private long interval;
    private ArrayList<String> scheduledRunTimes;
    private String currentDay;
    private Map<String, List<Date>> currentDayRemainingTimes = new HashMap<String, List<Date>>();

    public void init() {
        boolean automaticGenerationEnabled = ServerConfigurationService.getBoolean("lap.automatic.generation.enabled", true);

        if (automaticGenerationEnabled) {
            // get run dates from config
            String[] runTimes = ServerConfigurationService.getStrings("lap.data.generation.times");
            if (ArrayUtils.isEmpty(runTimes)) {
                runTimes = Constants.DEFAULT_DATA_GENERATION_TIMES;
            }

            scheduledRunTimes = new ArrayList<String>(runTimes.length);
            for (String runTime : runTimes) {
                scheduledRunTimes.add(runTime);
            }

            // only run if scheduled times exist
            if (!scheduledRunTimes.isEmpty()) {
                interval = ServerConfigurationService.getInt("lap.data.generation.check.interval", Constants.DEFAULT_DATA_GENERATION_CHECK_INTERVAL);
                setCurrentDayRemainingTimes();

                start();
            } else {
                log.error("Cannot setup automatic generation of data. No times are configured.");
            }
        }
    }

    public void destroy() {
        stop();
    }

    protected void start() {
        threadStop = false;

        thread = new Thread(this, getClass().getName());
        thread.setDaemon(true);
        thread.start();

        log.info("Automatic data extraction thread started.");
    }

    /**
     * Stop the clean and report thread.
     */
    protected void stop() {
        if (thread == null) return;

        // signal the thread to stop
        threadStop = true;

        // wake up the thread
        thread.interrupt();

        thread = null;

        log.info("Automatic data extraction thread started.");
    }

    @Override
    public void run() {
        while ((!threadStop) && (!Thread.currentThread().isInterrupted())) {
            Date currentDate = new Date();
            log.info("Thread running! " + currentDate);

            if (isTimeToRun()) {
                String directory = fileService.createDatedDirectoryName();

                boolean coursesSuccess = data.prepareCoursesCsv(directory);
                boolean gradesSuccess = data.prepareGradesCsv(directory);
                boolean studentsSuccess = data.prepareStudentsCsv(directory);
                boolean usageSuccess = data.prepareUsageCsv(directory);

                log.info("Data files created: courses: " + coursesSuccess + ", grades: " + gradesSuccess + ", students: " + studentsSuccess + ", usage: " + usageSuccess);
            }

            try{
                Thread.sleep(interval);
            } catch (Exception e) {
            }
        }
    }

    private void setCurrentDay() {
        currentDay = Constants.FORMAT_DATE_ONLY.format(new Date());
        if (!currentDayRemainingTimes.containsKey(currentDay)) {
            currentDayRemainingTimes.put(currentDay, new ArrayList<Date>());
        }
    }

    private void setCurrentDayRemainingTimes() {
        setCurrentDay();

        try {
            Date currentDate = new Date();
            List<Date> dates = new ArrayList<Date>();

            for (String s : scheduledRunTimes) {
                Date scheduledDate = Constants.FORMAT_DATE_TIME.parse(currentDay + " " + s);
                if (scheduledDate.after(currentDate)) {
                    dates.add(scheduledDate);
                }
            }
            currentDayRemainingTimes.put(currentDay, dates);
        } catch (Exception e) {
            log.error("Error parsing scheduled dates. Error: " + e, e);
        }
    }

    private boolean isTimeToRun() {
        boolean isTimeToRun = false;
        setCurrentDay();
        Date currentDate = new Date();
        List<Date> remainingTimes = currentDayRemainingTimes.get(currentDay);
        if (remainingTimes != null) {
            List<Date> toRemove = new ArrayList<Date>();
            for (Date remainingTime : remainingTimes) {
                if (currentDate.after(remainingTime)) {
                    isTimeToRun = true;
                    toRemove.add(remainingTime);
                }
            }
            remainingTimes.removeAll(toRemove);
        }

        return isTimeToRun;
    }

    private Data data;
    public void setData(Data data) {
        this.data = data;
    }

    private FileService fileService;
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }
}
