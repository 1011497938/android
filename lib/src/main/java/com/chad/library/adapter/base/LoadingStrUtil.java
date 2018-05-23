package com.chad.library.adapter.base;

/**
 * Created by SQ on 2017/5/21.
 */

public class LoadingStrUtil {
    public static String getZjuStr() {
        String[] list = new String[]{
                "大不自多 海纳江河",
                "惟学无际 际于天地",
                "形上谓道兮 形下谓器",
                "礼主别异兮 乐主和同",
                "知其不二兮 尔听斯聪",
                "国有成均 在浙之滨",
                "昔言求是 实启尔求真",
                "习坎示教 始见经纶",
                "无曰已是 无曰遂真",
                "靡革匪因 靡故匪新",
                "何以新之 开物前民",
                "嗟尔髦士 尚其有闻",
                "念哉典学 思睿观通",
                "有文有质 有农有工",
                "兼总条贯 知至知终",
                "成章乃达 若金之在熔",
                "尚亨于野 无吝于宗",
                "树我邦国 天下来同",
        };
        int index = (int) (Math.random() * list.length);
        return list[index];
    }

    public static String genHappyFace() {
        String[] list = new String[]{"｡:.ﾟヽ(｡◕‿◕｡)ﾉﾟ.:｡+ﾟ", "( ｡ớ ₃ờ)ھ", " (●’◡’●)ﾉ", "ヾ(*´▽‘*)ﾉ", "๑乛◡乛๑", "(*･ω･)", "ヾ(｡･ω･｡)", "(=ﾟωﾟ)ﾉ", "(○’ω’○)", "(。⌒∇⌒)。", "(ﾉ≧∀≦)ﾉ", "(￣ω￣;)", "(◕‿◕✿)", "♪(^∇^*)", "o(^▽^)o", "(●´▽｀●)", "(=￣ω￣=)", "o(*￣▽￣*)ブ", "(ღ˘⌣˘ღ)", "ヽ(○´∀`)ﾉ♪", "(*´∀`*)", "(⁎⚈᷀᷁ᴗ⚈᷀᷁⁎)", "( ´･◡･` )"};
        int index = (int) (Math.random() * list.length);
        return list[index];
    }

    public static String genUnhappyFace() {
        String[] list = new String[]{"(→_←)", " ╮(๑•́ ₃•̀๑)╭", "（ﾉ´д｀）", "(　TロT)σ", "( T﹏T )", "(,,Ծ‸Ծ,,)", "(ಥ_ಥ)", "(๑•́ ₃•̀๑)", "(;´༎ຶД༎ຶ`)", "(⌇ຶД⌇ຶ)", "(,,•́ . •̀,,) ", "( ´•︵•` )", "( >﹏<。)～", "°(ಗдಗ。)°", "(´；ω；｀)", "(｡ﾉω＼｡)ﾟ", "（¯﹃¯）", "( ཀ͝ ∧ ཀ͝ )"};
        int index = (int) (Math.random() * list.length);
        return list[index];
    }
}
