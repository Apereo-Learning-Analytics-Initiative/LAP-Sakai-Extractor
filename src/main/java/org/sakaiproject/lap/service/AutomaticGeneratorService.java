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

public class AutomaticGeneratorService implements Runnable {

    final protected Log log = LogFactory.getLog(getClass());

    private boolean threadStop;
    private Thread thread = null;
    private long interval;

    public void init() {
        boolean automaticGenerationEnabled = ServerConfigurationService.getBoolean("lap.automatic.generation.enabled", true);

        if (automaticGenerationEnabled) {
            List<String> scheduledRunTimes = dateService.getScheduledRunTimes();

            // only run if scheduled times exist
            if (!scheduledRunTimes.isEmpty()) {
                interval = ServerConfigurationService.getInt("lap.data.generation.check.interval", Constants.DEFAULT_DATA_GENERATION_CHECK_INTERVAL) * 1000;

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
            log.info("Thread running! " + new Date());

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

    
    private boolean isTimeToRun() {
        boolean isTimeToRun = false;
        Date currentDate = new Date();
        dateService.processCurrentDay(currentDate);

        Map<String, List<Date>> currentDayRemainingTimes = dateService.getRemainingTimes();
        List<Date> remainingTimes = currentDayRemainingTimes.get(dateService.getCurrentDay());
        if (remainingTimes != null) {
            List<Date> datesToRemove = new ArrayList<Date>();
            for (Date remainingTime : remainingTimes) {
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

    private FileService fileService;
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

}
