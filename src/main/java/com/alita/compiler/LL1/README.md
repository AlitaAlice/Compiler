Division:
张禧龙（组长）判断文法是否是LL（1）文法，如不是等价变换（消除左递归、提取左因子），使之等价变换为LL（1）文法的编码实现，整合的编码实现
李宝龙	构建Select集合，构造预测分析表实现，并实现给一个表达式，给出分析过程的编码实现
罗碧慧	构建First集合、Follow集合的编码实现


SOURCE:
E->T|E+T|E-T
T->F|T*F|T/F
F->(E)|i
i+i*i#


TARGET:
E->TG
G->+TG
G->-TG
G->ε
T->FS
S->*FS
S->/FS
S->ε
F->(E)
F->i

