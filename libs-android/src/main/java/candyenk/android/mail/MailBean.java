package candyenk.android.mail;

import java.net.URL;
import java.util.List;

/**
 * 邮件存储数据模型
 */
public class MailBean {
    public String nickName;//发件人签名
    public List<String> to;//收件人
    public String title;//邮件标题
    public String content;//邮件内容
    public List<URL> urls;//附件URL

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    static String userName;//邮箱登录名
    static String authPassword;//邮箱验证密码
    static String host;//SMTP服务地址
    static int port;//SMTP端口
    static boolean isSSL;//是否开启SSL

    public static void init(String userName, String auth, String host, int port) {
        MailBean.userName = userName;
        MailBean.authPassword = auth;
        MailBean.host = host;
        MailBean.port = port;
        MailBean.isSSL = port == 465;
    }

    public static void initQQ(String userName, String auth) {
        init(userName, auth, "smtp.qq.com", 465);
    }

    static void initCDK() {
        initQQ("candyenk@qq.com", "rehjtvbocvixdehj");
    }


}
