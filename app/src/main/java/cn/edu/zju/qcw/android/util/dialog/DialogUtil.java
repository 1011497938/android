package cn.edu.zju.qcw.android.util.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import cn.edu.zju.qcw.android.PapicApp;
import cn.edu.zju.qcw.android.base.widget.LoadingDialog;

/**
 * Created by SQ on 2017/5/24.
 */

public class DialogUtil {

    private volatile static DialogUtil instance;

    LoadingDialog loadingDialog;

    public static DialogUtil getInstance() {
        if (instance == null) {
            synchronized (DialogUtil.class) {
                if (instance == null) {
                    instance = new DialogUtil();
                }
            }
        }
        return instance;
    }

    public boolean isShowing(){
        return loadingDialog != null;
    }

    public void showLoading(Context context) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context, "");
        }
        loadingDialog.show();
    }

    public void closeLoading() {
        if (loadingDialog == null) {
            return;
        }
        loadingDialog.close();
        loadingDialog = null;
    }

    public static void showListDialog(Context context, String title, String[] items, DialogInterface.OnClickListener listener) {

        AlertDialog.Builder listDialog = new AlertDialog.Builder(context);
        listDialog.setTitle(title);
        listDialog.setItems(items, listener);
        listDialog.show();
    }

    public static void showNormalDialog(Context context, String title, DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setMessage(title);
        normalDialog.setNegativeButton("确定", listener);
        normalDialog.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        normalDialog.show();
    }

    public static void showNormalDialog(Context context, String title, DialogInterface.OnClickListener confirmListener, DialogInterface.OnClickListener cancelListener) {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setMessage(title);
        normalDialog.setNegativeButton("确定", confirmListener);
        normalDialog.setPositiveButton("取消", cancelListener);
        normalDialog.show();
    }
}
