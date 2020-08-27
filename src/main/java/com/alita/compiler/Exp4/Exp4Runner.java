package com.alita.compiler.Exp4;

import java.util.*;

public class Exp4Runner {
    static Map<String, Integer> map = new TreeMap<String, Integer>();
    static List<Character> list = new ArrayList<Character>();
    static List<String> ID = new ArrayList<String>();
    static List<Element> elements = new ArrayList<Element>();
    static String num;
    static StringBuffer str;
    static char now;
    static int syn, p, t = 1;
    static boolean flag = true;

    public static void main(String[] args) {
        System.out.println("please input with \"begin\" end with \"end #\"");
        Scanner cin = new Scanner(System.in);
        String code = cin.nextLine();
        for(int i = 0;i < code.length();i++)
            list.add(code.charAt(i));
        map.put("begin",1);
        map.put("end", 2);
        scanner();
        parser();
        if(flag){
            for(int i = 0;i < elements.size();i++) {

                Element e = elements.get(i);
                System.out.println(e.times + " = " + e.data1 + " " + e.op + " " + e.data2);
                if(i==0&&e.op!=""){
                    int result=calculate(Integer.parseInt(e.data1),e.op,Integer.parseInt(e.data2));
                    e.setValue(result);
                }
                else if(i!=elements.size()-1){
                    int result=0;
                    String tempData=elements.get(i-1).times;
                    if(e.data1==tempData){

                        result=calculate(elements.get(i-1).result,e.op,Integer.parseInt(e.data2));
                    }
                    else{
                        result=calculate(Integer.parseInt(e.data1),e.op,elements.get(i-1).result);
                    }

                    e.setValue(result);
                }



            }
            if(elements.size()==1){
                System.out.println(elements.get(0).data1);
            }
            else{
                Element e=elements.get(elements.size()-2);
                System.out.println(e.result);
            }

        }
        cin.close();
    }

    private static void scanner() {
        num = "";
        str = new StringBuffer("");
        now = list.get(p++);
        while (now == ' ')
            now = list.get(p++);
        if ((now >= 65 && now <= 90) || (now >= 97 && now <= 122)) { // 字母开头
            while ((now >= 48 && now < 57) || (now >= 65 && now <= 90) || (now >= 97 && now <= 122) || now == 95) { // 字母数字
                str.append(now);
                now = list.get(p++);
                if (map.containsKey(str.toString().trim()) && (now == ' ' || now == '\n')) {
                    syn = map.get(str.toString().trim());
                    return;
                }
            }
            p--;
            syn = 10; // 用户自定义变量
        } else if (now >= 48 && now <= 57) { // 数字开头
            num = "" + now;
            while ((now >= 48 && now <= 57) || now == 46) {
                now = list.get(p++);
                num += now;
            }
            num = num.substring(0,num.length() - 1);
            p--;
            syn = 11;//常量
        } else {
            switch (now) {
                case '+': {
                    str.replace(0, 0, "" + now);
                    syn = 13;
                    break;
                }
                case '-': {
                    str.replace(0, 0, "" + now);
                    syn = 14;
                    break;
                }
                case '*': {
                    str.replace(0, 0, "" + now);
                    syn = 15;
                    break;
                }
                case '/': {
                    str.replace(0, 0, "" + now);
                    syn = 16;
                    break;
                }
                case ';': {
                    syn = 26;
                    str.replace(0, 0, "" + now);
                    break;
                }
                case '(': {
                    syn = 27;
                    str.replace(0, 0, "" + now);
                    break;
                }
                case ')': {
                    syn = 28;
                    str.replace(0, 0, "" + now);
                    break;
                }
                case '=': {
                    str.append(now);
                    syn = 18;
                    break;
                }
                case '#': {
                    syn = 0;
                    str.replace(0, 0, "" + now);
                    break;
                }
                default:
                    syn = -1;
            }
        }
    }
    /*
     * <程序> => begin<语句串>end
     * <语句串> => <语句>{;<语句>}
     * <语句> => ID=<表达式>
     * <表达式> => <项>{+<项> | -<项>}
     * <项> => <因子>{*<因子> | /<因子>}
     * <因子> => ID | NUM | (<表达式>)
     *
     * ID是用户自定义变量， NUM是常量
     * 输入 begin a=9; x=2*3; b=a+x end #
     * 输出 success！
     * 输入 x=a+b*c end #
     * 输出 error！
     */
    private static void parser() {
        if(syn == 1) { // 当前单词为begin
            scanner();
            statementString();
            if(syn == 2) { // 当前单词为end
                scanner();
                if(syn == 0 && flag) // 当前单词为#
                    System.out.println("Success!");
            } else {
                if(flag)
                    System.out.println("Error,Miss end!");
                flag = false;
            }
        } else {
            System.out.println("Error,Miss begin!");
            flag = false;
        }
    }

    private static void statementString() { // 语句串
        statement();
        while(syn == 26) { // 分号
            scanner();
            statement();
        }
    }

    private static void statement() { // 语句
        String times,data1;
        if(syn == 10) {// ID
            times = str.toString();
            ID.add(str.toString());
            scanner();
            if(syn == 18) { // 等于号
                scanner();
                data1 = expression();//表达式
                memset(times,data1,"","");
            } else {
                System.out.println("Error,赋值符号错误！");
                flag = false;
            }
        } else {
            System.out.println("Error,语句错误！");
            flag = false;
        }
    }

    private static String expression() { // 表达式
        String times,data1,op,data2;
        data1 = term();
        while(syn == 13 || syn == 14) {// 当前单词为+、－
            if(syn == 13) // +
                op = "+";
            else // -
                op = "-";
            scanner();
            data2 = term();
            times = "t" + (t++);
            memset(times,data1,op,data2);
            data1 = times;
        }
        return data1;
    }

    private static String term() { // 项
        String times,data1,op,data2;
        data1 = factor();
        while(syn == 15 || syn == 16) { // 当前单词为*、/
            if(syn == 15) // *
                op = "*";
            else // /
                op = "/";
            scanner();
            data2 = factor();
            times = "t" + (t++);
            memset(times,data1,op,data2);
            data1 = times;
        }
        return data1;
    }

    private static String factor() { // 因子
        String data = "";
        if(syn == 10) { // ID
            if(!ID.contains(str.toString())) {
                System.out.println("Error,存在未定义变量" + str.toString());
                flag = false;
            } else {
                data = str.toString();
                scanner();
            }
        } else if(syn == 11) { // NUM
            data = num;
            scanner();
        }
        else if(syn == 27) { // 左括号
            scanner();
            data = expression();
            if(syn == 28)
                scanner();
            else {
                System.out.println("Error,')'错误!");
                flag = false;
            }
        } else {
            System.out.println("Error,表达式错误");
            flag = false;
        }
        return data;
    }
    static void memset(String times,String data1,String op,String data2) {
        Element e = new Element(times,data1,op,data2);
        elements.add(e);
    }

    static int calculate(int  data1,String op,int  data2){
        //代表第一次运算
        int result=0;
        switch(op){
            case "+":{
                result=data1+data2;
                break;
            }
            case "-":{
                result=data1-data2;
                break;
            }
            case "*":{
                result=data1*data2;
                break;
            }
            case "/":{
                result=data1/data2;
                break;
            }
            case "":{
                result=data1;
            }

            }

        return result;
    }
}