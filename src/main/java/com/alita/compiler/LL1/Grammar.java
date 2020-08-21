package com.alita.compiler.LL1;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

//文法数据类型
public class Grammar {
    //将文法存储在链表中
    LinkedList<LinkedList<String>> grammarLists;
    HashMap<String, LinkedList<String>> hashMap;
    LinkedList<String> terminatorLinkedList;
    static String lastString = new String();


    /**
     * 使用String数组初始化文法
     *
     * @param stringsArray String数组
     */
    public Grammar(String[] stringsArray) {
        LinkedList<String> terminatorLinkedList = new LinkedList<>();
        LinkedList<LinkedList<String>> grammarLists = new LinkedList<>();
        HashMap<String, LinkedList<String>> hashMap = new HashMap<>();
        LinkedList<String> linkedList;
        for (String strings : stringsArray) {
            //定义s用于改造strings
            String s = strings;
            linkedList = new LinkedList<>();
            //检索->位置用于后续操作
            int p = strings.indexOf("->");
            //将左部放入链表
            linkedList.offer(s.substring(0, p));
            s = s.substring(p + 2);
            //按照|符号拆分s得到右部项
            String[] rights = s.split("\\|");
            //将右部项添入链表
            Collections.addAll(linkedList, rights);
            grammarLists.offer(linkedList);
            hashMap.put(linkedList.peek(), linkedList);
            for (String right : linkedList) {
                int terminatorLength = 0;
                for (int i = 0; i < right.length(); i++) {
                    //使用terminatorLength判断是否存在小写字符串终结符
                    if (right.charAt(i) >= 'a' && right.charAt(i) <= 'z') {
                        terminatorLength++;
                    } else {
                        if (terminatorLength != 0) {
                            String terminator = right.substring(i - terminatorLength, i);
                            if (!terminatorLinkedList.contains(terminator)) {
                                terminatorLinkedList.offer(terminator);
                            }
                            terminatorLength = 0;
                        }
                        //接收其它特殊符号终结符
                        if (right.charAt(i) < 'A' || right.charAt(i) > 'Z') {
                            //不接受ε和'
                            if (right.charAt(i) == 'ε' || right.charAt(i) == '\'') continue;
                            String terminator = right.substring(i, i + 1);
                            if (!terminatorLinkedList.contains(terminator)) {
                                terminatorLinkedList.offer(terminator);
                            }
                        }
                    }
                }
                if (terminatorLength != 0) {
                    String terminator = right.substring(right.length() - terminatorLength);
                    if (!terminatorLinkedList.contains(terminator)) {
                        terminatorLinkedList.offer(terminator);
                    }
                }
            }
        }
        this.grammarLists = grammarLists;
        this.hashMap = hashMap;
        this.terminatorLinkedList = terminatorLinkedList;
    }

    /**
     * 打印当前文法
     */
    public void print() {
        System.out.println("文法如下：");
        for (LinkedList<String> s : this.grammarLists) {
            System.out.println(printProduction(s));
        }
    }
    /**
     * 打印经过消除文法左递归算法文法
     */
    public void print2() {
        System.out.println("文法如下：");
        for (LinkedList<String> s : this.grammarLists) {
            System.out.println(printProduction(s));
            for (int i = 1; i < s.size(); i++) {
                lastString += s.get(0).replace("\n","") +"->"+s.get(i)+"\n";
            }
        }
        //System.out.println("last="+lastString);
    }
    /**
     * 打印产生式
     *
     * @param production 产生式
     * @return 返回字符串化的产生式，形如A->α|β
     */
    private String printProduction(LinkedList<String> production) {
        StringBuilder sb = new StringBuilder();
        String left = production.poll();
        sb.append(left).append("->");
        for (int i = 0; i < production.size(); i++) {
            String temp = production.poll();
            if (i == 0) {
                sb.append(temp);
            } else {
                sb.append("|").append(temp);
            }
            production.offer(temp);
        }
        production.offerFirst(left);
        return sb.toString();
    }

