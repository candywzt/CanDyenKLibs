package candyenk.android.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 安卓编辑框工具
 * 静态功能
 * 键盘的显示/影藏
 */
public class UEdit {
    /**
     * 显示输入法
     */
    public static void showKeyBoard(Context context, EditText editText, View parentView) {
        if (editText == null || context == null) return;
        if (parentView == null) parentView = editText;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        imm.toggleSoftInputFromWindow(parentView.getWindowToken(), InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 隐藏输入法
     */
    public static void hideKeyBoard(Context context, EditText editText, View parentView) {
        if (editText == null || context == null) return;
        if (parentView == null) parentView = editText;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(parentView.getWindowToken(), 0);
        editText.clearFocus();
    }
}
