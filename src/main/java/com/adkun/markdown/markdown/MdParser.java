package com.adkun.markdown.markdown;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Markdown相关工具
 *
 * @author adkun
 */
public class MdParser {

    public static final String OTHER = "OTHER";
    public static final String SPACE = " ";
    public static final String CODE_BEGIN = "CODE_BEGIN";
    public static final String CODE_END = "CODE_END";
    public static final String QUOTE_BEGIN = "QUOTE_BEGIN";
    public static final String QUOTE_END = "QUOTE_END";
    public static final String UNORDER_BEGIN = "UNORDER_BEGIN";
    public static final String UNORDER_END = "UNORDER_END";
    public static final String ORDER_BEGIN = "ORDER_BEGIN";
    public static final String ORDER_END = "ORDER_END";
    public static final String CODE_LINE = "CODE_LINE";
    public static final String QUOTE_LINE = "QUOTE_LINE";
    public static final String UNORDER_LINE = "UNORDER_LINE";
    public static final String ORDER_LINE = "ORDER_LINE";
    public static final String BLANK_LINE = "BLANK_LINE";
    public static final String TITLE = "TITLE";

    /**
     * 按行存储Markdown字符串
     */
    private ArrayList<String> mdList;

    /**
     * 存储Markdown文件每一行对应类型
     */
    private ArrayList<String> mdListType;

    /**
     * 构造方法，将每行读入
     *
     * @param mdList
     */
    public MdParser(List<String> mdList) {
        this.mdList = (ArrayList<String>) mdList;
    }

