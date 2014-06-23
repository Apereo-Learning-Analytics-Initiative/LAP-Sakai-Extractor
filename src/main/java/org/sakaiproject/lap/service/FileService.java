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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.lap.Constants;

public class FileService {

    private final Log log = LogFactory.getLog(FileService.class);

    
    private String storagePath = "";

    public void init() {
        storagePath = createStoragePath();
        // create the root directory
        createNewDirectory("");
    }

    /**
     * Creates a string representing the path to the storage directory
     * @return the path string
     */
    private String createStoragePath() {
        String storagePath = ServerConfigurationService.getString("lap.data.storage.path", "");
        if (StringUtils.isBlank(storagePath)) {
            String rootDirectory = ServerConfigurationService.getString("bodyPath@org.sakaiproject.content.api.ContentHostingService", "");
            rootDirectory = addTrailingSlash(rootDirectory);

            storagePath = addTrailingSlash(rootDirectory + Constants.DEFAULT_CSV_STORAGE_DIRECTORY);
        }

        return storagePath;
    }

    private String createNewDirectory(String directoryName) {
        File newDirectory = new File(storagePath + directoryName);

        // if the directory does not exist, create it
        if (!newDirectory.exists()) {
            try{
                newDirectory.mkdir();
            } catch(Exception e){
                log.error("Cannot create new directory: " + e, e);
            }
        }

        String path = newDirectory.getPath();

        return path;
    }

    public String createDatedDirectoryName() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_FILE_NAME, Locale.ENGLISH);
        String directoryName = sdf.format(date);

        return directoryName;
    }

    public File createNewFile(String datedDirectory, String fileName) {
        if (StringUtils.isBlank(datedDirectory)) {
            throw new NullArgumentException("File directory cannot be null or blank");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("File name cannot be null or blank");
        }

        File newFile = null;

        String directory = createNewDirectory(datedDirectory);
        directory = addTrailingSlash(directory);

        try {
            newFile = new File(directory + fileName);
            newFile.createNewFile();
        } catch (Exception e) {
            log.error("Error creating new file: " + e, e);
        }

        return newFile;
    }

    public boolean writeStringToFile(File file, String dataString) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            bufferedWriter.write(dataString);
            bufferedWriter.close();

            return true;
        } catch (Exception e) {
            log.error("Error writing string to file: " + e, e);

            return false;
        }
    }

    public List<String> parseDirectory() {
        List<String> folders = new ArrayList<String>();
        File directory = new File(storagePath);

        for (File subDirectory : directory.listFiles()) {
            if (subDirectory.isDirectory()) {
                folders.add(subDirectory.getName());
            }
        }

        Collections.sort(folders, new FilenameComparatorByDate());

        return folders;
    }

    public File getFile(String datedDirectory, String fileName) {
        if (StringUtils.isBlank(datedDirectory)) {
            throw new NullArgumentException("File directory cannot be null or blank");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("File name cannot be null or blank");
        }

        datedDirectory = addTrailingSlash(datedDirectory);

        File file = new File(storagePath + datedDirectory + fileName);

        return file;
    }

    public String readFileIntoString(String datedDirectory, String fileName) {
        String fileString = "";

        try {
            File file = getFile(datedDirectory, fileName);

            InputStream inputStream = new FileInputStream(file);
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer);
            fileString = writer.toString();
        } catch (Exception e) {
            log.error("Error reading file into string: " + e, e);
        }

        return fileString;
    }

    public boolean saveStringToFile(String dataString, String directory, String name) {
        File file = createNewFile(directory, name);
        boolean success = writeStringToFile(file, dataString);

        return success;
    }

    private String addTrailingSlash(String path) {
        if (!StringUtils.endsWith(path, "/")) {
            path += "/";
        }

        return path;
    }

    /**
     * Compare directory names by converting name to date
     */
    public class FilenameComparatorByDate implements Comparator<String> {
        @Override
        public int compare(String arg0, String arg1) {
            SimpleDateFormat fileNameDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_FILE_NAME, Locale.ENGLISH);

            try {
                Date date1 = fileNameDateFormat.parse(arg0);
                Date date2 = fileNameDateFormat.parse(arg1);
                return date2.compareTo(date1);
            } catch (ParseException e) {
                log.error("Error comparing dates of files: " + e, e);
                return 0;
            }
        }
    }

}
