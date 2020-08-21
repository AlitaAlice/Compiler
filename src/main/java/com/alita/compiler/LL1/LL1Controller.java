package com.alita.compiler.LL1;
import java.util.*;

/**
 *  界面模块控制器
 */
public class LL1Controller {

    private Analyzer analyzer = null;
    private LL1 lang = null;
    /**
     * 开始分析文法
     * @param text 传入的文法string
     */
    public void analysisLang(String text){

        lang = new LL1();
        ArrayList<String> array = new ArrayList<String>();
        setLL1(array,text);
        lang.setLL1Array(array);
        lang.initNvNt();
        TreeSet nvSet = lang.getNvSet();
        //ArrayList<String> array1=lang.direct_left_recursion(array,nvSet);
        lang.setLL1Array(array);//文法表达式集合
        lang.setS('E');//开始符
        lang.init();
    }
    /***
     * 人间修改分析
     * 2020.5.21
     */
    public void analysisStr(String text){
        analyzer = new Analyzer(this);
        analyzer.setStartChar('E');
        analyzer.setLang(lang);
        analyzer.setStr(text);
        analyzer.overAnalyze();
    }
    /**
     * 设置文法的 表达式数据
     * @param array
     * @param text 读取到的文法文本（文本格式：每行一个表达式，回车换行）
     */
    private static void setLL1(ArrayList<String> array, String text) {
        String[] item = text.split("\\s+");
        for (String str :item) {
            array.add(str);
        }
    }
}
