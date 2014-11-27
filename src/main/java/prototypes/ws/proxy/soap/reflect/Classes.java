/*
 * Copyright 2014 jlamande.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package prototypes.ws.proxy.soap.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.constants.Messages;

public class Classes {

    private static final Logger LOGGER = LoggerFactory.getLogger(Classes.class);

    private Classes() {
    }

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
        } catch (SecurityException ex) {
            LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
        } catch (IllegalArgumentException ex) {
            LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
        } catch (IllegalAccessException ex) {
            LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
        } catch (InvocationTargetException ex) {
            LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
        } catch (NoSuchMethodException ex) {
            LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
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
        } catch (SecurityException ex) {
            LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("Error : {}", ex);
        } catch (NoSuchMethodException ex) {
            LOGGER.warn(Messages.MSG_ERROR_DETAILS, ex);
        }
    }

    public static void setField(Class<? extends Object> clazz,
            Object targetObj, String fieldName, Object newValue) {
        LOGGER.debug("Set field {}", fieldName);
        try {
            Field field = clazz.getDeclaredField(fieldName);

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(targetObj, newValue);
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (NoSuchFieldException ex) {
            LOGGER.error(ex.getMessage(), ex);
            if (LOGGER.isDebugEnabled()) {
                Field[] fields = clazz.getDeclaredFields();
                LOGGER.debug("Fields of class : {}", clazz);
                for (Field field : fields) {
                    LOGGER.debug("{}", field);
                }
            }
        }
    }

    public static void setStaticField(Class<? extends Object> clazz,
            String fieldName, Object newValue) {
        LOGGER.debug("Set field {}", fieldName);
        try {
            Field field = clazz.getDeclaredField(fieldName);

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(null, newValue);
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (NoSuchFieldException ex) {
            LOGGER.error(ex.getMessage(), ex);
            if (LOGGER.isDebugEnabled()) {
                Field[] fields = clazz.getDeclaredFields();
                LOGGER.debug("Fields of class : {}", clazz);
                for (Field field : fields) {
                    LOGGER.debug("{}", field);
                }
            }
        }
    }

    public static String[] getAllFieldsName(Class<? extends Object> clazz, String[] prefixFilters) {
        List<String> arList = new ArrayList();
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                boolean foundInFilters = false;
                for (String filter : prefixFilters) {
                    if (field.getName().startsWith(filter)) {
                        foundInFilters = true;
                    }
                }
                if (!foundInFilters) {
                    arList.add(field.getName());
                }
            }
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return arList.toArray(new String[arList.size()]);
    }
}
