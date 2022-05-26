package candyenk.android.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebUtil {

    public static void initWebView(Activity activity, WebView webView) {
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null); //软件解码
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null); //硬件解码
        webView.setSaveEnabled(true); //启用保存??
        webView.setKeepScreenOn(false); //保持亮屏？
        webView.setWebChromeClient(new WebChromeClient()); // 设置setWebChromeClient对象
        webView.setWebViewClient(new WebViewClient()); //设置此方法可在WebView中打开链接，反之用浏览器打开
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            //下载监听
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);
        });

        WebSettings settings = webView.getSettings();

        settings.setAllowContentAccess(true);//是否启用内容提供器content加载内容
        settings.setLoadsImagesAutomatically(true);//是否自动加载图片
        settings.setAllowFileAccessFromFileURLs(true);//是否启用File加载内容
        settings.setDefaultTextEncodingName("UTF-8");//设置文本编码格式
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//允许不安全源的加载
        settings.setJavaScriptEnabled(true);//是否支持js脚本加载
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//是否支持js打开新窗口
        settings.setMediaPlaybackRequiresUserGesture(true); //是否需要用户手势来播放Media，默认true
        //settings.setPluginState(WebSettings.PluginState.ON);//插件相关


        settings.setSupportZoom(true);// 设置可以支持缩放
        settings.setBuiltInZoomControls(true);//是否使用内置缩放控件(默认false)
        settings.setDisplayZoomControls(false);//是否显示原生缩放控件
        settings.setUseWideViewPort(true); // //是否支持"viewport"的HTML标签或使用wide viewport
        settings.setLoadWithOverviewMode(true);//是否启用自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //是否启用自适应屏幕
        settings.setLoadsImagesAutomatically(true); //图片自动缩放 打开


        settings.setAllowFileAccess(true);//是否可以访问文件
        settings.setDatabaseEnabled(true); //是否启用数据库API

        settings.setDomStorageEnabled(true);//是否开启本地DOM存储API
        settings.setAppCacheEnabled(true);//是否开启APP缓存API
        settings.setGeolocationEnabled(true);//是否开启定位功能
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //优先使用本地缓存
        //settings.setSavePassword(true);//保存密码(不再支持)

    }

    /**
     * WebView显示字符串????有毛用?
     */
    public static void loadData(WebView webView, String content) {
        webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null); //这种写法可以正确解码
    }

}
