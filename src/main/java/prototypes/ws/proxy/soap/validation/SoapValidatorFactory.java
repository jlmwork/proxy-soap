/*
 * Copyright 2014 jlamande.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package prototypes.ws.proxy.soap.validation;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.io.Files;
import prototypes.ws.proxy.soap.io.Requests;

public class SoapValidatorFactory {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SoapValidatorFactory.class);

    private static final Map<String, SoapValidator> validators = new TreeMap<String, SoapValidator>();

    /**
     * Make new soap validator to validate soap message with this wsdl
     * definition. Null returned if wsdl file not exists.
     *
     * @param wsdlPath
     * @return
     */
    public static SoapValidator createSoapValidator(String wsdlPath) {
        return createSoapValidator(wsdlPath, null, null);
    }

    /**
     *
     */
    public static void listValidators() {
        if (LOGGER.isDebugEnabled()) {
            Iterator<Map.Entry<String, SoapValidator>> it = validators
                    .entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, SoapValidator> pairs = it.next();
                LOGGER.debug(pairs.getKey() + " = " + pairs.getValue());
            }
        }
    }

    /**
     *
     */
    public static Map<String, SoapValidator> getValidators() {
        return validators;
    }

    private static SoapValidator createSoapValidator(String wsdlPath,
            String key, String from) {
        key = (key != null) ? key : wsdlPath;

        SoapValidator validator = validators.get(key);
        boolean createNew = false;
        if (validator == null) {
            LOGGER.debug("No existing WSDL Validator for key : '" + key + "'");
            createNew = true;
        } else {
            // validator already exists
            if (!Requests.isHttpPath(wsdlPath)) {
                // if a file path
                // need to check if local file has been modified since last
                // validator creation

                File newFile = new File(wsdlPath);
                // dont create a new validator if file does not exist
                createNew = (newFile.exists() && (newFile.lastModified() > validator
                        .getCreationTime()));
                LOGGER.debug("Existing WSDL Validator for path : " + wsdlPath
                        + ", creation time : " + newFile.lastModified()
                        + ", file modified time : " + newFile.lastModified());
            }
        }

        if (createNew) {
            LOGGER.debug("Create new WSDL Validator for path : " + wsdlPath);
            try {
                validator = new SoapValidatorSoapUI(wsdlPath, key, from);
                LOGGER.debug("Saves the new WSDL Validator under key : " + key);
                validators.put(key, validator);
            } catch (NotFoundSoapException e) {
                return null;
            }
        }
        return validator;
    }

    /**
     *
     * @param multiplePaths
     * @return
     */
    public static void createSoapValidators(String multiplePaths) {
        LOGGER.info("create soap validators");
        // catch paths
        String[] toScanPaths = multiplePaths.split(";");// File.pathSeparator);
        boolean cachesCleared = false;

        for (String path : toScanPaths) {
            // TODO : normalize paths for os portability
            // FileSystem.getFileSystem().normalize(path);

            // Direct access to a WSDL
            if (path.toUpperCase().endsWith(".WSDL")
                    || path.toUpperCase().endsWith("?WSDL")) {
                LOGGER.debug("Create validator for wsdl : " + path);
                createSoapValidator(path,
                        Requests.resolveSoapServiceFromURL(path), path);
            } // JARS or DIRS
            else {
                // For full archives import, cleanup of cache is required
                // but once per import
                if (!cachesCleared) {
                    SoapValidatorSoapUI.clearCaches();
                    cachesCleared = true;
                }
                String dirPath = path;
                if (path.toUpperCase().endsWith(".JAR")
                        || path.toUpperCase().endsWith(".ZIP")) {
                    LOGGER.debug("Archive : " + path);
                    String localPath = path;
                    if (Requests.isHttpPath(path)) {
                        // download remote jar
                        // TODO : treat resource caching
                        localPath = Files.download(path);
                    }
                    LOGGER.debug("Unzipping Archive");
                    String unzippedPath = Files.unzip(localPath);
                    LOGGER.debug("Archive unzipped to : " + unzippedPath);
                    // createSoapValidator(path,
                    // Requests.resolveSoapServiceFromURL(path));
                    dirPath = unzippedPath;
                }

                // must be scanned to find all WSDL
                LOGGER.debug("Scan dir for wsdl : " + dirPath);
                // dirs must be in local filesystem
                String[] wsdlPaths = Files.findFilesInDirByExt(dirPath, "wsdl");
                for (String filepath : wsdlPaths) {
                    LOGGER.debug("Create validator for wsdl : " + filepath);
                    createSoapValidator(filepath,
                            Requests.resolveSoapServiceFromURL(filepath), path);
                }
            }
        }
        LOGGER.info("soap validators creation done");
        listValidators();
        // return null;
    }
}
