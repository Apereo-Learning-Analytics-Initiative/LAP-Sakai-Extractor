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

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class LapExtractorProvider extends AbstractEntityProvider implements EntityProvider, Outputable, Describeable, ActionsExecutable {

    private final Log log = LogFactory.getLog(LapExtractorProvider.class);

    public static String PREFIX = "lap-sakai-extractor";
    public String getEntityPrefix() {
        return PREFIX;
    }

    public void init() {
        log.info("INIT LAP Sakai Extractor");
    }

    /**
     * GET /direct/lap-sakai-extractor/courses
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="courses", viewKey=EntityView.VIEW_LIST)
    public ActionReturn coursesCsv(EntityView view) {
        if (!extractorService.isAdminSession()){
            throw new SecurityException("User not allowed to access courses .csv.", null);
        }

        return new ActionReturn(Constants.ENCODING_UTF8, Formats.JSON, "{\"courses\": \"course1\"}");
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
            throw new SecurityException("User not allowed to access grades .csv.", null);
        }

        return new ActionReturn(Constants.ENCODING_UTF8, Formats.JSON, "{\"grades\": \"grade1\"}");
    }

    /**
     * GET /direct/lap-sakai-extractor/students
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="students", viewKey=EntityView.VIEW_LIST)
    public ActionReturn studentsCsv(EntityView view) {
        if (!extractorService.isAdminSession()){
            throw new SecurityException("User not allowed to access students .csv.", null);
        }

        return new ActionReturn(Constants.ENCODING_UTF8, Formats.JSON, "{\"students\": \"students1\"}");
    }

    /**
     * GET /direct/lap-sakai-extractor/usage
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="usage", viewKey=EntityView.VIEW_LIST)
    public ActionReturn usageCsv(EntityView view) {
        if (!extractorService.isAdminSession()){
            throw new SecurityException("User not allowed to access usage .csv.", null);
        }

        return new ActionReturn(Constants.ENCODING_UTF8, Formats.JSON, "{\"usage\":\"usage1\"}");
    }

    /**
     * POST /direct/lap-sakai-extractor/generate
     * 
     * @param view
     * @return
     */
    @EntityCustomAction(action="generate", viewKey=EntityView.VIEW_NEW)
    public ActionReturn generateNewData(EntityView view, Map<String, Object> params) {
        if (!extractorService.isAdminSession()){
            throw new SecurityException("User not allowed to generate new CSVs.", null);
        }

        String criteria = "";
        if (params.get("criteria") != null) {
            criteria = (String) params.get("criteria");
        }

        String directory = fileService.createDatedDirectoryName();

        boolean usageCsvCreated = data.prepareUsageCsv(criteria, directory);

        return new ActionReturn(Constants.ENCODING_UTF8, Formats.TXT, "CSV created successfully: " + Boolean.toString(usageCsvCreated));
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
