package candyenk.android.widget;

import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
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

import java.util.HashSet;
import java.util.function.Consumer;


/**
 * 自定义Adapter上拉弹窗
 * 拦截层叠创建
 * 四次重构
 */
public class DialogBottom extends BottomSheetDialog {
    /*************************************静态变量**************************************************/
    private static final HashSet<View> mList = new HashSet<>();//拉起的控件列表
    /*************************************成员变量**************************************************/
    protected String TAG;
    protected Context context; //拉起弹窗的Activity
    protected Context viewContext;
    protected View parentView; //调用上拉弹窗的View对象
    protected boolean ok;//是否已经初始化成功
    protected CardView dialogView;//弹窗布局对象
    protected FrameLayout titleView;  //标题控件
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
     * 无法使用同一View拉起多个Dialog,但可使用null拉起一个多余的dialog
     */
    public DialogBottom(Context context) {
        this(context, null);
    }

    public DialogBottom(View view) {
        this(view.getContext(), view);
    }

    protected DialogBottom(Context context, View view) {
        super(context, R.style.Theme_CDKDialog_Bottom);
        this.TAG = this.getClass().getSimpleName();
        this.context = context;
        this.viewContext = new ContextThemeWrapper(context, R.style.Theme_CDKActivity);
        this.parentView = view;
        this.ok = mList.add(view);
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
        mList.remove(this.parentView);
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
     * 都为null则不显示
     */
    public void setOnButtonClickListener(View.OnClickListener left, View.OnClickListener right) {
        if (!ok) return;
        if (left != null) {
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setOnClickListener(left);
        }
        if (right != null) {
            rightButton.setVisibility(View.VISIBLE);
            rightButton.setOnClickListener(right);
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
        TextView tv = V.getChild(titleView, 0);
        tv.setText(title);
        tv.setVisibility(View.VISIBLE);
        titleView.setVisibility(View.VISIBLE);

    }

    /**
     * 设置标题是否居中,默认false
     * 需设置标题内容,否则不显示
     */
    public void setTitleCenter(boolean isCenter) {
        if (!ok) return;
        V.FL(V.getChild(titleView, 0))
                .lGravity((isCenter ? Gravity.CENTER : Gravity.LEFT) | Gravity.BOTTOM)
                .marginDP(isCenter ? 0 : 20, 0, 0, 0).refresh();
    }

    /**
     * 设置是否显示右上角关闭按钮,默认false
     */
    public void setShowClose(boolean isShow) {
        if (!ok) return;
        V.getChild(titleView, 1).setVisibility(View.VISIBLE);
        titleView.setVisibility(View.VISIBLE);
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
    private void initLayout() {
        dialogView = new MaterialCardView(viewContext);
        V.LL(dialogView).size(-1, -2).backgroundRes(R.color.back_all).radiusDP(20).refresh();

        ImageView iv = new AppCompatImageView(viewContext);
        V.FL(iv).sizeDP(-1, 120).parent(dialogView).drawable(R.drawable.background_transparent_gradual_change).scaleType(ImageView.ScaleType.FIT_XY).refresh();

        LinearLayout ll = new LinearLayout(viewContext);
        V.FL(ll).size(-1, -2).parent(dialogView).orientation(1).refresh();

        titleView = new FrameLayout(viewContext);
        V.LL(titleView).size(-1, -2).marginDP(0, 20, 0, 0).hide().parent(ll).refresh();

        TextView tv = new MaterialTextView(viewContext);
        V.FL(tv).size(-2).lGravity(Gravity.LEFT | Gravity.BOTTOM).textSize(20).textColorRes(R.color.text_title).hide().marginDP(20, 0, 0, 0).parent(titleView).refresh();

        ImageView iv1 = new ImageView(viewContext);
        V.FL(iv1).sizeDP(30).lGravity(Gravity.RIGHT | Gravity.BOTTOM).hide().drawable(android.R.drawable.ic_delete).marginDP(0, 0, 20, 0).parent(titleView).refresh();
        iv1.setOnClickListener(v -> dismiss());

        listView = new RecyclerView(viewContext);
        V.LL(listView).size(-1, -2).weight(1).parent(ll).refresh();
        listView.setLayoutManager(new LinearLayoutManager(context));

        LinearLayout ll1 = new LinearLayout(viewContext);
        V.LL(ll1).size(-1, -2).orientation(0).parent(ll).refresh();

        leftButton = new MaterialButton(viewContext);
        V.LL(leftButton).sizeDP(-1, 60).marginDP(20).weight(1).text(R.string.yes).hide().parent(ll1).refresh();

        rightButton = new MaterialButton(viewContext, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        V.LL(rightButton).sizeDP(-1, 60).marginDP(20).weight(1).text(R.string.no).hide().parent(ll1).refresh();

        super.setContentView(dialogView);
    }
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/

}

