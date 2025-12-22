package org.qiuhua.troveserver.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringUtils {

    @Nullable
    public static Float stringToFloat(String string) {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static Double stringToDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }


    /**
     * 将字符串转换成Component
     * @param string
     * @return
     */
    public static Component stringToComponent(String string){
        return Component.text(string);
    }

    public static Component stringToComponentPixel(String string){
        return Component.text(string).font(Key.key("uifont", "pixel0"));
    }


    public static List<Component> stringToComponent(List<String> list){
        return list.stream()
                .map(StringUtils::stringToComponent)
                .collect(Collectors.toList());
    }

    public static List<Component> stringToComponentPixel(List<String> list){
        return list.stream()
                .map(StringUtils::stringToComponentPixel)
                .collect(Collectors.toList());
    }



    /**
     * 将&的颜色代码转换成§
     * @param str 需要转换的字符串
     * @return 转换的结果
     */
    public static String colorCodeConversion(String str) {
        return str.replaceAll("&", "§");
    }

    /**
     * 将列表中全部字符串的&颜色代码转换成§
     * @param strList 需要转换颜色代码的字符串列表
     * @return 转换后的字符串列表
     */
    public static List<String> colorCodeConversion(List<String> strList) {
        List<String> convertedList = new ArrayList<>();
        for (String str : strList) {
            convertedList.add(colorCodeConversion(str));
        }
        return convertedList;
    }


    /**
     * 去除全部颜色代码
     * @param str 需要去除的字符串
     * @return 去除后的结果
     */
    public static String removeColorCode(String str) {
        return str.replaceAll("(?i)§[0-9a-fk-orx]", "");
    }

    /**
     * 去除列表中全部字符串的颜色代码
     * @param strList 需要去除颜色代码的字符串列表
     * @return 去除颜色代码后的字符串列表
     */
    public static List<String> removeColorCode(List<String> strList) {
        List<String> cleanedList = new ArrayList<>();
        for (String str : strList) {
            cleanedList.add(removeColorCode(str));
        }
        return cleanedList;
    }


    /**
     * 批量替换指定字符
     * @param textList 需要替换的字符串
     * @param replacements 替换的键对值  key是要替换的字符 value是替换后的
     * @return 返回替换完成的列表
     */
    public static List<String> replacePlaceholders(List<String> textList, Map<String, String> replacements) {
        return textList.stream()
                .map(s -> {
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        s = s.replace(entry.getKey(), entry.getValue());
                    }
                    return s;
                })
                .collect(Collectors.toList());
    }

    /**
     * 替换指定字符
     * @param text 需要替换的字符串
     * @param replacements 替换的键值对，key是要替换的字符，value是替换后的字符
     * @return 返回替换完成的字符串
     */
    public static String replacePlaceholders(String text, Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }

}
