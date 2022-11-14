package candyenk.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import candyenk.android.R;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import candyenk.android.widget.BottomBar;
import candyenk.java.utils.UArrays;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * CDKActivity
 * 一次重构
 */
public abstract class CDKActivity extends AppCompatActivity {
    /*************************************静态变量**************************************************/
    //public static boolean useAnimation = true;
    /*************************************成员变量**************************************************/
    protected String TAG;
    protected Context viewContext;//创建控件使用的Context
    protected Bundle saveData;//保存的数据
    private FrameLayout container;//根布局
    private LinearLayout titleBar;//标题栏控件
    private BottomBar bottomBar;//底栏控件
    private View child;//添加的子控件
    private List<CDKFragment> fList;//添加的Fragment数组
    private int[] sign = {0};//标记<fragment当前页面>

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

    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    protected abstract void intentInit();//传递初始化

    protected abstract void viewInit();//控件初始化

    protected abstract void contentInit(Bundle save);//内容初始化

    protected abstract void eventInit();//事件初始化

    protected abstract Bundle saveData(Bundle bundle);//保存状态

    //Activity创建,启动的初始化
    @Override
    protected void onCreate(Bundle save) {
        L.e(TAG, "启动创建-Create");
        contextInit(save);
        super.onCreate(save);
        intentInit();
        viewInit();
        contentInit(save);
        eventInit();
    }

    //Activity前台可见
    @Override
    protected void onStart() {
        L.e(TAG, "前台可见-Start");
        super.onStart();
    }

    //Activity可操作,处于栈顶
    @Override
    protected void onResume() {
        L.e(TAG, "处于栈顶-Resume");
        super.onResume();
    }

    //Activity失去焦点,不再栈顶
    @Override
    protected void onPause() {
        L.e(TAG, "退出栈顶-Pause");
        super.onPause();
    }

    //Activity不可见,被其他Activity覆盖
    @Override
    protected void onStop() {
        L.e(TAG, "退出可见-Stop");
        super.onStop();
    }

    //Activity前台可见,从覆盖它的activity回来
    @Override
    protected void onRestart() {
        L.e(TAG, "重新可见-Restart");
        super.onRestart();
    }

    //Activity被销毁,生命周期结束
    protected void onDestroy() {
        L.e(TAG, "生命结束-Destroy");
        super.onDestroy();
    }

