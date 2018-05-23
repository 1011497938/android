package cn.edu.zju.qcw.android.util.toast;

import android.widget.Toast;
import cn.edu.zju.qcw.android.PapicApp;

/**
 * Created by SQ on 2017/5/17.
 */

public class ToastHelper {
    public static void showShortToast(String message) {
        Toast.makeText(PapicApp.getAppContext(), message, Toast.LENGTH_SHORT).show();

    }
}
