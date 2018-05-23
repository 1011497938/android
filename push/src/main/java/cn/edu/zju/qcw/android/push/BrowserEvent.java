package cn.edu.zju.qcw.android.push;

/**
 * Created by SQ on 2017/5/18.
 */

public class BrowserEvent {

    String url;

    public BrowserEvent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
