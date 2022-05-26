package candyenk.java.i18n;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public interface LanguageInterface {
    /**
     *
     */

    /**
     * 通过KEY获取字符串
     */
    String get(String... keys);

    /**
     * 获取对应语言条目数量
     */
    int getLangNumber(String locale);

    /**
     * 获取所有条目的数量
     */
    int getAllLangNumber();

    /**
     * 通过Key获取子组字符串
     */
    LanguageInterface getChild(String... keys);

    /**
     * 获取父级组字符串
     */
    LanguageInterface getParent();

    /**
     * 获取当前默认语言
     */
    String getLang();

    /**
     * 修改下次获取字符串的语言代码
     */
    LanguageInterface lang(String locale);

    /**
     * 修改默认语言
     */
    void setDefaultLanguage(String locale);

    /**
     * 从文件创建语言表
     */
    LanguageInterface createLanguage(File... files);

    /**
     * 从JSONMap创建语言表
     *
     * @return
     */
    LanguageInterface createLanguage(Map<String, JsonObject> map);

    /******************************************************************************************************************/
    /************************************************默认方法***********************************************************/
    /******************************************************************************************************************/

    /**
     * 重置默认语言
     */
    default void setDefaultLanguage() {
        setDefaultLanguage(Locale.getDefault().toString());
    }

    /**
     * 获取默认语言条目数量
     */
    default int getLangNumber() {
        return getLangNumber(getLang());
    }

    /**
     * Key拼接
     */
    default String keySplicing(String... keys) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            sb.append(keys[i]);
            if (i < keys.length - 1) sb.append(".");
        }
        return sb.toString();
    }

    /**
     * Key分割
     */
    default String[] keySplit(String key) {
        return key.split(".");
    }

    /**
     * Key对比
     */
    default boolean keyEquals(String[] keys1, String[] keys2) {
        return Arrays.equals(keys1, keys2);
    }


}
