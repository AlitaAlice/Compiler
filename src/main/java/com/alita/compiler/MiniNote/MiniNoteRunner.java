package com.alita.compiler.MiniNote;//记事本主体类

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MiniNoteRunner extends JFrame implements ActionListener {

    JMenuBar menuBar = new JMenuBar();
    JMenu file = new JMenu("文件(F)"),       //菜单
            edit = new JMenu("编辑(E)"),
            format = new JMenu("格式(O)"),
            view = new JMenu("查看(V)"),
            help = new JMenu("帮助(H)");

    JMenuItem[] menuItem = {                  //菜单下拉项
            new JMenuItem("新建(N)"),
            new JMenuItem("打开(O)"),
            new JMenuItem("保存(S)"),
            new JMenuItem("打印(P)"),
            new JMenuItem("全选(A)"),
            new JMenuItem("复制(C)"),
            new JMenuItem("剪切(T)"),
            new JMenuItem("粘贴(P)"),
            new JMenuItem("自动换行(W)"),
            new JMenuItem("字体(F)"),
            new JMenuItem("状态栏可见(S)"),
            new JMenuItem("帮助主题(H)"),
            new JMenuItem("关于记事本(A)"),
            new JMenuItem("页面设置(U)"),
            new JMenuItem("退出(X)"),
            new JMenuItem("查找(F)"),
            new JMenuItem("查找下一个(N)"),
            new JMenuItem("替换(R)"),
            new JMenuItem("状态栏不可见(L)")
    };

    JPopupMenu popupMenu = new JPopupMenu();
    ;//右键菜单
    JMenuItem[] menuItem1 = {
            new JMenuItem("撤销(Z)"),
            new JMenuItem("剪切(X)"),
            new JMenuItem("复制(C)"),
            new JMenuItem("粘贴(V)"),
            new JMenuItem("删除(D)"),
            new JMenuItem("全选(A)"),
    };

    private JTextArea textArea;               //文本区域
    private JScrollPane js;                   //滚动条
    private JPanel jp;
    private FileDialog openFileDialog;        //打开保存对话框
    private FileDialog saveFileDialog;
    private Toolkit toolKit;                   //获取默认工具包。
    private Clipboard clipboard;               //获取系统剪切板
    private String fileName;                  //设置默认的文件名
    private JToolBar toolBar2 = null;
    private JLabel label = null;
    private JLabel labelTime = null;
    private JLabel labelCodeStyle = null;
    private JLabel labelColumn = null;
    private Icon statusBar_status = null;
    private String codestyle = "UTF-8";   // 状态栏图标
    private static int column;

    public static String getLineInfo() {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        return ste.getFileName() + ": Line " + ste.getLineNumber();
    }

    /**
     * MiniEdit  方法定义
     * <p>
     * 实现记事本初始化
     **/
    public MiniNoteRunner() {


        /**
         * 状态栏
         */
        toolBar2 = new JToolBar();
        toolBar2.setFloatable(false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        label = new JLabel("当前字数:0  ");
        labelTime = new JLabel("日期: " + sdf.format(new Date()));
        labelCodeStyle = new JLabel("编码: " + codestyle);
        labelColumn = new JLabel("当前行数:0  ");
        toolBar2.add(label);
        toolBar2.add(labelColumn);
        toolBar2.addSeparator(new Dimension(180, 5));
        toolBar2.add(labelTime);
        toolBar2.addSeparator(new Dimension(180, 5));
        toolBar2.add(labelCodeStyle);
        add(toolBar2, BorderLayout.SOUTH);

        fileName = "无标题";
        toolKit = Toolkit.getDefaultToolkit();
        clipboard = toolKit.getSystemClipboard();
        textArea = new JTextArea();
        js = new JScrollPane(textArea);
        jp = new JPanel();
        openFileDialog = new FileDialog(this, "打开", FileDialog.LOAD);
        saveFileDialog = new FileDialog(this, "另存为", FileDialog.SAVE);
        js.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jp.setLayout(new GridLayout(1, 1));
        jp.add(js);
        textArea.setComponentPopupMenu(popupMenu);   //文本区域添加右键
        textArea.add(popupMenu);
        add(jp);
        setTitle("迷你记事本");
        setFont(new Font("Times New Roman", Font.PLAIN, 15));
        setBackground(Color.white);
        setSize(800, 600);
//        add(toolBar2, BorderLayout.SOUTH);
        setJMenuBar(menuBar);
        Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize(); // 获得显示器大小对象
        Dimension frameSize = getSize();             // 获得窗口大小对象
        setLocation((displaySize.width - frameSize.width) / 2,
                (displaySize.height - frameSize.height) / 2);
        setLocation((displaySize.width - frameSize.width) / 2,
                (displaySize.height - frameSize.height) / 2);
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(format);
        menuBar.add(view);
        menuBar.add(help);
        for (int i = 0; i < 4; i++) {
            file.add(menuItem[i]);
            edit.add(menuItem[i + 4]);
        }
        for (int i = 0; i < 3; ++i) {
            edit.add(menuItem[i + 15]);
        }
        for (int i = 0; i < 2; ++i) {
            format.add(menuItem[i + 8]);
//            help.add(menuItem[i+11]);
        }
        help.add(menuItem[12]);
        view.add(menuItem[10]);
        view.add(menuItem[18]);
        file.add(menuItem[14]);
        for (int i = 0; i < 6; ++i) {
            popupMenu.add(menuItem1[i]);
        }

        //窗口监听
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });
        //注册各个菜单项的事件监听器
        for (int i = 0; i < menuItem.length; i++) {
            menuItem[i].addActionListener(this);
        }
        for (int i = 0; i < menuItem1.length; i++) {
            menuItem1[i].addActionListener(this);
        }
        registerListener();
    }


    private void changeTextLengthStatus(){                     // 文本监听
        String content = textArea.getText().trim();
        label.setText("当前字数: "+content.length()+"  ");
    }
    private void changeTextStatus(){
        labelColumn.setText("当前行数: "+column+"  ");
    }
    private void registerListener() {
        textArea.addMouseListener(new MouseAdapter() {         // 为文本框添加鼠标监听器
            @Override
            public void mouseClicked(MouseEvent e) {
                try {

                    Rectangle rec = textArea.modelToView(textArea.getCaretPosition());
                    int height = rec.y / rec.height + 1;
                    int start = textArea.getLineStartOffset(height - 1);
                    int end = textArea.getLineEndOffset(height - 1);
                    column = rec.y / rec.height + 1;

                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                changeTextStatus();
            }
        });

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override                                         // 监听文本区改变
            public void insertUpdate(DocumentEvent e) {
//                isItemsAvalible();                            // 一旦文本有改变就设置各按钮的可用性
                changeTextLengthStatus();                     // 实时显示文本字数
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }

        });
    }
    /**
     * actionPerformed  方法定义
     * <p>
     * 动作触发实现
     **/
    public void actionPerformed(ActionEvent e) {
        
        //toolBar2.add(label);
        boolean VisibleTool = false;
        Object eventSource = e.getSource();
        if (eventSource == menuItem[0]) //新建动作
        {
            textArea.setText("");
        } else if (eventSource == menuItem[1])//打开动作
        {
            openFileDialog.setVisible(true);
            fileName = openFileDialog.getDirectory() + openFileDialog.getFile();
            if (fileName != null) {
                openFile(fileName);
            }
        } else if (eventSource == menuItem[2])//保存动作
        {
            saveFileDialog.setVisible(true);
            fileName = saveFileDialog.getDirectory() + saveFileDialog.getFile();
            if (fileName != null) {
                writeFile(fileName);
            }
        } else if (eventSource == menuItem[14])//退出动作
        {
            System.exit(0);
        } else if (eventSource == menuItem[4] || eventSource == menuItem1[5])  //全选动作
        {
            textArea.selectAll();
        } else if (eventSource == menuItem[5] || eventSource == menuItem1[2])  //复制动作
        {
            String text = textArea.getSelectedText();
            StringSelection selection = new StringSelection(text);
            clipboard.setContents(selection, null);
        } else if (eventSource == menuItem[6] || eventSource == menuItem1[1])//剪切动作
        {
            String text = textArea.getSelectedText();
            StringSelection selection = new StringSelection(text);
            clipboard.setContents(selection, null);
            textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
        } else if (eventSource == menuItem[7] || eventSource == menuItem1[3])//粘贴动作
        {
            Transferable contents = clipboard.getContents(this);
            if (contents == null) return;
            String text;
            text = "";
            try {
                text = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception ex) {

            }
            textArea.replaceRange(text, textArea.getSelectionStart(), textArea.getSelectionEnd());
        } else if (eventSource == menuItem[8])  //自动换行
        {
            if (textArea.getLineWrap()) textArea.setLineWrap(false);
            else textArea.setLineWrap(true);

        } else if (eventSource == menuItem[9])   //字体
        {//实例化字体类
            FontDialog fontdialog = new FontDialog(new JFrame(), "字体", true);
            textArea.setFont(fontdialog.showFontDialog());             //设置字体
        }
        else if (eventSource == menuItem[10])  //状态栏
        {
                toolBar2.setVisible(!VisibleTool);
        }
        else if (eventSource == menuItem[18])  //状态栏
        {
            toolBar2.setVisible(VisibleTool);
        }else if (eventSource == menuItem[11])   //帮助
        {
            try {
                String filePath = "C:/WINDOWS/Help/notepad.hlp";
                Runtime.getRuntime().exec("cmd.exe /c " + filePath);
            } catch (Exception ee) {
                JOptionPane.showMessageDialog(this, "打开系统的记事本帮助文件出错!", "错误信息", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (eventSource == menuItem[12])    //关于记事本
        {
            String help = "迷你记事本  版本1.0\n课程设计:JAVA";
            JOptionPane.showConfirmDialog(null, help, "关于记事本",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);

        } else if (eventSource == menuItem[15] || eventSource == menuItem[16])  //查找下一个
        {
            search();

        } else if (eventSource == menuItem[17]) //替换
        {
            substitude();
        } else if (eventSource == menuItem[3])    //打印
        {
            try {
                textArea.print();
            } catch (PrinterException e1) {
                e1.printStackTrace();
            }

        }
    }
//    ta.addCaretListener(new CaretListener() {
//
//        @Override
//        public void caretUpdate(CaretEvent e) {
//            // TODO Auto-generated method stub
//            try {
//                int pos = ta.getCaretPosition();
//                // 获取行数
//                int line = ta.getLineOfOffset(pos) + 1;
//                // 获取列数
//                int col = pos - ta.getLineStartOffset(line - 1) + 1;
//                labelLine.setText("行数：" + line);
//                labelColumn.setText("列数：" + col);
//                labelNum.setText("字数：" + ta.getText().length());
//            } catch (Exception ex) {
//                labelTips.setText("无法获得当前光标位置 ");
//            }
//        }
//    });

    /**
     * openFile方法
     * <p>
     * 从TXT读进数据到记事本
     **/
    public void openFile(String fileName) {

        try {
            File file = new File(fileName);
            FileReader readIn = new FileReader(file);
            int size = (int) file.length();
            int charsRead = 0;
            char[] content = new char[size];
            while (readIn.ready()) {
                charsRead += readIn.read(content, charsRead, size - charsRead);
            }
            readIn.close();
            textArea.setText(new String(content, 0, charsRead));
        } catch (Exception e) {
            System.out.println("Error opening file!");
        }
    }

    /**
     * saveFile方法
     * <p>
     * 从记事本写进数据到TXT
     **/
    public void writeFile(String fileName) {
        try {
            File file = new File(fileName);
            FileWriter write = new FileWriter(file);
            write.write(textArea.getText());
            write.close();
        } catch (Exception e) {
            System.out.println("Error closing file!");
        }
    }

    /**
     * substitude方法
     * <p>
     * 实现替换功能
     */
    public void substitude() {

        final JDialog findDialog = new JDialog(this, "查找与替换", true);
        Container con = findDialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel searchContentLabel = new JLabel("查找内容(N) :");
        JLabel replaceContentLabel = new JLabel("替换为(P)　 :");
        final JTextField findText = new JTextField(30);
        final JTextField replaceText = new JTextField(30);
        final JCheckBox matchcase = new JCheckBox("区分大小写(C)");
        ButtonGroup bGroup = new ButtonGroup();
        final JRadioButton up = new JRadioButton("向上(U)");
        final JRadioButton down = new JRadioButton("向下(D)");
        down.setSelected(true);  //默认向下搜索
        bGroup.add(up);
        bGroup.add(down);
        JButton searchNext = new JButton("查找下一个(F)");
        JButton replace = new JButton("替换(R)");
        final JButton replaceAll = new JButton("全部替换(A)");


        //"替换"按钮的事件处理
        replace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (replaceText.getText().length() == 0 && textArea.getSelectedText() != null)
                    textArea.replaceSelection("");
                if (replaceText.getText().length() > 0 && textArea.getSelectedText() != null)
                    textArea.replaceSelection(replaceText.getText());
            }
        });

        //"替换全部"按钮的事件处理
        replaceAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setCaretPosition(0); //将光标放到编辑区开头 
                int a = 0, b = 0, replaceCount = 0;
                if (findText.getText().length() == 0) {
                    JOptionPane.showMessageDialog(findDialog, "请填写查找内容!", "提示", JOptionPane.WARNING_MESSAGE);
                    findText.requestFocus(true);
                    return;
                }
                while (a > -1) {
                    int FindStartPos = textArea.getCaretPosition();//获取光标位置
                    String str1, str2, str3, str4, strA, strB;
                    str1 = textArea.getText();
                    str2 = str1.toLowerCase();
                    str3 = findText.getText();
                    str4 = str3.toLowerCase();
                    if (matchcase.isSelected())   //大小写区分
                    {
                        strA = str1;
                        strB = str3;
                    } else {
                        strA = str2;
                        strB = str4;
                    }

                    if (up.isSelected())         //向上搜索
                    {
                        if (textArea.getSelectedText() == null) {
                            a = strA.lastIndexOf(strB, FindStartPos - 1);
                        } else {
                            a = strA.lastIndexOf(strB, FindStartPos - findText.getText().length() - 1);
                        }
                    } else   //向下搜索
                    {
                        if (textArea.getSelectedText() == null) {
                            a = strA.indexOf(strB, FindStartPos);

                        } else {
                            a = strA.indexOf(strB, FindStartPos - findText.getText().length() + 1);
                        }
                    }

                    if (a > -1) {
                        if (up.isSelected()) {
                            textArea.setCaretPosition(a);
                            b = findText.getText().length();
                            textArea.select(a, a + b);
                        } else if (down.isSelected()) {
                            textArea.setCaretPosition(a);
                            b = findText.getText().length();
                            textArea.select(a, a + b);
                        }
                    } else {
                        if (replaceCount == 0) {
                            JOptionPane.showMessageDialog(findDialog, "找不到您查找的内容!", "记事本", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(findDialog, "成功替换" + replaceCount + "个匹配内容!", "替换成功", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    if (replaceText.getText().length() == 0 && textArea.getSelectedText() != null)   //用空字符代替选定内容
                    {
                        textArea.replaceSelection("");
                        replaceCount++;
                    }
                    if (replaceText.getText().length() > 0 && textArea.getSelectedText() != null)   //用指定字符代替选定内容
                    {
                        textArea.replaceSelection(replaceText.getText());
                        replaceCount++;
                    }
                }//end while
            }
        });

        //"查找下一个"按钮事件处理
        searchNext.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int a = 0, b = 0;
                int FindStartPos = textArea.getCaretPosition();
                String str1, str2, str3, str4, strA, strB;
                str1 = textArea.getText();
                str2 = str1.toLowerCase();
                str3 = findText.getText();
                str4 = str3.toLowerCase();

                //"区分大小写"的CheckBox被选中
                if (matchcase.isSelected())   //区分大小写
                {
                    strA = str1;
                    strB = str3;
                } else      //不区分大小写
                {
                    strA = str2;
                    strB = str4;
                }
                if (up.isSelected())  //向上搜索
                {
                    if (textArea.getSelectedText() == null) {
                        a = strA.lastIndexOf(strB, FindStartPos - 1);
                    } else {
                        a = strA.lastIndexOf(strB, FindStartPos - findText.getText().length() - 1);
                    }
                } else if (down.isSelected()) {
                    if (textArea.getSelectedText() == null) {
                        a = strA.indexOf(strB, FindStartPos);
                    } else {
                        a = strA.indexOf(strB, FindStartPos - findText.getText().length() + 1);
                    }
                }
                if (a > -1) {
                    if (up.isSelected()) {
                        textArea.setCaretPosition(a);
                        b = findText.getText().length();
                        textArea.select(a, a + b);
                    } else if (down.isSelected()) {
                        textArea.setCaretPosition(a);
                        b = findText.getText().length();
                        textArea.select(a, a + b);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "找不到您查找的内容!", "记事本",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        //"取消"按钮及事件处理 
        JButton cancel = new JButton("取消");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findDialog.dispose();
            }
        });
        //创建"查找与替换"对话框的界面
        JPanel bottomPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel topPanel = new JPanel();
        JPanel direction = new JPanel();

        direction.setBorder(BorderFactory.createTitledBorder("方向"));
        direction.add(up);
        direction.add(down);

        JPanel replacePanel = new JPanel();
        replacePanel.setLayout(new GridLayout(1, 2));
        replacePanel.add(searchNext);
        replacePanel.add(replace);
        replacePanel.add(replaceAll);
        replacePanel.add(cancel);

        topPanel.add(searchContentLabel);
        topPanel.add(findText);

        centerPanel.add(replaceContentLabel);
        centerPanel.add(replaceText);
        centerPanel.add(replacePanel);

        bottomPanel.add(matchcase);
        bottomPanel.add(direction);

        con.add(replacePanel);
        con.add(topPanel);
        con.add(centerPanel);
        con.add(bottomPanel);

        //设置"查找与替换"对话框的大小、可更改大小(否)、位置和可见性	
        findDialog.setSize(550, 240);
        findDialog.setResizable(true);
        findDialog.setLocation(230, 280);
        findDialog.setVisible(true);
    }//方法mySearch()结束

    /**
     * search方法
     * <p>
     * 实现查找功能
     */
    public void search() {

        final JDialog findDialog = new JDialog(this, "查找下一个", true);
        Container con = findDialog.getContentPane();
        con.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel searchContentLabel = new JLabel(" 查找内容(N) :");
        final JTextField findText = new JTextField(17);
        final JCheckBox matchcase = new JCheckBox("区分大小写(C)");
        ButtonGroup bGroup = new ButtonGroup();
        final JRadioButton up = new JRadioButton("向上(U)");
        final JRadioButton down = new JRadioButton("向下(D)");
        down.setSelected(true);  //默认向下搜索
        bGroup.add(up);
        bGroup.add(down);
        JButton searchNext = new JButton("查找下一个(F)");

        //"查找下一个"按钮事件处理
        searchNext.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int a = 0, b = 0;
                int FindStartPos = textArea.getCaretPosition();
                String str1, str2, str3, str4, strA, strB;
                str1 = textArea.getText();
                str2 = str1.toLowerCase();
                str3 = findText.getText();
                str4 = str3.toLowerCase();

                //"区分大小写"的CheckBox被选中
                if (matchcase.isSelected())   //不区分大小写
                {
                    strA = str1;
                    strB = str3;
                } else      //区分大小写
                {
                    strA = str2;
                    strB = str4;
                }
                if (up.isSelected())  //向上搜索
                {
                    if (textArea.getSelectedText() == null) {
                        a = strA.lastIndexOf(strB, FindStartPos - 1);
                    } else {
                        a = strA.lastIndexOf(strB, FindStartPos - findText.getText().length() - 1);
                    }
                } else if (down.isSelected()) {
                    if (textArea.getSelectedText() == null) {
                        a = strA.indexOf(strB, FindStartPos);
                    } else {
                        a = strA.indexOf(strB, FindStartPos - findText.getText().length() + 1);
                    }
                }
                if (a > -1) {
                    if (up.isSelected()) {
                        textArea.setCaretPosition(a);
                        b = findText.getText().length();
                        textArea.select(a, a + b);
                    } else if (down.isSelected()) {
                        textArea.setCaretPosition(a);
                        b = findText.getText().length();
                        textArea.select(a, a + b);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "找不到您查找的内容!", "记事本",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        //"取消"按钮及事件处理  
        JButton cancel = new JButton("         取消         ");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findDialog.dispose();
            }
        });
        //创建"替换"对话框的界面
        JPanel bottomPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel topPanel = new JPanel();
        JPanel direction = new JPanel();
        direction.setBorder(BorderFactory.createTitledBorder("方向"));
        direction.add(up);
        direction.add(down);
        topPanel.add(searchContentLabel);
        topPanel.add(findText);
        topPanel.add(searchNext);
        bottomPanel.add(matchcase);
        bottomPanel.add(direction);
        bottomPanel.add(cancel);
        con.add(topPanel);
        con.add(centerPanel);
        con.add(bottomPanel);
        //设置"替换"对话框的大小、可更改大小(否)、位置和可见性	
        findDialog.setSize(425, 200);
        findDialog.setResizable(true);
        findDialog.setLocation(230, 280);
        findDialog.setVisible(true);
    }


    /**
     * 主函数
     **/
    public static void main(String[] args) {

        MiniNoteRunner note = new MiniNoteRunner();
        note.setVisible(true);

    }

}