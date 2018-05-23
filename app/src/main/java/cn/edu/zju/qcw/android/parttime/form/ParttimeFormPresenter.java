package cn.edu.zju.qcw.android.parttime.form;

import android.widget.Toast;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.common.bean.AVExceptionBean;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import me.riddhimanadib.formmaster.helper.FormBuildHelper;

/**
 * Created by SQ on 2017/5/23.
 */

public class ParttimeFormPresenter extends BaseMvpPresenter<ParttimeFormActivity, ParttimeFormModel> {
    public ParttimeFormPresenter(ParttimeFormActivity view) {
        super(view);
    }

    @Override
    public ParttimeFormModel initModel() {
        return new ParttimeFormModel();
    }

    public void submit(FormBuildHelper buildHelper) {
        DialogUtil.getInstance().showLoading(getView());

        AVObject object = AVObject.create("Parttime");
        object.put("NO", buildHelper.getFormElement(0).getValue());
        object.put("name", buildHelper.getFormElement(1).getValue());
        object.put("sex", buildHelper.getFormElement(2).getValue());
        object.put("section", buildHelper.getFormElement(7).getValue());
        object.put("grade", buildHelper.getFormElement(5).getValue());
        object.put("major", buildHelper.getFormElement(6).getValue());
        object.put("phone", Long.valueOf(buildHelper.getFormElement(4).getValue()));
        object.put("xh", Long.valueOf(buildHelper.getFormElement(3).getValue()));
        object.put("intro", buildHelper.getFormElement(8).getValue());
        object.put("owner", AVUser.getCurrentUser());

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null){
                    ToastHelper.showShortToast("报名成功！可至个人中心查看已报名兼职");
                    getView().onBackPressed();
                }else{
                    ToastHelper.showShortToast(new AVExceptionBean(e).getErrorMessage());
                }
                DialogUtil.getInstance().closeLoading();
            }
        });
    }
}
