package cn.edu.zju.qcw.android.util.String;

/**
 * Created by SQ on 2017/5/15.
 */

public class StringUtil {
    public static String genHappyFace() {
        String[] list = new String[]{"｡:.ﾟヽ(｡◕‿◕｡)ﾉﾟ.:｡+ﾟ","( ｡ớ ₃ờ)ھ"," (●’◡’●)ﾉ","ヾ(*´▽‘*)ﾉ","๑乛◡乛๑","(*･ω･)","ヾ(｡･ω･｡)","(=ﾟωﾟ)ﾉ","(○’ω’○)","(。⌒∇⌒)。","(ﾉ≧∀≦)ﾉ","(￣ω￣;)","(◕‿◕✿)","♪(^∇^*)","o(^▽^)o","(●´▽｀●)","(=￣ω￣=)","o(*￣▽￣*)ブ","(ღ˘⌣˘ღ)","ヽ(○´∀`)ﾉ♪","(*´∀`*)","(⁎⚈᷀᷁ᴗ⚈᷀᷁⁎)","( ´･◡･` )"};
        int index=(int)(Math.random()*list.length);
        return list[index];
    }

    public static String genUnhappyFace() {
        String[] list = new String[]{"(→_←)"," ╮(๑•́ ₃•̀๑)╭","（ﾉ´д｀）","(　TロT)σ","( T﹏T )","(,,Ծ‸Ծ,,)","(ಥ_ಥ)","(๑•́ ₃•̀๑)","(;´༎ຶД༎ຶ`)","(⌇ຶД⌇ຶ)","(,,•́ . •̀,,) ","( ´•︵•` )","( >﹏<。)～","°(ಗдಗ。)°","(´；ω；｀)","(｡ﾉω＼｡)ﾟ","（¯﹃¯）","( ཀ͝ ∧ ཀ͝ )"};
        int index=(int)(Math.random()*list.length);
        return list[index];
    }
}
