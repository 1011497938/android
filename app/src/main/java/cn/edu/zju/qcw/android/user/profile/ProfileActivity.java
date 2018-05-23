package cn.edu.zju.qcw.android.user.profile;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpActivty;
import cn.edu.zju.qcw.android.base.widget.TitleBar;
import cn.edu.zju.qcw.android.user.profile.clipimage.ClipImageActivity;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import cn.edu.zju.qcw.android.util.ticket.UriUtils;
import com.avos.avoscloud.AVUser;

import java.io.File;

/**
 * Created by SQ on 2017/5/22.
 */

public class ProfileActivity extends BaseMvpActivty<ProfilePresenter> {

    private final int INTENT_GET_PHOTO = 100;

    private final int INTENT_TAKE_PHOTO = 200;

    public static final int REQUEST_CLIP_IMAGE = 2028;

    private String outputPath;

    @BindView(R.id.user_profile_titlebar)
    TitleBar titleBar;
    @BindView(R.id.headImg)
    ImageView headImg;
    @BindView(R.id.headCell)
    RelativeLayout headCell;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.logoutCell)
    RelativeLayout logoutCell;

    @Override
    protected int initLayout() {
        return R.layout.activity_profile;
    }

    @Override
    protected ProfilePresenter initPresenter() {
        return new ProfilePresenter(this);
    }

    @Override
    protected void init() {
        titleBar.setRightEnable(false);
        getPresenter().getData();
    }

    @Override
    protected boolean isThemeStyle() {
        return true;
    }

    @Override
    protected void initListeners() {
        titleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().save(username.getText().toString(), outputPath);
            }
        });
    }

    public void setViewWithData(final AVUser user) {
        if (user.getAVFile("head") != null) {
            ImageHelper.loadCircleImage(this, user.getAVFile("head").getUrl(), headImg);
        }
        username.setText(user.getUsername());
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                titleBar.setRightEnable(s.length() > 0 && !username.getText().equals(user.getUsername()));
            }
        });
    }


    @OnClick({R.id.headCell, R.id.logoutCell})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.headCell:
                DialogUtil.showListDialog(ProfileActivity.this, null, new String[]{"相册", "拍照"}, new DialogInterface.OnClickListener() {
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
                break;
            case R.id.logoutCell:
                DialogUtil.showNormalDialog(this, "确定要退出登录吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPresenter().logout();
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case INTENT_TAKE_PHOTO:
            case INTENT_GET_PHOTO:
                if (null != data) {
                    Uri mImageCaptureUri = data.getData();
                    String imgagePath = UriUtils.getImageAbsolutePath(this, mImageCaptureUri);
                    if (TextUtils.isEmpty(imgagePath)) return;
                    outputPath = new File(getExternalCacheDir(), "userHead.png").getPath();
                    ClipImageActivity.prepare()
                            .aspectX(1).aspectY(1)//裁剪框横向及纵向上的比例
                            .inputPath(imgagePath).outputPath(outputPath)
                            .startForResult(this, REQUEST_CLIP_IMAGE);
                }
                break;
            case REQUEST_CLIP_IMAGE:
                String path = ClipImageActivity.ClipOptions.createFromBundle(data).getOutputPath();
                if (path != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    headImg.setImageBitmap(bitmap);
                    titleBar.setRightEnable(true);
                }
        }
    }
}
