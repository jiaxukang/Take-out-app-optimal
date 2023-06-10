package com.itk.takeout.common;

/**
 * user save and get current id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * set value
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * get value
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
