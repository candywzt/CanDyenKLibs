package candyenk.android.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.R;
import candyenk.android.tools.V;
import candyenk.java.io.FileInfo;
import candyenk.java.utils.UArrays;
import candyenk.java.utils.UData;
import candyenk.java.utils.UTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
/*** Android 媒体文件读取权限 READ_EXTERNAL_STORAGE ***/
/*** Android 外部存储写入权限 WRITE_EXTERNAL_STORAGE ***/
/*** Android 外部存储管理权限 MANAGE_EXTERNAL_STORAGE ***/

/**
 * 文件选择器上拉弹窗
 * 没有动画,遗憾
 */
public class DialogFileChooser extends DialogBottom {

    /*************************************静态变量**************************************************/
    private final List<FileInfo> infoList = new ArrayList<>();//当前展示Info列表
    private final FileAdapter adapter = new FileAdapter();//文件适配器

    /*************************************成员变量**************************************************/
    private FileInfo rootInfo;//当前展示File对象
    private Comparator<FileInfo> sortOrder;//排序方式
    private Predicate<FileInfo> filter;//过滤方式
    private boolean isShowHide;//是否显示隐藏文件

    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/

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
        updateDialog(rootInfo);
        setContent(adapter);
        setTitle("选择文件");
    }

    /**
     * 展示指定的自定义FileInfo对象组
     */
    public DialogFileChooser(Context context, FileInfo... infos) {
        super(context);
        updateDialog(infos);
        setContent(adapter);
        setTitle("选择文件");
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    /**
     * 设置标题显示内容
     * 调整为显示后也能修改标题
     */
    @Override
    public void setTitle(CharSequence title) {
        titleView.setText(title);
        titleView.setVisibility(View.VISIBLE);
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
        if (this.rootInfo == null) return;
        this.sortOrder = sortOrder;
        updateDialog(this.rootInfo);
    }

    /**
     * 设置文件过滤方式
     * 返回false表示该文件被剔除,返回true表示改文件被保留
     * 如果设置不显示隐藏文件,那么隐藏文件不会出现在过滤列表中
     * 自定义列表无效
     */
    public void setFilter(Predicate<FileInfo> filter) {
        if (this.rootInfo == null) return;
        this.filter = filter;
        updateDialog(this.rootInfo);
    }

    /**
     * 设置是否显示隐藏文件
     * 自定义列表无效
     */
    public void setShowHideFile(boolean isShowHide) {
        if (this.rootInfo == null) return;
        this.isShowHide = isShowHide;
        updateDialog(this.rootInfo);
    }

    /**
     * 更新Dialog数据
     */
    public void updateDialog(FileInfo rootInfo) {
        if (rootInfo.isCustom() || rootInfo == null || rootInfo.getFile() == null)
            throw new NullPointerException("File地址不能为不存在");
        this.rootInfo = rootInfo;
        updateInfoList(rootInfo);
        if (titleView.getVisibility() == View.VISIBLE) setTitle(rootInfo.getPath());
        if (isShow) updateAdapter();
    }

    /**
     * 更新自定义Dialog数据
     */
    public void updateDialog(FileInfo... infos) {
        if (infos == null || infos.length == 0)
            throw new NullPointerException("Files地址不能不存在");
        this.rootInfo = null;
        updateInfoList(infos);
        if (titleView.getVisibility() == View.VISIBLE) setTitle("选择文件");
        if (isShow) updateAdapter();
    }

    /**
     * 设置选择点击监听器
     */
    public void setOnItemClickListener(BiConsumer<FileInfo, View> onClickListener) {
        adapter.onClickListener = onClickListener;
    }

    /**
     * 设置选择长按监听器
     */
    public void setOnItemLongClickListener(BiConsumer<FileInfo, View> onLongClickListener) {
        adapter.onLongClickListener = onLongClickListener;
    }

    /**
     * 设置上级文件夹默认事件
     */
    public void setSuperDefault(View.OnClickListener l) {
        this.adapter.superOnClick = l;
    }

    /**
     * 设置空文件夹默认事件
     */
    public void setEmptyDefault(View.OnClickListener l) {
        this.adapter.emptyOnClick = l;
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/

    /*** 更新 Adapter 数据 ***/
    private void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

    /*** 更新数据列表***/
    private void updateInfoList(FileInfo info) {
        infoList.clear();
        FileInfo[] list = info.listInfos(this.isShowHide, true);
        if (list.length > 1) {
            UArrays.addArrays(infoList, list, f -> {
                if (f.equals(FileInfo.superInfo) || f.equals(FileInfo.emptyInfo)) return f;
                if (!isShowHide && f.isHide()) return null;
                if (filter != null && !filter.test(f)) return null;
                return f;
            });
            if (sortOrder == null) Collections.sort(infoList);
            else Collections.sort(infoList, (o1, o2) -> {
                if (o1.equals(FileInfo.superInfo)) return -1;
                if (o2.equals(FileInfo.superInfo)) return 1;
                return sortOrder.compare(o1, o2);
            });
        } else UArrays.addArrays(infoList, list, null);
    }

    /*** 更新自定义数据列表***/
    private void updateInfoList(FileInfo[] infos) {
        infoList.clear();
        UArrays.addArrays(infoList, infos, null);
    }
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
    /*** 私有内部适文件配器类 ***/
    private class FileAdapter extends RecyclerView.Adapter<FileHolder> {
        private BiConsumer<FileInfo, View> onClickListener;//选项点击监听
        private BiConsumer<FileInfo, View> onLongClickListener;//选项长按监听
        private View.OnClickListener superOnClick;//上级文件夹点击事件
        private View.OnClickListener emptyOnClick;//空文件夹点击事件

        @Override
        public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new FileHolder(createEmptyLayout());
            } else {
                return new FileHolder(createFileLayout());
            }
        }

        @Override
        public void onBindViewHolder(FileHolder holder, int position) {
            FileInfo info = infoList.get(position);
            switch (getItemViewType(position)) {
                case 0: //空文件夹
                    if (emptyOnClick == null) holder.setEmptyDirectory();
                    else holder.setOnClick(emptyOnClick);
                    break;
                case 1://返回上一级条目
                    holder.title.setText(info.getName());
                    holder.summary.setVisibility(View.GONE);
                    if (superOnClick == null) holder.setUpdateDialog(rootInfo.getParent());
                    else holder.setOnClick(superOnClick);
                    break;
                case 2://文件夹
                    holder.title.setText(info.getName());
                    holder.summary.setText(getDirectoryDescribe(info));
                    if (onClickListener == null) holder.setUpdateDialog(info);
                    else holder.setOnClick(v -> onClickListener.accept(info, holder.itemView));
                    if (onLongClickListener != null)
                        holder.setOnLongClick(v -> {
                            onLongClickListener.accept(info, holder.itemView);
                            return true;
                        });
                    break;
                case 3://文件
                    holder.icon.setImageResource(iconType(info));
                    holder.title.setText(info.getName());
                    holder.summary.setText(getFileDescribe(info));
                    holder.arrow.setVisibility(View.GONE);
                    if (onClickListener == null) holder.setFileContent(info);
                    else holder.setOnClick(v -> onClickListener.accept(info, holder.itemView));
                    if (onLongClickListener != null)
                        holder.setOnLongClick(v -> {
                            onLongClickListener.accept(info, holder.itemView);
                            return true;
                        });
                    break;
            }
        }

        @Override
        public int getItemCount() {
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
        public int getItemViewType(int position) {
            FileInfo info = infoList.get(position);
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

        /***创建文件项目布局***/
        private LinearLayout createFileLayout() {
            LinearLayout l1 = new LinearLayout(viewContext);
            V.createRV(l1).setSize(-1, -2).setPaddingDP(5, 5, 5, 5).refresh();
            l1.setOrientation(LinearLayout.HORIZONTAL);
            l1.setGravity(Gravity.CENTER_VERTICAL);
            l1.setBackgroundResource(R.drawable.bg_cdk);

            ImageView iv = new ImageView(viewContext);
            V.createLL(iv).setSizeDP(60, 60).refresh();
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(R.drawable.file_folder);
            l1.addView(iv);

            LinearLayout l2 = new LinearLayout(viewContext);
            V.createLL(l2).setSize(-1, -1).setWeight(1).refresh();
            l2.setOrientation(LinearLayout.VERTICAL);
            l2.setGravity(Gravity.CENTER_VERTICAL);
            l1.addView(l2);

            TextView tv = new TextView(viewContext);
            V.createLL(tv).setSize(-2, -2).refresh();
            tv.setTextSize(20);
            tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tv.setSingleLine(true);
            tv.setSelected(true);
            l2.addView(tv);

            TextView tv1 = new TextView(viewContext);
            V.createLL(tv1).setSize(-2, -2).refresh();
            tv1.setTextSize(10);
            tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tv.setSingleLine(true);
            tv.setSelected(true);
            l2.addView(tv1);

            ImageView iv2 = new ImageView(viewContext);
            V.createLL(iv2).setSizeDP(20, 20).refresh();
            iv2.setImageResource(R.drawable.ic_right_arrow);
            l1.addView(iv2);
            return l1;
        }

        /*** 创建空文件夹项目 ***/
        private LinearLayout createEmptyLayout() {
            LinearLayout l1 = new LinearLayout(viewContext);
            V.createRV(l1).setSize(-1, -2).refresh();
            l1.setOrientation(LinearLayout.VERTICAL);
            l1.setGravity(Gravity.CENTER_HORIZONTAL);
            l1.setBackgroundResource(R.drawable.bg_cdk);

            ImageView iv = new ImageView(viewContext);
            V.createLL(iv).setSizeDP(120, 120).refresh();
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(R.drawable.file_unknown);
            l1.addView(iv);

            TextView tv = new TextView(viewContext);
            V.createLL(tv).setSize(-2, -2).setMarginDP(0, 0, 0, 40).refresh();
            tv.setTextSize(20);
            tv.setText(R.string.file_chooser_no_file);
            l1.addView(tv);
            return l1;
        }

        /**
         * 获取对应的图标资源值
         */
        private int iconType(FileInfo info) {
            switch (info.getType()) {
                case DIRECTORY: return R.drawable.file_folder;
                case TEXT: return R.drawable.file_document;
                case IMAGE: return R.drawable.file_picture;
                case VIDEO: return R.drawable.file_video;
                case COMPRESS: return R.drawable.file_zip;
                case AUDIO: return R.drawable.file_audio;
                default: return R.drawable.file_unknown;
            }
        }

        /*** 获取文件描述内容 ***/
        private String getFileDescribe(FileInfo fileInfo) {
            StringBuilder sb = new StringBuilder();
            if (fileInfo.isCustom()) return fileInfo.getPath();
            sb.append(context.getString(R.string.file_chooser_filesize))
                    .append(":")
                    .append(UData.B2A(fileInfo.getSize()))
                    .append("\t")
                    .append(context.getString(R.string.file_chooser_lmd))
                    .append(":")
                    .append(UTime.D2S(fileInfo.getLmd()));
            return sb.toString();
        }

        /*** 获取文件夹描述 ***/
        private String getDirectoryDescribe(FileInfo fileInfo) {
            StringBuilder sb = new StringBuilder();
            if (fileInfo.isCustom()) return fileInfo.getPath();
            sb.append(context.getString(R.string.file_chooser_lmd))
                    .append(":")
                    .append(UTime.D2S(fileInfo.getLmd()));
            return sb.toString();
        }
    }

    /*** File项目视图 ***/
    private class FileHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView title;
        private TextView summary;
        private ImageView arrow;

        public FileHolder(View itemView) {
            super(itemView);
            try {
                icon = V.getChild(itemView, 0, ImageView.class);
                ViewGroup vg = V.getChild(itemView, 1, ViewGroup.class);
                title = V.getChild(vg, 0, TextView.class);
                summary = V.getChild(vg, 1, TextView.class);
                arrow = V.getChild(itemView, 2, ImageView.class);
            } catch (Exception e) {

            }
        }

        private void setOnClick(View.OnClickListener l) {
            itemView.setOnClickListener(l);
        }

        private void setOnLongClick(View.OnLongClickListener l) {
            itemView.setOnLongClickListener(l);
        }

        /**
         * 更新弹窗
         */
        private void setUpdateDialog(FileInfo rootInfo) {
            itemView.setOnClickListener(v -> updateDialog(rootInfo));
        }

        /**
         * 空文件夹项目默认点击事件
         */
        private void setEmptyDirectory() {
            itemView.setOnClickListener(v -> {
                Toast.makeText(context, "什么都没有哦!", Toast.LENGTH_SHORT).show();
            });
        }

        /**
         * 普通文件项目默认点击事件
         */
        private void setFileContent(FileInfo info) {
            itemView.setOnClickListener(v -> {
                Toast.makeText(context, "文件:" + info.getPath(), Toast.LENGTH_SHORT).show();
            });
        }
    }


}
