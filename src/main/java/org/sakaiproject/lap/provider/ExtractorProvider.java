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
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
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
import org.sakaiproject.lap.util.DateUtils;
import org.sakaiproject.lap.util.FileUtils;

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
     * POST /direct/lap-sakai-extractor/download
     * 
     * @param view
     * @param params
     * @return
     */
    @EntityCustomAction(action="download", viewKey=EntityView.VIEW_NEW)
    public ActionReturn download(EntityView view, Map<String, Object> params) {
        if (!extractorService.isAdminSession()){
            throw new SecurityException("User not allowed to access data extractions", null);
        }

        String action = "";
        if (StringUtils.isNotBlank((String) params.get("action"))) {
            action = (String) params.get("action");
        }

        if (!Constants.AVAILABLE_FILE_LISTING.containsKey(action)) {
            throw new NullArgumentException("action");
        } else {
            action += ".csv";
        }

        Map<String, Map<String, String>> extractionDates = data.getExtractionDates();

        String extractionDate = "";
        if (StringUtils.isNotBlank((String) params.get("extraction-date"))) {
            extractionDate = (String) params.get("extraction-date");
        }

        if (!extractionDates.containsKey(extractionDate)) {
            throw new NullArgumentException("extraction-date");
        }

        String csvData = data.getCsvDataString(extractionDate, action);

        return new ActionReturn(Constants.ENCODING_UTF8, Formats.TXT, csvData);
    }

    /**
     * POST /direct/lap-sakai-extractor/extraction
     * 
     * @param view
     * @param params
     * @return
     */
    @EntityCustomAction(action="extraction", viewKey=EntityView.VIEW_NEW)
    public ActionReturn extractData(EntityView view, Map<String, Object> params) {
        if (!extractorService.isAdminSession()){
            throw new SecurityException("User not allowed to extract data.", null);
        }

        boolean isManualExtraction = true;

        String criteria = "";
        if (StringUtils.isNotBlank((String) params.get("criteria"))) {
            criteria = (String) params.get("criteria");
        }

        String startDate = "";
        if (StringUtils.isNotBlank((String) params.get("startDate"))) {
            startDate = (String) params.get("startDate") + DateUtils.DATE_START_TIME;
        }

        String endDate = "";
        if (StringUtils.isNotBlank((String) params.get("endDate"))) {
            endDate = (String) params.get("endDate") + DateUtils.DATE_END_TIME;
        }

        String directory = FileUtils.createDatedDirectoryName(isManualExtraction);

        boolean activityCsvCreated = data.prepareActivityCsv(criteria, startDate, endDate, directory, isManualExtraction);
        boolean gradesCsvCreated = data.prepareGradesCsv(criteria, directory, isManualExtraction);
        String success = Boolean.toString((activityCsvCreated && gradesCsvCreated));

        return new ActionReturn(Constants.ENCODING_UTF8, Formats.TXT, success);
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

        Map<String, Map<String, String>> lastExtractionDate = data.getLatestExtractionDate();
        String nextExtractionDate = data.getNextScheduledExtractionDate();
        Map<String, Map<String, String>> allExtractionDates = data.getExtractionDates();
        Map<String, String> availableFiles = data.getAvailableFiles();

        Map<String, Object> responseData = new HashMap<String, Object>(4);
        responseData.put(Constants.REST_MAP_KEY_LATEST_EXTRACTION_DATE, lastExtractionDate);
        responseData.put(Constants.REST_MAP_KEY_NEXT_EXTRACTION_DATE, nextExtractionDate);
        responseData.put(Constants.REST_MAP_KEY_ALL_EXTRACTION_DATES, allExtractionDates);
        responseData.put(Constants.REST_MAP_KEY_AVAILABLE_FILES, availableFiles);

        Gson gson = new Gson();
        String json = gson.toJson(responseData, HashMap.class);

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

}
