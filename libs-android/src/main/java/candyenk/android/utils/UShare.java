package candyenk.android.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
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
 * <files-path path="images/" name="my_images"/>
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
     * 启动ResultAPI
     * ResultAPI
     *
     * @param intent   启动目标和传递数据
     * @param data     启动参数
     * @param callback 回调
     */
    public static void start(@NonNull ComponentActivity activity, @NonNull Intent intent, Bundle data, @NonNull BiConsumer<Boolean, Intent> callback) {
        register(activity, new ActivityResultContracts.StartActivityForResult(), result -> callback.accept(result.getResultCode() == Activity.RESULT_OK, result.getData())).launch(intent);
    }

    /**
     * 获取媒体文件(使用多媒体提供器)
     * ResultAPI
     *
     * @param type     文件类型(MIME)
     *                 API(TIRAMISU)可传入null以同时选择image和video
     * @param callback 文件Uri(或null)
     */
    public static void getMediaFile(@NonNull ComponentActivity activity, @NonNull Consumer<Uri> callback, @NonNull String type) {
        register(activity, new ActivityResultContracts.GetContent() {
            @NotNull
            @Override
            public Intent createIntent(@NotNull Context context, @NotNull String input) {
                Intent intent = super.createIntent(context, input);
                if (USDK.T()) intent.setAction(MediaStore.ACTION_PICK_IMAGES);
                intent.removeCategory(Intent.CATEGORY_OPENABLE);
                return intent;
            }
        }, callback::accept).launch(type);
    }


    /**
     * 获取多个媒体文件(使用多媒体提供器)
     * ResultAPI
     *
     * @param type     文件类型(MIME)
     * @param callback 文件Uri(或空List)
     */
    public static void getMediaFiles(@NonNull ComponentActivity activity, @NonNull Consumer<List<Uri>> callback, String type) {
        if (USDK.T()) getMediaFiles(activity, callback, type, 0);
        else {
            register(activity, new ActivityResultContracts.GetMultipleContents() {
                @NotNull
                @Override
                public Intent createIntent(@NotNull Context context, @NotNull String input) {
                    Intent intent = super.createIntent(context, input);
                    intent.removeCategory(Intent.CATEGORY_OPENABLE);
                    return intent;
                }
            }, callback::accept).launch(type);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void getMediaFiles(@NonNull ComponentActivity activity, @NonNull Consumer<List<Uri>> callback, @NonNull String type, int maxItems) {
        register(activity, new ActivityResultContracts.GetMultipleContents() {
            @NotNull
            @Override
            public Intent createIntent(@NotNull Context context, @NotNull String input) {
                Intent intent = super.createIntent(context, input);
                intent.setAction(MediaStore.ACTION_PICK_IMAGES);
                intent.removeCategory(Intent.CATEGORY_OPENABLE);
                if (maxItems > 0) intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxItems);
                return intent;
            }
        }, callback::accept).launch(type);
    }

    /**
     * 获取文件(系统文件提供器)
     * ResultAPI
     *
     * @param type     文件类型(MIME)
     * @param callback 文件Uri(或null)
     */
    public static void getDocumentFile(@NonNull ComponentActivity activity, @NonNull Consumer<Uri> callback, String... type) {
        register(activity, new ActivityResultContracts.OpenDocument(), callback::accept).launch(type);
    }

    /**
     * 获取多个文件(系统文件提供器)
     * ResultAPI
     *
     * @param type     文件类型(MIME)
     * @param callback 文件Uri列表(或空list)
     */
    public static void getDocumentFiles(@NonNull ComponentActivity
                                                activity, @NonNull Consumer<List<Uri>> callback, String... type) {
        register(activity, new ActivityResultContracts.OpenMultipleDocuments(), callback::accept).launch(type);
    }

    /**
     * 授权文件夹所有权
     * ResultAPI
     *
     * @param from     默认指向
     * @param callback 文件夹Uri(或null)
     */
    public static void authDocuments(@NonNull ComponentActivity activity, @NonNull Consumer<Uri> callback, Uri from) {
        register(activity, new ActivityResultContracts.OpenDocumentTree(), callback::accept).launch(from);
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
    public static void saveFile(@NonNull ComponentActivity activity, @NonNull Consumer<Uri> callback, String type, String fileName) {
        register(activity, new ActivityResultContracts.CreateDocument(type), callback::accept).launch(fileName);
    }

    /**
     * 调用相机拍摄图片
     * ResultAPI
     *
     * @param uri      图片保存位置
     * @param callback 保存结果
     */
    public static void takePicture(@NonNull ComponentActivity activity, Consumer<Boolean> callback, Uri uri) {
        register(activity, new ActivityResultContracts.TakePicture(), callback == null ? null : callback::accept).launch(uri);
    }

    /**
     * 调用相机拍摄缩略图
     * 这个用处不大吧
     * ResultAPI
     *
     * @param callback Bitmap图像
     */
    public static void takePicturePreview(@NonNull ComponentActivity activity, @NonNull Consumer<Bitmap> callback) {
        register(activity, new ActivityResultContracts.TakePicturePreview(), callback::accept).launch(null);
    }

    /**
     * 调用相机拍摄视频
     * ResultAPI
     *
     * @param uri      视频保存位置
     * @param callback 保存结果
     */
    public static void takeVideo(@NonNull ComponentActivity activity, Consumer<Boolean> callback, Uri uri) {
        register(activity, new ActivityResultContracts.CaptureVideo(), callback == null ? null : callback::accept).launch(uri);
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
        return "activity_rq#" + ai.getAndIncrement();
    }

}
