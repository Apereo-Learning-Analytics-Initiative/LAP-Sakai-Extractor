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
import org.sakaiproject.lap.dao.Data;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class MainController extends AbstractController {
    final protected Log log = LogFactory.getLog(getClass());

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> model = new HashMap<String,Object>();

        // drop-down of existing directories
        Map<String, String> directoryListing = data.getDirectoryListing();
        model.put("directories", directoryListing);

        // last date the data generation was run
        String latestRunDate = data.getLatestRunDate(directoryListing);
        model.put("latestRunDate", latestRunDate);

        // next schedule date the data generation will run
        String nextRunDate = data.getNextScheduledRunDate();
        model.put("nextRunDate", nextRunDate);

        if (StringUtils.equalsIgnoreCase("POST", request.getMethod())) {
            if (request.getParameter("statusMessageType") != null && request.getParameter("statusMessage") != null) {
                String messageType = request.getParameter("statusMessageType");
                if (StringUtils.equalsIgnoreCase("success", messageType)) {
                    model.put("success", request.getParameter("statusMessage"));
                } else {
                    model.put("error", "There was an error processing the CSV files. Error: " + request.getParameter("statusMessage"));
                }
            }
        }

        return new ModelAndView("main", model);
    }

    private Data data;
    public void setData(Data data) {
        this.data = data;
    }

}
