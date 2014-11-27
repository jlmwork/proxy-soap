/*
 * Copyright 2014 JL06436S.
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
package prototypes.ws.proxy.soap.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import prototypes.ws.proxy.soap.constants.Messages;
import prototypes.ws.proxy.soap.reflect.Classes;

/**
 *
 * @author JL06436S
 */
public class BooleanExecutableExpression extends Expression {

    CompiledScript script = null;
    private transient final ScriptEngineManager manager = new ScriptEngineManager();
    private transient final ScriptEngine engine = manager.getEngineByName("JavaScript");
    private transient final Map<String, Map<String, Method>> classGetters = Collections.synchronizedMap(new HashMap<String, Map<String, Method>>());

    protected BooleanExecutableExpression() {
        super();
    }

    public BooleanExecutableExpression(String name, String body) {
        super(name, body);
        validate();
        this.setBody(body);
    }

    protected final void setBody(String body) {
        this.body = body;
        try {
            if (engine instanceof Compilable) {
                if (script == null) {
                    Compilable compilingEngine = (Compilable) engine;
                    script = compilingEngine.compile(body);
                }
            } else {
                logger.warn("Engine {}-{} cannot compile scripts", engine.getFactory().getEngineName(), engine.getFactory().getEngineVersion());
            }
        } catch (ScriptException ex) {
            logger.warn(Messages.MSG_ERROR_DETAILS, ex);
            throw new IllegalArgumentException("Script expression '" + body + "' is not correct.", ex);
        }
    }

    /**
     *
     * @param o
     * @return
     */
    public Boolean execute(Object o) {
        if (o != null) {
            try {
                Bindings bindings = engine.createBindings();
                Map<String, Method> fieldMethods = resolveGetters(o.getClass());
                for (Map.Entry<String, Method> entry : fieldMethods.entrySet()) {
                    try {
                        bindings.put(entry.getKey(), entry.getValue().invoke(o, new Object[0]));
                    } catch (InvocationTargetException ex) {
                        logger.warn(Messages.MSG_ERROR_DETAILS, ex);
                        throw new IllegalArgumentException("Error with given object class '" + o.getClass() + "'.", ex);
                    } catch (IllegalAccessException ex) {
                        logger.warn(Messages.MSG_ERROR_DETAILS, ex);
                        throw new IllegalArgumentException("Error with given object class '" + o.getClass() + "'.", ex);
                    }
                }
                Object result;
                if (script != null) {
                    result = script.eval(bindings);
                } else {
                    result = engine.eval(body, bindings);
                }
                return (Boolean) result;
            } catch (ScriptException ex) {
                logger.warn("Error on script", ex);
            } catch (java.lang.IllegalArgumentException ex) {
                logger.warn("Error on script execution", ex);
            }
        }
        return null;
    }

    private Map<String, Method> resolveGetters(Class<?> clazz) {
        Map<String, Method> fieldGetters = classGetters.get(clazz.getName());
        if (fieldGetters == null) {
            fieldGetters = new HashMap<String, Method>();
            String[] fieldNames = Classes.getAllFieldsName(clazz, new String[]{"serial", "_", "UID"});
            for (String fieldName : fieldNames) {
                char first = Character.toUpperCase(fieldName.charAt(0));
                String methodName = first + fieldName.substring(1);
                logger.debug("Attribute name to resolve : {}", methodName);

                // try the get...
                Method m = org.apache.commons.lang3.reflect.MethodUtils.getAccessibleMethod(clazz, "get" + methodName, new Class[0]);
                // try a is...
                if (m == null) {
                    m = org.apache.commons.lang3.reflect.MethodUtils.getAccessibleMethod(clazz, "is" + methodName, new Class[0]);
                }
                if (m != null) {
                    logger.debug("Attribute method name resolved to : {}", m.getName());
                    fieldGetters.put(fieldName, m);
                }
            }
            classGetters.put(clazz.getName(), fieldGetters);
        }
        return fieldGetters;
    }
}
