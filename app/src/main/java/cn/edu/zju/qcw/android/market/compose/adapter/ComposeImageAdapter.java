package cn.edu.zju.qcw.android.market.compose.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.base.BaseObserver;
import cn.edu.zju.qcw.android.market.compose.view.ComposeActivity;
import cn.edu.zju.qcw.android.util.SizeUtils;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by SQ on 2017/5/22.
 */

public class ComposeImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    @BindView(R.id.selectedImg)
    ImageView selectedImg;
    @BindView(R.id.deleteImg)
    ImageView deleteImg;

    public ComposeImageAdapter() {
        super(R.layout.item_compose_image, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, final String path) {
        ButterKnife.bind(this, helper.itemView);

        io.reactivex.Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
                e.onNext(ImageHelper.zoomImage(BitmapFactory.decodeFile(path), SizeUtils.getWidth() / 4, SizeUtils.getWidth() / 4));
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Bitmap>(){
                    @Override
                    public void onNext(Bitmap value) {
                        selectedImg.setImageBitmap(value);
                    }
                });
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ComposeActivity)mContext).getPresenter().removeIndex(path);
            }
        });
    }


}
