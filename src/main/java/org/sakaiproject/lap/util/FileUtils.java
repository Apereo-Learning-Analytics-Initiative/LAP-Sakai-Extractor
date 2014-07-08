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
package org.sakaiproject.lap.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.lap.Constants;
import org.sakaiproject.lap.util.DateUtils.DateComparatorLatestToEarliest;

public class FileUtils {

    /**
     * Creates a string representing the path to the storage directory
     * If none is specified, use the ContentHostingService path
     * 
     * @return the path string
     */
    public static String createStoragePath() {
        String storagePath = ServerConfigurationService.getString("lap.data.storage.path", "");
        if (StringUtils.isBlank(storagePath)) {
            String rootDirectory = ServerConfigurationService.getString("bodyPath@org.sakaiproject.content.api.ContentHostingService", "");
            rootDirectory = addTrailingSlash(rootDirectory);

            storagePath = addTrailingSlash(rootDirectory + Constants.DEFAULT_CSV_STORAGE_DIRECTORY);
        }

        return storagePath;
    }

    /**
     * Create a new directory name for storing files
     * Format: yyyyMMdd_HHmmss
     * 
     * @param isManualExtraction is this directory for a manual extraction?
     * @return the directory name
     */
    public static String createDatedDirectoryName(boolean isManualExtraction) {
        String extractionExtension = ExtractorUtils.getExtractionTypeExtension(isManualExtraction);

        Date date = new Date();
        String directoryName = DateUtils.SDF_FILE_NAME.format(date) + extractionExtension;

        return directoryName;
    }

    /**
     * Method to parse a directory for sub-directories
     * 
     * @param directory the directory to parse
     * @param type the type of extraction (manual, scheduled, "" = get all)
     * @return a listing of the sub-directory names
     */
    public static List<String> parseDirectory(String directory, String type) {
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException("Directory");
        }
        // if type extension passed in does not match a configured type, get all sub-directories
        if (!Constants.EXTRACTION_TYPE_MAP.containsKey(type)) {
            type = "";
        }

        boolean getAll = StringUtils.isBlank(type);

        List<String> directories = new ArrayList<String>();
        File fileDirectory = new File(directory);

        for (File subDirectory : fileDirectory.listFiles()) {
            // only store sub-directory names
            if (subDirectory.isDirectory()) {
                String directoryExtractionType = StringUtils.substring(subDirectory.getName(), subDirectory.getName().length() - 2);
                if (getAll || StringUtils.equalsIgnoreCase(directoryExtractionType, type)) {
                    directories.add(subDirectory.getName());
                }
            }
        }

        // sort the list, newest directories first
        Collections.sort(directories, new DateComparatorLatestToEarliest());

        return directories;
    }

    /**
     * Add a trailing slash to the end of the path, if none exists
     * 
     * @param path the path string
     * @return the path with a trailing slash
     */
    public static String addTrailingSlash(String path) {
        if (!StringUtils.endsWith(path, System.getProperty("file.separator"))) {
            path += System.getProperty("file.separator");
        }

        return path;
    }

}
