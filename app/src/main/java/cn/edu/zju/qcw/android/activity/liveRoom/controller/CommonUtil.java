package cn.edu.zju.qcw.android.activity.liveRoom.controller;


import cn.edu.zju.qcw.android.PapicApp;

public class CommonUtil {

    public static int dip2px(float dpValue) {
        float scale = PapicApp.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
