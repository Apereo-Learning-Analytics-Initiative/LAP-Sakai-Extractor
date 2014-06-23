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
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

/**
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 */
public class ExtractorService {

    final protected Log log = LogFactory.getLog(ExtractorService.class);

    public boolean isAdminSession() {
        String sessionId = sessionManager.getCurrentSession().getId();

        return isAdminSession(sessionId);
    }

    /**
     * Check to see if the session is for a super admin
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
     * Get a translated string from a code and replacement args
     * 
     * @param code
     * @param args
     * @return the translated string
     */
    public String getMessage(String code, Object[] args) {
        String msg;
        try {
            msg = getMessageSource().getMessage(code, args, null);
        } catch (NoSuchMessageException e) {
            msg = "Missing message for code: "+code;
        }
        return msg;
    }

    private SessionManager sessionManager;
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private SecurityService securityService;
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    UserDirectoryService userDirectoryService;
    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    private MessageSource messageSource;
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    public MessageSource getMessageSource() {
        return messageSource;
    }

}