/*
 * Copyright 2014 JL06436S.
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
package prototypes.ws.proxy.soap.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JL06436S
 */
public class ZipOut {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ZipOut.class);

    ZipOutputStream zipOut;

    PrintWriter currentWriter = null;

    public ZipOut(OutputStream out) {
        zipOut = new ZipOutputStream(out);
    }

    public PrintWriter getFileWriter(String fileName) {
        try {
            // TODO : if currentWriter != null, a file is already being written
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            // for file generation on-the-fly
            parameters.setSourceExternalStream(true);
            parameters.setFileNameInZip(fileName);
            zipOut.putNextEntry(null, parameters);
            currentWriter = new PrintWriter(zipOut);
        } catch (ZipException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return currentWriter;
    }

    public void closeFileWriter() {
        try {
            if (currentWriter != null) {
                currentWriter.flush();
                //currentWriter.close();
                currentWriter = null;
            }
            if (zipOut != null) {
                zipOut.closeEntry();
            }
        } catch (ZipException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

    }

    public void addDirToZipStream(String dirPath, String[] extensions) {
        dirPath = FilenameUtils.normalize(dirPath);
        // Files entries
        File dir = new File(dirPath);
        if (!dir.exists()) {
            LOGGER.warn("Directory {} to zip doesn't exist", dirPath);
            return;
        }
        LOGGER.debug("Add directory {} to zip", dirPath);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        InputStream inputStream = null;
        String filename = "";
        try {

            List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
            for (File file : files) {
                //LOGGER.debug("File {} to add to zip", file.getAbsolutePath());
                filename = file.getCanonicalPath().replace(dirPath, "");
                //Initialize inputstream
                inputStream = new FileInputStream(file);
                byte[] readBuff = new byte[4096];
                int readLen = -1;

                parameters.setSourceExternalStream(false);
                String folderInZip = file.getParent().replace(dirPath, "");
                if (!"".equals(folderInZip) && folderInZip.startsWith(File.separator)) {
                    folderInZip = folderInZip.substring(1);
                }
                LOGGER.debug("Folder in zip {}", folderInZip);
                parameters.setRootFolderInZip(folderInZip);
                zipOut.putNextEntry(file, parameters);
                //Read the file content and write it to the OutputStream
                //zipOut.write(Files.read(file.getAbsolutePath()).getBytes());
                while ((readLen = inputStream.read(readBuff)) != -1) {
                    zipOut.write(readBuff, 0, readLen);
                }
                zipOut.closeEntry();

                inputStream.close();
            }
        } catch (ZipException e) {
            LOGGER.warn("Error while adding file {} : {}", filename, e.getMessage());
        } catch (IOException e) {
            LOGGER.warn("Error while opening file {} : {}", filename, e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Error on inputStream close {} ", e.getMessage());
                }
            }
        }
    }

    public void finish() {
        if (zipOut != null) {
            try {
                zipOut.finish();
            } catch (ZipException ex1) {
                LOGGER.error("Error on zip finish {} ", ex1.getMessage());
            } catch (IOException ex1) {
                LOGGER.error("Error on zip finish {} ", ex1.getMessage());
            }
        }
    }

}
