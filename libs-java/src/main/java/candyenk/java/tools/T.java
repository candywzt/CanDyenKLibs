package candyenk.java.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Java耗时调试工具
 */
public class T {
    private static final List<Long> timeList = new ArrayList<>();

    /**
     * 标记当前时间
     */
    public static void i() {
        timeList.add(System.currentTimeMillis());
    }

    /**
     * 打印所有标记时间
     * 打印结束是否清空标记时间
     */
    public static void s() {
        s(true);
    }

    public static void s(boolean isClear) {
        i();
        StringBuilder sb = new StringBuilder("耗时:");
        for (int i = 1; i < timeList.size(); i++) {
            if (i != 1) sb.append("-");
            sb.append(timeList.get(i) - timeList.get(i - 1)).append("ms");
        }
        System.out.println("====================");
        System.out.println(sb);
        System.out.println("====================");
        if (isClear) timeList.clear();
    }

    /**
     * 打印并再次标记
     * 打印结束是否清空标记时间再标记
     */
    public static void si() {
        si(true);
    }

    public static void si(boolean isClear) {
        s(isClear);
        i();
    }
}
