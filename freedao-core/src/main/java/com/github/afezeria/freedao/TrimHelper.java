package com.github.afezeria.freedao;

/**
 *
 */
public class TrimHelper {

    public static StringBuilder appendAndTrim(StringBuilder source,
                                              String prefix,
                                              String[] prefixOverrides,
                                              String[] suffixOverrides) {
        boolean ret = true;
        for (int i = 0; i < source.length(); i++) {
            if (!Character.isWhitespace(source.charAt(i))) {
                ret = false;
                break;
            }
        }
        if (ret) {
            return new StringBuilder();
        }
        int left = 0;
        int right = source.length() - 1;

        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) > ' ') {
                left = i;
                break;
            }
        }
        for (int i = right; i >= 0; i--) {
            if (source.charAt(i) > ' ') {
                right = i;
                break;
            }
        }
        for (String s : prefixOverrides) {
            if (source.length() - left < s.length()) {
                break;
            }
            boolean match = true;
            for (int i = 0; i < s.length(); i++) {
                if (source.charAt(left + i) != s.charAt(i)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                left = left + s.length();
                break;
            }
        }
        for (String s : suffixOverrides) {
            if (right - left < s.length()) {
                break;
            }
            boolean match = true;
            for (int i = 0; i < s.length(); i++) {
                if (source.charAt(right - i) != s.charAt(s.length() - 1 - i)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                right = right - s.length();
                break;
            }
        }
        return new StringBuilder().append(prefix).append(source, left, right + 1);
    }


    public static void main(String[] args) {
        StringBuilder target = new StringBuilder("abc ");
        StringBuilder source = new StringBuilder(" and");
//        appendAndTrim(target, source, "where", new String[]{"and", "or"}, new String[]{"1"});
//        System.out.println(builder);
//        builder.append(" and a = ? and c like '%d' where");
//        trimPostfix(builder, "where");
        System.out.println(target);
    }
}
