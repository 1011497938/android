package cn.edu.zju.qcw.android.common.comment.adapter;

import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SQ on 2017/5/17.
 */

public class CommentAdapter extends BaseQuickAdapter<AVObject, BaseViewHolder> {


    @BindView(R.id.userHead)
    ImageView userHead;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.content)
    TextView content;

    public CommentAdapter() {
        super(R.layout.item_comment, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, AVObject item) {
        ButterKnife.bind(this, helper.itemView);

        content.setText(item.getString("content"));
        time.setText(genTimeInterval(item.getCreatedAt()));
        if (AVUser.getCurrentUser() != null) {
            if (item.getAVObject("owner").getObjectId().equals(AVUser.getCurrentUser().getObjectId())) {
                if (AVUser.getCurrentUser().getAVFile("head") == null) return;
                ImageHelper.loadCircleImage(mContext, AVUser.getCurrentUser().getAVFile("head").getUrl(), userHead);
                userName.setText(AVUser.getCurrentUser().getUsername());
                return;
            }
        }
        if (item.getAVObject("owner").get("head") != null && item.getAVObject("owner").get("head") instanceof AVFile) {
            ImageHelper.loadCircleImage(mContext, item.getAVObject("owner").getAVFile("head").getUrl(), userHead);
        } else {
            ImageHelper.loadCircleImage(mContext, null, userHead);
        }
        userName.setText(item.getAVUser("owner").getUsername());
    }

    public String genTimeInterval(Date date) {
        long intevalTime = (new Date().getTime() - date.getTime()) / 1000;
        long minutes = intevalTime / 60;
        long hours = intevalTime / 60 / 60;
        long day = intevalTime / 60 / 60 / 24;
        long month = intevalTime / 60 / 60 / 24 / 30;
        long yers = intevalTime / 60 / 60 / 24 / 365;

        if (minutes <= 5) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (day < 30) {
            return day + "天前";
        } else if (month < 12) {
            return getMonthFormat(date);
        } else if (yers >= 1) {
            return getYearFormat(date);
        }
        return "";
    }

    public String getMonthFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        return sdf.format(date);
    }

    public String getYearFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }
}
