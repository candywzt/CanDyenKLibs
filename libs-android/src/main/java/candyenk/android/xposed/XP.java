package candyenk.android.xposed;

import candyenk.java.utils.UReflex;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Xposed主类
 */
public class XP {
    /**
     * 查找方法
     * 不会报错
     * 没找到就是null
     */
    public static Class<?> fc(ClassLoader cl, String n) {
        return UReflex.fc(cl, n);
    }

    /**
     * 查找构造方法
     *
     * @param oc 构造方法所在的类
     * @param c  构造方法参数
     */
    public static XPUC findC(Class<?> oc, Class<?>... c) {
        return new XPUC(UReflex.findC(oc, c));
    }

    /**
     * 查找本类方法
     *
     * @param oc   方法所在的类
     * @param name 方法名
     * @param c    方法参数类型集合
     */
    public static XPUM findM(Class<?> oc, String name, Class<?>... c) {
        return new XPUM(UReflex.findM(oc, name, c));
    }

    /**
     * 查找类所有同名方法
     */
    public static XPUMS findAll(Class<?> oc, String name) {
        return new XPUMS(UReflex.findMS(oc, false, m -> name.equals(m.getName())));
    }

    /**
     * 查找类所有构造方法
     */
    public static XPUCS findAll(Class<?> oc) {
        return new XPUCS(UReflex.findCS(oc, false, null));
    }

    /**
     * Xposed专用Constructor包装
     */
    public static class XPUC extends UReflex.UC {
        private XC_MethodHook.Unhook uh;

        protected XPUC(UReflex.UC<?> uc) {
            super(uc == null ? null : uc.getC());
            if (uc != null) super.setE(uc.getE());
        }


        /**
         * Hook当前构造方法
         * 只能hook一次,重复调用无效
         * unhook后可重复调用
         */
        public XPUC hook(IHook hook) {
            if (hook == null || uh != null) return this;
            this.uh = XposedBridge.hookMethod(getC(), hook.hook());
            return this;
        }

        /**
         * 移除Hook
         * 只能移除本实例实现的hook
         */
        public XPUC unhook() {
            if (uh != null) uh.unhook();
            this.uh = null;
            return this;
        }

        /**
         * 复制后可再次Hook
         */
        public XPUC copy() {
            return new XPUC(this);
        }
    }

    /**
     * Xposed专用多构造方法集合
     */
    public static class XPUCS extends XPUC {
        private final Set<Constructor<?>> c = new HashSet<>();
        private final Set<XC_MethodHook.Unhook> uh = new HashSet<>();

        protected XPUCS(Collection<Constructor<?>> ms) {
            super(null);
            this.c.addAll(ms);
        }

        private <T> XPUCS(Set<UReflex.UC<T>> ms) {
            super(null);
            for (UReflex.UC<T> um : ms) this.c.add(um.getC());
        }

        @Override
        public XPUCS hook(IHook hook) {
            if (hook == null || !uh.isEmpty()) return this;
            for (Constructor<?> mm : getCS()) uh.add(XposedBridge.hookMethod(mm, hook.hook()));
            return this;
        }

        @Override
        public XPUCS unhook() {
            for (XC_MethodHook.Unhook u : uh) if (u != null) u.unhook();
            uh.clear();
            return this;
        }

        @Override
        public XPUCS copy() {
            return new XPUCS(getCS());
        }

        @Override
        public boolean isNull() {
            return c.isEmpty();
        }

        /**
         * @deprecated 禁止使用
         */
        @Override
        public Constructor<?> getC() {
            return null;
        }

        /**
         * @deprecated 禁止使用
         */
        @Override
        public Object newInstance(Object... a) {
            return null;
        }

        /**
         * 获取方法集合
         */
        public Set<Constructor<?>> getCS() {
            return c;
        }
    }

    /**
     * Xposed专用Method包装类
     */
    public static class XPUM extends UReflex.UM {
        private XC_MethodHook.Unhook uh;

        public XPUM(UReflex.UM um) {
            super(um == null ? null : um.getM());
            if (um != null) super.setE(um.getE());
        }

        /**
         * Hook当前方法
         * 只能hook一次,重复调用无效
         * unhook后可重复调用
         */
        public XPUM hook(IHook hook) {
            if (hook == null || uh != null) return this;
            this.uh = XposedBridge.hookMethod(getM(), hook.hook());
            return this;
        }

        /**
         * 移除Hook
         * 只能移除本实例实现的hook
         */
        public XPUM unhook() {
            if (uh != null) uh.unhook();
            return this;
        }

        /**
         * 复制后可再次Hook
         */
        public XPUM copy() {
            return new XPUM(this);
        }
    }

    /**
     * Xposed专用多方法集合
     */
    public static class XPUMS extends XPUM {
        private final Set<Method> m = new HashSet<>();
        private final Set<XC_MethodHook.Unhook> uh = new HashSet<>();

        protected XPUMS(Collection<Method> ms) {
            super(null);
            this.m.addAll(ms);
        }

        private XPUMS(Set<UReflex.UM> ms) {
            super(null);
            for (UReflex.UM um : ms) this.m.add(um.getM());
        }

        @Override
        public XPUMS hook(IHook hook) {
            if (hook == null || !uh.isEmpty()) return this;
            for (Method mm : getMS()) uh.add(XposedBridge.hookMethod(mm, hook.hook()));
            return this;
        }

        @Override
        public XPUMS unhook() {
            for (XC_MethodHook.Unhook u : uh) if (u != null) u.unhook();
            uh.clear();
            return this;
        }

        @Override
        public XPUMS copy() {
            return new XPUMS(getMS());
        }

        @Override
        public boolean isNull() {
            return m.isEmpty();
        }

        /**
         * @deprecated 禁止使用
         */
        @Override
        public Method getM() {
            return null;
        }

        /**
         * @deprecated 禁止使用
         */
        @Override
        public <T> T invoke(Object o, Object... a) {
            return null;
        }

        /**
         * @deprecated 禁止使用
         */
        @Override
        public <T> T invoke(Object o, Class<T> r, Object... a) {
            return null;
        }


        /**
         * 获取方法集合
         */
        public Set<Method> getMS() {
            return m;
        }
    }
}
