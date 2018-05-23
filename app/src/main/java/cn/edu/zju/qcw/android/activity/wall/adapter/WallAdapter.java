package cn.edu.zju.qcw.android.activity.wall.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.activity.wall.bean.WallBean;
import cn.edu.zju.qcw.android.activity.wall.model.WallModel;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.avos.avoscloud.AVObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by SQ on 2017/5/17.
 */

public class WallAdapter extends BaseQuickAdapter<WallBean, BaseViewHolder> {


    @BindView(R.id.userHead)
    ImageView userHead;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.content)
    TextView content;

    public WallAdapter() {
        super(R.layout.item_wall, new ArrayList<WallBean>());
    }

    @Override
    protected void convert(BaseViewHolder helper, WallBean item) {
        ButterKnife.bind(this, helper.itemView);

        if (!TextUtils.isEmpty(item.getHeadUrl())) {
            ImageHelper.loadCircleImage(mContext, item.getHeadUrl(), userHead);
        }
        if (!TextUtils.isEmpty(item.getContent())){
            content.setText(item.getContent());
        }
        if (!TextUtils.isEmpty(item.getUsername())){
            username.setText(item.getUsername());
        }

    }

}
