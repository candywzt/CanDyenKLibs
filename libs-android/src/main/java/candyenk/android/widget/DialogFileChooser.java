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
import candyenk.java.utils.UArrays;
import candyenk.java.utils.UData;
import candyenk.java.utils.UTime;
import candyenk.java.utils.UFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 文件选择器上拉弹窗
 * TODO:权限异常
 * 没有动画,遗憾
 * 注意事项:
 * 自定义文件会替代对应文件夹名称以及描述(我觉得这样挺好,就不改了)
 */
public class DialogFileChooser extends DialogBottom {
    private File rootFile;
    private final List<FileInfo> infoList;
    private final FileAdapter adapter;

    private Comparator<FileInfo> sortOrder;//排序方式
    private Predicate<FileInfo> filter;//过滤方式
    private boolean isShowHide;//是否显示隐藏文件

    public DialogFileChooser(Context context, File rootFile, boolean isShowHide) {
        this(context, rootFile, null, null, isShowHide);

    }

    public DialogFileChooser(Context context, File... files) {
        this(context, null, files, null, true);

    }

    public DialogFileChooser(Context context, FileInfo... infos) {
        this(context, null, null, infos, true);

    }


    private DialogFileChooser(Context context, File rootFile, File[] files, FileInfo[] infos, boolean isShowHide) {
        super(context);
        this.isShowHide = isShowHide;
        this.rootFile = rootFile;//可能为NULL
        this.infoList = new ArrayList<>();
        addItem(rootFile);
        addItem(files);
        addItem(infos);
        completionList();
        this.adapter = new FileAdapter();
        //listView.addItemDecoration(new RecycleItemsDecortion.SpaceItemDecoration(0, 15));
        //this.adapter.setHasStableIds(true);
        setContent(adapter);
        setTitle("选择文件");
    }

    /**
     * 设置选择监听器
     */
    public void setItemOnCLickListener(Consumer<FileInfo> consumer) {
        if (consumer != null) adapter.consumer = consumer;
    }

    /**
     * 设置文件排序方式
     * 只在普通文件列表中有效
     * 当文件1要排在文件2的前面时返回-1,排在后面返回1,相等返回0
     */
    public void setSortOrder(Comparator<FileInfo> sortOrder) {
        this.sortOrder = sortOrder;
        if (isShow) updateDialog(this.rootFile);
    }

    /**
     * 设置文件过滤方式
     * 返回false表示该文件被剔除,返回true表示改文件被保留
     * 只在普通文件列表中有效
     * 如果设置不显示隐藏文件,那么隐藏文件不会出现在过滤列表中
     */
    public void setFilter(Predicate<FileInfo> filter) {
        this.filter = filter;
        if (isShow) updateDialog(this.rootFile);
    }

    /**
     * 设置是否显示隐藏文件
     * 只在普通文件列表中有效
     */
    public void setShowHide(boolean isShowHide) {
        this.isShowHide = isShowHide;
        if (isShow) updateDialog(this.rootFile);
    }

    /**
     * 设置标题显示内容
     * 调整为显示后也能修改标题
     */
    @Override
    public void setTitle(CharSequence title) {
        titleView.setText(title);
        titleView.setVisibility(View.VISIBLE);
    }

    /**
     * 更新数据
     */
    public void updateDialog(File rootFile) {
        this.rootFile = rootFile;
        infoList.clear();
        addItem(rootFile);
        setTitle(rootFile.getAbsolutePath());
        updateAdapter();
    }

    public void updateDialog(File... files) {
        this.rootFile = null;
        infoList.clear();
        addItem(files);
        updateAdapter();
    }

    public void updateDialog(FileInfo... infos) {
        this.rootFile = null;
        infoList.clear();
        addItem(infos);
        updateAdapter();
    }


    private void completionList() {
        if (infoList.size() > 0 && rootFile != null && rootFile.getParentFile() != null) {
            infoList.add(0, null);//添加一个空值代表上级
        } else if (infoList.size() == 0 && rootFile != null && rootFile.getParentFile() != null) {
            infoList.add(null);//添加一个空值代表上级
            infoList.add(null);//再加一个空值代表空文件夹
        } else if (infoList.size() == 0) {
            infoList.add(null);//添加一个空值代表空文件夹
        }
    }

    private void updateAdapter() {
        completionList();
        adapter.notifyDataSetChanged();
    }

    private void addItem(File file) {
        if (file != null) {
            UArrays.addArrays(infoList, file.listFiles(), f -> {
                if (isShowHide || !file.getName().startsWith(".")) {
                    if (filter == null || filter.test(FileInfo.create(f))) {
                        return FileInfo.create(f);
                    }
                }
                return null;
            });
            if (this.sortOrder == null) {
                Collections.sort(infoList);
            } else {
                Collections.sort(infoList, sortOrder);
            }
        }
    }

