package com.alita.compiler.LL1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import static com.alita.compiler.LL1.Grammar.lastString;


public class LL1Runner  {
    public static void main(String[] args) {
        LL1Controller controller = new LL1Controller();
        String[] strings = new String[]{"E->T|E+T|E-T", "T->F|T*F|T/F", "F->(E)|i"};
        Grammar grammar = new Grammar(strings);
        grammar.eliminateDirectLeftRecursion();
        System.out.print("经过消除文法左递归算法，");
        grammar.print2();
        lastString = lastString.replace("E'", "G");
        lastString = lastString.replace("T'", "S");
        System.out.println("------最终的LL1文法为-------\n" + lastString);
        controller.analysisLang(lastString);
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入符号串");
        String str2 = sc.next();
        controller.analysisStr(str2);
    }
}
