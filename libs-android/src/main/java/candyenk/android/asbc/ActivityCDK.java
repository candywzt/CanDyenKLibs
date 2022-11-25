package candyenk.android.asbc;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import candyenk.android.R;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import candyenk.android.widget.BottomBar;
import candyenk.java.utils.UArrays;
import com.google.android.material.textview.MaterialTextView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CDKActivity
 * 一次重构
 */
public abstract class ActivityCDK extends AppCompatActivity {
    /*************************************静态变量**************************************************/
    //public static boolean useAnimation = true;
    /*************************************成员变量**************************************************/
    protected String TAG;//TAG
    protected Bundle saveData;//保存的数据
    protected FrameLayout container;//根布局
    private final Map<Integer, ActivityCallBack> acbMap = new HashMap<>();//Activity回调表
    private final Map<Integer, PermissionsCallBack> pcbMap = new HashMap<>();//权限申请回调表
    private TextView titleBar;//标题栏控件
    private BottomBar bottomBar;//底栏控件
    private View child;//添加的子控件
    private List<FragmentCDK> fList;//添加的Fragment数组
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
    /**
     * Activity回调
     */
    public interface ActivityCallBack {
        /**
         * @param code 返回结果
         * @param data 返回内容
         * @return 返回false则用后即焚
         */
        boolean callback(int code, Intent data);
    }

    /**
     * 权限申请回调
     */
    public interface PermissionsCallBack {
        /**
         * @param codes 授权结果
         * @return 返回false则用后即焚
         */
        boolean callback(int[] codes);
    }

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
    protected final void onCreate(Bundle save) {
        L.e(TAG, "启动创建-Create");
        contextInit(save);
        super.onCreate(save);
        initLayout();
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
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        L.e(TAG, "保存状态-Save");
        Bundle save = saveData(outState);
        if (save == null) save = outState;
        save.putInt("sign0", sign[0]);
        super.onSaveInstanceState(save);
    }

    //Activity回调函数
    @Override
    protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityCallBack acb = acbMap.get(requestCode);
        if (acb != null && !acb.callback(resultCode, data)) removeActiveCallback(requestCode);
    }

    //权限回调
    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsCallBack pcb = pcbMap.get(requestCode);
        if (pcb != null && !pcb.callback(grantResults)) removePermissionCallback(requestCode);
    }

    /**
     * 设置内容布局
     * 与setFragment二选一
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        this.setContentView(LayoutInflater.from(this).inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        if (this.child != null) container.removeView(this.child);
        this.child = view;
        if (view == null) return;
        view.setElevation(0);
        view.setBackgroundResource(R.color.transparent);
        container.addView(view);
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
    public void setFragment(Class<? extends FragmentCDK>... fragment) {
        setContentView(null);
        if (bottomBar == null) createBottomLayout();
        bottomBar.removeAllItem();
        bindingFragmentManager(UArrays.toArray(fragment, FragmentCDK.class, this::createFragment));
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

    /**
     * 添加Activity回调
     *
     * @param requestCode 回调代码,唯一
     * @param callBack    回调体
     * @return 返回false说明代码重复, 添加失败
     */
    public boolean addActiveCallback(int requestCode, ActivityCallBack callBack) {
        if (acbMap.containsKey(requestCode)) return false;
        acbMap.put(requestCode, callBack);
        return true;
    }

    /**
     * 添加权限申请回调
     *
     * @param requestCode 回调代码,唯一
     * @param callBack    回调体
     * @return 返回false说明代码重复, 添加失败
     */
    public boolean addPermissionCallback(int requestCode, PermissionsCallBack callBack) {
        if (pcbMap.containsKey(requestCode)) return false;
        pcbMap.put(requestCode, callBack);
        return true;
    }

    /**
     * 移除Activity回调
     */
    public void removeActiveCallback(int requestCode) {
        acbMap.remove(requestCode);
    }

    /**
     * 移除权限回调
     */
    public void removePermissionCallback(int requestCode) {
        pcbMap.remove(requestCode);
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
                titleBar.setText(text);
            }
        }
    }

    /*** 初始化Context ***/
    private void contextInit(Bundle save) {
        this.TAG = this.getClass().getSimpleName();
        this.saveData = save;
        setTitle(null);
        if (save != null) {
            sign[0] = save.getInt("sign0", 0);
        }
    }

    /*** 初始化根布局 ***/
    private void initLayout() {
        container = new FrameLayout(this);
        container.setId(31636368);
        V.eleDP(container, -100);
        container.setBackgroundResource(R.color.back_all);
        super.setContentView(container);

        ImageView iv = new ImageView(this);
        V.FL(iv).size(-1, -1).eleDP(-90).parent(container).refresh();
        iv.setScaleType(ImageView.ScaleType.FIT_START);
        iv.setImageResource(R.drawable.bg_cdk_head);

        titleBar = new MaterialTextView(this);
        V.FL(titleBar).size(-1, -2).paddingDP(40, 60, 0, 40).eleDP(100).parent(container).refresh();
        if (getTitle() != null) setTitle(getTitle());
        else titleBar.setVisibility(View.GONE);
        titleBar.setTextColor(getColor(R.color.text_title));
        titleBar.setTextSize(20);
        titleBar.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    /*** 创建底栏布局 ***/
    private void createBottomLayout() {
        bottomBar = new BottomBar(this);
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
    private FragmentCDK createFragment(Class<? extends FragmentCDK> classz) {
        FragmentCDK f = null;
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
            f = (FragmentCDK) getSupportFragmentManager().findFragmentByTag(classz.getSimpleName());
        }
        f.activity = this;
        return f;
    }


    /*** 绑定Fragment页面管理器 ***/
    private void bindingFragmentManager(FragmentCDK... fragments) {
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

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
}