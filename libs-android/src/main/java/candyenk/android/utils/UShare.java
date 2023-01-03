package candyenk.android.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Android共享工具
 * 处理安卓应用间数据交换工具
 * <p>
 * 清单添加内容:
 * <provider
 * android:name="androidx.core.content.FileProvider"
 * android:authorities="com.candyenk.demo.FileProvider"
 * android:exported="false"
 * android:grantUriPermissions="true">
 * <meta-data
 * android:name="android.support.FILE_PROVIDER_PATHS"
 * android:resource="@xml/share_paths"/>
 * </provider>
 * <p>
 * <?xml version="1.0" encoding="utf-8"?>
 * <paths>
 * <files-path path="images/" name="myimages"/>
 * </paths>
 * <files-path/> —— Context.getFilesDir()
 * <cache-path/> —— Context.getCacheDir()
 * <external-path/> —— Environment.getExternalStorageDirectory()
 * <external-files-path/> —— Context.getExternalFilesDir(String)
 * <external-cache-path/> —— Context.getExternalCacheDir()
 * <external-media-path/> —— Context.getExternalMediaDirs()
 */
public class UShare {
    /*************************************静态变量**************************************************/
    public static final AtomicInteger ai = new AtomicInteger();//原子返回值
    /*************************************成员变量**************************************************/

    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/

