import java.io.BufferedWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class WordRandomizerFilterWriter extends FilterWriter {
    public WordRandomizerFilterWriter(Writer out) {
        super(out);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        write(String.valueOf(cbuf),off,len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        str = WordRandomizer.randomize(str);
        super.write(str, off,len);
    }

    public void write(String str) throws IOException{
        write(str,0,str.length());
    }
}
