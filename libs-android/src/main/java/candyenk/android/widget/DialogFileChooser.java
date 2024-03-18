package candyenk.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.R;
import candyenk.android.asbc.AdapterRVCDK;
import candyenk.android.asbc.HolderCDK;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import candyenk.java.io.FileInfo;
import candyenk.java.utils.UArrays;
import candyenk.java.utils.UData;
import candyenk.java.utils.UTime;
import com.google.android.material.textview.MaterialTextView;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
/*** Android 媒体文件读取权限 READ_EXTERNAL_STORAGE ***/
/*** Android 外部存储写入权限 WRITE_EXTERNAL_STORAGE ***/
/*** Android 外部存储管理权限 MANAGE_EXTERNAL_STORAGE ***/

/**
 * 文件选择器上拉弹窗
 * 同时只能存在一个弹窗
 * 没有动画,遗憾
 */
public class DialogFileChooser extends DialogBottomRV {
    /*************************************成员变量**************************************************/
    private final FileAdapter adapter;//文件适配器
    private Context lastSign;//重复标记
    private CharSequence defaultTitle;//默认标题


    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    /**
     * 以指定的File对象为父级展示子文件
     */
    public DialogFileChooser(Context context, File rootFile) {
        this(context, FileInfo.create(rootFile));
    }

    /**
     * 以指定的FileInfo对象为父级展示子文件
     */
    public DialogFileChooser(Context context, FileInfo rootInfo) {
        super(context);
        this.adapter = new FileAdapter(listView);
        super.ok = checkSign();
        if (!ok) return;
        updateDialog(rootInfo);
        initAdapter();
    }

