package cn.edu.zju.qcw.android.parttime.form;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import me.riddhimanadib.formmaster.helper.FormBuildHelper;
import me.riddhimanadib.formmaster.model.FormElement;
import me.riddhimanadib.formmaster.model.FormHeader;
import me.riddhimanadib.formmaster.model.FormObject;

import java.util.ArrayList;
import java.util.List;

public class ParttimeFormActivity extends BaseMvpActivty<ParttimeFormPresenter> {

    public static final String PARTTIME_ID = "parttimeID";

    private RecyclerView mRecyclerView;
    private FormBuildHelper mFormBuilder;

    @BindView(R.id.parttime_register_titlebar)
    TitleBar titleBar;

    @Override
    protected int initLayout() {
        return R.layout.activity_parttime_form;
    }

    @Override
    protected ParttimeFormPresenter initPresenter() {
        return new ParttimeFormPresenter(this);
    }

    @Override
    protected void init() {
        setupForm();
    }

    @Override
    protected void initListeners() {
        titleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFormBuilder.isValidForm()){
                    getPresenter().submit(mFormBuilder);
                }else{
                    ToastHelper.showShortToast(StringUtil.genHappyFace() + " 信息要填完整哦");
                }
            }
        });
    }

    private void setupForm() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mFormBuilder = new FormBuildHelper(this, mRecyclerView);

        FormHeader header0 = FormHeader.createInstance().setTitle("");
        FormElement number = FormElement.createInstance().setType(FormElement.TYPE_EDITTEXT_EMAIL).setTitle("兼职编号").setHint("必填").setRequired(true);
        if (!TextUtils.isEmpty(getIntent().getStringExtra(PARTTIME_ID))){
            number.setValue(getIntent().getStringExtra(PARTTIME_ID));
        }

        FormHeader header1 = FormHeader.createInstance().setTitle("信息");
        FormElement name = FormElement.createInstance().setType(FormElement.TYPE_EDITTEXT_TEXT_SINGLELINE).setTitle("姓名").setHint("必填").setRequired(true);

        List<String> genderList = new ArrayList<>();
        genderList.add("男");
        genderList.add("女");
        FormElement gender = FormElement.createInstance().setType(FormElement.TYPE_SPINNER_DROPDOWN).setTitle("性别").setOptions(genderList);
        FormElement studentNum = FormElement.createInstance().setType(FormElement.TYPE_EDITTEXT_NUMBER).setTitle("学号").setHint("必填").setRequired(true);
        FormElement phone = FormElement.createInstance().setType(FormElement.TYPE_EDITTEXT_NUMBER).setTitle("电话").setHint("必填").setRequired(true);

        List<String> gradeList = new ArrayList<>();
        gradeList.add("大一");
        gradeList.add("大二");
        gradeList.add("大三");
        gradeList.add("大四");
        gradeList.add("研究生");
        gradeList.add("其他");
        FormElement grade = FormElement.createInstance().setType(FormElement.TYPE_SPINNER_DROPDOWN).setTitle("年级").setOptions(gradeList);

        FormElement major = FormElement.createInstance().setType(FormElement.TYPE_EDITTEXT_TEXT_SINGLELINE).setTitle("专业");

        List<String> locationList = new ArrayList<>();
        locationList.add("紫金港");
        locationList.add("玉泉");
        locationList.add("西溪");
        locationList.add("之江");
        locationList.add("华家池");
        locationList.add("舟山");
        FormElement location = FormElement.createInstance().setType(FormElement.TYPE_SPINNER_DROPDOWN).setTitle("校区").setOptions(locationList);

        FormHeader header2 = FormHeader.createInstance().setTitle("关于你");
        FormElement about = FormElement.createInstance().setType(FormElement.TYPE_EDITTEXT_TEXT_MULTILINE).setTitle("自我介绍");


        List<FormObject> formItems = new ArrayList<>();
        formItems.add(header0);
        formItems.add(number.setTag(0));
        formItems.add(header1);
        formItems.add(name.setTag(1));
        formItems.add(gender.setTag(2));
        formItems.add(studentNum.setTag(3));
        formItems.add(phone.setTag(4));
        formItems.add(grade.setTag(5));
        formItems.add(major.setTag(6));
        formItems.add(location.setTag(7));
        formItems.add(header2);
        formItems.add(about.setTag(8));
        mFormBuilder.addFormElements(formItems);
        mFormBuilder.refreshView();
    }
}
