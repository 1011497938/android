package cn.edu.zju.qcw.android.common.audio;

import android.media.MediaPlayer;
import jp.co.recruit_lifestyle.android.widget.PlayPauseButton;

import java.io.IOException;
import java.security.PublicKey;

/**
 * Created by SQ on 2017/6/13.
 */

public class AudioPlayer {
    public static volatile AudioPlayer instance;

    private MediaPlayer mediaPlayer;

    private String url;

    private PlayPauseButton button;

    public static AudioPlayer getInstance() {
        if (instance == null) {
            synchronized (AudioPlayer.class) {
                if (instance == null) {
                    instance = new AudioPlayer();
                    instance.mediaPlayer = new MediaPlayer();
                }
            }
        }
        return instance;
    }

    public void playUrl(String url) {
        if (url.equals(this.url)) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }else{
                mediaPlayer.start();
            }
        }else{
            this.url = url;
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.prepareAsync();
            } catch (IllegalArgumentException e) {
                button.setPlayed(false);
            } catch (IllegalStateException e) {
                button.setPlayed(false);
            } catch (IOException e) {
                button.setPlayed(false);
            }
        }
    }

    public void setButton(PlayPauseButton button) {
        if (this.button != null && this.button != button) {
            this.button.setPlayed(false);
        }
        this.button = button;
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public String getUrl() {
        return url;
    }
}
