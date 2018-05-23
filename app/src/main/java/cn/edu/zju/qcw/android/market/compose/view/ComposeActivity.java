package cn.edu.zju.qcw.android.market.compose.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.SpacesItemDecoration;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.widget.SQNumberPicker;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.market.compose.adapter.ComposeImageAdapter;
import cn.edu.zju.qcw.android.market.compose.presenter.ComposePresenter;
import cn.edu.zju.qcw.android.user.profile.clipimage.ClipImageActivity;
import cn.edu.zju.qcw.android.util.Animation.AnimUtil;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import cn.edu.zju.qcw.android.util.ticket.UriUtils;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.File;

public class ComposeActivity extends BaseMvpActivty<ComposePresenter> {

    private final int INTENT_GET_PHOTO = 100;

    private final int INTENT_TAKE_PHOTO = 200;

    public static final int REQUEST_CLIP_IMAGE = 2028;

    @BindView(R.id.market_compose_titlebar)
    public TitleBar titleBar;
    @BindView(R.id.title)
    public TextInputEditText title;
    @BindView(R.id.description)
    public EditText description;
    @BindView(R.id.price)
    public EditText price;
    @BindView(R.id.images)
    public RecyclerView imageView;
    @BindView(R.id.addImg)
    ImageView addImg;
    @BindView(R.id.selectKind)
    ImageView selectKind;
    @BindView(R.id.kindPicker)
    public SQNumberPicker picker;
    @BindView(R.id.pickerContainer)
    RelativeLayout pickerContainer;

    @BindArray(R.array.compose_kind)
    public String[] kindArray;

    private ComposeImageAdapter adapter;

    private Integer selectKindValue;

    @Override
    protected int initLayout() {
        return R.layout.activity_compose;
    }

    @Override
    protected ComposePresenter initPresenter() {
        return new ComposePresenter(this);
    }

    @Override
    protected void init() {
        titleBar.setRightEnable(false);

        adapter = new ComposeImageAdapter();
        adapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return 1;
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        imageView.setLayoutManager(gridLayoutManager);
        imageView.setAdapter(adapter);
        imageView.addItemDecoration(new SpacesItemDecoration(10));

        picker.setDisplayedValues(kindArray);
        picker.setMinValue(0);
        picker.setMaxValue(kindArray.length - 1);
        picker.setValue(0);
        picker.setDividerColor("#7DFFFFFF");

        getPresenter().initData();
    }

    @Override
    protected void initListeners() {
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPresenter().getImgPath().size() > 3) {
                    ToastHelper.showShortToast(StringUtil.genHappyFace() + " 最多只能上传四张图片哦");
                    return;
                }
                DialogUtil.showListDialog(ComposeActivity.this, null, new String[]{"相册", "拍照"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, INTENT_GET_PHOTO);
                        }
                        if (i == 1) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                                }
                            }
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, INTENT_TAKE_PHOTO);
                        }
                    }
                });
            }
        });
        selectKind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePicker();
            }
        });

        titleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                titleBar.setRightEnable(
                        title.getText().length() > 0
                                && description.getText().length() > 0
                                && price.getText().length() > 0
                );
            }
        };
        title.addTextChangedListener(textWatcher);
        description.addTextChangedListener(textWatcher);
        price.addTextChangedListener(textWatcher);
    }

    public ComposeImageAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case INTENT_TAKE_PHOTO:
            case INTENT_GET_PHOTO://相册选择回来的回调
                if (null != data) {
                    Uri mImageCaptureUri = data.getData();
                    String imgagePath = UriUtils.getImageAbsolutePath(this, mImageCaptureUri);
                    if (TextUtils.isEmpty(imgagePath)) return;
                    String outputPath = new File(getExternalCacheDir(), "selectedimg" + String.valueOf(getAdapter().getData().size()) + ".png").getPath();
                    ClipImageActivity.prepare()
                            .aspectX(1).aspectY(1)//裁剪框横向及纵向上的比例
                            .inputPath(imgagePath).outputPath(outputPath)
                            .startForResult(this, REQUEST_CLIP_IMAGE);
                }
                break;
            case REQUEST_CLIP_IMAGE:
                String path = ClipImageActivity.ClipOptions.createFromBundle(data).getOutputPath();
                if (path != null) {
                    getPresenter().addPath(path);
                }
        }
    }

    private void submit() {
        if (getPresenter().getImgPath().size() == 0) {
            ToastHelper.showShortToast(StringUtil.genHappyFace() + " 至少要上传一张图片哦");
            return;
        }
        if (TextUtils.isEmpty(title.getText().toString()) || TextUtils.isEmpty(description.getText().toString())) {
            ToastHelper.showShortToast(StringUtil.genHappyFace() + " 信息要填写完整哦");
            return;
        }
        if (selectKindValue == null) {
            ToastHelper.showShortToast(StringUtil.genHappyFace() + " 还没有选择分类哦");
            return;
        }
        if (TextUtils.isEmpty(price.getText().toString())) {
            ToastHelper.showShortToast(StringUtil.genHappyFace() + " 还没有填写价格哦");
            return;
        }
        getPresenter().submit();
    }

    private void togglePicker() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(picker.getApplicationWindowToken(), 0);
        }
        if (pickerContainer.getVisibility() == View.VISIBLE) {
            AnimUtil.alphaAnimation(pickerContainer, 1, 0, 500, null);
            return;
        }
        if (pickerContainer.getVisibility() == View.GONE) {
            AnimUtil.alphaAnimation(pickerContainer, 0, 1, 500, null);
        }
    }


    @OnClick({R.id.kindCancel, R.id.kindConfirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.kindCancel:
                togglePicker();
                break;
            case R.id.kindConfirm:
                togglePicker();
                selectKindValue = picker.getValue();
                break;
        }
    }

    public Integer getSelectKindValue() {
        return selectKindValue;
    }

}
