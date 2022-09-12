package candyenk.android.mail;

import android.app.Activity;
import android.util.Log;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


public class SMTP {
    private static final String TAG = SMTP.class.getSimpleName();
    private final Activity activity;
    private final MailBean mail;
    private String errorMsg;
    private MailSendCallBack callBack;

    public SMTP(Activity activity) {
        this.activity = activity;
        this.mail = new MailBean();
    }

    public SMTP CDK() {
        MailBean.initCDK();
        return this;
    }

    /**
     * 设置发送者昵称
     *
     * @param nickName 昵称
     */
    public SMTP setNickName(String nickName) {
        mail.nickName = nickName;
        return this;
    }

    /**
     * 添加接收方邮件地址
     *
     * @param address 收件人
     */
    public SMTP addToAddr(String... address) {
        for (String s : address) {
            if (s.isEmpty()) continue;
            if (mail.to == null) mail.to = new ArrayList<>();
            mail.to.add(s);
        }
        return this;
    }

    /**
     * 设置邮件标题
     *
     * @param title 标题文字
     */
    public SMTP setTitle(String title) {
        this.mail.title = title;
        return this;
    }

    /**
     * 添加邮件内容
     *
     * @param content 内容(HTNL格式)
     */
    public SMTP setContent(String content) {
        mail.content = content;
        return this;
    }

    /**
     * 添加附件
     *
     * @param url 附件URL
     */
    public SMTP addAttachment(URL... url) {
        for (URL u : url) {
            if (u == null) continue;
            if (mail.urls == null) mail.urls = new ArrayList<>();
            mail.urls.add(u);
        }
        return this;
    }

    /**
     * 设置回调事件
     *
     * @param callBack 回调事件
     */
    public SMTP setCallback(MailSendCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    /**
     * 设置MailBean对象
     *
     * @param mailBean 自行创建的Bean
     */
    public SMTP setMailBean(MailBean mailBean) {
        this.mail.nickName = mailBean.nickName;
        this.mail.to = mailBean.to;
        this.mail.title = mailBean.title;
        this.mail.content = mailBean.content;
        this.mail.urls = mailBean.urls;
        return this;
    }

    /**
     * 执行发送操作
     */
    public void send() {
        new Thread(() -> {
            boolean b = doSend();
            activity.runOnUiThread(() -> {
                doClose(b);
            });
        }).start();
    }


    /**
     * 发送邮件
     */
    private boolean doSend() {
        try {
            MimeMessage mimeMessage = new MimeMessage(createSession());
            mimeMessage.getSession().setDebug(true);
            mimeMessage.setFrom(createFrom());
            mimeMessage.addRecipients(Message.RecipientType.TO, createAddress());
            mimeMessage.setSubject(mail.title);
            mimeMessage.setSentDate(new Date());
            mimeMessage.setContent(createMultipart());
            Log.e("啦啦啦",MailBean.authPassword);
            Transport.send(mimeMessage, MailBean.userName, MailBean.authPassword);

        } catch (Exception e) {
            errorMsg = e.toString();
            e.printStackTrace();
            return true;
        }
        return false;
    }

    private Properties createProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", MailBean.host);
        if (MailBean.isSSL) {
            props.put("mail.smtp.socketFactory.port", MailBean.port);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", MailBean.port);
        }
        return props;
    }

    private Session createSession() {
        return Session.getInstance(createProperties(), !MailBean.isSSL ? null : new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailBean.userName, MailBean.authPassword);
            }
        });
    }

    private InternetAddress createFrom() throws UnsupportedEncodingException {
        return new InternetAddress(MailBean.userName, mail.nickName, "UTF-8");
    }

    private InternetAddress[] createAddress() throws AddressException {
        List<String> toList = mail.to;
        if (toList == null) toList = new ArrayList();
        InternetAddress[] addrs = new InternetAddress[toList.size()];
        for (int i = 0; i < toList.size(); i++) {
            addrs[i] = new InternetAddress(toList.get(i));
        }
        return addrs;
    }

    private Multipart createMultipart() throws MessagingException, UnsupportedEncodingException {
        Multipart multipart = new MimeMultipart();

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(mail.content, "text/html;charset=utf-8");
        multipart.addBodyPart(mimeBodyPart);
        List<URL> urlList = mail.urls;
        if (urlList == null) urlList = new ArrayList();
        for (URL url : urlList) {
            MimeBodyPart part = new MimeBodyPart();
            String protocol = url.getProtocol().toLowerCase();
            DataSource dataSource = protocol.startsWith("http") ?
                    new URLDataSource(url) : protocol.startsWith("file") ?
                    new FileDataSource(url.getPath()) : null;
            if (dataSource == null) continue;
            part.setDataHandler(new DataHandler(dataSource));
            part.setFileName(MimeUtility.encodeText(dataSource.getName()));
            multipart.addBodyPart(part);
        }
        return multipart;
    }

    /**
     * 发送结束
     */
    private void doClose(boolean isError) {
        if (isError && callBack != null) {
            callBack.onSendFailed(errorMsg);
        } else if (!isError && callBack != null) {
            callBack.onSendSeccond();
        }
    }

    public interface MailSendCallBack {
        void onSendSeccond();

        void onSendFailed(String errorMsg);
    }
}
