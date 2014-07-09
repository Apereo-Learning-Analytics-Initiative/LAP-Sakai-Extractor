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

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.lap.Constants;
import org.sakaiproject.lap.dao.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to handle file download requests
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
public class FileDownloadController {

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping(method = RequestMethod.POST)
    public void doDownload(HttpServletRequest request, HttpServletResponse response) {
        String datedDirectory = request.getParameter("extraction-date");
        String action = request.getParameter("action");
        String fileName = action + ".csv";
        String csvData = data.getCsvDataString(datedDirectory, fileName);

        response.setContentType(Constants.MIME_TYPE_CSV);
        response.setHeader("Content-Disposition", "attachment; filename='"+ fileName + "'");

        try {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(csvData.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch(Exception e) {
            log.error("Error sending CSV file to browser: " + e, e);
        }
    }

    private Data data;
    public void setData(Data data) {
        this.data = data;
    }

}
