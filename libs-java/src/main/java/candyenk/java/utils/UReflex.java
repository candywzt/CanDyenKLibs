package candyenk.java.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import candyenk.java.tools.T;

/**
 * Java反射工具
 */
public class UReflex {
    /**
     * 获取类实例
     *
     * @param classz 类Class对象
     * @param args   构造方法参数集
     * @return 类实例
     */
    public static <T> T newInstance(Class<T> classz, Object... args) {
        T object = null;
        Class[] cs = new Class[args.length];
        for (int i = 0; i < args.length; i++) cs[i] = args[i].getClass();
        try {
            Constructor<T> d = classz.getDeclaredConstructor(cs);
            d.setAccessible(true);
            object = d.newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 执行方法
     *
     * @param classz      方法所在的类对象
     * @param obj         执行方法的类对象
     * @param methodName  方法名
     * @param args        参数列表
     * @param resultClass 返回值类型
     * @return 方法返回值
     */
    public static <T> T invoke(Class<?> classz, Object obj, String methodName, Class<T> resultClass, Object... args) {
        T result = null;
        Class[] cs = new Class[args.length];
        for (int i = 0; i < args.length; i++) cs[i] = args[i].getClass();
        try {
            if (classz == null && obj != null) classz = obj.getClass();
            Method m = classz.getDeclaredMethod(methodName, cs);
            m.setAccessible(true);
            result = (T) m.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Object invoke(Class<?> classz, Object obj, String methodName, Object... args) {
        return invoke(classz, obj, methodName, null, args);
    }

    public static Object invoke(Object obj, String methodName, Object... args) {
        return invoke(null, obj, methodName, null, args);
    }

    /**
     * 获取对象的成员变量
     *
     * @param classz   对象的类型
     * @param obj      指定对象
     * @param varName  变量名
     * @param varClass 变量类型
     * @return 变量
     */
    public static <T> T getVar(Class<?> classz, Object obj, String varName, Class<T> varClass) {
        T object = null;
        try {
            Field f = classz.getDeclaredField(varName);
            object = (T) f.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 设置对象的成员变量
     *
     * @param classz  对象的类型
     * @param obj     指定对象
     * @param varName 变量名
     * @param value   设置的值
     * @return 变量
     */
    public static void setVar(Class<?> classz, Object obj, String varName, Object value) {
        T object = null;
        try {
            Field f = classz.getDeclaredField(varName);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
