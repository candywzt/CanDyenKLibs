package candyenk.android.widget;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.R;
import candyenk.android.tools.GD;
import candyenk.android.tools.V;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


/**
 * 自定义Adapter上拉弹窗
 * 拦截层叠创建
 * 四次重构
 */
public class DialogBottom extends BottomSheetDialog {
    /*************************************静态变量**************************************************/
    private static View lastSign;//重复标记
    /*************************************成员变量**************************************************/
    protected String TAG;
    protected Context context; //拉起弹窗的Activity
    protected Context viewContext;
    protected View parentView; //调用上拉弹窗的View对象
    protected boolean ok;//是否已经初始化成功
    protected CardView dialogView;//弹窗布局对象
    protected TextView titleView;  //标题控件
    protected ImageView closeView;  //关闭控件
    protected RecyclerView listView; //列表控件
    protected Button leftButton, rightButton;  //左右按钮控件
    protected RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;
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
     * 构造方法
     * 会自动阻止多重点击,不过避免使用null
     */
    public DialogBottom(Context context) {
        this(context, null);
    }

    public DialogBottom(View view) {
        this(view.getContext(), view);
    }

    protected DialogBottom(Context context, View view) {
        super(context, R.style.Theme_CDK_Dialog);
        this.TAG = this.getClass().getSimpleName();
        this.context = context;
        this.viewContext = new ContextThemeWrapper(context, R.style.Theme_CDK);
        this.parentView = view;
        this.ok = checkSign();
        if (ok) initLayout();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && context instanceof Service) {//悬浮窗模式(API26)
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);//解决横屏显示不全
    }

    @Override
    public void show() {
        if (!ok) return;
        if (listView.getAdapter() == null) return;
        super.show();
    }

    @Override
    public void dismiss() {
        lastSign = null;
        super.dismiss();
    }

    /*** 创建RecycleView单击长按双击监听 ***/
    protected RecyclerView.OnItemTouchListener createListener(Consumer<RecyclerView.ViewHolder> onClick, Consumer<RecyclerView.ViewHolder> onLongClick, Consumer<RecyclerView.ViewHolder> onDoubleClick) {
        return new RecyclerView.OnItemTouchListener() {
            private final GestureDetectorCompat gd = GD.create(viewContext)
                    .click(this::cilck)
                    .longClick(this::longClick)
                    .doubleClick(this::doubleClick).buildCompat();

            private void cilck(MotionEvent e) {c(onClick, e);}

            private void longClick(MotionEvent e) {c(onLongClick, e);}

            private void doubleClick(MotionEvent e) {c(onDoubleClick, e);}

            private void c(Consumer<RecyclerView.ViewHolder> c, MotionEvent e) {
                View child = listView.findChildViewUnder(e.getX(), e.getY());
                if (c != null && child != null) {
                    c.accept(listView.getChildViewHolder(child));
                }
            }

            @Override
            public boolean onInterceptTouchEvent(@NonNull @NotNull RecyclerView rv, @NonNull @NotNull MotionEvent e) {
                gd.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull @NotNull RecyclerView rv, @NonNull @NotNull MotionEvent e) {
                gd.onTouchEvent(e);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        };
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置上拉弹窗底部按钮点击事件
     * 该方法参数决定按钮显示数量
     */
    public void setOnButtonClickListener(View.OnClickListener left, View.OnClickListener right) {
        if (!ok) return;
        leftButton.setOnClickListener(left);
        rightButton.setOnClickListener(right);
        leftButton.setVisibility(left == null ? View.GONE : View.VISIBLE);
        rightButton.setVisibility(right == null ? View.GONE : View.VISIBLE);
        V.getParent(leftButton).setVisibility((left == null && right == null) ? View.GONE : View.VISIBLE);
        if (left == null || right == null) {
            V.LL(rightButton).marginDP(0, 0, 0, 0).refresh();
            V.LL(leftButton).marginDP(0, 0, 0, 0).refresh();
        } else {
            V.LL(rightButton).marginDP(20, 0, 0, 0).refresh();
            V.LL(leftButton).marginDP(0, 0, 20, 0).refresh();
        }
    }

    /**
     * 设置左右按钮文本
     * 设置按键监听才会显示按钮
     */
    public void setButtonText(CharSequence left, CharSequence right) {
        if (!ok) return;
        leftButton.setText(left);
        rightButton.setText(right);
    }

    /**
     * 设置弹窗外触关闭和返回键关闭
     * 默认 true,true
     */
    public void setCancelable(boolean touchOff, boolean backOff) {
        if (!ok) return;
        setCanceledOnTouchOutside(touchOff);
        setCancelable(backOff);
    }

    /**
     * 设置标题文本
     * 调用该方法以显示标题栏
     */
    public void setTitle(CharSequence title) {
        if (!ok) return;
        if (title == null) {
            titleView.setVisibility(View.GONE);
        } else {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置标题是否居中,默认false
     * 需设置标题内容,否则不显示
     */
    public void setTitleCenter(boolean isCenter) {
        if (!ok) return;
        V.LL(titleView).gravity(isCenter ? Gravity.CENTER : Gravity.LEFT).paddingDP(isCenter ? 0 : 20, 0, 0, 0).refresh();
    }

    /**
     * 设置是否显示右上角关闭按钮,默认false
     */
    public void setShowClose(boolean isShow) {
        if (!ok) return;
        closeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置内容-自定义适配器
     * 拉起弹窗后不可修改
     */
    public void setContent(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        if (!ok) return;
        if (adapter == null) return;
        this.adapter = adapter;
        listView.setAdapter(adapter);
    }

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 创建弹窗根布局 ***/
    @SuppressLint("RtlHardcoded")
    private void initLayout() {
        dialogView = new MaterialCardView(viewContext);
        V.LL(dialogView).size(-1, -2).backgroundRes(R.color.back_all).radiusDP(20).refresh();

        ImageView iv = new AppCompatImageView(viewContext);
        V.FL(iv).sizeDP(-1, 120).drawable(R.drawable.background_transparent_gradual_change).scaleType(ImageView.ScaleType.FIT_XY).parent(dialogView).refresh();

        LinearLayout ll = new LinearLayout(viewContext);
        V.FL(ll).size(-1, -2).paddingDP(0, 20, 0, 0).orientation(1).parent(dialogView).refresh();

        titleView = new MaterialTextView(viewContext);
        V.LL(titleView).size(-1, -2).textSize(20).textColorRes(R.color.text_title).hide().paddingDP(20, 0, 0, 0).parent(ll).refresh();

        closeView = new ImageView(viewContext);
        V.FL(closeView).sizeDP(40, 40).lGravity(Gravity.TOP | Gravity.RIGHT).hide().drawable(android.R.drawable.ic_delete).paddingDP(0, 10, 10, 0).parent(dialogView).refresh();
        closeView.setOnClickListener(v -> dismiss());

        listView = new RecyclerView(viewContext);
        V.LL(listView).size(-1, -2).weight(1).parent(ll).refresh();
        listView.setLayoutManager(new LinearLayoutManager(context));

        LinearLayout ll1 = new LinearLayout(viewContext);
        V.LL(ll1).size(-1, -2).orientation(0).paddingDP(20, 0, 20, 20).hide().parent(ll).refresh();

        leftButton = new MaterialButton(viewContext);
        V.LL(leftButton).sizeDP(-1, 60).marginDP(0, 0, 20, 0).weight(1).text(R.string.yes).hide().parent(ll1).refresh();

        rightButton = new MaterialButton(viewContext, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        V.LL(rightButton).sizeDP(-1, 60).marginDP(20, 0, 0, 0).weight(1).text(R.string.no).hide().parent(ll1).refresh();

        super.setContentView(dialogView);
    }

    /*** 检查是否合法 ***/
    private boolean checkSign() {
        if (lastSign == null || this.parentView != lastSign) {
            lastSign = this.parentView;
            return true;
        } else return false;
    }
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/

}

