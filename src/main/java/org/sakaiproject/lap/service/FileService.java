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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.lap.Constants;

/**
 * Handles all the needed file operations
 * 
 * @author Robert E. Long (rlong @ unicon.net)
 *
 */
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
     * If none is specified, use the ContentHostingService path
     * 
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

    /**
     * Creates a new directory for storing files
     * 
     * @param directoryName the name of the directory
     * @return the path to the directory
     */
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

    /**
     * Create a new directory name for storing files
     * Format: yyyyMMdd_HHmmss
     * 
     * @return 
     */
    public String createDatedDirectoryName() {
        Date date = new Date();
        String directoryName = DateService.SDF_FILE_NAME.format(date);

        return directoryName;
    }

    /**
     * Creates a new file with the given name in a given directory
     * If file exists, get the file instead
     * 
     * @param directory the directory to store the file
     * @param fileName the name of the new file
     * @return the new file
     */
    public File createNewFile(String directory, String fileName) {
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException("File directory cannot be null or blank");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("File name cannot be null or blank");
        }

        File newFile = null;

        String newDirectory = createNewDirectory(directory);
        newDirectory = addTrailingSlash(directory);

        try {
            newFile = new File(storagePath + newDirectory + fileName);
            boolean exists = newFile.exists();

            if (!exists) {
                newFile.createNewFile();
            }
        } catch (Exception e) {
            log.error("Error creating new file: " + e, e);
        }

        return newFile;
    }

    /**
     * Method to parse a directory for subdirectories
     * 
     * @param directory the directory to parse
     * @return a listing of the subdirectory names
     */
    public List<String> parseDirectory(String directory) {
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException("Directory cannot be null or blank");
        }

        List<String> directories = new ArrayList<String>();
        File fileDirectory = new File(directory);

        for (File subDirectory : fileDirectory.listFiles()) {
            // only store subdirectory names
            if (subDirectory.isDirectory()) {
                directories.add(subDirectory.getName());
            }
        }

        // sort the list, newest directories first
        Collections.sort(directories, comparatorService.new DateComparatorLatestToEarliest());

        return directories;
    }

    /**
     * Retrieves a file with the given name from the given directory
     * 
     * @param directory the directory
     * @param fileName
     * @return
     */
    public File getFile(String directory, String fileName) {
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException("File directory cannot be null or blank");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("File name cannot be null or blank");
        }

        directory = addTrailingSlash(directory);

        File file = new File(storagePath + directory + fileName);

        return file;
    }

    /**
     * Reads the contents of a file into a string
     * 
     * @param directory
     * @param fileName
     * @return
     */
    public String readFileIntoString(String directory, String fileName) {
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException("File directory cannot be null or blank");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("File name cannot be null or blank");
        }

        String fileString = "";

        try {
            File file = getFile(directory, fileName);

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
        if (StringUtils.isBlank(dataString)) {
            throw new NullArgumentException("Data string cannot be null or blank");
        }
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException("Directory name cannot be null or blank");
        }
        if (StringUtils.isBlank(name)) {
            throw new NullArgumentException("File name cannot be null or blank");
        }

        File file = createNewFile(directory, name);

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

    /**
     * Add a trailing slash to the end of the path, if none exists
     * 
     * @param path the path string
     * @return the path with a trailing slash
     */
    private String addTrailingSlash(String path) {
        if (!StringUtils.endsWith(path, "/")) {
            path += "/";
        }

        return path;
    }

    public String getStoragePath() {
        return storagePath;
    }

    private ComparatorService comparatorService;
    public void setComparatorService(ComparatorService comparatorService) {
        this.comparatorService = comparatorService;
    }

}
