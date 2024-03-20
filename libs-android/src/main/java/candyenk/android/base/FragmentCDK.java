package candyenk.android.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import candyenk.android.R;
import org.jetbrains.annotations.NotNull;

/**
 * CDKFragment
 * 就配合CDKActivity用
 * 一次重构
 */
public abstract class FragmentCDK extends Fragment {
    /*************************************静态变量**************************************************/
    //public static boolean useAnimation = true;
    /*************************************成员变量**************************************************/
    protected String TAG = this.getClass().getSimpleName();
    protected ActivityCDK activity;//创建和使用本Fragment的Activity
    protected Bundle saveData;//保存的数据
    protected ViewGroup parent;//父级控件
    protected View container;//根控件
    protected LayoutInflater inflater;
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
    protected abstract void viewInit();//控件初始化

    protected abstract void contentInit(Bundle save);//内容初始化

    protected abstract void eventInit();//事件初始化

    protected abstract Bundle saveData(Bundle bundle);//保存状态数据

    /**
     * 获取当前Fragment的图标
     * 子类重写该方法
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public Drawable getIcon() {
        return activity.getDrawable(R.drawable.ic_file_picture);
    }

    /**
     * 获当前Fragment的标题
     * 子类重写该方法
     */
    public CharSequence getTitle() {
        return TAG;
    }

    //关联activity
    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    //Fragment创建
    @Override
    public void onCreate(@Nullable Bundle saveData) {
        super.onCreate(saveData);
    }

    //Fragment首次绘制界面
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle saveData) {
        this.inflater = inflater;
        this.saveData = saveData;
        this.parent = container;
        viewInit();
        contentInit(saveData);
        eventInit();
        return this.container;
    }

    //Activity的OnCreate方法结束
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //Fragment可见
    @Override
    public void onStart() {
        super.onStart();
    }

    //Fragment处于栈顶
    @Override
    public void onResume() {
        super.onResume();
    }

    //Fragment退出栈顶
    @Override
    public void onPause() {
        super.onPause();
    }

    //Fragment不可见
    @Override
    public void onStop() {
        super.onStop();
    }

    //Fragment视图被移除
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //Fragment被移除
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Fragment与Activity关联被移除
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(saveData(outState));
    }

    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置内容布局
     * 通过布局资源ID
     */
    public void setContentView(int layoutResID) {
        this.setContentView(inflater.inflate(layoutResID, parent, false));
    }

    /**
     * 设置内容布局
     * 与setFragment二选一
     */
    public void setContentView(View view) {
        this.container = view;
    }

    /**
     * 通过ID查找控件
     */
    public <T extends View> T findViewById(@IdRes int id) {
        if (container == null) return null;
        return container.findViewById(id);
    }

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/

}
