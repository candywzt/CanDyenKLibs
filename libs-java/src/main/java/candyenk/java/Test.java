package candyenk.java;

import com.google.gson.JsonObject;
import candyenk.java.i18n.Language;
import candyenk.java.utils.FileUtil;
import candyenk.java.utils.R;

import java.io.File;
import java.net.URI;

public class Test {
    public static void main(String[] args) {
        File[] files = new File(R.getLanguage(R.UURI)).listFiles();
        Language l = Language.INDTANCE();
        l.createLanguage(files);
        System.out.println("lKey(key.ccc.5):" + l.get("key", "ccc", "5"));
        System.out.println("l全部:" + l.test());
        Language ll = l.getChild("key.bbb");
        System.out.println("llKey(key.bbb.5):" + ll.get("key", "bbb", "5"));
        System.out.println("llKey(key.bbb.5):" + ll.get("5"));
        System.out.println("ll全部:" + ll.test());
        ll = l.getChild("key", "bbb");
        System.out.println("ll2Key(key.bbb.5):" + ll.get("key", "bbb", "5"));
        System.out.println("ll2Key(key.bbb.5):" + ll.get("5"));
        System.out.println("ll2全部:" + ll.test());
    }


    private static void write() {
        File files = new File(URI.create(getName()));
        JsonObject json = new JsonObject();
        for (int i1 = 0; i1 < 10; i1++) {
            json.addProperty("key.ccc." + i1, "value" + i1);
        }
        FileUtil.writeFile(files.getAbsolutePath(), json.toString(), true);
    }

    private static String getName() {
        URI uri = R.getLanguage(R.UURI);
        String s = uri.toString() + "zh_CN.json";
        return s;
    }

}
