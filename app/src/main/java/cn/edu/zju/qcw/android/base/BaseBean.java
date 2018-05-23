package cn.edu.zju.qcw.android.base;

/**
 * Created by SQ on 2017/3/26.
 */

public abstract class BaseBean {

    protected final String baseUrl = "https://papiccms.applinzi.com/";

    public String getUrl() {
        if (setApi() == null || setUrlId() == null) {
            return null;
        }
        return baseUrl + setApi() + setUrlId();
    }

    public String getTitle() {
        return setTitle();
    }

    protected String setTitle() {
        return null;
    }

    protected abstract String setApi();

    protected abstract String setUrlId();

}
