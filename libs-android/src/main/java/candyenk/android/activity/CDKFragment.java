package candyenk.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import candyenk.android.R;
import candyenk.android.utils.L;


public abstract class CDKFragment extends Fragment {
    private FrameLayout container;//根布局
    private LinearLayout titleBar;//底栏控件
    private View childView;//添加的子控件
    private LayoutInflater inflater;
    private CharSequence showTitle;//手动显示的标题
    private Bundle savedInstanceState, saveData;
    protected String TAG;

    /**********************************************************************************************/
    /****************************************抽象方法************************************************/
    /**********************************************************************************************/
    /**
     * @return 用于外部显示的本Fragment的标题
     * 当activity设置null标题时可调用setTitle方法显示Fragment标题
     * 当activity设置“”标题时该标题将被强制显示在activity标题栏
     * 当activity设置任意标题是该标题不会显示
     */
    abstract public CharSequence getTitle();

    /**
     * @return 用于外部显示的本Fragment的图标
     */
    abstract public int getIconResId();

    protected abstract void viewInit();//控件初始化

    protected abstract void contentInit(Bundle savedInstanceState);//内容初始化

    protected abstract void eventInit();//事件初始化

    protected abstract Bundle saveData(Bundle bundle);//保存状态数据

    /**********************************************************************************************/
    /****************************************方法重写************************************************/
    /**********************************************************************************************/

    //关联activity
    @Override
    public void onAttach(Context context) {
        this.TAG = this.getClass().getSimpleName();
        L.e(TAG, "Fragment被关联到Activity");
        super.onAttach(context);
    }

    //Fragment创建
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        L.e(TAG, "Fragment被创建");
        super.onCreate(savedInstanceState);
    }

    //Fragment首次绘制界面
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        L.e(TAG, "Fragment绘制界面");
        this.inflater = inflater;
        viewInit();
        contentInit(savedInstanceState);
        eventInit();
        return this.container;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        L.e(TAG, "Activity的onCreate方法已返回");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        L.e(TAG, "Fragment可见");
        super.onStart();
    }

    @Override
    public void onResume() {
        L.e(TAG, "Fragment处于栈顶");
        super.onResume();
    }

    @Override
    public void onPause() {
        L.e(TAG, "Fragment退出栈顶");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "Fragment不可见");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        L.e(TAG, "Fragment视图被移除");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        L.e(TAG, "Fragment被移除");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        L.e(TAG, "Fragment与Activity关联被移除");
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(saveData(outState));
    }
    /**********************************************************************************************/
    /****************************************私有方法************************************************/
    /**********************************************************************************************/
    //初始化底层布局
    private FrameLayout initLayout(View childView) {
        container = (FrameLayout) inflater.inflate(R.layout.fragment_base, null);
        if (childView != null) {
            this.childView = childView;
            childView.setElevation(0f);//设置子控件高度为0
            container.addView(childView);
        }
        initView();
        return container;
    }

    //初始化标题栏
    private void initView() {
        titleBar = container.findViewById(R.id.base_titleBar);
        if (showTitle != null) {
            setTitle(getTitle());
        }
    }
    /**********************************************************************************************/
    /****************************************公开方法************************************************/
    /**********************************************************************************************/
    /**
     * 获取状态数据
     */
    public Bundle getSaveBundle() {
        return this.savedInstanceState;
    }

    /**
     * 设置保存状态数据
     */
    public void setSaveData(Bundle bundle) {
        this.saveData = bundle;
    }

    /**
     * 设置内容布局
     * 通过布局资源ID
     */
    public void setContentView(int layoutResID) {
        this.setContentView(inflater.inflate(layoutResID, null));
    }

    /**
     * 设置内容布局
     */
    public void setContentView(View view) {
        container = initLayout(view);
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return childView.findViewById(id);
    }

    /**
     * 设置Fragment标题
     * 设置null隐藏标题
     * 只当activity不设置或设置null标题时有效
     */
    public void setTitle(CharSequence title) {
        if (container == null) {
            //尚未初始化时标记开启标题栏
            this.showTitle = title;
        } else {
            TextView tv = (TextView) titleBar.getChildAt(0);
            if (title == null) {
                titleBar.setVisibility(View.GONE);
            } else {
                titleBar.setVisibility(View.VISIBLE);
                tv.setText(title);
            }
        }

    }
}
