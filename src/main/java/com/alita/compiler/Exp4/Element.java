package com.alita.compiler.Exp4;

/**
 * Title:
 * Description:
 * Company:
 *
 * @author im.alitaalice@gmail.com
 * @date Created in 17:48 2020/8/27
 */
class Element {
    String times;
    String data1;
    String op;
    String data2;
    int result;
    Element(String times,String data1,String op,String data2) {
        this.times = times;
        this.data1 = data1;
        this.op = op;
        this.data2 = data2;
    }
    public void setValue(int result) {
        this.result=result;
    }
}