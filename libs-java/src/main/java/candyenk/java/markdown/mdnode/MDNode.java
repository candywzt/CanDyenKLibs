package candyenk.java.markdown.mdnode;

/**
 * MD树节点接口
 * 用于表示一段MD内容
 */
public interface MDNode {
    /**
     * 该节点能否嵌套行内节点
     */
    boolean canNesting();

    /**
     * 获取当前节点的父节点
     */
    MDNode getParent();

    /**
     * 获取当前节点的子节点组
     */
    MDNode[] getChilds();
}
