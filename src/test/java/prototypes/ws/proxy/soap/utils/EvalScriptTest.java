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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.junit.Test;

/**
 *
 * @author jlamande
 */
public class EvalScriptTest {

    @Test
    public void test() throws Exception {
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        System.out.println(factory.getEngineFactories());
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        // create a Java object
        String name = "Tom";

        // create the binding
        engine.put("greetingname", name);

        // evaluate JavaScript code from String
        // dont work in nashorn (JDK 8) so will need to import an engine as rhino
        // in order to use Javascript
        //engine.eval("println('Hello, ' + greetingname)");
        //engine.eval("println('The name length is ' +  greetingname.length)");
    }
}
