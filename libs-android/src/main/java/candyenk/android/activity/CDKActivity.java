package candyenk.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import candyenk.android.R;
import candyenk.android.qq.QQ;
import candyenk.android.utils.L;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import java.util.function.Supplier;

public abstract class CDKActivity extends AppCompatActivity {
    private FrameLayout container;//根布局
    private LinearLayout titleBar, bottomBar;//标题栏控件和底栏控件
    private View childView;//添加的子控件
    private CDKFragment[] fList = new CDKFragment[3];//添加的Pages数组
    private int signPageNum, signIndex, signTitleType;//添加的Pages数量,当前所处的页面index,标题设置类型
    private FragmentManager fm;
    private Bundle savedInstanceState, saveData;
    protected String TAG;

    /**********************************************************************************************/
    /****************************************抽象方法***********************************************/
    /**********************************************************************************************/
    protected abstract void intentInit();//传递初始化

    protected abstract void viewInit();//控件初始化

    protected abstract void contentInit(Bundle savedInstanceState);//内容初始化

    protected abstract void eventInit();//事件初始化

    protected abstract Bundle saveData(Bundle bundle);//保存状态
    /**********************************************************************************************/
    /****************************************方法重写************************************************/
    /**********************************************************************************************/

    //Activity创建,启动的初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.TAG = this.getClass().getSimpleName();
        this.savedInstanceState = savedInstanceState;
        L.e(TAG, "活动创建");
        L.e(TAG, "Intent数据:" + getIntent());
        L.e(TAG, "Bundle数据:" + getSaveBundle());
        super.onCreate(savedInstanceState);
        intentInit();//传递初始化
        viewInit();//控件初始化,包括布局
        contentInit(savedInstanceState);//内容初始化,包括加载布局内容
        eventInit();//事件初始化
        readData();
    }

    //Activity前台可见
    @Override
    protected void onStart() {
        L.e(TAG, "活动可见");
        super.onStart();
        FragmentInit();//Fragment初始化
    }

    //Activity可操作,处于栈顶
    @Override
    protected void onResume() {
        L.e(TAG, "活动处于栈顶");
        super.onResume();
    }

    //Activity失去焦点,不再栈顶
    @Override
    protected void onPause() {
        L.e(TAG, "活动退出栈顶");
        super.onPause();
    }

    //Activity不可见,被其他Activity覆盖
    @Override
    protected void onStop() {
        L.e(TAG, "活动不可见");
        super.onStop();
    }

    //Activity前台可见,从覆盖它的activity回来
    @Override
    protected void onRestart() {
        L.e(TAG, "活动重启");
        super.onRestart();
    }

    //Activity被销毁,生命周期结束
    protected void onDestroy() {
        L.e(TAG, "活动销毁");
        super.onDestroy();
    }

    //保存Activity状态
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        L.e(TAG, "状态被保存");
        super.onSaveInstanceState(getSaveData(outState));
    }

    //回调函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //QQ工具回调函数
        if (QQ.listener != null) {
            if (requestCode == Constants.REQUEST_LOGIN) {
                Tencent.onActivityResultData(requestCode, resultCode, data, QQ.listener);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**********************************************************************************************/
    /****************************************私有方法***********************************************/
    /**********************************************************************************************/
    //设置Fragment页面布局
    private void setContentFragment() {
        this.setContentView(null);//设置布局
        bottomBar.setVisibility(View.VISIBLE);
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (getSaveBundle() == null) {
            for (int i = 0; i < signPageNum; i++) {
                ft.add(container.getId(), fList[i], fList[i].getClass().getSimpleName())
                        .hide(fList[i]);
                addBottomBar(i);//添加一个底栏图标
            }
            ft.show(fList[signIndex]);
            ft.commit();
        } else {
            L.e(TAG, "页面数量:" + signPageNum + "当前页面:" + signIndex);
            for (int i = 0; i < signPageNum; i++) {
                fList[i] = (CDKFragment) fm.findFragmentByTag(fList[i].getClass().getSimpleName());
                addBottomBar(i);//添加一个底栏图标
            }
        }

    }

    //初始化Fragment页面设置
    private void FragmentInit() {
        if (signPageNum > 0) {
            setPages(signIndex);
        }
    }

    //初始化底层布局
    private View initLayout(View childView) {
        container = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        if (childView != null) {
            this.childView = childView;
            childView.setElevation(0f);//设置子控件高度为0
            container.addView(childView);
        }
        initView();
        return container;
    }

    //初始化标题栏和底栏
    private void initView() {
        titleBar = container.findViewById(R.id.base_titleBar);
        bottomBar = container.findViewById(R.id.base_bottomBar);
    }

    //添加底栏按钮
    private void addBottomBar(int index) {
        if (signPageNum == 0 || signPageNum == 1 || signPageNum > 3) {
            L.e(TAG, "跳过按钮添加,页面数量:" + signPageNum);
            return;
        }
        LinearLayout child = null;
        switch (index) {
            case 0:
                if (signPageNum != 1) {
                    child = bottomBar.findViewById(R.id.base_left);
                }
                break;
            case 1:
                if (signPageNum == 2) {
                    child = bottomBar.findViewById(R.id.base_right);
                } else {
                    child = bottomBar.findViewById(R.id.base_center);
                }
                break;
            case 2:
                child = bottomBar.findViewById(R.id.base_right);
                break;
        }

        CDKFragment f = fList[index];
        if (f.getIconResId() != 0) {
            ((ImageView) child.findViewById(R.id.imageView)).setImageResource(f.getIconResId());
        }
        if (f.getTitle() != null) {
            ((TextView) child.findViewById(R.id.textView)).setText(f.getTitle());
        }
        child.setOnClickListener((v) -> {
            setPages(index);
        });

        child.setVisibility(View.VISIBLE);
    }

    //保存当前页面数据
    private Bundle getSaveData(Bundle bundle) {
        bundle.putInt("signIndex", this.signIndex);
        if (saveData(bundle) != null) {
            return saveData(bundle);
        } else {
            return bundle;
        }

    }

    //还原当前页面数据
    private void readData() {
        Bundle bundle = getSaveBundle();
        if (bundle != null) {
            this.signIndex = bundle.getInt("signIndex");
        }
    }
    /**********************************************************************************************/
    /****************************************公开方法***********************************************/
    /**********************************************************************************************/
    /**
     * 获取保存的状态
     */
    public Bundle getSaveBundle() {
        return this.savedInstanceState;
    }

    /**
     * 获取添加的字布局
     */
    public <T extends ViewGroup> T getLayout() {
        return (T) childView;
    }

    /**
     * 设置要保存的数据
     */
    public void setSaveData(Bundle bundle) {
        this.saveData = bundle;
    }

    /**
     * 设置内容布局
     * 通过布局资源ID
     */
    @Override
    public void setContentView(int layoutResID) {
        this.setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    /**
     * 设置内容布局
     * 通过布局View控件,记得移除View的父级
     */
    @Override
    public void setContentView(View view) {
        super.setContentView(initLayout(view));
    }

    /**
     * 设置内容布局
     * 设置多Fragment切换布局
     * 函数
     */
    public void setContentView(int index, Supplier<CDKFragment>... sup) {
        for (Supplier<CDKFragment> f : sup) {
            if (signPageNum < 3) {
                fList[signPageNum] = f.get();
                signPageNum++;
            } else {
                L.e(TAG, "添加的<sup>Fragment超出3个,对超出的部分不做处理");
            }
        }
        if (index > signPageNum - 1) {
            L.e(TAG, "设置的页面index超出现有页面数量,做默认0处理");
            signIndex = 0;
        } else {
            signIndex = index;
        }
        setContentFragment();
    }

    /**
     * 设置内容布局
     * 设置多Fragment切换布局
     */
    public void setContentView(int index, CDKFragment... fragments) {
        for (CDKFragment f : fragments) {
            if (signPageNum < 3) {
                fList[signPageNum] = f;
                signPageNum++;
            } else {
                L.e(TAG, "添加的Fragment超出3个,对超出的部分不做处理");
            }
        }
        if (index > signPageNum - 1) {
            L.e(TAG, "设置的页面index超出现有页面数量,做默认0处理");
            signIndex = 0;
        } else {
            signIndex = index;
        }
        setContentFragment();
    }

    /**
     * <br>设置标题
     * <br>设置内容则强制关闭Fragment标题显示
     * <br>内容为NULL则关闭activity标题栏,由Fragment设置标题(默认)
     * <br>内容设置为""(空字符串),则在Activity标题栏强制显示Fragment标题
     */
    public void setTitle(CharSequence title) {
        if (container == null) {
            L.e(TAG, "请先添加布局再设置Activity标题");
            return;
        }
        TextView tv = (TextView) titleBar.getChildAt(0);
        if (title == null) {
            signTitleType = 0;
            titleBar.setVisibility(View.GONE);
        } else if ("".equals(title)) {
            signTitleType = -1;
        } else {
            signTitleType = 1;
            titleBar.setVisibility(View.VISIBLE);
            tv.setText(title);
        }
    }

    public void setTitleText(int stringResourceId) {
        setTitle(getString(stringResourceId));
    }

    /**
     * 切换Page页面
     */
    public void setPages(int index) {
        if (index > signPageNum - 1 || index < 0) {
            L.e(TAG, "所选页不存在,切换失败,页数:" + signPageNum + ",切换页:" + index);
            return;
        }
        CDKFragment f = fList[index];
        if (index != signIndex) {
            int e1 = index > signIndex ? R.anim.fragment_right_in : R.anim.fragment_left_in;
            int e2 = index > signIndex ? R.anim.frgament_left_out : R.anim.fragment_right_out;
            fm.beginTransaction()
                    .setCustomAnimations(e1, e2)
                    .hide(fList[signIndex])
                    .show(f)
                    .commit();
            signIndex = index;
        }
        if (signTitleType == -1) {
            //设置Fragment标题为当前Activity标题
            //隐藏Fragment标题
            CharSequence title = f.getTitle();
            TextView tv = (TextView) titleBar.getChildAt(0);
            if (title == null) {
                titleBar.setVisibility(View.GONE);
            } else {
                titleBar.setVisibility(View.VISIBLE);
                tv.setText(title);
            }
            f.setTitle(null);
        } else if (signTitleType == 1) {
            //强制关闭Fragment标题显示
            f.setTitle(null);
        }


    }

    /**
     * 开启多功能按钮
     * 与底栏按钮选其一
     * 不允许长按
     */
    public void setMultiFunctionButton(boolean b) {
        //TODO:多功能按钮没实现
        //TODO:长按下拉屏幕没实现
        bottomBar.setVisibility(View.VISIBLE);
        LinearLayout funView = bottomBar.findViewById(R.id.base_center);
        funView.setVisibility(View.VISIBLE);

    }


}