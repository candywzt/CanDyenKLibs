package candyenk.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import candyenk.android.R;
import candyenk.android.tools.V;
import com.google.android.material.textview.MaterialTextView;

/**
 * CDK图标项目控件
 * 属性:
 * title(String):标题文本
 * icon(Reference):图标,默认无
 */
public class ItemIcon extends Item {
    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    public ItemIcon(Context context) {
        this(context, null);
    }

    public ItemIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ItemIcon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    protected void initLayout() {
        V.paddingDP(this, 20, 0, 20, 0).orientation(1).gravity(Gravity.CENTER).backgroundRes(R.drawable.bg_cdk).refresh();

        iconView = new ImageView(context);
        V.LL(iconView).sizeDP(40).parent(this).refresh();

        titleView = new MaterialTextView(context);
        V.LL(titleView).size(-2, -2).textSize(18).gravity(Gravity.CENTER).textColorRes(R.color.text_main).parent(this).refresh();
    }


    /**********************************************************************************************/
    /******************************************公共方法*********************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/


}

