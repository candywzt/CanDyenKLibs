package candyenk.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
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
    @Override
    protected void initLayout() {
        this.setGravity(Gravity.CENTER);
        this.setOrientation(VERTICAL);
        this.setBackgroundResource(R.drawable.bg_cdk);

        iconView = new ImageView(context);
        V.LL(iconView).padding(8).sizeDP(48).parent(this);

        titleView = new MaterialTextView(context);
        V.LL(titleView).size(-2, -2).textSize(16).gravity(Gravity.CENTER).textColorRes(R.color.text_main).parent(this);
    }

    @Override
    public void setIconResource(int res) {
        if (iconView == null) return;
        if (res > 0) {
            V.LL(iconView).size(V.UN, V.getLLP(iconView).width).drawable(res).visible().refresh();
        } else {
            V.LL(iconView).size(V.UN, -2).visibility(res == 0 ? View.INVISIBLE : View.GONE).refresh();
        }
    }

    @Override
    public void setIconDrawable(Drawable drawable) {
        if (iconView == null) return;
        if (drawable != null) {
            V.LL(iconView).size(V.UN, V.getLLP(iconView).width).drawable(drawable).visible().refresh();
        } else {
            V.LL(iconView).size(V.UN, -2).invisible().refresh();
        }
    }

    /**********************************************************************************************/
    /******************************************公共方法*********************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/


}

