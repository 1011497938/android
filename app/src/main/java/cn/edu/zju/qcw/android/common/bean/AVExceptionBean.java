package cn.edu.zju.qcw.android.common.bean;

import cn.edu.zju.qcw.android.base.BaseBean;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import com.avos.avoscloud.AVException;
import com.google.gson.Gson;

/**
 * Created by SQ on 2017/5/19.
 */

public class AVExceptionBean extends BaseBean{
    int code;
    String error;
    AVException e;

    public AVExceptionBean(AVException e) {
        this.e = e;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorMessage() {
        Gson gson = new Gson();
        try {
            AVExceptionBean bean = gson.fromJson(e.getMessage(), AVExceptionBean.class);
            if(bean.getCode() == 202) {
                return StringUtil.genUnhappyFace()+" 用户名已经被占用了";
            }
            return bean.getError();
        }catch (Exception e){
            return "未知错误";
        }
    }

    @Override
    protected String setApi() {
        return null;
    }

    @Override
    protected String setUrlId() {
        return null;
    }
}
