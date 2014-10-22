package prototypes.ws.proxy.soap.io.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class BufferedHttpResponseWrapper extends HttpServletResponseWrapper {
    private final BufferedServletOutputStream bufferedServletOut = new BufferedServletOutputStream();

    private PrintWriter printWriter = null;
    private ServletOutputStream outputStream = null;

    public BufferedHttpResponseWrapper(HttpServletResponse origResponse) {
        super(origResponse);
    }

    public byte[] getBuffer() {
        return this.bufferedServletOut.getBuffer();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (this.outputStream != null) {
            throw new IllegalStateException(
                    "The Servlet API forbids calling getWriter( ) after"
                            + " getOutputStream( ) has been called");
        }

        if (this.printWriter == null) {
            this.printWriter = new PrintWriter(this.bufferedServletOut);
        }
        return this.printWriter;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.printWriter != null) {
            throw new IllegalStateException(
                    "The Servlet API forbids calling getOutputStream( ) after"
                            + " getWriter( ) has been called");
        }

        if (this.outputStream == null) {
            this.outputStream = this.bufferedServletOut;
        }
        return this.outputStream;
    }

    // override methods that deal with the response buffer

    @Override
    public void flushBuffer() throws IOException {
        if (this.outputStream != null) {
            this.outputStream.flush();
        } else if (this.printWriter != null) {
            this.printWriter.flush();
        }
    }

    @Override
    public int getBufferSize() {
        return this.bufferedServletOut.getBuffer().length;
    }

    @Override
    public void reset() {
        this.bufferedServletOut.reset();
    }

    @Override
    public void resetBuffer() {
        this.bufferedServletOut.reset();
    }

    @Override
    public void setBufferSize(int size) {
        this.bufferedServletOut.setBufferSize(size);
    }

    static class BufferedServletOutputStream extends ServletOutputStream {
        // the actual buffer
        private ByteArrayOutputStream bos = new ByteArrayOutputStream();

        /**
         * @return the contents of the buffer.
         */
        public byte[] getBuffer() {
            return this.bos.toByteArray();
        }

        /**
         * This method must be defined for custom servlet output streams.
         */
        @Override
        public void write(int data) {
            this.bos.write(data);
        }

        // BufferedHttpResponseWrapper calls this method
        public void reset() {
            this.bos.reset();
        }

        // BufferedHttpResponseWrapper calls this method
        public void setBufferSize(int size) {
            // no way to resize an existing ByteArrayOutputStream
            this.bos = new ByteArrayOutputStream(size);
        }
    }
}
