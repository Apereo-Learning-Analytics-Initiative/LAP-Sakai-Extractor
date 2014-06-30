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

package org.sakaiproject.lap.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Describeable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.sakaiproject.lap.Constants;
import org.sakaiproject.lap.dao.Data;
import org.sakaiproject.lap.service.ExtractorService;
import org.sakaiproject.lap.service.FileService;

import com.google.gson.Gson;

/**
 * Entity provider for REST endpoints
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class ExtractorProvider extends AbstractEntityProvider implements EntityProvider, Outputable, Describeable, ActionsExecutable {

    private final Log log = LogFactory.getLog(getClass());

    public static String PREFIX = "lap-sakai-extractor";
    public String getEntityPrefix() {
        return PREFIX;
    }

    public void init() {
        log.info("INIT LAP Sakai Extractor");
    }

    /**
     * GET /direct/lap-sakai-extractor/activity
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="activity", viewKey=EntityView.VIEW_LIST)
    public ActionReturn activityCsv(EntityView view) {
        if (!extractorService.isAdminSession()){
            throw new SecurityException("User not allowed to access activity data", null);
        }

        return new ActionReturn(Constants.ENCODING_UTF8, Formats.JSON, "{\"activity\":\"activity1\"}");
    }

    /**
     * POST /direct/lap-sakai-extractor/extraction
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="extraction", viewKey=EntityView.VIEW_NEW)
    public ActionReturn extractData(EntityView view, Map<String, Object> params) {
        if (!extractorService.isAdminSession()){
            throw new SecurityException("User not allowed to extract data.", null);
        }

        String criteria = "";
        if (params.get("criteria") != null) {
            criteria = (String) params.get("criteria");
        }

        String startDate = "";
        if (params.get("startDate") != null) {
            startDate = (String) params.get("startDate");
        }

        String endDate = "";
        if (params.get("endDate") != null) {
            endDate = (String) params.get("endDate");
        }

        String directory = fileService.createDatedDirectoryName();

        boolean activityCsvCreated = data.prepareActivityCsv(criteria, startDate, endDate, directory);
        boolean gradesCsvCreated = data.prepareGradesCsv(criteria, directory);
        String success = Boolean.toString((activityCsvCreated && gradesCsvCreated));

        return new ActionReturn(Constants.ENCODING_UTF8, Formats.TXT, success);
    }

    /**
     * GET /direct/lap-sakai-extractor/grades
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="grades", viewKey=EntityView.VIEW_LIST)
    public ActionReturn gradesCsv(EntityView view) {
        if (!extractorService.isAdminSession()){
            throw new SecurityException("User not allowed to access grades data", null);
        }

        return new ActionReturn(Constants.ENCODING_UTF8, Formats.JSON, "{\"grades\": \"grade1\"}");
    }

    /**
     * GET /direct/lap-sakai-extractor/statistics
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="statistics", viewKey=EntityView.VIEW_LIST)
    public ActionReturn statistics(EntityView view) {
        if (!extractorService.isAdminSession()){
            throw new SecurityException("User not allowed to access statistics data.", null);
        }

        String lastRunDate = data.getLatestRunDate();
        String nextRunDate = data.getNextScheduledRunDate();
        List<String> allRunDates = data.getAllRunDates();
        
        Map<String, Object> data = new HashMap<String, Object>(3);
        data.put("lastRunDate", lastRunDate);
        data.put("nextRunDate", nextRunDate);
        data.put("allRunDates", allRunDates);

        Gson gson = new Gson();
        String json = gson.toJson(data, HashMap.class);
        
        return new ActionReturn(Constants.ENCODING_UTF8, Formats.JSON, json);
    }

    public String[] getHandledOutputFormats() {
        return new String[] {Formats.JSON};
    }

    public String[] getHandledInputFormats() {
        return new String[] {Formats.JSON, "form"};
    }

    private ExtractorService extractorService;
    public void setExtractorService(ExtractorService extractorService) {
        this.extractorService = extractorService;
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
