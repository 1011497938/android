package cn.edu.zju.qcw.android.market.compose.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.base.mvp_activity.BaseMvpPresenter;
import cn.edu.zju.qcw.android.common.bean.AVExceptionBean;
import cn.edu.zju.qcw.android.market.compose.model.ComposeModel;
import cn.edu.zju.qcw.android.market.compose.view.ComposeActivity;
import cn.edu.zju.qcw.android.util.String.StringUtil;
import cn.edu.zju.qcw.android.util.dialog.DialogUtil;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import cn.edu.zju.qcw.android.util.toast.ToastHelper;
import com.avos.avoscloud.*;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SQ on 2017/5/22.
 */

public class ComposePresenter extends BaseMvpPresenter<ComposeActivity, ComposeModel> {


    public void setImgPath(List<String> imgPath) {
        this.imgPath = imgPath;
    }

    private List<String> imgPath = new ArrayList<>();

    public ComposePresenter(ComposeActivity view) {
        super(view);
    }

    @Override
    public ComposeModel initModel() {
        return new ComposeModel();
    }

    public void initData() {
        getView().getAdapter().setNewData(imgPath);
    }

    public void addPath(String path) {
        if (imgPath.size() < 4) {
            imgPath.add(path);
            if (imgPath.size() == 1) {
                getView().getAdapter().setNewData(imgPath);
            } else {
                getView().getAdapter().notifyItemInserted(imgPath.size());
            }
        }
    }

    public void submit() {
        if (imgPath.isEmpty()) {
            ToastHelper.showShortToast("没有上传图片");
            return;
        }
        DialogUtil.getInstance().showLoading(getView());

        final AVObject object = AVObject.create("Goods");
        object.put("creatBy", AVUser.getCurrentUser());
        object.put("title", getView().title.getText().toString());
        object.put("description", getView().description.getText().toString());
        object.put("price", getView().price.getText().toString());
        object.put("kind",getView().kindArray[getView().getSelectKindValue()]);
        Observable.create(new ObservableOnSubscribe<AVObject>() {
            @Override
            public void subscribe(ObservableEmitter<AVObject> e) throws Exception {
                List<AVFile> list = new ArrayList<AVFile>();
                for (String s : imgPath) {
                    AVFile avfile = AVFile.withAbsoluteLocalPath(object.getObjectId() + "_Goods.jpeg", s);
                    avfile.save();
                    list.add(avfile);
                }
                object.put("firstImg", list.get(0));
                object.put("image", list);
                e.onNext(object);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<AVObject>(){
                    @Override
                    public void onNext(AVObject value) {
                        if (!isActivityBind()) return;
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    ToastHelper.showShortToast(StringUtil.genHappyFace() + " 发布成功");
                                    getView().onBackPressed();
                                }else{
                                    ToastHelper.showShortToast(new AVExceptionBean(e).getErrorMessage());
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastHelper.showShortToast(StringUtil.genUnhappyFace() + "哎呀出错了，检查下网络吧~");
                    }

                    @Override
                    public void onComplete() {
                        DialogUtil.getInstance().closeLoading();
                    }
                });
    }

    public void removeIndex(String path) {
        int index = imgPath.indexOf(path);
        imgPath.remove(path);
        getView().getAdapter().notifyItemRemoved(index);
    }

    public List<String> getImgPath() {
        return imgPath;
    }
}


