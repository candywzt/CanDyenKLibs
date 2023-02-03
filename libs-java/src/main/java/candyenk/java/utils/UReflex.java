package candyenk.java.utils;


import candyenk.java.tools.T;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Java反射工具
 */
public class UReflex {
    /**
     * 查找方法
     * 不会报错
     * 没找到就是null
     */
    public static Class<?> fc(String n) {
        try {
            return Class.forName(n);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 查找方法
     * 不会报错
     * 没找到就是null
     */
    public static Class<?> fc(ClassLoader cl, String n) {
        try {
            return Class.forName(n, true, cl);
        } catch (Throwable e) {
            return null;
        }
    }

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
        Constructor<T> cc = null;
        Throwable e = null;
        try {
            cc = oc.getConstructor(c);
        } catch (Throwable e1) {
            try {
                cc = oc.getDeclaredConstructor(c);
            } catch (Throwable e2) {
                e = e2;
            }
        }
        return new UC<>(e == null ? cc : null).setE(e);
    }

    /**
     * 查找本类方法
     *
     * @param o    方法所在的对象
     * @param name 方法名
     * @param c    方法参数类型集合
     */
    public static UM findM(Object o, String name, Class<?>... c) {
        return findM(o.getClass(), name, c);
    }

    /**
     * 查找本类方法
     *
     * @param oc   方法所在的类
     * @param name 方法名
     * @param c    方法参数类型集合
     */
    public static UM findM(Class<?> oc, String name, Class<?>... c) {
        if (UString.isEmpty(name)) return new UM(null).setE(new NullPointerException("findM(方法名为空)"));
        Method m = null;
        Throwable e = null;
        try {
            m = oc.getDeclaredMethod(name, c);
        } catch (Throwable e1) {
            try {
                m = oc.getMethod(name, c);
            } catch (Throwable e2) {
                e = e2;
            }
        }
        return new UM(e == null ? m : null).setE(e);
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
        if (UString.isEmpty(name)) return new UF(null).setE(new NullPointerException("findM(方法名为空)"));
        Field f = null;
        Throwable e = null;
        try {
            f = oc.getDeclaredField(name);
        } catch (Throwable e1) {
            try {
                f = oc.getField(name);
            } catch (Throwable e2) {
                e = e2;
            }
        }
        return new UF(e == null ? f : null).setE(e);
    }

    /**
     * 筛选构造方法
     * 返回不可变Set
     *
     * @param o    构造方法所在的对象
     * @param p    是否包括未声明的构造方法
     * @param pred 筛选条件,返回true加入清单
     */
    public static <T> Set<UC<T>> findCS(T o, boolean p, Predicate<Constructor<?>> pred) {
        return findCS((Class<T>) o.getClass(), p, pred);
    }

    /**
     * 筛选构造方法
     * 返回不可变Set
     *
     * @param oc   构造方法所在的类
     * @param p    是否包括未声明的构造方法
     * @param pred 筛选条件,返回true加入清单
     */
    public static <T> Set<UC<T>> findCS(Class<T> oc, boolean p, Predicate<Constructor<?>> pred) {
        Set<UC<T>> ul = new HashSet<>();
        Constructor<T>[] cs = UArrays.merge((Constructor<T>[]) oc.getDeclaredConstructors(), p ? (Constructor<T>[]) oc.getConstructors() : null);
        if (cs != null) for (Constructor<T> c : cs) if (pred == null || pred.test(c)) ul.add(new UC<>(c));
        return Collections.unmodifiableSet(ul);
    }

    /**
     * 筛选方法
     * 返回不可变Set
     *
     * @param o    方法所在的对象
     * @param p    是否包括父类public方法
     * @param pred 筛选条件,返回true加入清单
     */
    public static Set<UM> findMS(Object o, boolean p, Predicate<Method> pred) {
        return findMS(o.getClass(), p, pred);
    }

    /**
     * 筛选方法
     * 返回不可变Set
     *
     * @param oc   方法所在的类
     * @param p    是否包括父类public方法
     * @param pred 筛选条件,返回true加入清单
     */
    public static Set<UM> findMS(Class<?> oc, boolean p, Predicate<Method> pred) {
        Set<UM> ul = new HashSet<>();
        Method[] ms = UArrays.merge(oc.getDeclaredMethods(), p ? oc.getMethods() : null);
        for (Method m : ms) if (pred == null || pred.test(m)) ul.add(new UM(m));
        return Collections.unmodifiableSet(ul);
    }

    /**
     * 筛选字段
     * 返回不可变Set
     *
     * @param o    字段所在的对象
     * @param p    是否包括父类public方法
     * @param pred 筛选条件,返回true加入清单
     */
    public static Set<UF> findFS(Object o, boolean p, Predicate<Field> pred) {
        return findFS(o.getClass(), p, pred);
    }

    /**
     * 筛选字段
     * 返回不可变Set
     *
     * @param oc   字段所在的类
     * @param p    是否包括父类public字段
     * @param pred 筛选条件,返回true加入清单,为null则全部符合
     */
    public static Set<UF> findFS(Class<?> oc, boolean p, Predicate<Field> pred) {
        Set<UF> ul = new HashSet<>();
        Field[] fs = UArrays.merge(oc.getDeclaredFields(), p ? oc.getFields() : null);
        for (Field f : fs) if (pred == null || pred.test(f)) ul.add(new UF(f));
        return Collections.unmodifiableSet(ul);
    }

    /**
     * 获取对象继承链
     * 返回不可变List
     * 包括自身和Object
     */
    public static List<Class<?>> inheritChain(Object o) {
        return inheritChain(o.getClass());
    }

    /**
     * 获取类继承链
     * 返回不可变List
     * 包括自身和Object.class
     */
    public static List<Class<?>> inheritChain(Class<?> c) {
        if (c == null) return Collections.emptyList();
        else if (c == Object.class) return Collections.singletonList(Object.class);
        else if (c.getSuperclass() == Object.class) return Arrays.asList(c, Object.class);
        List<Class<?>> list = new ArrayList<>();
        list.add(c);
        while ((c = c.getSuperclass()) != null) list.add(c);
        return Collections.unmodifiableList(list);
    }

    /**
     * 一个小小的带实例Method包装类
     * 用来连调使用
     */
    public static class UC<T> {
        private final Constructor<T> c;//Find到的方法
        private Throwable e;//c为空时=Find期间的异常,c不为空时=newInstance期间的异常(没有发生异常那就是空)

        public UC(Constructor<T> c) {
            this.c = c;
        }

        /*** 设置当前异常 ***/
        protected UC<T> setE(Throwable e) {
            this.e = e;
            return this;
        }

        /**
         * 获取当前位置的异常
         */
        public Throwable getE() {
            if (e == null) return null;
            else if (e.getCause() == null) return e;
            else return e.getCause();
        }

        /**
         * 获取当前构造方法
         */
        public Constructor<T> getC() {
            return c;
        }

        /**
         * 构建新的实例
         * 异常存在时方法无效且返回null
         */
        public T newInstance(Object... a) {
            if (c == null || e != null) return null;
            try {
                c.setAccessible(true);
                return c.newInstance(a);
            } catch (Throwable e) {
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
        private final Method m;//Find到的方法
        private Throwable e;//m为空时=Find期间的异常,m不为空时=invoke期间的异常(没有发生异常那就是空)

        protected UM(Method m) {
            this.m = m;
        }


        /*** 设置当前异常 ***/
        protected UM setE(Throwable e) {
            this.e = e;
            return this;
        }

        /**
         * 获取当前为止的异常
         */
        public Throwable getE() {
            if (e == null) return null;
            else if (e.getCause() == null) return e;
            else return e.getCause();
        }

        /**
         * 获取当前方法
         */
        public Method getM() {
            return m;
        }

        /**
         * 执行无返回值方法
         * 异常存在时方法无效且返回null
         */
        public <T> T invoke(Object o, Object... a) {
            if (m == null || o == null || e != null) return null;
            try {
                m.setAccessible(true);
                return (T) m.invoke(o, a);
            } catch (Throwable e) {
                e.printStackTrace(System.err);
                this.e = e;
                return null;
            }
        }

        /**
         * 执行有返回值方法
         * 异常存在时方法无效且返回null
         */
        public <T> T invoke(Object o, Class<T> r, Object... a) {
            if (m == null || o == null || e != null) return null;
            try {
                m.setAccessible(true);
                return (T) m.invoke(o, a);
            } catch (Throwable e) {
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
        private final Field f;//Find到的字段
        private Throwable e;//f为空时=Find期间的异常,f不为空时=getset期间的异常(没有发生异常那就是空)

        protected UF(Field f) {
            this.f = f;
        }

        /*** 设置当前异常 ***/
        protected UF setE(Throwable e) {
            this.e = e;
            return this;
        }

        /**
         * 获取当前为止的异常
         */
        public Throwable getE() {
            if (e == null) return null;
            else if (e.getCause() == null) return e;
            else return e.getCause();
        }

        /**
         * 获取当前字段
         */
        public Field getF() {
            return f;
        }

        /**
         * 修改字段值
         * 异常存在时方法无效
         */
        public void set(Object o, Object a) {
            if (f == null || o == null || e != null) return;
            try {
                f.setAccessible(true);
                f.set(o, a);
            } catch (Throwable e) {
                e.printStackTrace(System.err);
                this.e = e;
            }
        }

        /**
         * 获取字段值
         * 异常存在时返回null
         */
        public <T> T get(Object o) {
            if (f == null || o == null || e != null) return null;
            try {
                f.setAccessible(true);
                return (T) f.get(o);
            } catch (Throwable e) {
                e.printStackTrace(System.err);
                this.e = e;
                return null;
            }
        }

        /**
         * 获取字段名称
         */
        public String getName() {
            return f == null ? "" : f.getName();
        }

        /**
         * 获取字段类型
         */
        public Class<?> getType() {
            return f == null ? Object.class : f.getType();
        }

        /**
         * 判断该字段是不是指定类或指定类的子类
         *
         * @param c 指定类
         */
        public boolean is(Class<?> c) {
            if (f == null) return false;
            return c.isAssignableFrom(f.getType());
        }

        /**
         * 获取字段值(具体类型)
         */
        public <T> T get(Object o, Class<T> c) {
            return (T) get(o);
        }

        @Override
        public String toString() {
            return "UF{" +
                    "f=" + f +
                    ", e=" + e +
                    '}';
        }
    }
}
