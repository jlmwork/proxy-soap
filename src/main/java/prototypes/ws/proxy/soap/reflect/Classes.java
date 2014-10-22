package prototypes.ws.proxy.soap.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Classes {

    private static final Logger LOGGER = LoggerFactory.getLogger(Classes.class);

    public static Object callPrivateMethod(Class<? extends Object> clazz,
            String methodName, Object calledObject,
            Class<?>[] paramArrayOfClass, Object[] paramArrayOfObject) {
        try {
            Method method = clazz.getDeclaredMethod(methodName,
                    paramArrayOfClass);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method.invoke(calledObject, paramArrayOfObject);
        } catch (SecurityException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    public static void setMethodAccessible(Class<? extends Object> clazz,
            String methodName, Class<?>[] paramArrayOfClass) {
        try {
            Method method = clazz.getDeclaredMethod(methodName,
                    paramArrayOfClass);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void setField(Class<? extends Object> clazz,
            Object targetObj, String fieldName, Object newValue) {
        LOGGER.debug("Set field " + fieldName);
        try {
            Field field = clazz.getDeclaredField(fieldName);

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(targetObj, newValue);
        } catch (SecurityException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            LOGGER.error(e.getMessage(), e);
            if (LOGGER.isDebugEnabled()) {
                Field[] fields = clazz.getDeclaredFields();
                LOGGER.debug("Fields of class : " + clazz);
                for (Field field : fields) {
                    LOGGER.debug("" + field);
                }
            }
        }
    }

    public static void setStaticField(Class<? extends Object> clazz,
            String fieldName, Object newValue) {
        LOGGER.debug("Set field " + fieldName);
        try {
            Field field = clazz.getDeclaredField(fieldName);

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(null, newValue);
        } catch (SecurityException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            LOGGER.error(e.getMessage(), e);
            if (LOGGER.isDebugEnabled()) {
                Field[] fields = clazz.getDeclaredFields();
                LOGGER.debug("Fields of class : " + clazz);
                for (Field field : fields) {
                    LOGGER.debug("" + field);
                }
            }
        }
    }
}
