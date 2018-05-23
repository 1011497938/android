package cn.edu.zju.qcw.android.activity.livelist.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import cn.edu.zju.qcw.android.util.network.NetworkHelper;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created by SQ on 2017/5/17.
 */

public class LiveListAdapter extends BaseQuickAdapter<AVObject, BaseViewHolder> {


    @BindView(R.id.coverImg)
    ImageView coverImg;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.ownerImg)
    ImageView ownerImg;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.mask)
    ImageView mask;
    @BindView(R.id.username)
    TextView username;

    public LiveListAdapter() {
        super(R.layout.item_live, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, final AVObject item) {
        ButterKnife.bind(this, helper.itemView);

        ImageHelper.loadImage(mContext, item.getString("coverUrl"), coverImg);

        if (item.getAVObject("user").get("head") != null && item.getAVObject("user").get("head") instanceof AVFile) {
            ImageHelper.loadCircleImage(mContext, item.getAVObject("user").getAVFile("head").getUrl(), ownerImg);
        } else {
            ImageHelper.loadCircleImage(mContext, null, ownerImg);
        }

        if (!TextUtils.isEmpty(item.getString("title"))) {
            title.setText(item.getString("title"));
            mask.setVisibility(View.VISIBLE);
        }
        username.setText(item.getAVUser("user").getUsername());

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result = NetworkHelper.getStringWithUrl("https://papiccms.applinzi.com/api/liveRoom/" + item.getObjectId());
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        number.setText(result.split("\"")[1]);//这个api太愚蠢了……凑活用吧……
                    }
                });
            }
        }).start();
    }
}
