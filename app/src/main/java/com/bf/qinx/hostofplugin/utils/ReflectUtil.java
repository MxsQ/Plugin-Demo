package com.bf.qinx.hostofplugin.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by QinX on 2019-06-28.
 * <p>
 * Description :
 */
public class ReflectUtil {
    public static Object getField(Object obj, String fieldName) throws Exception{
        Class<?> clazz =  obj.getClass();

        Field field = null;

        while(clazz != null){
            if(!hasField(clazz, fieldName)){
                clazz = clazz.getSuperclass();
                continue;
            } else {
                field = clazz.getDeclaredField(fieldName);
                break;
            }
        }

        if (field == null){
            return null;
        }

        field.setAccessible(true);
        return field.get(obj);
    }

    public static Object getStaticField(Class<?> clazz, String fieldName) throws Exception{
        Field field = null;

        while(clazz != null){
            if(!hasField(clazz, fieldName)){
                clazz = clazz.getSuperclass();
                continue;
            } else {
                field = clazz.getDeclaredField(fieldName);
                break;
            }
        }

        if (field == null){
            return null;
        }

        field.setAccessible(true);
        return field.get(null);
    }

    public static void setField(Object object, String fieldName, Object newValue) throws Exception{
        Class<?> clazz =  object.getClass();
//        Field field = clazz.getDeclaredField(fieldName);

        Field field = null;

        while(clazz != null){
            if(!hasField(clazz, fieldName)){
                clazz = clazz.getSuperclass();
                continue;
            } else {
                field = clazz.getDeclaredField(fieldName);
                break;
            }
        }


        if (field == null){
            return ;
        }

        field.setAccessible(true);
        field.set(object, newValue);
    }

    public static boolean hasField(Class<?> clazz, String fieldName){
        if (clazz == null){
            return false;
        }
        Field[] fields= clazz.getDeclaredFields();
        for (Field f : fields){
            if (f.getName().equals(fieldName)){
                return true;
            }
        }

        return false;
    }


    public static Method getMethod(Object obj, String methodName, Class<?>[] args) throws Exception{
        Method method = null;
        Class<?> clazz = obj.getClass();

        Class<?>[] argsArray = args;
        if (argsArray == null){
            argsArray = new Class<?>[]{};
        }

        while (clazz != null){
            if (hasMethod(clazz, methodName, args)){
                method = clazz.getDeclaredMethod(methodName, argsArray);
                break;
            }
            clazz = clazz.getSuperclass();
        }

        return method;
    }

    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>[] args){

        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods){
            if (m.getName().equals(methodName)
                    || matchParameterType(m, args)){
                return true;
            }
        }

        return false;
    }

    private static boolean matchParameterType(Method method, Class<?>[] args){
        if (method.getParameterTypes().length != args.length){
            return false;
        }

        Class<?>[] types = method.getParameterTypes();
        for (int i=0; i > args.length; i++){
            if (types[i] != args[i]){
                return false;
            }
        }

        return true;
    }
}
