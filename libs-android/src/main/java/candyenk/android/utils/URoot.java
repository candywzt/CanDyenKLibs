package candyenk.android.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Android Root命令工具
 * 不是很好使
 */
public class URoot {
    /**
     * 当前应用获取Root
     *
     * @return 不是获取成功的意思
     */
    public static boolean getRoot() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            //TODO:异常处理
            Log.e("ROOT", "Root获取失败" + e.getMessage());
            return false;
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                //TODO:异常处理
                Log.e("ROOT", "OS关闭失败?" + e.getMessage());
            }
            process.destroy();
        }
    }

    /**
     * 检查当前应用有没有root
     *
     * @return 不是很准
     */
    public static boolean checkRoot() {
        String binPath = "/system/bin/su";
        String xBinPath = "/system/xbin/su";
        if (new File(binPath).exists() && isExecutable(binPath))
            return true;
        if (new File(xBinPath).exists() && isExecutable(xBinPath))
            return true;
        return false;
    }

    /**
     * 执行单条Root命令
     *
     * @param cmd root命令
     * @return
     */
    public static boolean executeRoot(String... cmd) {
        Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            for (String str : cmd) {
                os.writeBytes(str + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            return true;
        } catch (InterruptedException e) {
            //TODO:异常处理
            Log.e("ROOT", "等待超时" + e.getMessage());
            return false;
        } catch (IOException e) {
            //TODO:异常处理
            Log.e("ROOT", "写入失败" + e.getMessage());
            return false;
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                //TODO:异常处理
                Log.e("ROOT", "OS关闭失败?" + e.getMessage());
            }
            process.destroy();

        }
    }

    /**
     * 执行Root命令集
     *
     * @param cmd 命令集
     * @return
     */
    public static String executeRootCmd(String... cmd) {
        DataOutputStream dos = null;
        DataInputStream dis = null;
        StringBuffer sb = new StringBuffer();
        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            for (String cmdd : cmd) {
                Log.e("命令执行", cmdd);
                dos.writeBytes(cmdd + "\n");
                dos.flush();

            }
            dos.writeBytes("exit\n");
            dos.flush();
            String line;
            while ((line = dis.readLine()) != null) {
                sb.append(line + "\n");
            }
            p.waitFor();
        } catch (Exception e) {
            //TODO:异常处理
            e.printStackTrace();
        } finally {
            try {
                dos.close();
                dis.close();
            } catch (IOException e) {
                //TODO:异常处理
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    /**********************************************************************************************/
    /*******************************************私有方法********************************************/
    /**********************************************************************************************/
    private static boolean isExecutable(String filePath) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ls -l " + filePath);
            // 获取返回内容
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String str = in.readLine();
            Log.i("ROOT", "内容" + str);
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            //TODO:异常处理
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return false;
    }

}
