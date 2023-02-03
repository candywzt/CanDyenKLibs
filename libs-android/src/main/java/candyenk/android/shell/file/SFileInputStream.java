package candyenk.android.shell.file;

import java.io.IOException;
import java.io.InputStream;

public class SFileInputStream extends InputStream {

    public SFileInputStream(SFile file) {

    }


    @Override
    public int read() throws IOException {
        return 0;
    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return 0;
    }


    @Override
    public long skip(long n) throws IOException {
        return 0;
    }

    @Override
    public int available() throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }

}
