/**
 * Copyright 2014 Sakaiproject Licensed under the
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.lap.util.FileUtils;

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
        storagePath = FileUtils.createStoragePath();
        // create the root directory
        createNewDirectory("", false);
    }

    /**
     * Creates a new directory for storing files
     * 
     * @param directoryName the name of the directory
     * @param isManualExtraction is this from a manual extraction?
     * @return the path to the directory
     */
    private String createNewDirectory(String directoryName, boolean isManualExtraction) {
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
     * Creates a new file with the given name in a given directory
     * If file exists, get the file instead
     * 
     * @param directory the directory to store the file
     * @param fileName the name of the new file
     * @param isManualExtraction is this from a manual extraction?
     * @return the new file
     */
    public File createNewFile(String directory, String fileName, boolean isManualExtraction) {
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException("File directory");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("File name");
        }

        File newFile = null;

        String newDirectory = createNewDirectory(directory, isManualExtraction);
        newDirectory = FileUtils.addTrailingSlash(directory);

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
     * Retrieves a file with the given name from the given directory
     * 
     * @param directory the directory
     * @param fileName the file name
     * @return the file object
     */
    public File getFile(String directory, String fileName) {
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException("File directory");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("File name");
        }

        directory = FileUtils.addTrailingSlash(directory);

        File file = new File(storagePath + directory + fileName);

        return file;
    }

    /**
     * Reads the contents of a file into a string
     * 
     * @param directory the directory of the file
     * @param fileName the file name
     * @return the data string
     */
    public String readFileIntoString(String directory, String fileName) {
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException("File directory");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new NullArgumentException("File name");
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

    /**
     * Saves the contents of a string to the given file
     * 
     * @param dataString the string of data
     * @param directory the directory to store the file
     * @param name the file name
     * @param isManualExtraction is this from a manual extraction?
     * @return true, if file saved successfully
     */
    public boolean saveStringToFile(String dataString, String directory, String name, boolean isManualExtraction) {
        if (StringUtils.isBlank(dataString)) {
            throw new NullArgumentException("Data string");
        }
        if (StringUtils.isBlank(directory)) {
            throw new NullArgumentException("Directory name");
        }
        if (StringUtils.isBlank(name)) {
            throw new NullArgumentException("File name");
        }

        File file = createNewFile(directory, name, isManualExtraction);

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

    public String getStoragePath() {
        return storagePath;
    }

}
