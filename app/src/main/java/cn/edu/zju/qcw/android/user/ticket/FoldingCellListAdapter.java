package cn.edu.zju.qcw.android.user.ticket;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.util.image.ImageHelper;
import com.avos.avoscloud.AVObject;
import scanqrcodelib.util.QRCodeEncoder;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;

import static cn.edu.zju.qcw.android.util.image.GlideTopRoundTransform.CORNER_BOTTOM_LEFT;
import static cn.edu.zju.qcw.android.util.image.GlideTopRoundTransform.CORNER_TOP_LEFT;
import static cn.edu.zju.qcw.android.util.image.GlideTopRoundTransform.CORNER_TOP_RIGHT;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
public class FoldingCellListAdapter extends ArrayAdapter<AVObject> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;


    public FoldingCellListAdapter(Context context, List<AVObject> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item for selected view
        final AVObject item = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        final ViewHolder viewHolder;
        if (cell == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.ticket_cell, parent, false);
            // binding view parts to view holder
            viewHolder = new ViewHolder(cell);
            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        // bind data from selected element to view through view holder
        viewHolder.titleClubName.setText(item.getAVObject("ticket").getString("owner"));
        viewHolder.titleActivityName.setText(item.getAVObject("ticket").getString("title"));
        viewHolder.titleDate.setText(new SimpleDateFormat("MM月dd日 HH:mm").format(item.getAVObject("ticket").getDate("activityDate")));
        viewHolder.titleLocation.setText(item.getAVObject("ticket").getString("location"));
        viewHolder.activityTitle.setText(item.getAVObject("ticket").getString("title"));
        viewHolder.hosterName.setText(item.getAVObject("ticket").getString("name"));
        viewHolder.location.setText(item.getAVObject("ticket").getString("location"));
        viewHolder.otherInfo.setText(item.getAVObject("ticket").getString("otherInfo"));
        viewHolder.joinWay.setText(item.getAVObject("ticket").getString("joinWay"));
        viewHolder.activityTime.setText(new SimpleDateFormat("HH:mm").format(item.getAVObject("ticket").getDate("activityDate")));
        viewHolder.activityDate.setText(new SimpleDateFormat("MM月dd日").format(item.getAVObject("ticket").getDate("activityDate")));

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap map = QRCodeEncoder.syncEncodeQRCode(item.getObjectId(), 500, Color.BLACK);
                ((Activity) FoldingCellListAdapter.this.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.qrCode.setImageBitmap(map);
                    }
                });
            }
        }).run();

        ImageHelper.loadOptionRoundImage(
                getContext(), item.getAVObject("ticket").getAVFile("headImg").getUrl(), 5, CORNER_TOP_LEFT | CORNER_BOTTOM_LEFT)
                .placeholder(R.drawable.ticket_close_img_placeholder)
                .into(viewHolder.titleImage);
        ImageHelper.loadOptionRoundImage(getContext(), item.getAVObject("ticket").getString("coverUrl"), 5, CORNER_TOP_LEFT | CORNER_TOP_RIGHT)
                .placeholder(R.drawable.ticket_open_poster_bg)
                .into(viewHolder.poster);
        ImageHelper.loadRoundImage(getContext(), item.getAVObject("ticket").getAVFile("headImg").getUrl(), viewHolder.hosterHead, 5);
        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public View.OnClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    }

    static class ViewHolder {
        @BindView(R.id.poster)
        ImageView poster;
        @BindView(R.id.hoster_head)
        ImageView hosterHead;
        @BindView(R.id.hoster_name)
        TextView hosterName;
        @BindView(R.id.location)
        TextView location;
        @BindView(R.id.joinWay)
        TextView joinWay;
        @BindView(R.id.activityTime)
        TextView activityTime;
        @BindView(R.id.activityDate)
        TextView activityDate;
        @BindView(R.id.otherInfo)
        TextView otherInfo;
        @BindView(R.id.qrcode)
        ImageView qrCode;
        @BindView(R.id.title_image)
        ImageView titleImage;
        @BindView(R.id.title_clubName)
        TextView titleClubName;
        @BindView(R.id.title_activityName)
        TextView titleActivityName;
        @BindView(R.id.title_date)
        TextView titleDate;
        @BindView(R.id.title_location)
        TextView titleLocation;
        @BindView(R.id.activityTitle)
        TextView activityTitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
