package core;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public final class Tool {

    public Tool() {
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static <T> T getSystemProperty(String key, Class<T> clazz) {
        return convertBaseType(System.getProperty(key), clazz);
    }

    public static <T> T convertType(Object obj, Class<T> cla) {
        return obj instanceof String ? convertBaseType((String)obj, cla) : (T) obj;
    }

    public static <T> T convertBaseType(String value, Class<T> cla) {
        if (value == null) {
            return null;
        } else if (!Byte.TYPE.isAssignableFrom(cla) && !Byte.class.isAssignableFrom(cla)) {
            if (!Short.TYPE.isAssignableFrom(cla) && !Short.class.isAssignableFrom(cla)) {
                if (!Integer.TYPE.isAssignableFrom(cla) && !Integer.class.isAssignableFrom(cla)) {
                    if (!Long.TYPE.isAssignableFrom(cla) && !Long.class.isAssignableFrom(cla)) {
                        if (!Float.TYPE.isAssignableFrom(cla) && !Float.class.isAssignableFrom(cla)) {
                            if (!Double.TYPE.isAssignableFrom(cla) && !Double.class.isAssignableFrom(cla)) {
                                return !Boolean.TYPE.isAssignableFrom(cla) && !Boolean.class.isAssignableFrom(cla) ? null : (T) Boolean.valueOf(value);
                            } else {
                                return (T) Double.valueOf(value);
                            }
                        } else {
                            return (T) Float.valueOf(value);
                        }
                    } else {
                        return (T) Long.valueOf(value);
                    }
                } else {
                    return (T) Integer.valueOf(value);
                }
            } else {
                return (T) Short.valueOf(value);
            }
        } else {
            return (T) Byte.valueOf(value);
        }
    }

    public static boolean isSameType(Class<?> c1, Class<?> c2) {
        if (c1 == c2) {
            return true;
        } else if (!c1.isAssignableFrom(c2) && !c2.isAssignableFrom(c1)) {
            if (Byte.class.isAssignableFrom(c1) && Byte.TYPE.isAssignableFrom(c2) || Byte.TYPE.isAssignableFrom(c1) && Byte.class.isAssignableFrom(c2)) {
                return true;
            } else if (Boolean.class.isAssignableFrom(c1) && Boolean.TYPE.isAssignableFrom(c2) || Boolean.TYPE.isAssignableFrom(c1) && Boolean.class.isAssignableFrom(c2)) {
                return true;
            } else if (Character.class.isAssignableFrom(c1) && Character.TYPE.isAssignableFrom(c2) || Character.TYPE.isAssignableFrom(c1) && Character.class.isAssignableFrom(c2)) {
                return true;
            } else if (Short.class.isAssignableFrom(c1) && Short.TYPE.isAssignableFrom(c2) || Short.TYPE.isAssignableFrom(c1) && Short.class.isAssignableFrom(c2)) {
                return true;
            } else if (Integer.class.isAssignableFrom(c1) && Integer.TYPE.isAssignableFrom(c2) || Integer.TYPE.isAssignableFrom(c1) && Integer.class.isAssignableFrom(c2)) {
                return true;
            } else if ((!Long.class.isAssignableFrom(c1) || !Long.TYPE.isAssignableFrom(c2)) && (!Long.TYPE.isAssignableFrom(c1) || !Long.class.isAssignableFrom(c2))) {
                if ((!Float.class.isAssignableFrom(c1) || !Float.TYPE.isAssignableFrom(c2)) && (!Float.TYPE.isAssignableFrom(c1) || !Float.class.isAssignableFrom(c2))) {
                    return Double.class.isAssignableFrom(c1) && Double.TYPE.isAssignableFrom(c2) || Double.TYPE.isAssignableFrom(c1) && Double.class.isAssignableFrom(c2);
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static String getClassesDesc(Class<?>[] classes) {
        StringBuilder msg = new StringBuilder(16);
        int len = classes.length;

        for(int i = 0; i < len; ++i) {
            if (i > 0) {
                msg.append(",");
            }

            msg.append(classes[i].getSimpleName());
        }

        return msg.toString();
    }

    public static Method methodReader(Field f) {
        try {
            return f.getDeclaringClass().getMethod(methodReaderName(f));
        } catch (SecurityException var2) {
            var2.printStackTrace();
        } catch (NoSuchMethodException var3) {
            var3.printStackTrace();
        }

        return null;
    }

    public static String methodReaderName(Field f) {
        String fName = f.getName();
        StringBuilder builder = new StringBuilder();
        if (!Boolean.class.isAssignableFrom(f.getType()) && !Boolean.TYPE.isAssignableFrom(f.getType())) {
            builder.append("get");
            builder.append(fName.substring(0, 1).toUpperCase());
            builder.append(fName.substring(1));
        } else {
            String name = f.getName();
            if (name.startsWith("is") && name.length() > 2 && Character.isUpperCase(name.charAt(2))) {
                builder.append(name);
            } else {
                builder.append("is");
                builder.append(fName.substring(0, 1).toUpperCase());
                builder.append(fName.substring(1));
            }
        }

        return builder.toString();
    }
}
