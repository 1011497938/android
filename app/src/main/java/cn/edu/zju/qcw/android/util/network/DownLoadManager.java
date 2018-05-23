package cn.edu.zju.qcw.android.util.network;

import android.content.Context;
import android.util.Log;
import okhttp3.ResponseBody;

import java.io.*;

/**
 * Created by SQ on 2017/5/24.
 */

public class DownLoadManager {

    private static final String TAG = "DownLoadManager";

    private static String APK_CONTENTTYPE = "application/vnd.android.package-archive";

    private static String PNG_CONTENTTYPE = "image/png";

    private static String JPG_CONTENTTYPE = "image/jpg";

    public static  String MP4_CONTENTTYPE = "video/mp4";

    public static String AVI_CONTENTTYPE = "video/x-msvideo";

    public static String MOV_CONTENTTYPE = "video/quicktime";



    private static String fileSuffix = "";

    public static String writeResponseBodyToDisk(Context context, ResponseBody body) {

        Log.d(TAG, "contentType:>>>>" + body.contentType().toString());

        String type = body.contentType().toString();

        if (type.equals(APK_CONTENTTYPE)) {

            fileSuffix = ".apk";
        } else if (type.equals(PNG_CONTENTTYPE)) {
            fileSuffix = ".png";
        } else if (type.equals(MP4_CONTENTTYPE)){
            fileSuffix = ".mp4";
        } else if (type.equals(AVI_CONTENTTYPE)) {
            fileSuffix = ".avi";
        } else if (type.equals(MOV_CONTENTTYPE)) {
            fileSuffix = ".mov";
        }

        // 其他类型同上 自己判断加入.....


        String path = context.getExternalFilesDir(null) + File.separator + "ad" + fileSuffix;


        Log.d(TAG, "path:>>>>" + path);

        try {
            File futureStudioIconFile = new File(path);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();


                return path;
            } catch (IOException e) {
                return "";
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return "";
        }
    }
}