    private String printProduction2(LinkedList<String> production) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < production.size(); i++) {
            sb.append(production.get(0)).append("->");
            String temp = production.get(i);
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     * 判断链表代表的产生式是否为直接左递归，若是则消除之
     *
     * @param production 待处理产生式
     * @return 若未进行直接左递归消除则直接返回原式（即该产生式未出现直接左递归），否则返回后产生式
     */
    protected LinkedList<LinkedList<String>> eliminateProductionLeftRecursion(LinkedList<String> production) {
        //复制production值进行不改变原始值操作
        LinkedList<String> productionCopy = new LinkedList<>(production);

        //若产生式非直接左递归则返回自身
        if (!isDirectLeftRecursion(productionCopy)) {
            return new LinkedList<LinkedList<String>>() {{
                offer(productionCopy);
            }};
        }

        //单独取出左项
        String left = productionCopy.poll();
        //定义两个链表分别存储存储替换后产生式
        LinkedList<String> linkedList1 = new LinkedList<String>() {{
            offer(left);
        }};
        LinkedList<String> linkedList2 = new LinkedList<String>() {{
            offer(left + "'");
        }};

        //判断右部每个项是否为直接左递归，若是将其添入链表1，否则添入链表2
        for (String s : productionCopy) {
            if (!left.equals(s.substring(0, left.length()))) {
                if (!s.equals("ε")) {
                    linkedList1.offer(s + left + "'");
                } else {
                    linkedList1.offer(left + "'");
                }
            } else {
                linkedList2.offer(s.substring(left.length()) + left + "'");
            }
        }
        linkedList2.offer("ε");

        this.hashMap.put(linkedList1.peek(), linkedList1);
        this.hashMap.put(linkedList2.peek(), linkedList2);

        return new LinkedList<LinkedList<String>>() {{
            offer(linkedList1);
            offer(linkedList2);
        }};
    }

    /**
     * 判断产生式是否为直接左递归
     *
     * @param production 待判断产生式
     * @return true|false
     */
    private boolean isDirectLeftRecursion(LinkedList<String> production) {
        //复制production值进行不改变原始值操作
        LinkedList<String> productionCopy = new LinkedList<>(production);
        String left = productionCopy.poll();
        for (String s : productionCopy) {
            if (s.length() >= left.length() && left.equals(s.substring(0, left.length()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断产生式是否为间接左递归
     *
     * @param production 待判断产生式
     * @return true|false
     */
    private boolean isIndirectLeftRecursion(LinkedList<String> production) {
        //复制production值进行不改变原始值操作
        LinkedList<String> productionCopy = new LinkedList<>(production);
        String left = productionCopy.poll();
        for (String s : productionCopy) {
            //判断最左是否为非终结符
            if (s.charAt(0) >= 'A' && s.charAt(0) <= 'Z') {
                //获取非终结符
                String key = s.length() > 1 && s.charAt(1) == '\'' ? s.substring(0, 2) : s.substring(0, 1);
                LinkedList<String> linkedList = hashMap.get(key);
                if (linkedList != null && linkedList != hashMap.get(left)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 消除文法间接左递归主函数，由Grammar类直接调用
     */
    public void eliminateIndirectLeftRecursion() {
        //遍历所有产生式，进行间接左递归展开
        for (LinkedList<String> strings : this.grammarLists) {
            //只对存在直接左递归与间接左递归的产生式操作
            if (isIndirectLeftRecursion(strings) && isDirectLeftRecursion(strings)) {
                //创建new_production用于替换未展开产生式
                LinkedList<String> new_production = new LinkedList<>(strings);
                String left = new_production.poll();
                for (String string : strings) {
                    //判断最左是否为非终结符
                    if (string.charAt(0) >= 'A' && string.charAt(0) <= 'Z') {
                        //获取非终结符
                        String key = string.length() > 1 && string.charAt(1) == '\'' ? string.substring(0, 2) : string.substring(0, 1);
                        LinkedList<String> linkedList = hashMap.get(key);
                        if (linkedList != null && linkedList != hashMap.get(left)) {
                            System.out.println("文法中存在 " + left + " 与 " + key + " 间接左递归");
                            //从产生式中删除此右部item
                            int index = new_production.indexOf(string);
                            new_production.remove(string);
                            //对该产生式做展开
                            String suffix = string.substring(key.length());
                            String leftLinkedList = linkedList.poll();//去除产生式左部
                            for (String prefix : linkedList) {
                                if (!prefix.equals("ε")) {
                                    new_production.add(index, prefix + suffix);
                                    index++;
                                } else {
                                    new_production.add(index, suffix);
                                    index++;
                                }
                            }
                            linkedList.offerFirst(leftLinkedList);
                        }
                    }
                }
                //更新文法

                this.grammarLists.remove(strings);
                new_production.offerFirst(left);
                this.grammarLists.offer(new_production);
                //更新hashmap
                hashMap.put(new_production.peek(), new_production);
            }
        }

        //遍历所有产生式，删除直接左递归
        eliminateDirectLeftRecursion();

        //打印消除结果
        System.out.print("经过消除文法左递归算法，");
        print();
    }

    /**
     * 消除文法直接左递归主函数，由Grammar类直接调用
     */
    public void eliminateDirectLeftRecursion() {
        System.out.print("消除直接左递前，");
        print();
        //遍历文法中每一产生式，判断其是否直接左递归，若是则进行处理
        for (int i = 0; i < this.grammarLists.size(); i++) {
            if (isDirectLeftRecursion(this.grammarLists.get(i))) {
                int index = this.grammarLists.indexOf(this.grammarLists.get(i));
                LinkedList<LinkedList<String>> tempLinkedList = eliminateProductionLeftRecursion(this.grammarLists.get(i));
                this.grammarLists.remove(this.grammarLists.get(i));
                this.grammarLists.addAll(index, tempLinkedList);
                //打印消除直接左递归操作
                System.out.println("产生式： " + printProduction(this.grammarLists.get(i)) + " 存在直接左递归");
                i += tempLinkedList.size() - 1;
            }
        }
    }

    /**
     * 提取公共左因子算法
     */
    public void extractLeftFactor() {
        System.out.print("此时，");
        print();
        LinkedList<LinkedList<String>> tempLinkedList = new LinkedList<>();
        for (LinkedList<String> strings : this.grammarLists) {
            //判断当前产生式是否存在公共左因子，若存在，进行处理
            if (isCommonLeftFactor(strings)) {
                //分离左部
                String left = strings.poll();
                //存储右部公共左因子
                StringBuilder sb = new StringBuilder();
                //获取待处理的公共左因子
                LinkedList<String> stringsCopy = null;
                for (String string : strings) {
                    //创建产生式的copy链表，用于对比及操作
                    if (stringsCopy == null) stringsCopy = new LinkedList<>(strings);
                    //每次移去一个元素，将时间复杂度优化到log(n)
                    stringsCopy.poll();
                    //遍历stringCopy，判断是否存在公共左因子
                    for (String stringCopy : stringsCopy) {
                        //获取两个右部公共左因子长度
                        int length = commonLeftFactorLength(string, stringCopy);
                        if (length != 0) {
                            //若StringBuilder此时为空，或公共左因子长度短于sb长度，则将其公共左因子赋值给sb
                            if (sb.length() == 0 || length < sb.length()) {
                                sb.delete(0, sb.length());
                                sb.append(string, 0, length);
                            }
                        }
                    }
                }
                String commonLeftFactor = sb.toString();

                //根据所得公共左因子，对产生式进行变换，并暂存入中间链表
                LinkedList<String> linkedList1 = new LinkedList<>();
                linkedList1.offer(left);
                linkedList1.offer(commonLeftFactor + left + "'");
                LinkedList<String> linkedList2 = new LinkedList<>();
                for (String s : strings) {
                    if (s.contains(commonLeftFactor)) {
                        if (s.length() == commonLeftFactor.length()) {
                            linkedList2.offer("ε");
                        } else {
                            linkedList2.offerFirst(s.substring(commonLeftFactor.length()));
                        }
                    } else {
                        linkedList1.offer(s);
                    }
                }
                linkedList2.offerFirst(left + "'");
                tempLinkedList.offer(linkedList1);
                tempLinkedList.offer(linkedList2);

                //重新插入左部
                strings.offerFirst(left);

                //输出操作日志
                System.out.println("产生式： " + printProduction(strings) + " 存在公共左因子: " + commonLeftFactor + " ，对其进行处理");
            }
        }
        boolean loop = !tempLinkedList.isEmpty();
        //对文法中存在公共左因子的产生式进行替换
        for (int i = 0; i < this.grammarLists.size(); i++) {
            if (isCommonLeftFactor(this.grammarLists.get(i))) {
                int index = this.grammarLists.indexOf(this.grammarLists.get(i));
                this.grammarLists.remove(this.grammarLists.get(i));
                this.grammarLists.add(index++, tempLinkedList.poll());
                this.grammarLists.add(index, tempLinkedList.poll());
                i++;
            }
        }

        if (loop) {
            //若文法发生变动，则再次检查文法是否存在公共左因子，直至文法在提取左因子算法中不存在变动为止
            extractLeftFactor();
        } else {
            //打印改造后文法
            System.out.println("提取左因子算法处理完毕");
        }

    }

    /**
     * 计算两个产生式右部公共左因子长度
     *
     * @param s1 右部1
     * @param s2 右部2
     * @return 公共左因子长度
     */
    private int commonLeftFactorLength(String s1, String s2) {
        int length = 0;
        for (int i = 0; i < Math.min(s1.length(), s2.length()); i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                length++;
            } else {
                break;
            }
        }
        return length;
    }

    /**
     * 判断传入产生式是否存在公共左因子
     *
     * @param production 待判断产生式
     * @return 返回判断结果
     */
    private boolean isCommonLeftFactor(LinkedList<String> production) {
        HashMap<Character, Boolean> hashMap = new HashMap();
        for (String string : production) {
            if (hashMap.get(string.charAt(0)) == null) {
                hashMap.put(string.charAt(0), true);
            } else {
                return true;
            }
        }
        return false;
    }
}
