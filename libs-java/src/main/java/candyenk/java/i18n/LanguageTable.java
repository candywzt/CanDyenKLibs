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
 * 条目可嵌套
 * 条目key可使用(点)连接(类似Minecraft)
 * 建议使用当前
 */
public class LanguageTable implements LanguageInterface {
    private static final Logger L = Logger.getLogger(LanguageTable.class.getSimpleName());//TODO:换换日志工具
    private static final LanguageTable INSTANCE = new LanguageTable();

    private final Map<String, JsonObject> allLanguage = new HashMap<>();//各语言文件条目内容
    private final Map<String, Integer> langNumber = new HashMap<>();//各语言文件有效条目数量
    private final List<String> keyList = new ArrayList<>();//所有条目的KEY集合

    private String langCode = Locale.getDefault().toString();//默认语言代码
    private String langCodeTemporary = langCode;//临时语言代码
    private String keyPrefix = "";//当前语言key前缀
    private LanguageTable parent;//父级语言文件
    /******************************************************************************************************************/
    /***********************************************构造方法*********************************************************/
    /******************************************************************************************************************/
    private LanguageTable() {
    }
    /******************************************************************************************************************/
    /***********************************************静态方法*********************************************************/
    /******************************************************************************************************************/
    /**
     * 获取最顶级的语言文件
     */
    public static LanguageTable INDTANCE() {
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
    public LanguageTable getChild(String... keys) {
        Map<String, JsonObject> map = new HashMap<>();
        if (keys.length == 0) {
            L.warning("获取子字符串语言组所需的KEY为空");
        } else {
            String key = keySplicing(keys);
            map = getChildMap(key);
        }
        LanguageTable child = new LanguageTable();
        child.parent = this;
        child.keyPrefix = keySplicing(keys);
        child.createLanguage(map);
        return child;
    }

    @Override
    public LanguageTable getParent() {
        return parent;
    }

    @Override
    public String getLang() {
        return langCode;
    }

    @Override
    public LanguageTable lang(String locale) {
        langCodeTemporary = locale;
        return this;
    }

    @Override
    public void setDefaultLanguage(String locale) {
        langCode = locale;
        langCodeTemporary = locale;
    }


    @Override
    public LanguageTable createLanguage(Map<String, JsonObject> map) {
        keyList.addAll(getKeySet(map));
        allLanguage.putAll(map);
        return this;
    }


    @Override
    public LanguageTable createLanguage(File... files) {
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
        JsonObject json = allLanguage.get(locale);
        if (json == null) {
            L.warning(locale + "语言文件不存在");
        } else {

            if (string == null) {
                string = "";
                L.warning("获取字符串所需的KEY:" + key + "不在 " + locale + " 语言文件中");
            }
        }
        return string;
    }

    //获取子语言map
    private Map<String, JsonObject> getChildMap(String key) {
        Map<String, JsonObject> map = new HashMap<>();
        allLanguage.forEach((k, v) -> {
            JsonObject json = v.get(key).getAsJsonObject();
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
            Set<String> set1 = getKeysForJson(keyPrefix, map.get(k).getAsJsonObject());
            set.addAll(set1);
            langNumber.put(k, set1.size());
        });
        return set;
    }

    //从JsonObject获取所有Key
    private Set<String> getKeysForJson(String key, JsonObject json) {
        Set<String> set = new HashSet<>();
        json.keySet().forEach(k -> {
            JsonElement e = json.get(k);
            if (e.isJsonPrimitive()) {
                set.add(k);
            } else if (e.isJsonObject()) {
                set.addAll(getKeysForJson(keySplicing(key, k), e.getAsJsonObject()));
            }
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
            sb.append("语言内容:").append(v.toString()).append("\n");
        });
        return sb.toString();
    }
}
