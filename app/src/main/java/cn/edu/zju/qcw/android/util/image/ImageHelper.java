package cn.edu.zju.qcw.android.util.image;

import android.content.Context;
import android.graphics.*;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;

import cn.edu.zju.qcw.android.R;
import cn.edu.zju.qcw.android.util.Logger;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * Created by SQ on 2017/3/27.
 */

public class ImageHelper {

    public static final int ACTIVITY_IMAGE_RADIUS = 3;

    public static void loadImage(Fragment fragment, String url, ImageView imageView) {
        if (TextUtils.isEmpty(url)) return;

        Glide.with(fragment)
                .load(url)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        if (TextUtils.isEmpty(url)) return;

        Glide.with(context)
                .load(url)
                .centerCrop()
                .crossFade()
                .placeholder(ContextCompat.getDrawable(context, R.drawable.common_rect_placeholer))
                .into(imageView);
    }

    public static void loadCircleImage(Context context, String url, ImageView imageView) {
        try{
            Glide.with(context)
                    .load(url)
                    .crossFade()
                    .transform(new CenterCrop(context), new GlideCircleTransform(context))
                    .placeholder(R.drawable.placeholder_round)
                    .into(imageView);
        }catch (Exception e) {
            Logger.e("catch exception of load circle image by glide");
        }
    }

    public static void loadRoundImage(Context context, String url, ImageView imageView, float Radius) {
        if (TextUtils.isEmpty(url)) return;
        Glide.with(context)
                .load(url)
                .crossFade()
                .transform(new CenterCrop(context), new GlideRoundTransform(context, Radius))
                .into(imageView);
    }

    public static DrawableRequestBuilder<String> loadOptionRoundImage(Context context, String url, float dpRadius, int corners) {
        return Glide.with(context)
                .load(url)
                .crossFade()
                .transform(new CenterCrop(context), new GlideTopRoundTransform(context, dpRadius, corners));
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    public static Bitmap compress(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            // 循环判断如果压缩后图片是否大于50kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            // 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
            if (options <= 0) {
                break;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        // 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}