    /**
     * 展示指定的自定义FileInfo对象组
     */
    public DialogFileChooser(Context context, FileInfo... infos) {
        super(context);
        this.adapter = new FileAdapter(listView);
        super.ok = checkSign();
        if (!ok) return;
        updateDialog(infos);
        initAdapter();
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setTitleCenter(boolean isCenter) {
        L.e(TAG, "不支持的操作" + TAG + ".setTitleCenter(boolean)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setShowClose(boolean isShow) {
        L.e(TAG, "不支持的操作" + TAG + ".setShowClose(boolean)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setContent(AdapterRVCDK<? extends HolderCDK> adapter) {
        L.e(TAG, "不支持的操作" + TAG + ".setContent(RVAdapterCDK)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        L.e(TAG, "不支持的操作" + TAG + ".setLayoutManager(RecyclerView.LayoutManager)");
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置文件排序方式
     * 当文件1要排在文件2的前面时返回-1,排在后面返回1,相等返回0
     * 自定义列表无效
     */
    public void setSortOrder(Comparator<FileInfo> sortOrder) {
        if (ok) adapter.setSortOrder(sortOrder);
    }

    /**
     * 设置文件过滤方式
     * 返回false表示该文件被剔除,返回true表示改文件被保留
     * 如果设置不显示隐藏文件,那么隐藏文件不会出现在过滤列表中
     * 自定义列表无效
     */
    public void setFilter(Predicate<FileInfo> filter) {
        if (ok) adapter.setFilter(filter);
    }

    /**
     * 设置是否显示隐藏文件
     * 自定义列表无效
     */
    public void setShowHideFile(boolean isShowHide) {
        if (ok) adapter.setShowHideFile(isShowHide);
    }

    /**
     * 更新Dialog数据
     */
    public void updateDialog(FileInfo rootInfo) {
        if (!ok) return;
        setTitle(rootInfo.getPath());
        adapter.updateDialog(rootInfo);
        updateAdapter();

    }

    /**
     * 更新自定义Dialog数据
     */
    public void updateDialog(FileInfo... infos) {
        if (!ok) return;
        setTitle(defaultTitle);
        updateAdapter();
        adapter.updateDialog(infos);
    }

    /**
     * 设置文件夹点击监听器
     * 默认:打开文件夹
     */
    public void setOnFolderClickListener(BiConsumer<FileInfo, View> onClickListener) {
        adapter.folderClick = onClickListener;
    }

    /**
     * 设置文件夹长按监听器
     * 默认:无
     */
    public void setOnFolderLongClickListener(BiConsumer<FileInfo, View> onLongClickListener) {
        adapter.folderLong = onLongClickListener;
    }

    /**
     * 设置文件点击监听器
     * 默认:无
     */
    public void setOnFileClickListener(BiConsumer<FileInfo, View> onClickListener) {
        adapter.fileClick = onClickListener;
    }

    /**
     * 设置文件长按监听器
     * 默认:无
     */
    public void setOnFileLongClickListener(BiConsumer<FileInfo, View> onLongClickListener) {
        adapter.fileLong = onLongClickListener;
    }


    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 初始化Adapter ***/
    private void initAdapter() {
        super.setContent(adapter);
        this.defaultTitle = "选择文件";
        setTitle(defaultTitle);
        adapter.emptyClick = (f, v) -> {
            Popup p = new Popup(v);
            p.setContent("什么都没有哦!");
            p.setLocation(Popup.CENTER, Popup.CENTER);
            p.show();
        };
        adapter.emptyLong = (f, v) -> {};
        adapter.superClick = (f, v) -> updateDialog(adapter.getRootInfo().getParent());
        adapter.superLong = (f, v) -> {};
        adapter.folderClick = (f, v) -> updateDialog(f);
        adapter.folderLong = (f, v) -> {};
        adapter.fileClick = (f, v) -> {};
        adapter.fileLong = (f, v) -> {};

    }

    /*** 更新 Adapter 数据 ***/
    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter() {
        adapter.notifyDataSetChanged();
    }


    /*** 检查是否合法 ***/
    private boolean checkSign() {
        if (lastSign == null || this.context != lastSign) {
            lastSign = this.context;
            return true;
        } else return false;
    }
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
    /*** 文件配器类 ***/
    public static class FileAdapter extends AdapterRVCDK<FileHolder> {
        private final RecyclerView listView;
        private final Context context;
        private final List<FileInfo> infoList = new ArrayList<>();//当前展示Info列表
        private FileInfo rootInfo;//当前展示Info对象
        private Comparator<FileInfo> sortOrder;//排序方式
        private Predicate<FileInfo> filter;//过滤方式
        private boolean isShowHide;//是否显示隐藏文件
        private BiConsumer<FileInfo, View> emptyClick, emptyLong, superClick, superLong, folderClick, folderLong, fileClick, fileLong;

        private FileAdapter(RecyclerView view) {
            this.listView = view;
            this.context = view.getContext();
        }

        @Override
        public FileHolder onCreate(ViewGroup parent, int type) {
            FileHolder h = new FileHolder(createLayout(type), type);

            setOnClickListener(listView, (Consumer<FileHolder>) fh -> {
                FileInfo f = infoList.get(fh.getAdapterPosition());
                View v = fh.itemView;
                switch (fh.type) {
                    case 0: emptyClick.accept(f, v); break;
                    case 1: superClick.accept(f, v); break;
                    case 2: folderClick.accept(f, v); break;
                    case 3: fileClick.accept(f, v); break;
                }
            });
            setOnLongClickListener(listView, (Consumer<FileHolder>) fh -> {
                FileInfo f = infoList.get(fh.getAdapterPosition());
                View v = fh.itemView;
                switch (fh.type) {
                    case 0: emptyLong.accept(f, v); break;
                    case 1: superLong.accept(f, v); break;
                    case 2: folderLong.accept(f, v); break;
                    case 3: fileLong.accept(f, v); break;
                }
            });
            return h;
        }

        @Override
        public void onBind(@NotNull FileHolder h, int p) {
            h.setContent(infoList.get(p));
        }

        @Override
        public int getCount() {
            return infoList.size();
        }

        /**
         * 当前项目类型
         * 0:空文件夹
         * 1:上级文件夹
         * 2:文件夹
         * 3:文件
         */
        @Override
        public int getType(int p) {
            FileInfo info = infoList.get(p);
            if (info == FileInfo.emptyInfo) {
                return 0;
            } else if (info == FileInfo.superInfo) {
                return 1;
            } else if (info.isDirectory()) {
                return 2;
            } else {
                return 3;
            }
        }

        /*** 获取当前FileInfo ***/
        private FileInfo getRootInfo() {
            return this.rootInfo;
        }

        /***创建文件项目布局***/
        private LinearLayout createLayout(int type) {
            if (type != 0) {
                Item item = new Item(context);
                V.RL(item).size(-1, -2).refresh();
                return item;
            } else {
                LinearLayout l1 = new LinearLayout(context);
                V.RV(l1).size(-1, -2).orientation(1).gravity(Gravity.CENTER_HORIZONTAL).backgroundRes(R.drawable.bg_cdk).refresh();

                ImageView iv = new ImageView(context);
                V.LL(iv).sizeDP(120).paddingDP(8).scaleType(ImageView.ScaleType.CENTER_CROP).drawable(R.drawable.ic_file_unknown).parent(l1);

                TextView tv = new MaterialTextView(context);
                V.LL(tv).size(-2).paddingDP(0, 0, 0, 40).textSize(16).textColorRes(R.color.text_main).text(R.string.file_chooser_no_file).parent(l1);
                return l1;
            }
        }

        /*** 设置排序方式 ***/
        public void setSortOrder(Comparator<FileInfo> sortOrder) {
            if (rootInfo == null) return;
            this.sortOrder = sortOrder;
            updateDialog(rootInfo);
        }

        /**
         * 设置文件过滤方式
         * 返回false表示该文件被剔除,返回true表示改文件被保留
         * 如果设置不显示隐藏文件,那么隐藏文件不会出现在过滤列表中
         * 自定义列表无效
         */
        public void setFilter(Predicate<FileInfo> filter) {
            if (rootInfo == null) return;
            this.filter = filter;
            updateDialog(rootInfo);
        }

        /**
         * 设置是否显示隐藏文件(默认false)
         * 自定义列表无效
         */
        public void setShowHideFile(boolean isShowHide) {
            if (rootInfo == null) return;
            this.isShowHide = isShowHide;
            updateDialog(rootInfo);
        }

        /**
         * 更新Dialog数据
         */
        public void updateDialog(FileInfo rootInfo) {
            if (rootInfo == null ||rootInfo.isCustom() ||  rootInfo.getFile() == null)
                throw new NullPointerException("File地址不能为不存在");
            this.rootInfo = rootInfo;
            infoList.clear();
            FileInfo[] list = rootInfo.listInfos(this.isShowHide, true);
            if (list.length > 1) {
                UArrays.add(infoList, list, f -> {
                    if (f.equals(FileInfo.superInfo) || f.equals(FileInfo.emptyInfo)) return f;
                    if (!isShowHide && f.isHide()) return null;
                    if (filter != null && !filter.test(f)) return null;
                    return f;
                });
                if (sortOrder == null) Collections.sort(infoList);
                else infoList.sort((o1, o2) -> {
                    if (o1.equals(FileInfo.superInfo)) return -1;
                    if (o2.equals(FileInfo.superInfo)) return 1;
                    return sortOrder.compare(o1, o2);
                });
            } else UArrays.add(infoList, list, null);
        }

        /**
         * 更新自定义Dialog数据
         */
        public void updateDialog(FileInfo... infos) {
            if (infos == null || infos.length == 0)
                throw new NullPointerException("Files地址不能不存在");
            this.rootInfo = null;
            this.infoList.clear();
            UArrays.add(infoList, infos, null);
        }
    }

    /*** File项目视图 ***/
    private static class FileHolder extends HolderCDK {
        private Item item;
        private final int type;

        public FileHolder(View itemView, int type) {
            super(itemView);
            this.type = type;
            if (type == 0) return;
            this.item = (Item) itemView;
            item.getSummaryView().setVisibility(type == 1 ? View.GONE : View.VISIBLE);
            item.getArrowView().setVisibility(type == 3 ? View.GONE : View.VISIBLE);
        }

        /*** 设置内容显示 ***/
        private void setContent(FileInfo info) {
            if (type == 0) return;
            item.setIconResource(iconType(info));
            item.setTitleText(info.getName());
            if (type != 1) item.setSummaryText(getDescribe(info));
        }

        /**
         * 获取对应的图标资源值
         */
        private int iconType(FileInfo info) {
            switch (info.getType()) {
                case DIRECTORY: return R.drawable.ic_file_folder;
                case TEXT: return R.drawable.ic_file_document;
                case IMAGE: return R.drawable.ic_file_picture;
                case VIDEO: return R.drawable.ic_file_video;
                case COMPRESS: return R.drawable.ic_file_zip;
                case AUDIO: return R.drawable.ic_file_audio;
                default: return R.drawable.ic_file_unknown;
            }
        }

        /*** 获取文件描述内容 ***/
        @SuppressLint("StringFormatInvalid")
        private String getDescribe(FileInfo fileInfo) {
            StringBuilder sb = new StringBuilder();
            if (fileInfo.isCustom()) return fileInfo.getPath();
            if (type == 3) {
                String s = context.getString(R.string.file_chooser_filesize);
                sb.append(String.format(s, UData.B2A(fileInfo.getSize()))).append("\t");
            }
            String s = context.getString(R.string.file_chooser_lmd);
            sb.append(String.format(s, UTime.D2S(fileInfo.getLmd())));
            return sb.toString();
        }
    }
}
