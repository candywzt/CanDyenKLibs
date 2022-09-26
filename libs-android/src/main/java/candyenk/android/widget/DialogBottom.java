package candyenk.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import candyenk.android.R;
import candyenk.android.viewgroup.NoLinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.HashSet;


/**
 * <h1>上拉弹窗</h1>
 * <br>自动拦截重复调用
 * <h2>现存问题</h2>
 * <p color = red>虚拟键无法沉浸</p>
 */
public class DialogBottom extends BottomSheetDialog {
    private static final String TAG = DialogBottom.class.getSimpleName();
    private static final HashSet<View> mList = new HashSet<>();//拉起的控件列表

    protected Context context; //拉起弹窗的Activity
    protected Context viewContext;
    protected View parentView; //调用上拉弹窗的View对象
    protected boolean isShow;//是否已经展示
    protected LinearLayout dialogView;//弹窗布局对象
    protected TextView titleView;  //标题文字控件
    protected RecyclerView listView; //列表控件
    protected Button leftButton, rightButton;  //左右按钮控件

    private AdapterDialogBottom adapter; //列表adp
    private AdapterView.OnItemClickListener itemCL; //列表点击事件
    private AdapterView.OnItemLongClickListener itemLCL; //列表点击事件


    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/

    public DialogBottom(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     * 无法使用同一View拉起多个Dialog,可使用null拉起一个dialog
     *
     * @param context 拉起弹窗的活动
     * @param view    拉起弹窗的View
     */
    public DialogBottom(Context context, View view) {
        super(context, R.style.Theme_CDKDialog_Bottom);
        this.context = context;
        this.viewContext = new ContextThemeWrapper(context, R.style.Theme_CDKActivity);
        this.parentView = view;
        this.isShow = !mList.add(view);
        if (!this.isShow) {
            initLayout();
        }
    }
    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);//解决横屏显示不全
        this.isShow = true;
    }

    /**
     * 拉起弹窗
     */
    public void show() {
        if (isShow) {
            Log.e(TAG, "拦截重复调用");
            return;
        }
        super.show();
    }

    /**
     * 关闭并销毁弹窗
     */
    public void dismiss() {
        mList.remove(this.parentView);
        super.dismiss();
    }

    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/
    private void initLayout() {
        dialogView = new LinearLayout(viewContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        dialogView.setLayoutParams(lp);
        dialogView.setGravity(Gravity.CENTER);

        CardView cv = new CardView(viewContext);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-1, -2);
        cv.setLayoutParams(lp2);
        cv.setRadius(dp2px(20));
        dialogView.addView(cv);

        ImageView iv = new ImageView(viewContext);
        FrameLayout.LayoutParams lp3 = new FrameLayout.LayoutParams(-1, dp2px(120));
        iv.setLayoutParams(lp3);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageDrawable(context.getResources().getDrawable(R.drawable.background_transparent_gradual_change));
        cv.addView(iv);

        LinearLayout ll = new LinearLayout(viewContext);
        FrameLayout.LayoutParams lp4 = new FrameLayout.LayoutParams(-1, -2);
        ll.setLayoutParams(lp4);
        ll.setOrientation(LinearLayout.VERTICAL);
        cv.addView(ll);

        titleView = new TextView(viewContext);
        LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams(-1, dp2px(40));
        lp5.setMargins(dp2px(20), dp2px(20), dp2px(20), 0);
        titleView.setLayoutParams(lp5);
        titleView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        titleView.setTextSize(20);
        titleView.setTextColor(context.getResources().getColor(R.color.text_title));
        titleView.setVisibility(View.GONE);
        ll.addView(titleView);

        listView = new RecyclerView(viewContext);
        LinearLayout.LayoutParams lp6 = new LinearLayout.LayoutParams(-1, -2);
        lp6.weight = 1;
        listView.setLayoutParams(lp6);
        listView.setLayoutManager(new LinearLayoutManager(context));
        ll.addView(listView);

        LinearLayout ll2 = new LinearLayout(viewContext);
        LinearLayout.LayoutParams lp7 = new LinearLayout.LayoutParams(-1, -2);
        ll2.setLayoutParams(lp7);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        ll.addView(ll2);

        leftButton = new MaterialButton(viewContext);
        LinearLayout.LayoutParams lp8 = new LinearLayout.LayoutParams(-1, dp2px(60));
        lp8.setMargins(dp2px(20), dp2px(20), dp2px(20), dp2px(20));
        lp8.weight = 1;
        leftButton.setLayoutParams(lp8);
        leftButton.setText(R.string.yes);
        leftButton.setVisibility(View.GONE);
        ll2.addView(leftButton);

        rightButton = new MaterialButton(viewContext, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        LinearLayout.LayoutParams lp9 = new LinearLayout.LayoutParams(-1, dp2px(60));
        lp9.setMargins(dp2px(20), dp2px(20), dp2px(20), dp2px(20));
        lp9.weight = 1;
        rightButton.setLayoutParams(lp9);
        rightButton.setText(R.string.no);
        rightButton.setVisibility(View.GONE);
        ll2.addView(rightButton);

        super.setContentView(dialogView);
    }

    protected int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }
    /**********************************************************************************************/
    /*************************************公开方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置悬浮窗模式(可使用Service拉起弹窗)
     */
    public void setWindowMode() {
        getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
    }

    /**
     * 设置底部左右按钮的点击事件
     * 至少设置0个，至多设置两个，多余的没用
     * 依据是否设置点击事件、设置了几个点击事件来显示或隐藏底栏按钮
     * 至多显示两个按钮，不调用该方法即不显示按钮
     * 调用setButtonText(CharSequence... text)方法可改变按钮文本
     * 拉起弹窗后不可修改
     *
     * @param l 点击事件
     */
    public void setOnButtonClickListener(View.OnClickListener... l) {
        if (isShow) return;
        leftButton.setVisibility(View.VISIBLE);
        leftButton.setOnClickListener(v -> {
            l[0].onClick(v);
            dismiss();
        });
        if (l.length > 1) {
            rightButton.setVisibility(View.VISIBLE);
            rightButton.setOnClickListener(v -> {
                l[1].onClick(v);
                dismiss();
            });
        }
    }

    /**
     * 设置列表项目点击事件
     * 该方法与setContent(CharSequence... stringList)需同时设置才会起作用
     * 不分先后，顺序随意
     * parent 和id是空的
     * 拉起弹窗后不可修改
     *
     * @param l 项目点击监听
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        if (isShow) return;
        this.itemCL = l;
        if (adapter != null) {
            adapter.setOnItemClickListener((parent, view, position, id) -> {
                itemCL.onItemClick(parent, view, position, id);
                dismiss();
            });
            listView.setAdapter(adapter);
        }
    }

    /**
     * 设置列表项目长按事件
     * 该方法与setContent(CharSequence... stringList)需同时设置才会起作用
     * 不分先后，顺序随意
     * parent 和id是空的
     * 拉起弹窗后不可修改
     *
     * @param l 项目点击监听
     */
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener l) {
        if (isShow) return;
        this.itemLCL = l;
        if (adapter != null) {
            adapter.setOnItemClickListener((parent, view, position, id) -> {
                itemLCL.onItemLongClick(parent, view, position, id);
                dismiss();
            });
            listView.setAdapter(adapter);
        }
    }


    /**
     * 自定义布局的布局事件设置方法
     * 征用了一下View的点击事件hhh
     * 塞到adp里了
     * 拉起弹窗后不可修改
     *
     * @param l
     */
    public void setContentViewController(View.OnClickListener l) {
        if (isShow) return;
        adapter.setContentViewController(l);
    }

    /**
     * 设置左右按钮文本
     * 需同时调用setOnButtonClickListener(onButtonClickListener... l)方法
     * 该方法与按钮的显示无关，不调用该方法按钮将显示默认文本
     * 拉起弹窗后不可修改
     *
     * @param text
     */
    public void setButtonText(CharSequence... text) {
        if (isShow) return;
        leftButton.setText(text[0]);
        if (text.length > 1) rightButton.setText(text[1]);
    }

    /**
     * 设置弹窗外触关闭和返回键关闭
     * 默认 true,true
     * 拉起弹窗后不可修改
     *
     * @param touchOff 外触是否关闭
     * @param backOff  返回键是否关闭
     */
    public void setCancelable(boolean touchOff, boolean backOff) {
        if (isShow) return;
        //设置外触关闭
        setCanceledOnTouchOutside(touchOff);
        //设置返回键关闭
        setCancelable(backOff);
    }

    /**
     * 设置标题文本
     * 调用该方法将显示标题栏
     * 不调用则隐藏标题栏
     * 拉起弹窗后不可修改
     */
    public void setTitle(CharSequence title) {
        if (isShow) return;
        titleView.setText(title);
        titleView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置标题位置,默认左边
     * 好吧只能center|left|right
     * 拉起弹窗后不可修改
     *
     * @see Gravity
     */
    public void setTitleGravity(int gravity) {
        if (isShow) return;
        titleView.setGravity(gravity | Gravity.CENTER);
        titleView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置纯文本格式内容
     * 拉起弹窗后不可修改
     */
    public void setContent(CharSequence text) {
        if (isShow) return;
        adapter = new AdapterDialogBottom(text);
        listView.setAdapter(adapter);
    }

    /**
     * 设置文本项目列表格式内容
     * 调用该方法建议不要显示底栏按钮
     * 拉起弹窗后不可修改
     */
    public void setContent(CharSequence... stringList) {
        if (isShow) return;
        adapter = new AdapterDialogBottom(stringList);
        if (itemCL != null) {
            adapter.setOnItemClickListener((parent, view, position, id) -> {
                itemCL.onItemClick(parent, view, position, id);
                dismiss();
            });

        }
        listView.setAdapter(adapter);
    }

    /**
     * 设置内自定义布局内容
     * 拉起弹窗后不可修改
     */
    public void setContent(int viewid) {
        if (isShow) return;
        View view = LayoutInflater.from(viewContext).inflate(viewid, null, false);
        this.setContent(view);
    }

    /**
     * 设置内自定义View内容
     * 拉起弹窗后不可修改
     */
    public void setContent(View view) {
        if (isShow) return;
        adapter = new AdapterDialogBottom(view);
        listView.setAdapter(adapter);
    }

    /**
     * 设置内自定义适配器内容
     * 拉起弹窗后不可修改
     */
    public void setContent(RecyclerView.Adapter adapter) {
        if (isShow) return;
        listView.setAdapter(adapter);
    }

    /**********************************************************************************************/
    /***************************************适配器**************************************************/
    /**********************************************************************************************/
    private class AdapterDialogBottom extends RecyclerView.Adapter<AdapterDialogBottom.ViewHolder> {
        private final int type;
        private final Object object;
        private ViewGroup parent;
        private View.OnClickListener viewCL;
        private AdapterView.OnItemClickListener clickListener;
        private AdapterView.OnItemLongClickListener longClickListener;

        /*****************************************************************************************/
        /**************************************构造函数*********************************************/
        /*****************************************************************************************/
        public AdapterDialogBottom(CharSequence string) {
            this(1, string);
        }

        public AdapterDialogBottom(CharSequence[] mList) {
            this(2, mList);
        }

        public AdapterDialogBottom(View view) {
            this(3, view);
        }

        private AdapterDialogBottom(int type, Object object) {
            this.type = type;
            this.object = object;
        }
        /******************************************************************************************/
        /***************************************视图管理器*******************************************/
        /******************************************************************************************/
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            this.parent = parent;
            switch (type) {
                default:
                    return new ViewHolder(initLayoutString());
                case 2:
                    return new ViewHolder(initLayoutStrings());
                case 3:
                    return new ViewHolder((View) object);

            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (type) {
                default:
                    holder.textView.setText((CharSequence) object);
                    break;
                case 2:
                    holder.textView.setText(((CharSequence[]) object)[position]);
                    //绑定点击事件
                    holder.itemView.setOnClickListener(v -> {
                        if (clickListener != null) {
                            clickListener.onItemClick(null, v, position, 0);
                        }
                    });
                    //绑定长按事件
                    holder.itemView.setOnLongClickListener(v -> longClickListener != null&&
                        longClickListener.onItemLongClick(null, v, position, 0));
                    break;
                case 3:
                    if (viewCL != null) {
                        viewCL.onClick(holder.itemView);
                    }
                    break;
            }
        }

        /******************************************************************************************/
        /***************************************私有方法********************************************/
        /******************************************************************************************/
        private View initLayoutString() {
            TextView tv = new TextView(viewContext);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, -2);
            tv.setLayoutParams(lp);
            tv.setPadding(dp2px(10), dp2px(10), dp2px(10), dp2px(10));
            tv.setTextIsSelectable(true);
            return tv;
        }

        private View initLayoutStrings() {
            NoLinearLayout nl = new NoLinearLayout(viewContext);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, dp2px(60));
            nl.setLayoutParams(lp);
            nl.setOrientation(LinearLayout.VERTICAL);
            nl.setClickable(true);
            nl.setFocusable(true);
            TypedArray ta = viewContext.obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
            int res = ta.getResourceId(0, 0);
            ta.recycle();
            if (res != 0) nl.setBackgroundResource(res);

            TextView tv = new TextView(viewContext);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-1, -1);
            tv.setLayoutParams(lp2);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            nl.addView(tv);

            View v = new View(viewContext);
            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(-1, dp2px(2));
            lp3.setMargins(dp2px(30), dp2px(-1), dp2px(30), 0);
            v.setLayoutParams(lp3);
            v.setBackgroundColor(context.getResources().getColor(R.color.mainGC4_1));
            nl.addView(v);
            return nl;
        }
        /******************************************************************************************/
        /***************************************公开方法********************************************/
        /******************************************************************************************/
        /**
         * 项目点击事件
         * parent 和id是空的
         */
        public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
            this.clickListener = onItemClickListener;
        }

        /**
         * 项目长按事件
         * parent 和id是空的
         */
        public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
            this.longClickListener = onItemLongClickListener;
        }

        @Override
        public int getItemCount() {
            switch (type) {
                case 0:
                    return 0;
                default:
                    return 1;
                case 2:
                    return ((CharSequence[]) object).length;
            }
        }

        public void setContentViewController(View.OnClickListener l) {
            this.viewCL = l;
        }
        /******************************************************************************************/
        /************************************ViewHolder********************************************/
        /******************************************************************************************/
        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(View view) {
                super(view);
                if (type == 1) {
                    textView = (TextView) view;
                } else if (type == 2) {
                    textView = (TextView) ((ViewGroup) view).getChildAt(0);
                }
            }
        }
    }
}

