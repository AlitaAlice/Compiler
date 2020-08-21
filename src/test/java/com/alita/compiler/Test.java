package com.alita.compiler;

import com.alita.compiler.LL1.*;

import java.sql.Driver;

public class Test {
    /**
     * 消除左递归测试，间接左递归
     * 测试用例
     * "S->Aa|b",
     * "A->Ac|Sd|ε"
     */
    @org.junit.jupiter.api.Test
    void eliminateLeftRecursionTest1() {
        //初始化文法
        Grammar grammar = new Grammar(
                new String[]{
                        "S->Aa|b",
                        "A->Ac|Sd|ε"
                }
        );
        //对文法中间接左递归进行处理
        grammar.eliminateIndirectLeftRecursion();
    }

    /**
     * 消除左递归测试，直接左递归
     * 测试用例
     * "E->E+T|T",
     * "T->T*F|F",
     * "F->(E)|-F|id"
     */
    @org.junit.jupiter.api.Test
    void eliminateLeftRecursionTest2() {
        //初始化文法
        Grammar grammar = new Grammar(
                new String[]{
                        "E->T|E+T|E-T","T->F|T*F|T/F","F->(E)|i"
                }
        );
        //对文法中直接左递归进行处理
        grammar.eliminateDirectLeftRecursion();
        System.out.print("经过消除文法左递归算法，");
        grammar.print();
    }

    /**
     * 提取公共左因子测试1
     * 测试用例
     * "S->iCtS|iCtSeS|a",
     * "C->b"
     */
    @org.junit.jupiter.api.Test
    void extractLeftFactorTest1() {
        //初始化文法
//        Grammar grammar = new Grammar(
//                new String[]{
//                        "S->iCtS|iCtSeS|a",
//                        "C->b"
//                }
//        );
        Grammar grammar = new Grammar(
                new String[]{
//                        "E->T"," E->E+T","E->E-T"," T->F"," T->T*F","T->T/F"," F->(E)"," F->i"
                        "E->T|E+T| E-T","T->F|T*F|T/F","F->(E)|i"
                }
        );
        grammar.extractLeftFactor();
    }

    /**
     * 提取公共左因子测试2
     * 测试用例：课后作业
     * "X->AB|ABC|ABCD"
     */
    @org.junit.jupiter.api.Test
    void extractLeftFactorTest2() {
        //初始化文法
        Grammar grammar = new Grammar(
                new String[]{
                        "X->AB|ABC|ABCD"
                }
        );
        grammar.extractLeftFactor();
    }







}
