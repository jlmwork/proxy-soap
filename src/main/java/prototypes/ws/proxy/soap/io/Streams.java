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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julamand
 */
public class Streams {

    private static final Logger LOGGER = LoggerFactory.getLogger(Streams.class);

    public static void putStringAndClose(OutputStream os, String data)
            throws IOException {
        if (os != null) {
            putString(os, data, false);
            os.close();
        }
    }

    public static OutputStream putString(OutputStream os, String data,
            boolean zipped) throws IOException {
        if (data == null) {
            return os;
        }

        if (zipped) {
            os = new java.util.zip.GZIPOutputStream(os);
            os.write(data.getBytes());
        } else {
            os.write(data.getBytes());
        }

        return os;
    }

    public static void writeStringAndClose(Writer w, String data)
            throws IOException {
        if (w != null) {
            w.write(data);
            w.close();
        }
    }

    public static void writeAndClose(Writer w, byte[] data) throws IOException {
        if (w != null) {
            String encoding = detectCharset(data);
            if (encoding != null) {
                w.write(new String(data, encoding));
                w.close();
            } else {
                w.write(new String(data, "UTF-8"));
            }
        }
    }

    public static String getString(InputStream is, boolean zipped) {
        InputStream finalIS = is;
        if (zipped) {
            try {
                finalIS = new java.util.zip.GZIPInputStream(is);
            } catch (IOException e) {
            }
        }
        return getString(finalIS);
    }

    public static byte[] compressString(String toCompress) {
        GZIPOutputStream finalOS = null;
        try {
            ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
            finalOS = new GZIPOutputStream(bAOS);
            finalOS.write(toCompress.getBytes());
            finalOS.close();
            return bAOS.toByteArray();
        } catch (IOException e) {
            LOGGER.warn("Error on compressing {} ", e.getMessage());
        } finally {
            if (finalOS != null) {
                try {
                    finalOS.close();
                } catch (Exception e) {
                }
            }
        }
        return toCompress.getBytes();
    }

    public static String getString(InputStream is) {
        try {
            LOGGER.debug("Read from InputStream");
            byte[] bytes = getBytes(is);
            String encoding = detectCharset(bytes);
            LOGGER.trace("Size and Charset of bytes read from InputStream : {}", bytes.length, encoding);
            if (encoding != null) {
                return new String(bytes, encoding);
            }
            // use a default encoding (if no parameter, platform encoding would be used, Charset.defaultCharset())
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.warn("Error converting string : " + ex.getMessage());
            return "";
        }
    }

    private static String detectCharset(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        LOGGER.debug("Charset detected : " + detector.getDetectedCharset());
        return detector.getDetectedCharset();
    }

    public static byte[] getBytes(InputStream is, boolean zipped) {
        InputStream finalIS = is;
        if (zipped) {
            try {
                finalIS = new java.util.zip.GZIPInputStream(is);
            } catch (IOException e) {
            }
        }
        return getBytes(finalIS);
    }

    public static byte[] getBytes(InputStream iS) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBytes(iS, baos);
        LOGGER.debug("Size of bytes read from InputStream : {}", baos.size());
        return baos.toByteArray();
    }

    public static void getBytes(InputStream iS, OutputStream oS) {
        int readed;
        try {
            while ((readed = iS.read()) != -1) {
                oS.write(readed);
            }
        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            try {
                oS.close();
            } catch (IOException e) {
                // e.printStackTrace();
            }
            if (iS != null) {
                try {
                    iS.close();
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
        }
    }

    public static int copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[4096];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
