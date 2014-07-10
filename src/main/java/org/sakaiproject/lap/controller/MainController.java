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

package org.sakaiproject.lap.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.lap.service.ExtractorService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller to handle to main.jsp view
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
public class MainController extends AbstractController {

    final protected Log log = LogFactory.getLog(getClass());

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> model = new HashMap<String,Object>();

        if (StringUtils.equalsIgnoreCase("POST", request.getMethod())) {
            if (request.getParameter("status-type") != null) {
                String statusType = request.getParameter("status-type");
                if (StringUtils.equalsIgnoreCase("success", statusType)) {
                    model.put("success", extractorService.getMessage("message.success.extraction.files.creation", new String[]{}));
                } else {
                    model.put("error", extractorService.getMessage("message.error.extraction.files.creation", new String[]{request.getParameter("status-error-thrown")}));
                }
            }
        }

        return new ModelAndView("main", model);
    }

    private ExtractorService extractorService;
    public void setExtractorService(ExtractorService extractorService) {
        this.extractorService = extractorService;
    }

}
