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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.lap.Constants;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

/**
 * General methods for the application
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 */
public class ExtractorService {

    final protected Log log = LogFactory.getLog(ExtractorService.class);

    /**
     * Checks if the current session is for a super admin user
     * 
     * @return true, if the current user session is for an administrator
     */
    public boolean isAdminSession() {
        String sessionId = sessionManager.getCurrentSession().getId();

        return isAdminSession(sessionId);
    }

    /**
     * Check to see if the session is for a super admin
     * 
     * @param sessionId the id of the session
     * @return true, if the session is owned by a super admin, false otherwise
     */
    public boolean isAdminSession(String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            return false;
        }

        Session session = sessionManager.getSession(sessionId);
        String userId = session.getUserId();
        if (StringUtils.isNotBlank(userId)) {
            return securityService.isSuperUser(userId);
        } else {
            return false;
        }
    }

    /**
     * Get the appropriate extension
     * e.g "_M" or "_S"
     * 
     * @param isManualExtraction is this a manual extraction?
     * @return the type
     */
    public String getExtractionTypeExtension(boolean isManualExtraction) {
        String extractionType = (isManualExtraction) ? Constants.EXTRACTION_TYPE_EXTENSION_MANUAL : Constants.EXTRACTION_TYPE_EXTENSION_SCHEDULED;

        return extractionType;
    }

    /**
     * Gets the extension type directory name extention
     * e.g "_M" or "_S"
     * 
     * @param extractionType the full type name
     * @return the abbreviated name
     */
    public String getExtractionTypeExtension(String extractionType) {
        String extractionTypeExtension = "";

        if (StringUtils.equalsIgnoreCase(extractionType, Constants.EXTRACTION_TYPE_EXTENSION_SCHEDULED)) {
            extractionType = Constants.EXTRACTION_TYPE_EXTENSION_SCHEDULED;
        } else if (StringUtils.equalsIgnoreCase(extractionType, Constants.EXTRACTION_TYPE_NAME_MANUAL)) {
            extractionType = Constants.EXTRACTION_TYPE_EXTENSION_MANUAL;
        }

        return extractionTypeExtension;
    }

    /**
     * Gets the full name of the extraction type for a given directory 
     * e.g. "manual" or "scheduled"
     * 
     * @param directory the directory
     * @return the full name of the extraction type
     */
    public String calculateExtractionType(String directory) {
        String extractionType = "";
        // get the last 2 characters of 
        String fileNameExtractionType = StringUtils.substring(directory, directory.length() - 2, directory.length());

        if (StringUtils.endsWithIgnoreCase(fileNameExtractionType, Constants.EXTRACTION_TYPE_EXTENSION_SCHEDULED)) {
            extractionType = Constants.EXTRACTION_TYPE_NAME_SCHEDULED;
        } else if (StringUtils.endsWithIgnoreCase(fileNameExtractionType, Constants.EXTRACTION_TYPE_EXTENSION_MANUAL)) {
            extractionType = Constants.EXTRACTION_TYPE_NAME_MANUAL;
        }

        return extractionType;
    }

    private SessionManager sessionManager;
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private SecurityService securityService;
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

}
