package com.liner.screenboster.utils;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    @SuppressWarnings("rawtypes")
    @SuppressLint("PrivateApi")
    @Nullable
    public static IBinder getService(String serviceName){
        IBinder binder = null;
        try {
            Class aClass = Class.forName("android.os.ServiceManager");
            Method getService = getMethod(aClass, "getService", new Class[] {String.class});
            if(getService != null){
                Object result = invokeMethod(aClass, getService, new Object[]{serviceName});
                if(result != null){
                    binder = (IBinder) result;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return binder;
    }
    @Nullable
    public static Object getFieldValue(Object obj, String fieldName) {
        Object result = null;
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            result = field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    public static Method getMethod(Class clazz, String name, Class[] params){
        try {
            Method method = clazz.getMethod(name, params);
            if(method != null){
                method.setAccessible(true);
                return method;
            }
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static Method getMethod(Object object, String methodName){
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }

    public static Object invokeMethod(Object object, @Nullable Method method, Object[] args){
        if(method == null)
            return null;
        try {
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