    //保存Activity状态
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        L.e(TAG, "保存状态-Save");
        Bundle save = saveData(outState);
        if (save == null) save = outState;
        save.putInt("sign0", sign[0]);
        super.onSaveInstanceState(save);
    }

    //回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.e(TAG, "Activity回调:" + requestCode + "-" + resultCode + data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置内容布局
     * 与setFragment二选一
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        this.setContentView(LayoutInflater.from(viewContext).inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        if (container == null) createRootLayout();
        packLayout(view);
        super.setContentView(container);
    }

    /**
     * 设置标题
     * 设置为NULL则关闭Activity标题显示(或res为0)
     * 设置为非NULL则关闭Fragment标题显示
     */
    @Override
    public void setTitle(CharSequence title) {
        //TODO:这个地方能不能为空?
        super.setTitle(title);
        setTitleText(title);
    }

    @Override
    public void setTitle(int stringResourceId) {
        if (stringResourceId <= 0) setTitle(null);
        else setTitle(getString(stringResourceId));
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置Fragment
     * 与setContentView二选一
     * 最多添加5个多了无效
     */
    public void setFragment(Class<? extends CDKFragment>... fragment) {
        setContentView(null);
        if (bottomBar == null) createBottomLayout();
        bottomBar.removeAllItem();
        bindingFragmentManager(UArrays.toArray(fragment, CDKFragment.class, this::createFragment));
    }


    /**
     * 切换Fragment页面
     */
    public void setCurrentItem(int index) {
        if (sign[0] == index) return;
        if (fList == null || fList.get(index) == null) throw new ArrayIndexOutOfBoundsException("索引越界");
        //TODO:似乎只能使用资源ID
        int e1 = index > sign[0] ? R.anim.fragment_right_in : R.anim.fragment_left_in;
        int e2 = index > sign[0] ? R.anim.frgament_left_out : R.anim.fragment_right_out;
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(e1, e2)
                .hide(fList.get(sign[0]))
                .show(fList.get(index))
                .commit();
        sign[0] = index;
        if (getTitle() == null) setTitleText(fList.get(index).getTitle());

    }

    /**
     * 获取添加的布局
     */
    public <T extends View> T getLayout() {
        return (T) child;
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 设置实际显示的Title内容 ***/
    private void setTitleText(CharSequence text) {
        if (titleBar != null) {
            if (text == null) {
                titleBar.setVisibility(View.GONE);
            } else {
                titleBar.setVisibility(View.VISIBLE);
                TextView tv = (TextView) titleBar.getChildAt(0);
                tv.setText(text);
            }
        }
    }

    /*** 初始化Context ***/
    private void contextInit(Bundle save) {
        this.TAG = this.getClass().getSimpleName();
        this.saveData = save;
        this.viewContext = new ContextThemeWrapper(this, R.style.Theme_CDK);
        setTitle(null);
        if (save != null) {
            sign[0] = save.getInt("sign0", 0);
        }
    }

    /*** 创建根布局 ***/
    private void createRootLayout() {
        container = new FrameLayout(viewContext);
        container.setId(31636368);
        V.eleDP(container, -100);
        container.setBackgroundResource(R.color.back_all);

        ImageView iv = new ImageView(viewContext);
        V.FL(iv).size(-1, -1).eleDP(-90).parent(container).refresh();
        iv.setScaleType(ImageView.ScaleType.FIT_START);
        iv.setImageResource(R.drawable.bg_cdk_head);

        titleBar = new LinearLayout(viewContext);
        V.FL(titleBar).size(-1, -2).marginDP(40, 60, 0, 40).eleDP(100).parent(container).refresh();
        titleBar.setOrientation(LinearLayout.VERTICAL);
        if (getTitle() == null) titleBar.setVisibility(View.GONE);

        TextView tv = new MaterialTextView(viewContext);
        V.LL(tv).size(-2, -2).parent(titleBar).refresh();
        if (getTitle() != null) tv.setText(getTitle());
        tv.setTextColor(getColor(R.color.text_title));
        tv.setTextSize(20);
        tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    /*** 创建底栏布局 ***/
    private void createBottomLayout() {
        bottomBar = new BottomBar(viewContext);
        V.FL(bottomBar)
                .size(-1, -2)
                .eleDP(100)
                .lGravity(Gravity.BOTTOM)
                .paddingDP(10, 10, 10, 10)
                .marginDP(0, 0, 0, 40)
                .parent(container)
                .refresh();
    }

    /*** 创建Fragment ***/
    private CDKFragment createFragment(Class<? extends CDKFragment> classz) {
        CDKFragment f = null;
        if (saveData == null) {
            try {
                f = classz.getConstructor().newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(container.getId(), f, f.getClass().getSimpleName())
                        .hide(f)
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            f = (CDKFragment) getSupportFragmentManager().findFragmentByTag(classz.getSimpleName());
        }
        f.activity = this;
        f.viewContext = this.viewContext;
        return f;
    }


    /*** 绑定Fragment页面管理器 ***/
    private void bindingFragmentManager(CDKFragment... fragments) {
        if (fList == null) fList = new ArrayList<>(fragments.length);
        fList.clear();
        UArrays.addArrays(fList, fragments);
        fList.forEach(f -> {
            bottomBar.addItem(f.getTitle(), f.getIcon(), v -> {
                setCurrentItem(fList.indexOf(f));
            });
        });
        getSupportFragmentManager().beginTransaction().show(fList.get(sign[0])).commit();
    }


    /*** 包装子View ***/
    private void packLayout(View view) {
        if (this.child != null) container.removeView(this.child);
        this.child = view;
        if (view == null) return;
        view.setElevation(0);
        view.setBackgroundResource(R.color.transparent);
        container.addView(view);
    }
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
}