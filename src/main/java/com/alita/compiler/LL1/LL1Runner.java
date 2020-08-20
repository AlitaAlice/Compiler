package com.alita.compiler.LL1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

@Component
public class LL1Runner implements CommandLineRunner {
    @Override
    public void run(String... args)  {
        String text = null;
        LL1Controller controller = new LL1Controller();
        File file = new File("./data.txt");
        if (!file.exists()) {
            System.out.println("对不起，不包含指定路径的文件");
        } else {
            try {
                FileReader fr = new FileReader(file);
                char[] data = new char[100];
                int length = 0;
                while ((length = fr.read(data)) > 0) {
                    String str = new String(data, 0, length);
                    System.out.println("--------初始文法------------");
                    System.out.println(str);
                    System.out.println("--------消除左递归后的文法------------");
                    controller.analysisLang(str);
                }
                fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入符号串");
        String str2 = sc.next();
        controller.analysisStr(str2);
    }

    public void transfer(String str) {

    }

}
