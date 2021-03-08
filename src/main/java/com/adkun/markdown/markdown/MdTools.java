package com.adkun.markdown.markdown;

import java.util.ArrayList;
import java.util.List;

/**
 * 小工具
 * @author adkun
 */
public class MdTools {

    /**
     * 将字符串按\n分割
     * @param html
     * @return
     */
    public static List<String> splitStr(String html) {
        String[] strs = html.split("\n");
        List<String> list = new ArrayList<>(strs.length + 2);
        list.add("\n");
        for (int i = 0; i < strs.length; i++) {
            list.add(strs[i]);
        }
        list.add("\n");
        return list;
    }
}