    private void addItem(File[] files) {
        if (files != null && files.length > 0) {
            UArrays.addArrays(infoList, files, FileInfo::create);
        }
    }

    private void addItem(FileInfo[] infos) {
        if (infos != null && infos.length > 0) {
            UArrays.addArrays(infoList, infos, null);
        }
    }

    private class FileAdapter extends RecyclerView.Adapter {
        private Consumer<FileInfo> consumer;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new RecyclerView.ViewHolder(createLastLayout()) {
                    };
                case 1:
                    return new RecyclerView.ViewHolder(createEmptyLayout()) {
                    };
                case 2:
                    return new RecyclerView.ViewHolder(createDirectoryLayout()) {
                    };
                case 3:
                default:
                    return new RecyclerView.ViewHolder(createFileLayout()) {
                    };
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case 0:
                    //返回上一级条目
                    holder.itemView.setOnClickListener(v -> updateDialog(rootFile.getParentFile()));
                    break;
                case 1:
                    //空文件夹条目
                    holder.itemView.setOnClickListener(v -> Toast.makeText(context, "什么都没有哦~", Toast.LENGTH_SHORT).show());
                    break;
                case 2:
                case 3:
                    //文件(夹)条目
                    FileInfo info = infoList.get(position);
                    ViewGroup vg = (ViewGroup) holder.itemView;
                    ((ImageView) vg.getChildAt(0)).setImageResource(iconType(info));
                    ViewGroup vg1 = (ViewGroup) vg.getChildAt(1);
                    ((TextView) vg1.getChildAt(0)).setText(info.getName());
                    ((TextView) vg1.getChildAt(1)).setText(getFileDescribe(info));
                    holder.itemView.setOnClickListener(v -> {
                        if (consumer != null) consumer.accept(info);
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
         * 0:上级文件夹
         * 1:空文件夹
         * 2:文件夹
         * 3:文件
         */
        @Override
        public int getItemViewType(int position) {
            FileInfo info = infoList.get(position);
            if (info == null && position == 0) {
                return 0;
            } else if (info == null) {
                return 1;
            } else if (info.isDirectory()) {
                return 2;
            } else {
                return 3;
            }
        }

        //创建文件项目
        private LinearLayout createFileLayout() {
            LinearLayout l1 = new LinearLayout(viewContext);
            RecyclerView.LayoutParams p1 = new RecyclerView.LayoutParams(-1, -2);
            l1.setLayoutParams(p1);
            l1.setOrientation(LinearLayout.HORIZONTAL);
            l1.setGravity(Gravity.CENTER_VERTICAL);
            l1.setPadding(dp2px(5), dp2px(5), dp2px(5), dp2px(5));
            l1.setBackgroundResource(R.drawable.bg_cdk);

            ImageView iv = new ImageView(viewContext);
            LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(dp2px(60), dp2px(60));
            iv.setLayoutParams(p2);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            l1.addView(iv);

            LinearLayout l2 = new LinearLayout(viewContext);
            LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(-1, -1);
            p3.weight = 1;
            l2.setLayoutParams(p3);
            l2.setOrientation(LinearLayout.VERTICAL);
            l2.setGravity(Gravity.CENTER_VERTICAL);
            l1.addView(l2);

            TextView tv = new TextView(viewContext);
            LinearLayout.LayoutParams p4 = new LinearLayout.LayoutParams(-2, -2);
            tv.setLayoutParams(p4);
            tv.setTextSize(20);
            tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tv.setSingleLine(true);
            tv.setSelected(true);
            l2.addView(tv);

            TextView tv1 = new TextView(viewContext);
            LinearLayout.LayoutParams p5 = new LinearLayout.LayoutParams(-2, -2);
            tv1.setLayoutParams(p5);
            tv1.setTextSize(10);
            tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tv.setSingleLine(true);
            tv.setSelected(true);
            l2.addView(tv1);
            return l1;
        }

        //创建文件夹项目
        private LinearLayout createDirectoryLayout() {
            LinearLayout l1 = createFileLayout();
            ((ImageView) l1.getChildAt(0)).setImageResource(R.drawable.file_folder);
            TextView tv = new TextView(viewContext);
            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(-2, -2);
            tv.setLayoutParams(p1);
            tv.setPadding(dp2px(15), dp2px(15), dp2px(15), dp2px(15));
            tv.setTextSize(15);
            tv.setText(">");
            l1.addView(tv);
            return l1;
        }

        //创建上一级项目
        private LinearLayout createLastLayout() {
            LinearLayout l1 = createDirectoryLayout();
            LinearLayout l2 = (LinearLayout) l1.getChildAt(1);
            ((TextView) l2.getChildAt(0)).setText("../");
            l2.removeViewAt(1);
            return l1;
        }

        //创建空文件夹项目
        private LinearLayout createEmptyLayout() {
            LinearLayout l1 = new LinearLayout(viewContext);
            RecyclerView.LayoutParams p1 = new RecyclerView.LayoutParams(-1, -2);
            l1.setLayoutParams(p1);
            l1.setOrientation(LinearLayout.VERTICAL);
            l1.setGravity(Gravity.CENTER_HORIZONTAL);
            l1.setBackgroundResource(R.drawable.bg_cdk);

            ImageView iv = new ImageView(viewContext);
            LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(dp2px(120), dp2px(120));
            iv.setLayoutParams(p2);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(R.drawable.file_unknown);
            l1.addView(iv);

            TextView tv = new TextView(viewContext);
            LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(-2, -2);
            p3.setMargins(0, 0, 0, dp2px(40));
            tv.setLayoutParams(p3);
            tv.setTextSize(20);
            tv.setText(R.string.file_chooser_no_file);
            l1.addView(tv);
            return l1;
        }


        private int iconType(FileInfo info) {
            switch (info.getType()) {
                case DIRECTORY:
                    return R.drawable.file_folder;
                case TEXT:
                    return R.drawable.file_document;
                case IMAGE:
                    return R.drawable.file_picture;
                case VIDEO:
                    return R.drawable.file_video;
                case COMPRESS:
                    return R.drawable.file_zip;
                case AUDIO:
                    return R.drawable.file_audio;
                default:
                    return R.drawable.file_unknown;
            }
        }

        private String getFileDescribe(FileInfo fileInfo) {
            StringBuilder sb = new StringBuilder();
            if (fileInfo.isCustom()) return fileInfo.getPath();
            if (!fileInfo.isDirectory()) {
                sb.append(context.getString(R.string.file_chooser_filesize))
                        .append(":")
                        .append(UData.B2A(fileInfo.getSize()))
                        .append("      ");
            }
            sb.append(context.getString(R.string.file_chooser_lmd))
                    .append(":")
                    .append(UTime.D2S(fileInfo.getLmd()));
            return sb.toString();
        }
    }

    /**
     * 小小的方便自定义的文件信息
     */
    public static class FileInfo implements Comparable<FileInfo> {
        private static final Map<String, FileInfo> infoMap = new HashMap<>();
        private File file;
        private String name;
        private String path;
        private long size;
        private long lmd;
        private boolean isCustom;
        private UFile.TypeFile type;

        private FileInfo(File file) {
            if (file != null) {
                this.file = file;
                this.name = file.getName();
                this.path = file.getAbsolutePath();
                this.size = file.length();
                this.lmd = file.lastModified();
                this.type = UFile.getTypeFile(file);
            } else {
                this.isCustom = true;
            }
        }

        public static FileInfo create(File file) {
            if (file == null) return new FileInfo(null);
            FileInfo info = infoMap.get(file.getAbsolutePath());
            if (info == null || file.lastModified() != info.lmd) {
                info = new FileInfo(file);
                infoMap.put(info.path, info);
            }
            return info;
        }

        public File getFile() {
            return file;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
            this.isCustom = true;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            infoMap.remove(this.path);
            this.path = path;
            this.isCustom = true;
            infoMap.put(this.path, this);
        }

        public long getSize() {
            return size;
        }

        public long getLmd() {
            return lmd;
        }

        public UFile.TypeFile getType() {
            return type;
        }

        public void setType(UFile.TypeFile type) {
            this.type = type;
            this.isCustom = true;
        }

        public boolean isCustom() {
            return isCustom;
        }

        public boolean isDirectory() {
            return getType() == UFile.TypeFile.DIRECTORY;
        }

        /**
         * 排序方案
         * 目录优先
         */
        @Override
        public int compareTo(FileInfo f) {
            boolean tid = this.isDirectory();
            boolean fid = f.isDirectory();
            if (tid == fid) {
                return getPath().compareTo(f.getPath());
            } else {
                return tid ? -1 : 1;
            }
        }

        @Override
        public String toString() {
            return "FileInfo{" +
                    "file=" + file +
                    ", name='" + name + '\'' +
                    ", path='" + path + '\'' +
                    ", size=" + size +
                    ", lmd=" + lmd +
                    ", type=" + type.name() +
                    '}';
        }


    }
}
