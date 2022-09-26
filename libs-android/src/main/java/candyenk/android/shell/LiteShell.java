package candyenk.android.shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * 便捷的单条命令执行器
 */
public class LiteShell {
    private static final String TAG = LiteShell.class.getSimpleName();
    private String startCmd = "sh";
    private Process p;
    private boolean isOK;
    private String result;


    /*** 获取用户权限Shell ***/

    public static LiteShell sh() {
        return new LiteShell();
    }

    /*** 获取Root权限Shell ***/
    public static LiteShell su() {
        LiteShell ls = sh();
        ls.startCmd = "su";
        return ls;
    }

    /*** 执行命令 ***/
    public LiteShell exec(String... cmd) {
        try {
            p = Runtime.getRuntime().exec(startCmd);
            PrintStream ps = new PrintStream(p.getOutputStream());
            for (String s : cmd) ps.println(s);
            ps.flush();
            ps.close();
            isOK = p.waitFor() == 0;
        } catch (Exception e) {
        }
        return this;
    }

    /*** 执行需要的命令 ***/
    public LiteShell execResult(String cmd) {
        exec(cmd);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader be = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        StringBuilder sb = new StringBuilder();
        try {
            String line = br.readLine();
            if (br == null) line = be.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
                if (br == null) line = be.readLine();
            }
            br.close();
            be.close();
        } catch (Exception e) {

        }
        result = sb.toString();
        return this;
    }

    /*** 获取是否执行成功 ***/
    /*** 需返回命令执行失败会返回错误信息 ***/
    public boolean isok() {
        return isOK;
    }

    /*** 获取执行结果 ***/
    public String result() {
        return result;
    }

    private LiteShell() {
    }

}
