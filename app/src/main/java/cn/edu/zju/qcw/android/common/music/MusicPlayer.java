package cn.edu.zju.qcw.android.common.music;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by SQ on 2017/5/25.
 */

public class MusicPlayer implements MediaPlayer.OnPreparedListener{

    private volatile static MusicPlayer instance;

    private MediaPlayer mediaPlayer;

    private String url;

    public static MusicPlayer getInstance() {
        if (instance == null) {
            synchronized (MusicPlayer.class) {
                if (instance == null) {
                    instance = new MusicPlayer();
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setOnPreparedListener(instance);
                    instance.mediaPlayer = mediaPlayer;
                }
            }
        }
        return instance;
    }

    public boolean playUrl(String url) {
        if (url.equals(this.url)){
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                return false;
            }else{
                mediaPlayer.start();
                return true;
            }
        }
        this.url = url;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();//prepare之后自动播放
            return true;
            //mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
