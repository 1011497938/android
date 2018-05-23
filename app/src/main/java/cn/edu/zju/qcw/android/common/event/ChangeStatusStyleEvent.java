package cn.edu.zju.qcw.android.common.event;

/**
 * Created by SQ on 12/08/2017.
 */

public class ChangeStatusStyleEvent {

    public static final int STYLE_LIGHT = 0;

    public static final int STYLE_DARK = 1;

    public int style;

    public ChangeStatusStyleEvent(int style) {
        this.style = style;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}
