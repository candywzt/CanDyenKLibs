package candyenk.java.markdown;

import candyenk.java.markdown.mdnode.MDNode;

/**
 * MD解析器
 * 思路
 * MD文档标签分为一下两类:
 * 1:可嵌套标签
 * 2:不可嵌套标签(分割线)
 * <p>
 * 1:单行标签(分割线)
 * 2:行首标签(标题,代码块)
 * 3:行内标签
 * <p>
 * 现分组
 * 1:一级标签(单独存在)代码块 = 分割线
 * 2:行首标签(可嵌套) 引用 = 表格 = 标题
 * 3:行内标签()粗体 = 斜体 = 下划线 = 行内引用 = 图片 = 链接 =
 */
public class MD {

    private MD() {
    }

}
