package candyenk.java.utils;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * Java反射工具
 */
public class UReflex {
    /**
     * 查找方法
     * 报错则返回null
     */
    public static Class<?> fc(String className) {
        try {
            return Class.forName(className);
        } catch (Throwable e) {
            throw null;
        }
    }
    
    /**
     * 查找方法
     * 报错返回null
     */
    public static Class<?> fc(ClassLoader cl, String className) {
        try {
            return Class.forName(className, true, cl);
        } catch (Throwable e) {
            return null;
        }
    }
    
    /**
     * 查找构造方法
     *
     * @param oc 构造方法所在的类
     * @param c  构造方法参数
     */
    public static <T> UC<T> findC(Class<T> oc, Class<?>... c) {
        try {
            return new UC<>(oc.getConstructor(c));
        } catch (Throwable e) {
            try {
                return new UC<>(oc.getDeclaredConstructor(c));
            } catch (Throwable e1) {
                return new UC<>(e1);
            }
        }
    }
    
    /**
     * 查找本类方法
     *
     * @param oc   方法所在的类
     * @param name 方法名
     * @param c    方法参数类型集合
     */
    public static UM findM(Class<?> oc, String name, Class<?>... c) {
        if (UString.isEmpty(name)) return new UM(new NullPointerException("findM(方法名为空)"));
        try {
            return new UM(oc.getDeclaredMethod(name, c));
        } catch (Throwable e) {
            try {
                return new UM(oc.getMethod(name, c));
            } catch (Throwable e1) {
                return new UM(e1);
            }
        }
    }
    
    /**
     * 查找字段
     *
     * @param oc   字段所在的类
     * @param name 字段名
     */
    public static UF findF(Class<?> oc, String name) {
        if (UString.isEmpty(name)) return new UF(new NullPointerException("findM(方法名为空)"));
        try {
            return new UF(oc.getDeclaredField(name));
        } catch (Throwable e) {
            try {
                return new UF(oc.getField(name));
            } catch (Throwable e1) {
                return new UF(e1);
            }
        }
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
        Constructor<T>[] cs = UArrays.merge((Constructor<T>[]) oc.getDeclaredConstructors(), p ?
                (Constructor<T>[]) oc.getConstructors() : null);
        if (cs != null) for (Constructor<T> c : cs) if (pred == null || pred.test(c)) ul.add(new UC<>(c));
        return Collections.unmodifiableSet(ul);
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
     * @param oc   字段所在的类
     * @param p    是否包括父类public字段
     * @param pred 筛选条件,返回true加入清单,为null则全部符合
     */
    public static Set<UF> findFS(Class<?> oc, boolean p, Predicate<Field> pred) {
        Set<UF> ul = new HashSet<>();
        Field[] fs = UArrays.merge(oc.getDeclaredFields(), p ? oc.getFields() : null);
        if (fs == null) return Collections.emptySet();
        for (Field f : fs) if (pred == null || pred.test(f)) ul.add(new UF(f));
        return Collections.unmodifiableSet(ul);
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
     * 构造方法包装
     * 用来连调使用
     */
    public static class UC<T> {
        private Constructor<T> c;
        private Throwable e;
        
        public UC(Constructor<T> c) {
            this.c = c;
        }
        
        public UC(Throwable e) {
            this.e = e;
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
            return e;
        }
        
        /**
         * 获取当前构造方法
         */
        public Constructor<T> getC() {
            return c;
        }
        
        /**
         * 构建新的实例
         * 链条中有异常则抛出
         */
        public T newInstance(Object... a) {
            if (e != null) throw new RuntimeException(e);
            try {
                c.setAccessible(true);
                return c.newInstance(a);
            } catch (Throwable e) {
                throw new RuntimeException(e);
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
     * 方法包装
     * 用来连调使用
     */
    public static class UM {
        private Method m;
        private Throwable e;
        
        protected UM(Method m) {
            this.m = m;
        }
        
        protected UM(Throwable e) {
            this.e = e;
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
            if (e != null) throw new RuntimeException(e);
            try {
                m.setAccessible(true);
                return (T) m.invoke(o, a);
            } catch (Throwable e) {
                throw new RuntimeException(e);
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
     * 字符按包装
     * 用来连调使用
     */
    public static class UF {
        private Field f;
        private Throwable e;
        
        protected UF(Field f) {
            this.f = f;
        }
        
        protected UF(Throwable e) {
            this.e = e;
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
            if (e != null) throw new RuntimeException(e);
            try {
                f.setAccessible(true);
                f.set(o, a);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * 获取字段值
         * 异常存在时返回null
         */
        public <T> T get(Object o) {
            if (e != null) throw new RuntimeException(e);
            try {
                f.setAccessible(true);
                return (T) f.get(o);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        
        /**
         * 获取字段值(具体类型)
         */
        public <T> T get(Object o, Class<T> c) {
            return get(o);
        }
        
        /**
         * 获取字段名称
         */
        public String getName() {
            if (e != null) throw new RuntimeException(e);
            return f == null ? null : f.getName();
        }
        
        /**
         * 获取字段类型
         */
        public Class<?> getType() {
            if (e != null) throw new RuntimeException(e);
            return f == null ? null : f.getType();
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
        
        
        @Override
        public String toString() {
            return "UF{" + "f=" + f + ", e=" + e + '}';
        }
    }
}
