/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes.ws.proxy.soap.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author julamand
 */
public class Streams {

    public static void putStringAndClose(OutputStream os, String data)
            throws IOException {
        putString(os, data, false);
        os.close();
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

    public static String getString(InputStream is) {
        return new String(getBytes(is));

    }

    public static byte[] getBytes(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int readed;
        try {

            while ((readed = is.read()) != -1) {
                baos.write(readed);
            }

        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }

        return baos.toByteArray();
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
