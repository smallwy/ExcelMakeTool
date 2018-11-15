package core;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.util.*;

public class CoreConfig {
    private static Map<String, String> res = new HashMap();
    public static int MaxDataLength = 0;

    public static void load() {
        ResourceBundle bundle = ResourceBundle.getBundle("default");
        Set<String> keySet = bundle.keySet();
        Iterator var2 = keySet.iterator();

        String key;
        while(var2.hasNext()) {
            key = (String)var2.next();
            res.put(key, bundle.getString(key));
        }

        try {
            bundle = ResourceBundle.getBundle("config");
            keySet = bundle.keySet();
            var2 = keySet.iterator();

            while(var2.hasNext()) {
                key = (String)var2.next();
                res.put(key, bundle.getString(key));
            }


        } catch (Exception var4) {

        }


        MaxDataLength = intValue("MaxDataLength");
    }

    private CoreConfig() {
    }

    private static <T> T get(String key, Class<T> clazz) {
        String value = (String)res.get(key);
        return Tool.convertBaseType(value, clazz);
    }

    public static short shortValue(String key) {
        return ((Short)get(key, Short.TYPE)).shortValue();
    }

    public static int intValue(String key) {
        return ((Integer)get(key, Integer.TYPE)).intValue();
    }

    public static long longValue(String key) {
        return ((Long)get(key, Long.TYPE)).longValue();
    }

    public static float floatValue(String key) {
        return ((Float)get(key, Float.TYPE)).floatValue();
    }

    public static double doubleValue(String key) {
        return ((Double)get(key, Double.TYPE)).doubleValue();
    }

    public static boolean booleanValue(String key) {
        return ((Boolean)get(key, Boolean.TYPE)).booleanValue();
    }

    public static String stringValue(String key) {
        return (String)res.get(key);
    }
}
