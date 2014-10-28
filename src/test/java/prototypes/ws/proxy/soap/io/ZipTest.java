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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class ZipTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ZipTest.class);

    public static void main(String... args) throws Exception {
        String filename = "C:\\commons\\io\\\\lang\\project.xml";
        String normalized = FilenameUtils.normalize(filename);
        System.out.println(normalized);
    }

    public static void olderMain(String... args) throws Exception {
        ZipOut zipOut = new ZipOut(new FileOutputStream("/tmp/test4.zip"));
        zipOut.getFileWriter("test.csv").println("test");
        zipOut.closeFileWriter();
        zipOut.addDirToZipStream("E:\\dev\\projects\\sgel\\prototypes\\proxy-soap\\src\\main\\resources", new String[]{"xml", "properties"});
        zipOut.finish();
    }

    public void olderMain() {
        ZipOutputStream zipOut = null;
        InputStream inputStream = null;
        try {
            zipOut = new ZipOutputStream(new FileOutputStream("/tmp/test2.zip"));

            // CSV Entry
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            // for file generation on-the-fly
            parameters.setSourceExternalStream(true);
            parameters.setFileNameInZip("test.csv");
            zipOut.putNextEntry(null, parameters);
            zipOut.write("test\n".getBytes());
            zipOut.write("test\n".getBytes());
            zipOut.closeEntry();

            // Files entries
            String dirPath = "E:\\dev\\projects\\sgel\\prototypes\\proxy-soap\\src\\main\\resources";
            File dir = new File(dirPath);
            String[] extensions = new String[]{"xml", "properties"};
            List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
            for (File file : files) {
                System.out.println("file: " + file.getCanonicalPath().replace(dirPath, ""));
                //Initialize inputstream
                inputStream = new FileInputStream(file);
                byte[] readBuff = new byte[4096];
                int readLen = -1;

                parameters.setSourceExternalStream(false);
                String folderInZip = file.getParent().replace(dirPath, "");
                LOGGER.debug("Folder in zip {}", folderInZip);
                if (!"".equals(folderInZip)) {
                    parameters.setRootFolderInZip(folderInZip.substring(1));
                }
                zipOut.putNextEntry(file, parameters);
                //Read the file content and write it to the OutputStream
                //zipOut.write(Files.read(file.getAbsolutePath()).getBytes());
                while ((readLen = inputStream.read(readBuff)) != -1) {
                    zipOut.write(readBuff, 0, readLen);
                }
                LOGGER.debug("ios written");
                zipOut.closeEntry();

                inputStream.close();
            }

        } catch (ZipException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (IOException e) {
            LOGGER.error("Error {} ", e.getMessage());
        } finally {
            if (zipOut != null) {
                try {
                    zipOut.finish();
                } catch (ZipException ex1) {
                    LOGGER.error("Error on zip finish {} ", ex1.getMessage());
                } catch (IOException ex1) {
                    LOGGER.error("Error on zip finish {} ", ex1.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Error on inputStream close {} ", e.getMessage());
                }
            }
        }
    }
}
