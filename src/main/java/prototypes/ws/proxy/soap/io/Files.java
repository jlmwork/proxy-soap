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
package prototypes.ws.proxy.soap.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constants.Messages;

public class Files {

    private static final Logger LOGGER = LoggerFactory.getLogger(Files.class);

    private static final int BUFFER_SIZE = 4096;

    private Files() {
    }

    public static String[] findFilesInDirByExt(final String path,
            final String ext) {
        return findFilesInDirByExts(path, new String[]{ext});
    }

    public static String[] findFilesInDirByExts(final String path,
            String[] extensions) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            List<File> files = (List<File>) FileUtils.listFiles(dir,
                    extensions, true);
            if ((files != null) && (files.size() > 0)) {
                String[] fileNames = new String[files.size()];
                int i = 0;
                for (File file : files) {
                    fileNames[i++] = file.getAbsolutePath();
                }
                return fileNames;
            }
        } else {
            LOGGER.warn("dir " + path + " does not exist or is not a directory");
        }
        return new String[0];
    }

    public static void createDirectory(String dirPath) throws IOException {
        File temp = new File(dirPath);
        if (!temp.exists()) {
            temp.mkdir();
        }
    }

    public static File createTempDirectory() throws IOException {
        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: "
                    + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: "
                    + temp.getAbsolutePath());
        }

        return (temp);
    }

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified
     * by destDirectory (will be created if does not exists)
     *
     * @param zipFilePath
     * @return
     */
    public static String unzip(String zipFilePath) {
        try {
            File folder = createTempDirectory();
            if (!folder.exists()) {
                folder.mkdir();
            }
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(
                    zipFilePath));
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = folder.getAbsolutePath() + File.separator
                        + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
            return folder.getAbsolutePath();
        } catch (FileNotFoundException ex) {
            LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
        } catch (IOException ex) {
            LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
        }
        return null;
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath)
            throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    /**
     *
     * @param zipFile
     * @return
     */
    public static String unzipFile(String zipFile) {
        FileOutputStream fos = null;
        try {
            File folder = createTempDirectory();
            byte[] buffer = new byte[1024];

            // create output directory is not exists
            if (!folder.exists()) {
                folder.mkdir();
            }

            // get the zip file content
            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(zipFile));
            // get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(folder.getAbsolutePath()
                        + File.separator + fileName);

                LOGGER.debug("file unzip : {}", newFile.getAbsoluteFile());

                // create all non exists folders
                // else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
            return folder.getAbsolutePath();

        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
                }
            }
        }
        return null;
    }

    public static String download(String httpPath) {
        String localPath = "";
        LOGGER.debug("Remote file to download : {}", httpPath);
        try {
            File folder = createTempDirectory();
            if (!folder.exists()) {
                folder.mkdir();
            }
            URL url = new URL(httpPath);
            String distantFile = url.getFile();
            if (distantFile != null) {
                int pos = distantFile.lastIndexOf('/');
                if (pos != -1) {
                    distantFile = distantFile.substring(pos + 1,
                            distantFile.length());
                }
            }
            localPath = folder.getAbsolutePath() + File.separator + distantFile;
            LOGGER.debug("Local path to save to : {}", localPath);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(localPath);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            LOGGER.info("Remote file downloaded.");
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return localPath;
    }

    public static String readCompressed(String svgPath) {
        return read(svgPath, true);
    }

    public static byte[] readBytesCompressed(String svgPath) {
        return readBytes(svgPath, true);
    }

    public static String read(String svgPath) {
        return read(svgPath, false);
    }

    public static String read(String svgPath, boolean compressed) {
        String content = null;
        try {
            InputStream is = new FileInputStream(svgPath);
            if (compressed) {
                is = new GZIPInputStream(is);
            }
            content = Streams.getString(is);
        } catch (IOException ex) {
            LOGGER.warn("Error reading {}, {}", svgPath, ex);
        }
        return content;
    }

    public static byte[] readBytes(String svgPath, boolean compressed) {
        byte[] content = new byte[0];
        try {
            InputStream is = new FileInputStream(svgPath);
            if (compressed) {
                is = new GZIPInputStream(is);
            }
            content = Streams.getBytes(is);
        } catch (IOException ex) {
            LOGGER.warn("Error reading {}, {}", svgPath, ex);
        }
        return content;
    }

    public static String writeCompressed(String svgPath, String content) {
        return write(svgPath, content.getBytes(), true);
    }

    public static String writeCompressed(String svgPath, byte[] contentBytes) {
        return write(svgPath, contentBytes, true);
    }

    public static String write(String svgPath, String content) {
        return write(svgPath, content.getBytes(), false);
    }

    public static String write(String svgPath, byte[] contentBytes) {
        return write(svgPath, contentBytes, false);
    }

    public static String write(String svgPath, byte[] content, boolean compressed) {
        OutputStream os = null;
        String finalFileName = svgPath;
        try {
            os = new FileOutputStream(new File(finalFileName));
            if (compressed) {
                os = new GZIPOutputStream(os);
            }
            os.write(content);
        } catch (IOException ex) {
            finalFileName = "-1";
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        return finalFileName;
    }

    public static void deleteDirectory(String path) {
        try {
            LOGGER.debug("delete directory {} ", path);
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException ex) {
            LOGGER.warn("Cant delete directory {} : {}", path, ex);
        }
    }

    public static void deleteFile(String path) {
        try {
            LOGGER.debug("delete file {} ", path);
            FileUtils.forceDelete(new File(path));
        } catch (IOException ex) {
            LOGGER.warn("Cant delete file {} : {} ", path, ex);
        }
    }

    /**
     *
     *
     * @param classpathPath relative path of a file in classpath (no leading
     * slash)
     * @return found system file path or empty String if not found
     */
    public static String findFromClasspath(String classpathPath) {
        try {
            URL url = Files.class.getClassLoader().getResource(classpathPath);
            return url.toString();
        } catch (Exception ex) {
            LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
            return "";
        }

    }
}
