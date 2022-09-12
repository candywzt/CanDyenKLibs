package candyenk.android.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import candyenk.android.CDKApplication;
import candyenk.android.R;
import candyenk.android.handle.TextHandle;


public class CrashActivity extends CDKActivity {
    //控件声明
    private TextView reportView;
    private Button button1, button2, button3, button4;
    private Class<?> currentActivity;//崩溃前活动
    private Throwable crashReport;//奔溃内容
    private Spannable report;//奔溃日志


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //CDKApplication.finishAllActivity();
    }

    @Override
    protected void intentInit() {
        //传递初始化
        Intent intent = getIntent();
        crashReport = (Throwable) intent.getSerializableExtra("crashReport");
        currentActivity = (Class<?>) intent.getSerializableExtra("CurrentActivity");
    }

    @Override
    protected void viewInit() {
        //控件初始化
        setContentView(R.layout.activity_crash);
        button1 = findViewById(R.id.crash_button1);
        button2 = findViewById(R.id.crash_button2);
        button3 = findViewById(R.id.crash_button3);
        button4 = findViewById(R.id.crash_button4);
        reportView = findViewById(R.id.crash_report_text);
        setTitleText(R.string.crash_title_text);
    }

    @Override
    protected void contentInit(Bundle savedInstanceState) {
        //内容初始化
        report = getReport();
        reportView.setText(report);
    }

    @Override
    protected void eventInit() {
        //事件初始化
        button1.setOnClickListener(v -> {
            /*
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //与平时Activity页面跳转一样可传递序列化数据,在Launch页面内获得
            intent.putExtra("REBOOT", "reboot");
            startActivity(intent);
             */
            CDKApplication.restartApplication(this);
        });
        button2.setOnClickListener(v -> {
            /*
            CDKApplication.finishAllActivity();
             */
            CDKApplication.closeApplication(this);
        });
        button3.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText(getString(R.string.crash_title_text), report);
            cm.setPrimaryClip(mClipData);
            Toast.makeText(this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
        });
        button4.setOnClickListener(v -> {
            /*
            new DialogBottom(this, v)
                    .setTitle("反馈")
                    .setContent(R.layout.view_webview)
                    .setContentViewController(v1 -> {
                        WebView web = v1.findViewById(R.id.view_web);
                        web.loadUrl("https://wj.qq.com/s2/9654530/5215/");
                        UWeb.initWebView(this, web);
                        web.setWebChromeClient(new WebChromeClient() {
                            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                                return true;
                            }
                        });
                    })
                    .show();
             */
        });
    }

    @Override
    protected Bundle saveData(Bundle bundle) {
        return bundle;
    }


    /**********************************************************************************************/
    //拼接异常日志展示内容
    private Spannable getReport() {
        SpannableStringBuilder report = new SpannableStringBuilder();
        report.append("Version:");
        report.append("版本名" + "(" + "版本号" + ")\n");
        report.append("Android:");
        report.append(android.os.Build.VERSION.RELEASE + "(" + android.os.Build.MODEL + ")\n");
        if (currentActivity == null) {
            report.append("崩溃活动:未知\n");
        } else {
            report.append("崩溃活动:" + currentActivity + "\n");
        }

        if (crashReport == null) {
            report.append(getString(R.string.crash_error));
        } else {
            report.append(getCrashReport(crashReport));
        }
        return report;
    }

    /**
     * 获取APP崩溃异常报告
     */
    private Spannable getCrashReport(Throwable ex) {
        SpannableStringBuilder exceptionStr = new SpannableStringBuilder();
        exceptionStr.append(ex.getClass() + ":")
                .append(TextHandle.setTextColor(ex.getMessage(), getResources().getColor(R.color.red)) + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for (StackTraceElement ste : elements) {
            exceptionStr.append(ste.toString() + "\n");
        }
        return exceptionStr;


    }

}

