package candyenk.java.i18n;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Logger;

/**
 * 语言条目类
 * 使用多个JSON文件存储多种语言
 * 单一JSON存储单一语言
 * 条目不可嵌套
 * 全部置于根节点
 * 条目key可使用(点)连接(类似Minecraft)
 * 默认语言类就是这个
 */
public class Language implements LanguageInterface {
    private static final Logger L = Logger.getLogger(Language.class.getSimpleName());//TODO:换换日志工具
    private static final Language INSTANCE = new Language();

    private final Map<String, String[]> allLanguage = new HashMap<>();//各语言文件条目内容
    private final Map<String, Integer> langNumber = new HashMap<>();//各语言文件有效条目数量
    private final List<String> keyList = new ArrayList<>();//所有条目的KEY集合

    private Map<String, JsonObject> jsonMap;//存储JsonObject的Map

    private String langCode = Locale.getDefault().toString();//默认语言代码
    private String langCodeTemporary = langCode;//临时语言代码
    private String keyPrefix = "";//当前语言key前缀
    private Language parent;//父级语言文件
    /******************************************************************************************************************/
    /***********************************************构造方法*********************************************************/
    /******************************************************************************************************************/
    private Language() {
    }
    /******************************************************************************************************************/
    /***********************************************静态方法*********************************************************/
    /******************************************************************************************************************/
    /**
     * 获取最顶级的语言文件
     */
    public static Language INDTANCE() {
        return INSTANCE;
    }
    /******************************************************************************************************************/
    /***********************************************公共方法*********************************************************/
    /******************************************************************************************************************/
    @Override
    public String get(String... keys) {
        String str = "";
        if (keys.length == 0) {
            L.warning("获取字符串所需的KEY为空");
            return str;
        }
        String key = keySplicing(keys);
        str = getString(langCodeTemporary, key);
        if (str.isEmpty() && !langCodeTemporary.equals(langCode)) {
            str = getString(langCode, key);
            langCodeTemporary = langCode;
        }
        if (str.isEmpty() && !keyPrefix.isEmpty()) {
            key = keySplicing(keyPrefix, key);
            str = getString(langCodeTemporary, key);
            if (str.isEmpty() && !langCodeTemporary.equals(langCode)) {
                str = getString(langCode, key);
                langCodeTemporary = langCode;
            }
        }
        if (str.isEmpty()) L.warning("获取字符串所需的KEY: " + key + " 不存在");
        return str;
    }

    @Override
    public int getLangNumber(String locale) {
        return langNumber.get(locale);
    }

    @Override
    public int getAllLangNumber() {
        return keyList.size();
    }

    @Override
    public Language getChild(String... keys) {
        Map<String, JsonObject> map = new HashMap<>();
        if (keys.length == 0) {
            L.warning("获取子字符串语言组所需的KEY为空");
        } else {
            String key = keySplicing(keys);
            map = getChildMap(key);
        }
        Language child = new Language();
        child.parent = this;
        child.keyPrefix = keySplicing(keys);
        child.createLanguage(map);
        return child;
    }

    @Override
    public Language getParent() {
        return parent;
    }

    @Override
    public String getLang() {
        return langCode;
    }

    @Override
    public Language lang(String locale) {
        langCodeTemporary = locale;
        return this;
    }

    public void setDefaultLanguage(String locale) {
        langCode = locale;
        langCodeTemporary = locale;
    }


    @Override
    public Language createLanguage(Map<String, JsonObject> map) {
        jsonMap = map;
        keyList.addAll(getKeySet(map));
        map.forEach((k, v) -> {
            allLanguage.put(k, createLangs(v));
        });
        return this;
    }


    @Override
    public Language createLanguage(File... files) {
        Map<String, JsonObject> map = new HashMap<>();
        for (File file : files) {
            try {
                JsonElement element = JsonParser.parseReader(new FileReader(file));
                if (element.isJsonObject()) {
                    map.put(getFileName(file), element.getAsJsonObject());
                } else {
                    L.warning("文件:\"" + file.getAbsolutePath() + "\"不是有效的JSON对象文件,请检查");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return createLanguage(map);
    }

    /******************************************************************************************************************/
    /***********************************************私有方法*********************************************************/
    /******************************************************************************************************************/

    //获取指定条目
    private String getString(String locale, String key) {
        String string = "";
        int index = keyList.indexOf(key);
        if (index != -1) {
            string = getLangs(locale)[index];
            if (string == null) {
                string = "";
                L.warning("获取字符串所需的KEY:" + key + "不在 " + locale + " 语言文件中");
            }
        }
        return string;
    }

    //获取指定语言条目组
    private String[] getLangs(String locale) {
        String[] langs = allLanguage.get(locale);
        if (langs == null) {
            L.warning(locale + "语言文件不存在");
            langs = allLanguage.get(langCode);
        }
        return langs;
    }

    //获取子语言map
    private Map<String, JsonObject> getChildMap(String key) {
        Map<String, JsonObject> map = new HashMap<>();
        jsonMap.keySet().forEach(k -> {
            JsonObject obj = jsonMap.get(k);
            JsonObject json = new JsonObject();
            obj.keySet().forEach(k2 -> {
                if (k2.startsWith(key)) {
                    json.add(k2, obj.get(k2));
                }
            });
            map.put(k, json);
        });
        return map;
    }

    //从文件获取文件名
    private String getFileName(File file) {
        String filename = file.getName();
        if (filename.endsWith(".json")) {
            return filename.substring(0, filename.length() - 5);
        } else {
            L.warning("文件:" + filename + "不是JSON后缀文件");
            return filename;
        }
    }


    //从Map获取KeySet
    private Set<String> getKeySet(Map<String, JsonObject> map) {
        Set<String> set = new HashSet<>();
        map.forEach((k, v) -> {
            Set<String> set1 = map.get(k).getAsJsonObject().keySet();
            set.addAll(set1);
            langNumber.put(k, set1.size());
        });
        return set;
    }

    //创建条目组
    private String[] createLangs(JsonObject json) {
        String[] langs = new String[keyList.size()];
        for (int i = 0; i < keyList.size(); i++) {
            JsonElement e = json.get(keyList.get(i));
            langs[i] = json.get(keySplicing(keyList.get(i))).getAsString();
        }
        return langs;
    }
    /******************************************************************************************************************/
    /***********************************************TEST***************************************************************/
    /******************************************************************************************************************/
    public String test() {
        StringBuilder sb = new StringBuilder("语言条目如下:\n");
        sb.append("语言Key:").append(keyList).append("\n");
        allLanguage.forEach((k, v) -> {
            sb.append("语言名:").append(k).append("\n");
            sb.append("语言内容:").append(Arrays.toString(v)).append("\n");
        });
        return sb.toString();
    }
}
