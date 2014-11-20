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
package prototypes.ws.proxy.soap.web.rest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Priority;
import javax.inject.Singleton;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.message.filtering.spi.AbstractEntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityProcessor;
import org.glassfish.jersey.message.filtering.spi.EntityProcessorContext;
import org.slf4j.LoggerFactory;

/**
 * This entity processor is required to activate Entity Filtering on JPA Objects
 * as they can own special weaved attributes.
 *
 * Case : EclipseLink weaving adds _persistence_session object. When
 * EntityFilteringFeature of Jersey is activated the objectGraph of the
 * _persistence_session is null and triggers a NPE, like this one at
 * org.glassfish.jersey.message.filtering.ObjectGraphImpl.getFields(ObjectGraphImpl.java:94)
 * at
 * org.glassfish.jersey.moxy.internal.MoxyObjectProvider.createSubgraphs(MoxyObjectProvider.java:128)
 *
 * Execution Context :
 * MoxyObjectProvider.createSubgraphs(MoxyObjectProvider.java:128) path =
 * _persistence_session.sessionLog entityClass =
 * org.eclipse.persistence.sessions.Session
 * ObjectGraphImpl.getFields(ObjectGraphImpl.java:94) graph is null
 *
 * when done on a JPA entity => EntityInspector of Jersey try to graph
 * _persistence_... fields
 *
 * Solutions not working as JAXB is not involved in fields resolution for graph
 * building :
 *
 * building a custom object graph to filter those attributes static way :
 * http://blog.bdoughan.com/2013/03/moxys-object-graphs-inputoutput-partial.html
 * dynamic way :
 * http://blog.bdoughan.com/2013/03/moxys-object-graphs-partial-models-on.html
 *
 * @author JL06436S
 */
@Provider
@Singleton
@Priority(Integer.MAX_VALUE - 999)
public class JpaEntityProcessor extends AbstractEntityProcessor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory
            .getLogger(NameGenerator.class);

    @Override
    public Result process(final EntityProcessorContext context) {
        switch (context.getType()) {
            case PROPERTY_READER:
            case PROPERTY_WRITER:
            case METHOD_READER:
            case METHOD_WRITER:
                LOGGER.debug("Ask the processor to process a context");
                final Field field = context.getField();
                final Method method = context.getMethod();
                final boolean isProperty = field != null;
                String fieldName;
                if (isProperty) {
                    fieldName = field.getName();
                } else {
                    fieldName = ReflectionHelper.getPropertyName(method);
                }
                if (fieldName != null && fieldName.startsWith("_persistence_")) {
                    LOGGER.debug("Field {} will be ignored", fieldName);
                    // ignore fields prefixed by _
                    return EntityProcessor.Result.ROLLBACK;
                }
                return super.process(context);
            default:
            // NOOP.
        }
        return EntityProcessor.Result.SKIP;
    }
}
