package candyenk.java.markdown;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * MD字符串预处理器
 * 用于将各种形式内容处理成MD字符串列表
 */
public class Pretreat {
    private List<String> mdList;
    /******************************************************************************************************************/
    /************************************************构造方法***********************************************************/
    /******************************************************************************************************************/
    /**
     * 从MD字符串读取
     */
    public Pretreat(String mdString) {
        mdString = mdString.replaceAll("\\r\\n|\\r", "\n")
                .replaceAll("\\t", "    ")
                .replaceAll("\\u00a0", " ")
                .replaceAll("\\u2424", "\n");
        mdList = Arrays.asList(mdString.split("\n"));
    }

    /**
     * 从MD文件读取
     */
    public Pretreat(File mdFile) {
        this(readFile(mdFile));
    }

    /**
     * 从MD输入流读取
     */
    public Pretreat(InputStream mdIntputStream) {
        this(new InputStreamReader(mdIntputStream));
    }

    /**
     * 从MD阅读者读取
     */
    public Pretreat(Reader mdReader) {
        String line;
        BufferedReader reader;
        mdList = new ArrayList<>();
        if (mdReader instanceof BufferedReader) {
            reader = (BufferedReader) mdReader;
        } else {
            reader = new BufferedReader(mdReader);
        }
        try {
            while ((line = reader.readLine()) != null) {
                mdList.add(line);
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /******************************************************************************************************************/
    /************************************************公共方法***********************************************************/
    /******************************************************************************************************************/
    /**
     * 遍历所有字符串
     */
    public void forEach(Consumer<String> action) {
        mdList.forEach(action);
    }
    /******************************************************************************************************************/
    /*********************************************私有静态方法***********************************************************/
    /******************************************************************************************************************/
    private static FileInputStream readFile(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileInputStream;
    }
}
