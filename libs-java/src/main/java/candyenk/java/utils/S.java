package candyenk.java.utils;

import candyenk.java.i18n.Language;

import java.io.File;

/**
 * Java包多语言工具
 */
public class S {
    //多文件json格式
    /*
    单个文件表示单一语言
    条目不可嵌套
    全部置于根节点上
    一一对应不可重复
    使用一个map<语言代码,String[]条目内容>存储所有条目数组
    使用list<条目key>存储所有条目的key值
     */
    //多文件嵌套json格式
    /*
    单个文件表示单一语言
    条目可嵌套但不可出现条目数组
    同组不可重复
    一个类就是一个条目或者一个条目组
    手动确定key对应的事条目还是条目组
    使用一个静态map<语言代码,本类>存储所有语言对应的类条目
    使用一个int标志位标志当前类是一个条目还是一个条目组
    两个方法分别获取条目和条目组
     */
    //单文件json格式
    //单条目json格式
    //单文件json数组格式

    /**
     * 初始化多文件单列JSON
     */
    public static void initLanguage() {
        File[] files = new File(R.getLanguage(R.UURI)).listFiles();
        Language.INDTANCE().createLanguage(files);
    }
}