    /**
     * 外部调用方法
     *
     * @return
     */
    public String parseMdToHtml() {
        defineAreaType();
        defineLineType();
        parseToHtml();
        // 转换成一个String
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < mdList.size(); i++) {
            String str = mdList.get(i);
            if (" ".equals(str)) {
                sb.append("<br>");
            } else if (str.length() > 5 && "<code>".equals(str.substring(0, 6))) {
                sb.append(mdList.get(i));
                sb.append("<br>");
            } else {
                sb.append(mdList.get(i));
            }
        }
        return sb.toString();
    }

    /**
     * 判断每一块md对应的类型
     * 扫描两次，第一次扫描代码块
     * 第二次扫描有序列表、无序列表、引用块
     */
    private void defineAreaType() {
        ArrayList<String> tempList = new ArrayList<>();
        ArrayList<String> tempType = new ArrayList<>();
        tempType.add(OTHER);
        tempList.add(SPACE);

        boolean codeBegin = false, codeEnd = false;
        for (int i = 1; i < mdList.size() - 1; i++) {
            String line = mdList.get(i);
            if (line.length() > 2 && "```".equals(line.substring(0, 3))) {
                // 代码块
                if (!codeBegin && !codeEnd) {
                    // 如果现在不是代码块中
                    tempType.add(CODE_BEGIN);
                    tempList.add(SPACE);
                    codeBegin = true;
                } else if (codeBegin && !codeEnd) {
                    // 如果现在是在代码块中，说明代码块结束
                    tempType.add(CODE_END);
                    tempList.add(SPACE);
                    codeBegin = false;
                    codeEnd = false;
                } else {
                    tempType.add(OTHER);
                    tempList.add(line);
                }
            } else {
                // 非代码块
                if ("".equals(line)) {
                    tempType.add(OTHER);
                    tempList.add(SPACE);
                } else {
                    tempType.add(OTHER);
                    tempList.add(line);
                }
            }
        }
        tempType.add(OTHER);
        tempList.add(SPACE);


        mdList = (ArrayList<String>) tempList.clone();
        mdListType = (ArrayList<String>) tempType.clone();
        tempList.clear();
        tempType.clear();

        // 定位其它区
        boolean isCodeArea = false;
        tempList.add(SPACE);
        tempType.add(OTHER);
        for (int i = 1; i < mdList.size() - 1; i++) {
            String line = mdList.get(i);
            String lastLine = mdList.get(i - 1);
            String nextLine = mdList.get(i + 1);

            if (mdListType.get(i) == CODE_BEGIN) {
                // 进入代码块
                isCodeArea = true;
                tempList.add(line);
                tempType.add(CODE_BEGIN);
                continue;
            }
            if (mdListType.get(i) == CODE_END) {
                // 退出代码块
                isCodeArea = false;
                tempList.add(line);
                tempType.add(CODE_END);
                continue;
            }

            // 如果不是代码块
            if (!isCodeArea) {
                if (line.length() > 2 && line.charAt(0) == '>'
                        && lastLine.charAt(0) != '>'
                        && lastLine.charAt(0) == '>'
                ) {
                    // 进入引用区
                    tempList.add(SPACE);
                    tempList.add(line);
                    tempType.add(QUOTE_BEGIN);
                    tempType.add(OTHER);
                } else if (line.length() > 2 && line.charAt(0) == '>'
                        && lastLine.charAt(0) == '>' && nextLine.charAt(0) != '>'
                ) {
                    // 离开引用区
                    tempList.add(line);
                    tempList.add(SPACE);
                    tempType.add(OTHER);
                    tempList.add(QUOTE_END);
                } else if (line.length() > 2 && line.charAt(0) == '>'
                        && lastLine.charAt(0) != '>' && nextLine.charAt(0) != '>'
                ) {
                    // 单行引用
                    tempList.add(SPACE);
                    tempList.add(line);
                    tempList.add(SPACE);
                    tempType.add(QUOTE_BEGIN);
                    tempType.add(OTHER);
                    tempType.add(QUOTE_END);
                } else if ((line.charAt(0) == '-' && lastLine.charAt(0) != '-' && nextLine.charAt(0) == '-') ||
                        (line.charAt(0) == '+' && lastLine.charAt(0) != '+' && nextLine.charAt(0) == '+') ||
                        (line.length() > 1 && line.charAt(0) == '*' && line.charAt(1) != '*' && lastLine.charAt(0) != '*' && nextLine.charAt(0) == '*')) {
                    // 进入无序列表
                    tempList.add(SPACE);
                    tempList.add(line);
                    tempType.add(UNORDER_BEGIN);
                    tempType.add(OTHER);
                } else if ((line.charAt(0) == '-' && lastLine.charAt(0) == '-' && nextLine.charAt(0) != '-') ||
                        (line.charAt(0) == '+' && lastLine.charAt(0) == '+' && nextLine.charAt(0) != '+') ||
                        (line.charAt(0) == '*' && lastLine.charAt(0) == '*' && nextLine.charAt(0) != '*')) {
                    // 离开无序列表
                    tempList.add(line);
                    tempList.add(SPACE);
                    tempType.add(OTHER);
                    tempType.add(UNORDER_END);
                } else if ((line.charAt(0) == '-' && lastLine.charAt(0) != '-' && nextLine.charAt(0) != '-') ||
                        (line.charAt(0) == '+' && lastLine.charAt(0) != '+' && nextLine.charAt(0) != '+') ||
                        (line.length() > 1 && line.charAt(1) != '*' && line.charAt(0) == '*' && lastLine.charAt(0) != '*' && nextLine.charAt(0) != '*')) {
                    // 单行无序列表
                    tempList.add(SPACE);
                    tempList.add(line);
                    tempList.add(SPACE);
                    tempType.add(UNORDER_BEGIN);
                    tempType.add(OTHER);
                    tempType.add(UNORDER_END);
                } else if ((line.length() > 1 && (line.charAt(0) >= '1' || line.charAt(0) <= '9') && (line.charAt(1) == '.')) &&
                        !(lastLine.length() > 1 && (lastLine.charAt(0) >= '1' || line.charAt(0) <= '9') && (lastLine.charAt(1) == '.')) &&
                        (nextLine.length() > 1 && (nextLine.charAt(0) >= '1' || line.charAt(0) <= '9') && (nextLine.charAt(1) == '.'))) {
                    // 有序列表
                    tempList.add(SPACE);
                    tempList.add(line);
                    tempType.add(ORDER_BEGIN);
                    tempType.add(OTHER);
                } else if ((line.length() > 1 && (line.charAt(0) >= '1' || line.charAt(0) <= '9') && (line.charAt(1) == '.')) &&
                        (lastLine.length() > 1 && (lastLine.charAt(0) >= '1' || line.charAt(0) <= '9') && (lastLine.charAt(1) == '.')) &&
                        !(nextLine.length() > 1 && (nextLine.charAt(0) >= '1' || line.charAt(0) <= '9') && (nextLine.charAt(1) == '.'))) {
                    // 离开有序列表
                    tempList.add(line);
                    tempList.add(SPACE);
                    tempType.add(OTHER);
                    tempType.add(ORDER_END);
                } else if ((line.length() > 1 && (line.charAt(0) >= '1' || line.charAt(0) <= '9') && (line.charAt(1) == '.')) &&
                        !(lastLine.length() > 1 && (lastLine.charAt(0) >= '1' || line.charAt(0) <= '9') && (lastLine.charAt(1) == '.')) &&
                        !(nextLine.length() > 1 && (nextLine.charAt(0) >= '1' || line.charAt(0) <= '9') && (nextLine.charAt(1) == '.'))) {
                    // 单行有序列表
                    tempList.add(SPACE);
                    tempList.add(line);
                    tempList.add(SPACE);
                    tempType.add(ORDER_BEGIN);
                    tempType.add(OTHER);
                    tempType.add(ORDER_END);
                } else {
                    // 其它
                    tempList.add(line);
                    tempType.add(OTHER);
                }
            } else {
                // 不是代码块、有序列表、无序列表、引用
                tempList.add(line);
                tempType.add(OTHER);
            }
        }
        tempList.add(SPACE);
        tempType.add(OTHER);

        mdList = (ArrayList<String>) tempList.clone();
        mdListType = (ArrayList<String>) tempType.clone();
    }

    /**
     * 判断每一行markdown对应的html类型
     */
    private void defineLineType() {
        Deque<String> st = new LinkedList<>();
        for (int i = 0; i < mdList.size(); i++) {
            String line = mdList.get(i);
            String typeLine = mdListType.get(i);
            if (typeLine == QUOTE_BEGIN || typeLine == UNORDER_BEGIN ||
                    typeLine == ORDER_BEGIN || typeLine == CODE_BEGIN) {
                st.push(typeLine);
            } else if (typeLine == QUOTE_END || typeLine == UNORDER_END ||
                    typeLine == ORDER_END || typeLine == CODE_END) {
                st.pop();
            } else if (typeLine == OTHER) {
                if (!st.isEmpty()) {
                    // 引用行
                    if (st.peek() == QUOTE_BEGIN) {
                        mdList.set(i, line.trim().substring(1).trim());
                        mdListType.set(i, QUOTE_LINE);
                    } else if (st.peek() == UNORDER_BEGIN) {
                        // 无序列表行
                        mdList.set(i, line.trim().substring(1).trim());
                        mdListType.set(i, UNORDER_LINE);
                    } else if (st.peek() == ORDER_BEGIN) {
                        mdList.set(i, line.trim().substring(2).trim());
                        mdListType.set(i, ORDER_LINE);
                    } else {
                        // 代码行
                        mdListType.set(i, CODE_LINE);
                    }
                }
                line = mdList.get(i);
                typeLine = mdListType.get(i);
                if (line.trim().isEmpty()) {
                    // 空行
                    mdListType.set(i, BLANK_LINE);
                    mdListType.set(i, "");
                } else if (line.trim().charAt(0) == '#') {
                    // 标题行
                    mdListType.set(i, TITLE);
                    mdList.set(i, line.trim());
                }
            }
        }
    }

    /**
     * 根据每一行类型，将markdown转化为html
     */
    private void parseToHtml() {
        for (int i = 0; i < mdList.size(); i++) {
            String line = mdList.get(i);
            String typeLine = mdListType.get(i);

            if (typeLine == BLANK_LINE) {
                // 空行
                mdList.set(i, "");
            } else if (typeLine == OTHER) {
                // 普通行
                mdList.set(i, "<p>" + parseToHtmlInline(line.trim()) + "</p>");
            } else if (typeLine == TITLE) {
                // 标题
                int titleClass = 1;
                for (int j = 1; j < line.length(); j++) {
                    if (line.charAt(j) == '#') {
                        titleClass++;
                    } else {
                        break;
                    }
                }
                mdList.set(i, "<h" + titleClass + ">" + parseToHtmlInline(line.substring(titleClass).trim()) + "</h" + titleClass + ">");
            } else if (typeLine == UNORDER_BEGIN) {
                // 无序列表行
                mdList.set(i, "<ul>");
            } else if (typeLine == UNORDER_END) {
                mdList.set(i, "</ul>");
            } else if (typeLine == UNORDER_LINE) {
                mdList.set(i, "<li>" + parseToHtmlInline(line.trim()) + "</li>");
            } else if (typeLine == ORDER_BEGIN) {
                // 有序列表行
                mdList.set(i, "<ol>");
            } else if (typeLine == ORDER_END) {
                mdList.set(i, "</ol>");
            } else if (typeLine == ORDER_LINE) {
                mdList.set(i, "<li>" + parseToHtmlInline(line.trim()) + "</li>");
            } else if (typeLine == CODE_BEGIN) {
                mdList.set(i, "<pre>");
            } else if (typeLine == CODE_END) {
                mdList.set(i, "</pre>");
            } else if (typeLine == CODE_LINE) {
                mdList.set(i, "<code>" + line + "</code>");
            } else if (typeLine == QUOTE_BEGIN) {
                mdList.set(i, "<blockquote>");
            } else if (typeLine == QUOTE_END) {
                mdList.set(i, "</blockquote>");
            }

        }
    }

    /**
     * 将行内markdown转换成对应html
     *
     * @param line
     * @return
     */
    private String parseToHtmlInline(String line) {
        for (int i = 0; i < line.length(); i++) {
            // 图片
            if (i < line.length() - 4 && line.charAt(i) == '!' && line.charAt(i + 1) == '[') {
                int index1 = line.indexOf(']', i + 1);
                if (index1 != -1 && line.charAt(index1 + 1) == '(' && line.indexOf(')', index1 + 2) != -1) {
                    int index2 = line.indexOf(')', index1 + 2);
                    String picName = line.substring(i + 2, index1);
                    String picPath = line.substring(index1 + 2, index2);
                    line = line.replace(line.substring(i, index2 + 1), "<img alt='" + picName + "' src='" + picPath + "' />");
                }
            }
            // 链接
            if (i < line.length() - 3 && ((i > 0 && line.charAt(i) == '[' && line.charAt(i - 1) != '!') || (line.charAt(0) == '['))) {
                int index1 = line.indexOf(']', i + 1);
                if (index1 + 1 < line.length() && index1 != -1 && line.charAt(index1 + 1) == '(' && line.indexOf(')', index1 + 2) != -1) {
                    int index2 = line.indexOf(')', index1 + 2);
                    String linkName = line.substring(i + 1, index1);
                    String linkPath = line.substring(index1 + 2, index2);
                    line = line.replace(line.substring(i, index2 + 1), "<a href='" + linkPath + "'> " + linkName + "</a>");
                }
            }
            // 行内引用
            if (i < line.length() - 1 && line.charAt(i) == '`' && line.charAt(i + 1) != '`') {
                int index = line.indexOf('`', i + 1);
                if (index != -1) {
                    String quoteName = line.substring(i + 1, index);
                    line = line.replace(line.substring(i, index + 1), "<code>" + quoteName + "</code>");
                }
            }
            // 粗体
            if (i < line.length() - 2 && line.charAt(i) == '*' && line.charAt(i + 1) == '*') {
                if (i - 6 > -1 && "<code>".equals(line.substring(i - 6, i))) {
                    // 说明有行内引用
                    continue;
                }
                int index = line.indexOf("**", i);
                line = line.replaceFirst("\\*\\*", "<strong>");
                i = i + 8;
                index = line.indexOf("**", i);
                if (index != -1) {
                    String quoteName = line.substring(i, index);
                    line = line.replaceFirst(quoteName + "\\*\\*", quoteName + "</strong>");
                }
            }
            // 斜体
            if (i < line.length() - 2 && line.charAt(i) == '*' && line.charAt(i + 1) != '*') {
                if (i - 6 > -1 && "<code>".equals(line.substring(i - 6, i))) {
                    // 说明有行内引用
                    continue;
                } else if (i - 7 > -1 && "<code>".equals(line.substring(i - 7, i))) {
                    // 说明有行内引用
                    continue;
                }
                int index = line.indexOf('*', i + 1);
                if (index != -1 && line.charAt(index + 1) != '*') {
                    String quoteName = line.substring(i + 1, index);
                    line = line.replace(line.substring(i, index + 1), "<i>" + quoteName + "</i>");
                }
            }
        }
        return line;
    }

}