    /**
     * 注册Result事件
     * ResultAPI
     */
    public static <I, O> ActivityResultLauncher<I> register(@NonNull ComponentActivity activity, ActivityResultContract<I, O> contract, ActivityResultCallback<O> callback) {
        final AtomicReference<ActivityResultLauncher<I>> launcher = new AtomicReference<>();
        ActivityResultRegistry registry = activity.getActivityResultRegistry();
        LifecycleEventObserver observer = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NotNull LifecycleOwner source, @NotNull Lifecycle.Event event) {
                if (Lifecycle.Event.ON_DESTROY == event) {
                    launcher.get().unregister();
                    source.getLifecycle().removeObserver(this);
                }
            }
        };
        activity.getLifecycle().addObserver(observer);
        launcher.set(registry.register(createKey(), contract, result -> {
            observer.onStateChanged(activity, Lifecycle.Event.ON_DESTROY);
            callback.onActivityResult(result);
        }));
        return launcher.get();
    }

    /**
     * 启动Activity
     * ResultAPI
     *
     * @param intent          启动目标和传递数据
     * @param data            启动参数
     * @param callbackFail    失败回调
     * @param callbackSuccess 成功回调
     */
    public static void startActivity(@NonNull ComponentActivity activity, @NonNull Intent intent, Bundle data, Consumer<Intent> callbackFail, Consumer<Intent> callbackSuccess) {
        register(activity, new ActivityResultContracts.StartActivityForResult(), result -> {
            if (callbackSuccess != null && result.getResultCode() == Activity.RESULT_OK)
                callbackSuccess.accept(result.getData());
            else if (callbackFail != null) callbackFail.accept(result.getData());
        }).launch(intent);
    }

    /**
     * 获取文件(只读)
     * ResultAPI
     *
     * @param type     文件类型(MIME)
     * @param callback 文件Uri
     */
    public static void readDocumentFile(@NonNull ComponentActivity activity, Consumer<Uri> callback, String... type) {
        register(activity, new ActivityResultContracts.OpenDocument() {
            @NotNull
            @Override
            public Intent createIntent(@NotNull Context context, @NotNull String[] input) {
                return super.createIntent(context, input)
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .addCategory(Intent.CATEGORY_OPENABLE);
            }
        }, callback == null ? null : callback::accept).launch(type);

    }

    /**
     * 获取多个文件(只读)
     * ResultAPI
     *
     * @param type     文件类型(MIME)
     * @param callback 文件Uri
     */
    public static void readDocumentFiles(@NonNull ComponentActivity activity, Consumer<List<Uri>> callback, String... type) {
        register(activity, new ActivityResultContracts.OpenMultipleDocuments() {
            @NotNull
            @Override
            public Intent createIntent(@NotNull Context context, @NotNull String[] input) {
                return super.createIntent(context, input)
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .addCategory(Intent.CATEGORY_OPENABLE);
            }
        }, callback == null ? null : callback::accept).launch(type);

    }

    /**
     * 获取文件(可读写)
     * ResultAPI
     *
     * @param type     文件类型(MIME)
     * @param callback 文件Uri
     */
    public static void getDocumentFile(@NonNull ComponentActivity activity, Consumer<Uri> callback, String... type) {
        register(activity, new ActivityResultContracts.OpenDocument(), callback == null ? null : callback::accept).launch(type);
    }

    /**
     * 获取多个文件(可读写)
     * ResultAPI
     *
     * @param type     文件类型(MIME)
     * @param callback 文件Uri列表
     */
    public static void getDocumentFiles(@NonNull ComponentActivity activity, String[] type, Consumer<List<Uri>> callback) {
        register(activity, new ActivityResultContracts.OpenMultipleDocuments(), callback == null ? null : callback::accept).launch(type);
    }

    /**
     * 授权文件夹所有权
     * ResultAPI
     *
     * @param from     默认指向
     * @param callback 文件夹Uri
     */
    public static void authDocuments(@NonNull ComponentActivity activity, Uri from, Consumer<Uri> callback) {
        register(activity, new ActivityResultContracts.OpenDocumentTree(), callback == null ? null : callback::accept).launch(from);
    }

    /**
     * 保存文件
     * ResultAPI
     *
     * @param type     文件类型(MIME)
     *                 不建议使用通配符,无法正确匹配后缀
     * @param fileName 文件名(包括后缀)
     * @param callback 文件Uri
     */
    public static void saveFile(@NonNull ComponentActivity activity, String type, String fileName, Consumer<Uri> callback) {
        register(activity, new ActivityResultContracts.CreateDocument(type), callback == null ? null : callback::accept).launch(fileName);
    }

    /**
     * 调用相机拍摄图片
     * ResultAPI
     *
     * @param uri      图片保存位置
     * @param callback 保存结果
     */
    public static void takePicture(@NonNull ComponentActivity activity, Uri uri, Consumer<Boolean> callback) {
        register(activity, new ActivityResultContracts.TakePicture(), callback == null ? null : callback::accept).launch(uri);
    }

    /**
     * 调用相机拍摄缩略图
     * 这个用处不大吧
     * ResultAPI
     *
     * @param callback Bitmap图像
     */
    public static void takePicturePreview(@NonNull ComponentActivity activity, Consumer<Bitmap> callback) {
        register(activity, new ActivityResultContracts.TakePicturePreview(), callback == null ? null : callback::accept).launch(null);
    }

    /**
     * 调用相机拍摄视频
     * ResultAPI
     *
     * @param uri      视频保存位置
     * @param callback 保存结果
     */
    public static void takeVedio(@NonNull ComponentActivity activity, Uri uri, Consumer<Boolean> callback) {
        register(activity, new ActivityResultContracts.CaptureVideo(), callback == null ? null : callback::accept).launch(uri);
    }

    /**
     * 选择单个媒体(只读)
     * ResultAPI
     *
     * @param type     媒体类型(MIME)(image/*或video/*或自定义)
     * @param callback 媒体Uri
     */
    public static void takeMedia(@NonNull ComponentActivity activity, @NonNull String type, Consumer<Uri> callback) {
        ActivityResultContracts.PickVisualMedia.SingleMimeType mime = new ActivityResultContracts.PickVisualMedia.SingleMimeType(type);
        PickVisualMediaRequest pmr = new PickVisualMediaRequest.Builder().setMediaType(mime).build();
        register(activity, new ActivityResultContracts.PickVisualMedia(), callback::accept).launch(pmr);
    }

    /**
     * 选择多个媒体(只读)
     * ResultAPI
     *
     * @param type     媒体类型(MIME)(image/*或video/*或自定义)
     * @param callback 媒体Uri
     */
    public static void takeMedias(@NonNull ComponentActivity activity, @NonNull String type, Consumer<List<Uri>> callback) {
        ActivityResultContracts.PickVisualMedia.SingleMimeType mime = new ActivityResultContracts.PickVisualMedia.SingleMimeType(type);
        PickVisualMediaRequest pmr = new PickVisualMediaRequest.Builder().setMediaType(mime).build();
        register(activity, new ActivityResultContracts.PickMultipleVisualMedia(), callback::accept).launch(pmr);
    }

    /**
     * 调用通讯录选择单个联系人
     *
     * @param callback 联系人Uri
     */
    public static void pickContact(@NonNull ComponentActivity activity, @NonNull Consumer<Uri> callback) {
        register(activity, new ActivityResultContracts.PickContact(), callback::accept).launch(null);
    }

    /**
     * 打开默认浏览器
     */
    public static void startBrowser(@NonNull Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * 写入剪切板(字符序列)
     */
    public static void writeClipboard(@NonNull Context context, CharSequence text) {
        ClipboardManager cm = USys.getSystemService(context, Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("label", text);
        cm.setPrimaryClip(cd);
    }

    /**
     * 读取剪切板(字符序列)
     */
    public static CharSequence readClipboard(@NonNull Context context) {
        CharSequence text = "";
        try {
            ClipboardManager cm = USys.getSystemService(context, Context.CLIPBOARD_SERVICE);
            ClipData clip = cm.getPrimaryClip();
            ClipData.Item item = clip.getItemAt(0);
            text = item.coerceToText(context);
        } catch (Exception ignored) {}
        return text;
    }

    public static ClipData.Item[] readAllClipboard(@NonNull Context context) {
        ClipboardManager cm = USys.getSystemService(context, Context.CLIPBOARD_SERVICE);
        ClipData clip = cm.getPrimaryClip();
        ClipData.Item[] items = new ClipData.Item[clip.getItemCount()];
        for (int i = 0; i < items.length; i++) items[i] = clip.getItemAt(i);
        return items;
    }
    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/
    /*** 创建key ***/
    private static String createKey() {
        return "activity_rq_for_result#" + ai.getAndIncrement();
    }

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/


}
