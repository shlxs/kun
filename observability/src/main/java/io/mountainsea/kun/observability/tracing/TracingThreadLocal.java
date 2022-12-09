package io.mountainsea.kun.observability.tracing;

import java.util.HashMap;

/**
 *
 * 每个线程对应唯一一个HashMap，该HashMap里存放了所有在这个线程内定义的TracingThreadLocal对象，可能存在多条
 *
 * @author : ShaoHongLiang
 * @date : 2022/11/11
 */
public class TracingThreadLocal<T> extends ThreadLocal<T>{
    private static final ThreadLocal<HashMap<TracingThreadLocal<?>, Object>> holder =
            ThreadLocal.withInitial(() -> new HashMap<>());

    private static final HashMap<TracingThreadLocal<?>, Object> getThreadLocalMap(){
        return holder.get();
    }

    @Override
    public final void set(T value) {
        getThreadLocalMap().put(this, value);
    }


    @Override
    public T get() {
       return (T)getThreadLocalMap().get(this);
    }

    @Override
    public void remove() {
        getThreadLocalMap().remove(this);
    }


    /**
     * 拷贝一份
     * @return
     */
    public static HashMap<TracingThreadLocal<?>, Object> capture() {
        HashMap<TracingThreadLocal<?>, Object> threadLocalMap = getThreadLocalMap();
        final HashMap<TracingThreadLocal<?>, Object> captured = new HashMap<>(threadLocalMap.size());
        threadLocalMap.forEach((key, value) -> captured.put(key, value));
        return captured;
    }

    public static void restore(HashMap<TracingThreadLocal<?>, Object> captured) {
        HashMap<TracingThreadLocal<?>, Object> threadLocalMap = getThreadLocalMap();
        threadLocalMap.clear();
        captured.forEach((key, value) -> threadLocalMap.put(key, value));
    }

}
