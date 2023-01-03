package candyenk.java.utils;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Java反射工具
 */
public class UReflex {
    /**
     * 查找构造方法
     *
     * @param o 构造方法所在的对象
     * @param c 构造方法参数
     */
    public static <T> UC<T> findC(Object o, Class<?>... c) {
        return findC((T) o.getClass(), c);
    }

    /**
     * 查找构造方法
     *
     * @param oc 构造方法所在的类
     * @param c  构造方法参数
     */
    public static <T> UC<T> findC(Class<T> oc, Class<?>... c) {
        try {
            Constructor<T> con = oc.getConstructor(c);
            return new UC<>(con);
        } catch (Exception e) {
            return new UC<>(e);
        }

    }

    /**
     * 查找方法
     *
     * @param o    方法所在的对象
     * @param name 方法名
     * @param c    方法参数类型集合
     */
    public static UM findM(Object o, String name, Class<?>... c) {
        return findM(o.getClass(), name, c);
    }

    /**
     * 查找方法
     *
     * @param oc   方法所在的类
     * @param name 方法名
     * @param c    方法参数类型集合
     */
    public static UM findM(Class<?> oc, String name, Class<?>... c) {
        if (UString.isEmpty(name)) return new UM(new NullPointerException("findM(方法名为空)"));
        Method m = null;
        Exception e = null;
        try {
            m = oc.getDeclaredMethod(name, c);
        } catch (NoSuchMethodException e1) {
            try {
                m = oc.getMethod(name, c);
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Exception e1) {
            e = e1;
        }
        return m == null ? new UM(e) : new UM(m);
    }

    /**
     * 查找字段
     *
     * @param o    字段所在的对象
     * @param name 字段名
     */
    public static UF findF(Object o, String name) {
        return findF(o.getClass(), name);
    }

    /**
     * 查找字段
     *
     * @param oc   字段所在的类
     * @param name 字段名
     */
    public static UF findF(Class<?> oc, String name) {
        if (UString.isEmpty(name)) return new UF(new NullPointerException("findM(方法名为空)"));
        Field f = null;
        Exception e = null;
        try {
            f = oc.getDeclaredField(name);
        } catch (NoSuchFieldException e1) {
            try {
                f = oc.getField(name);
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Exception e1) {
            e = e1;
        }
        return f == null ? new UF(e) : new UF(f);
    }

    /**
     * 一个小小的带实例Method包装类
     * 用来连调使用
     */
    public static class UC<T> {
        public final Constructor<T> c;//Find到的方法
        public Exception e;//c为空时=Find期间的异常,c不为空时=newInstance期间的异常(没有发生异常那就是空)

        public UC(Constructor<T> c) {
            this.c = c;
        }

        public UC(Exception e) {
            this.c = null;
            this.e = e;
        }

        /**
         * 构建新的实例
         */
        public T newInstance(Object... a) {
            if (c == null) return null;
            try {
                return c.newInstance(a);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                this.e = e;
                return null;
            }
        }

        /**
         * 是不是空方法
         */
        public boolean isNull() {
            return c == null;
        }
    }

    /**
     * 一个小小的带实例Method包装类
     * 用来连调使用
     */
    public static class UM {
        public final Method m;//Find到的方法
        public Exception e;//m为空时=Find期间的异常,m不为空时=invoke期间的异常(没有发生异常那就是空)

        public UM(Method m) {
            this.m = m;
        }

        public UM(Exception e) {
            this.m = null;
            this.e = e;
        }

        /**
         * 执行无返回值方法
         */
        public <T> T invoke(Object o, Object... a) {
            if (m == null || o == null) return null;
            try {
                return (T) m.invoke(o, a);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                this.e = e;
                return null;
            }
        }

        /**
         * 执行有返回值方法
         */
        public <T> T invoke(Object o, Class<T> r, Object... a) {
            if (m == null || o == null) return null;
            try {
                return (T) m.invoke(o, a);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                this.e = e;
                return null;
            }
        }

        /**
         * 是不是空方法
         */
        public boolean isNull() {
            return m == null;
        }
    }

    /**
     * 一个小小的带实例Field包装类
     * 用来连调使用
     */
    public static class UF {
        public final Field f;//Find到的字段
        public Exception e;//f为空时=Find期间的异常,f不为空时=getset期间的异常(没有发生异常那就是空)

        public UF(Field f) {
            this.f = f;
        }

        public UF(Exception e) {
            this.f = null;
            this.e = e;
        }

        /**
         * 修改字段值
         */
        public void set(Object o, Object a) {
            if (f == null || o == null) return;
            try {
                f.setAccessible(true);
                f.set(o, a);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                this.e = e;
            }
        }

        /**
         * 获取字段值
         */
        public <T> T get(Object o) {
            if (f == null || o == null) return null;
            try {
                f.setAccessible(true);
                return (T) f.get(o);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                this.e = e;
                return null;
            }
        }

        /**
         * 获取字段值(具体类型)
         */
        public <T> T get(Object o, Class<T> c) {
            return (T) get(o);
        }

    }
}
