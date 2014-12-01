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
package prototypes.ws.proxy.soap.sample;

/**
 *
 * @author jlamande
 */
//@XmlRootElement
public class Sample {

    private int code;
    private String name;
    private String content;

    public Sample() {

    }

    public Sample(int code, String name, String content) {
        this.code = code;
        this.name = name;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Sample{" + "code=" + code + ", name=" + name + ", content=" + content + '}';
    }

}
