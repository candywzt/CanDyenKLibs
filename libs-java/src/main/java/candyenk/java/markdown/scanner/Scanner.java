package candyenk.java.markdown.scanner;

import java.util.ArrayList;
import java.util.List;

/**
 * MD扫描器
 * 用于将预处理器扫描到MD节点树中
 * 阶层:扫描器按阶层串行扫描整个字符串列表
 * 优先级:扫描器按优先级并行扫描一行字符串
 */
public interface Scanner {
    /**
     * 扫描器列表
     */
    List<Scanner> scannerList = new ArrayList<>();

    /**
     * 当前扫描器阶层
     */
    int getGrade();

    /**
     * 当前扫描器优先级
     */
    int getLevel();

    /**
     * 执行当前扫描器的扫描工作
     */
    void Scanning();

    /**
     * 初始化扫描器
     */
    default void initialization(Scanner... scanners) {

    }
}
