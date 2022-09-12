package candyenk.java.markdown.other;

import candyenk.java.tools.R;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.logging.Logger;

/**
 * 使用JS实现MD解析
 * Android不可用
 * marked - a markdown parser
 */
public class MDForJS {
    private static Logger l = Logger.getLogger(MDForJS.class.getSimpleName());
    private static Invocable invoke;

    static {
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            Reader reader = new FileReader(R.getJs("marked.min.js").getPath());
            engine.eval(reader);
            engine.eval("function md(s) {return marked.parse(s)}");
            if (engine instanceof Invocable) {
                invoke = (Invocable) engine;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ScriptException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 格式化蒙德字符串尾html
     */
    public static String prase(String mdString) {
        String html = "";
        if (!mdString.isEmpty() && checkInit()) {
            try {
                html = (String) invoke.invokeFunction("md", mdString);
            } catch (Exception e) {
                l.warning("MD-JavaScropt 脚本异常");
            }
        }
        return html;
    }

    /**
     * 检查MD引擎是哦后初始化完成
     */
    public static boolean checkInit() {
        if (invoke == null) {
            l.warning("MD-JavaScript 引擎初始化失败!");
            return false;
        }
        return true;
    }
}
