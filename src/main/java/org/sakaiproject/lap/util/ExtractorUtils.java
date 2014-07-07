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

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.lap.Constants;

public class ExtractorUtils {

    /**
     * Get the appropriate extension
     * e.g "_M" or "_S"
     * 
     * @param isManualExtraction is this a manual extraction?
     * @return the type
     */
    public static String getExtractionTypeExtension(boolean isManualExtraction) {
        String extractionType = (isManualExtraction) ? Constants.EXTRACTION_TYPE_EXTENSION_MANUAL : Constants.EXTRACTION_TYPE_EXTENSION_SCHEDULED;

        return extractionType;
    }

    /**
     * Gets the extension type directory name extension
     * e.g "manual" or "scheduled"
     * 
     * @param extractionType the type extension (e.g. "_M" or "_S")
     * @return the abbreviated name
     */
    public static String getExtractionTypeName(String extractionExtension) {
        String extractionTypeExtension = Constants.EXTRACTION_TYPE_MAP.get(extractionExtension);
        if (extractionExtension == null) {
            extractionExtension = "";
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
    public static String calculateExtractionType(String directory) {
        // get the last 2 characters of directory name
        String fileNameExtractionType = StringUtils.substring(directory, directory.length() - 2, directory.length());

        String extractionType = Constants.EXTRACTION_TYPE_MAP.get(fileNameExtractionType);
        if (extractionType == null) {
            extractionType = "";
        }

        return extractionType;
    }
}
