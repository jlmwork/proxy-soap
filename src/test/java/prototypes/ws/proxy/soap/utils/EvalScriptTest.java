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
package prototypes.ws.proxy.soap.utils;

import java.util.HashMap;
import java.util.Map;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import org.junit.Test;
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 *
 * @author jlamande
 */
public class EvalScriptTest {

    @Test
    public void test() throws Exception {
        // create a script engine manager
        ScriptEngineManager manager = new ScriptEngineManager();
        // create a JavaScript engine
        System.out.println(manager.getEngineFactories());
        for (ScriptEngineFactory factory : manager.getEngineFactories()) {
            System.out.println("Name : " + factory.getEngineName());
            System.out.println("Version : " + factory.getEngineVersion());
            System.out.println("Language name : " + factory.getLanguageName());
            System.out.println("Language version : " + factory.getLanguageVersion());
            System.out.println("Extensions : " + factory.getExtensions());
            System.out.println("Mime types : " + factory.getMimeTypes());
            System.out.println("Names : " + factory.getNames());
        }
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        // engine will be null if name is not found

        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.clear();
        bindings.put("s", new SoapExchange());
        engine.eval("var sortie = '';"
                + " sortie = (s.backEndResponseTime == 0) ? true : false", bindings);
        Boolean result = (Boolean) bindings.get("sortie");
        System.out.println("resultat = " + result);

        // create a Java object
        String name = "Tom";

        // create the binding
        engine.put("greetingname", name);

        // evaluate JavaScript code from String
        // dont work in nashorn (JDK 8) so will need to import an engine as rhino
        // in order to use Javascript
        //engine.eval("println('Hello, ' + greetingname)");
        //engine.eval("println('The name length is ' +  greetingname.length)");
        // Compile the expression once; relatively slow.
    }

    @Test
    public void testScriptEngineCompiled() throws Exception {
        Map<String, CompiledScript> m = new HashMap<String, CompiledScript>();
        // create a script engine manager
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        CompiledScript script = null;
        String scriptExpression = ""
                + "function fib(n) {"
                + "  if(n <= 1) return n; "
                + "  return fib(n-1) + fib(n-2); "
                + "};"
                + "fib(num);";

        if (engine instanceof Compilable) {
            script = m.get("fib");
            if (script == null) {
                Compilable compilingEngine = (Compilable) engine;
                script = compilingEngine.compile(scriptExpression);
                m.put("fib", script);
            }
        }
        Bindings bindings = engine.createBindings();
        bindings.put("num", "20");
        Object result = null;
        if (script != null) {
            result = script.eval(bindings);
        } else {
            result = engine.eval(scriptExpression, bindings);
        }
        System.out.println("Result : " + result);
    }

}
