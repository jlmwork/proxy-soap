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
package prototypes.ws.proxy.soap.web.rest;

import org.glassfish.jersey.message.filtering.SelectableEntityFilteringFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author jlamande
 */
public class App extends ResourceConfig {

    /**
     *
     * Tried to use singleton with hk2 but didn't work : Entity Inspector.
     * bind(EntityInspectorImpl.class) .to(EntityInspector.class)
     * .in(Singleton.class);
     *
     */
    public App() {
        // Register all resources present under the package.
        packages("prototypes.ws.proxy.soap.web.rest");
        // Register entity-filtering selectable feature.
        register(SelectableEntityFilteringFeature.class);
        register(JpaEntityProcessor.class);

        // make sampleresource a singleton
        // see : https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2331
        //register(SampleResource.class); // DOESNT WORK
        // http://blog.bdoughan.com/2012/05/moxy-as-your-jax-rs-json-provider.html
        register(MoxyXmlFeature.class);
        // Configure MOXy Json provider.
        register(new MoxyJsonConfig().setFormattedOutput(true).setMarshalEmptyCollections(false).resolver());

        // filtering could be used with views too
        // stackoverflow.com/questions/19194966/how-to-specify-and-organize-oxm-metadata-source-in-glassfish-v4-moxy-provider
        property(SelectableEntityFilteringFeature.QUERY_PARAM_NAME, "fields");
    }

}
