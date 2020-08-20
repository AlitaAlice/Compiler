package com.alita.compiler.LL1;



import java.util.*;

/**
 * Created by Administrator
 * 语句分析器
 */
public class Analyzer {
    /**
     * 控制器
     */
    private LL1Controller controller;
    /**
     * LL（1）文法
     */
    private LL1 Lang;
    /**
     * 开始符
     */
    private Character startChar;
    /**
     * 分析栈
     */
    private Stack<Character> analyzeStatck;
    /**
     * 剩余输入串
     */
    private String str;
    /**
     * 推导所用产生或匹配
     */
    private String useExp;

    public LL1 getLang() {
        return Lang;
    }

    public void setLang(LL1 lang) {
        Lang = lang;
    }

    public Character getStartChar() {
        return startChar;
    }

    public void setStartChar(Character startChar) {
        this.startChar = startChar;
    }

    public Stack<Character> getAnalyzeStatck() {
        return analyzeStatck;
    }

    public void setAnalyzeStatck(Stack<Character> analyzeStatck) {
        this.analyzeStatck = analyzeStatck;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getUseExp() {
        return useExp;
    }

    public void setUseExp(String useExp) {
        this.useExp = useExp;
    }

    public Analyzer(LL1Controller controller) {
        this.controller = controller;
        analyzeStatck = new Stack<Character>();
        // 结束符进栈
        analyzeStatck.push('#');
    }
    public Analyzer() {
        analyzeStatck = new Stack<Character>();
        // 结束符进栈
        analyzeStatck.push('#');
    }

    /**
     * 分析
     */
    public LinkedList<Object[]> analyze() {

        LinkedList<Object[]> stack = new LinkedList<>();
        // 开始符进栈
        analyzeStatck.push(startChar);
        System.out.println("开始符:" + startChar);
        int index = 1;
        Object[] value = new Object[5];
        value[0] = index;
        value[1] = analyzeStatck.toString();
        value[2] = str;
        value[4] = "初始化";
        stack.add(value);

        while (!analyzeStatck.empty()) {
            index++;
            if (analyzeStatck.peek() != str.charAt(0)) {
                // 到分析表中找到这个产生式
                String nowUseExpStr = LL1.findUseExp(Lang.getSelectMap(), analyzeStatck.peek(), str.charAt(0));

                if (nowUseExpStr == null){
                    System.out.println("无法匹配");
                    return null;
                }
                // 将之前的分析栈中的栈顶出栈
                Character c = analyzeStatck.peek();
                analyzeStatck.pop();
                // 将要用到的表达式入栈,反序入栈  //为空串时不推入栈
                if (null != nowUseExpStr && nowUseExpStr.charAt(0) != 'ε') {
                    for (int j = nowUseExpStr.length() - 1; j >= 0; j--) {
                        char currentChar = nowUseExpStr.charAt(j);
                        analyzeStatck.push(currentChar);
                    }
                }

                /***************/
                Object[] value1 = new Object[5];
                value1[0] = index;
                value1[1] = analyzeStatck.toString();
                value1[2] = str;
                value1[3] = c+"->"+nowUseExpStr;
                value1[4] = "";
//                value[4] = "初始化";
                // TODO: 操作
                stack.add(value1);
                /****************/

                System.out.println(index + "\t\t\t" + analyzeStatck.toString() + "\t\t\t" + str + "\t\t\t"
                        + c + "->" + nowUseExpStr);
            }
            // 如果可以匹配,分析栈出栈，串去掉一位
            if (analyzeStatck.peek() == str.charAt(0)) {
                Object[] value2 = new Object[5];
                value2[0] = index;
                value2[1] = analyzeStatck.toString();
                value2[2] = str;
                value2[3] = "";
                value2[4] = "“"+ str.charAt(0) + "”匹配";
//                value[4] = "初始化";
                // TODO:  操作
                stack.add(value2);

                System.out.println(index + "\t\t\t" + analyzeStatck.toString() + "\t\t\t" + str + "\t\t\t" + "“"
                        + str.charAt(0) + "”匹配");

                analyzeStatck.pop();
                str = str.substring(1);
            }
        }

        return stack;
    }


    public void overAnalyze(){
        int count=1;
        //开始符号进栈
        analyzeStatck.push('E');
        //获取预测分析表
        HashMap<Character, Map<Character,String>> table = Lang.getTable();
        //获取终结符集合和非终结符集合
        TreeSet<Character> nvSet = Lang.getNvSet();
        TreeSet<Character> ntSet = Lang.getNtSet();
        Scanner sc = new Scanner(System.in);
        System.out.println("步骤"+"\t"+"分析栈"+"\t"+"剩余串"+"\t"+"匹配表达式");

        //循环判断
        while(!(analyzeStatck.isEmpty())&&!(analyzeStatck.peek()=='#'&&str=="#")){
            String expression=null;
            System.out.print(count+"\t"+analyzeStatck.toString()+"\t"+str);
            //分析栈的栈顶元素与剩余字符串进行匹配
            if(ntSet.contains(analyzeStatck.peek())&&analyzeStatck.peek()==str.charAt(0)){
                System.out.println("\t"+"匹配");
                //字符串递减
                str = str.substring(1);
            }
            else{
                //获取匹配表达式
                expression = table.get(analyzeStatck.peek()).get(str.charAt(0));
                if(expression==null){
                    System.out.print("\t不是该文法的句子");
                    return;
                }
                else{
                    System.out.println("\t->"+expression);
                }
            }
            //分析栈出栈，
            analyzeStatck.pop();
            //表达式倒序入栈
            if(expression!=null&&expression.charAt(0)!='ε'){
                for(int i=expression.length()-1;i>=0;i--){
                    analyzeStatck.push(expression.charAt(i));
                }
            }
            count++;
        }
    }
}
