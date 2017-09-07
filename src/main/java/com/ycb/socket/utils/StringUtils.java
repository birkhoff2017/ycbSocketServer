package com.ycb.socket.utils;

import org.apache.commons.collections.map.HashedMap;

import java.util.*;

public class StringUtils {
    public static List<String> splitToStringList(String str, String sepKey) {
        List list = new LinkedList();

        if ((isNotNull(str)) && (isNotNull(sepKey))) {
            if (str.contains(sepKey)) {
                String[] items = str.split(sepKey, -1);

                for (String item : items) {
                    list.add(item);
                }
            } else {
                list.add(str);
            }

        }

        return list;
    }

    public static final boolean isNotNull(String source) {
        return !isNull(source);
    }

    public static final boolean isNull(String source) {
        if ((source != null) && ((!source.trim().equals("")) || (!source.trim().equalsIgnoreCase("null")))) {
            return false;
        }

        return true;
    }

    public static String listToString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    public static List<Float> getFloatFromString(String s) {
        List<Float> floatList = new ArrayList<>();
        if (null != s && !"".equals(s)) {
            if (s.contains(",")) {
                String[] arr = s.split(",");
                for (String ai : arr) {
                    Float f = Float.valueOf(ai);
                    floatList.add(f);
                }
            } else {
                Float f = Float.valueOf(s);
                floatList.add(f);
            }
        }
        return floatList;
    }

    public static Map<String, String> str2Map(String str) {
        String[] strArr = str.split(";");
        Map<String, String> map = new LinkedHashMap<>();
        for (String node : strArr) {
            map.put(node.split(":")[0], node.split(":")[1]);
        }
        return map;
    }

    public static Map<String, Object> converBatMap(Map<String, String> reqMap) {
        Iterator<Map.Entry<String, String>> entries = reqMap.entrySet().iterator();
        Map<String, String> bat1Map = new HashedMap();
        Map<String, String> bat2Map = new HashedMap();
        Map<String, String> bat3Map = new HashedMap();
        Map<String, String> bat4Map = new HashedMap();
        Map<String, String> bat5Map = new HashedMap();
        Map<String, String> bat6Map = new HashedMap();
        Map<String, String> bat7Map = new HashedMap();
        Map<String, String> bat8Map = new HashedMap();
        Map<String, String> bat9Map = new HashedMap();
        Map<String, String> bat10Map = new HashedMap();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            if (entry.getKey().contains("B1") && !entry.getKey().contains("B10")) {
                bat1Map.put(entry.getKey().substring(2), entry.getValue());
            } else if (entry.getKey().contains("B2")) {
                bat2Map.put(entry.getKey().substring(2), entry.getValue());
            } else if (entry.getKey().contains("B3")) {
                bat3Map.put(entry.getKey().substring(2), entry.getValue());
            } else if (entry.getKey().contains("B4")) {
                bat4Map.put(entry.getKey().substring(2), entry.getValue());
            } else if (entry.getKey().contains("B5")) {
                bat5Map.put(entry.getKey().substring(2), entry.getValue());
            } else if (entry.getKey().contains("B6")) {
                bat6Map.put(entry.getKey().substring(2), entry.getValue());
            } else if (entry.getKey().contains("B7")) {
                bat7Map.put(entry.getKey().substring(2), entry.getValue());
            } else if (entry.getKey().contains("B8")) {
                bat8Map.put(entry.getKey().substring(2), entry.getValue());
            } else if (entry.getKey().contains("B9")) {
                bat9Map.put(entry.getKey().substring(2), entry.getValue());
            } else if (entry.getKey().contains("B10")) {
                bat10Map.put(entry.getKey().substring(3), entry.getValue());
            }
        }
        Map<String, Object> returnMap = new HashedMap();
        if (!bat1Map.isEmpty()) {
            returnMap.put("1", bat1Map);
        }
        if (!bat2Map.isEmpty()) {
            returnMap.put("2", bat2Map);
        }
        if (!bat3Map.isEmpty()) {
            returnMap.put("3", bat3Map);
        }
        if (!bat4Map.isEmpty()) {
            returnMap.put("4", bat4Map);
        }
        if (!bat5Map.isEmpty()) {
            returnMap.put("5", bat5Map);
        }
        if (!bat6Map.isEmpty()) {
            returnMap.put("6", bat6Map);
        }
        if (!bat7Map.isEmpty()) {
            returnMap.put("7", bat7Map);
        }
        if (!bat8Map.isEmpty()) {
            returnMap.put("8", bat8Map);
        }
        if (!bat9Map.isEmpty()) {
            returnMap.put("9", bat9Map);
        }
        if (!bat10Map.isEmpty()) {
            returnMap.put("10", bat10Map);
        }
        return returnMap;
    }
}
