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
package prototypes.ws.proxy.soap.configuration;

import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.ManagedTypeImpl;
import org.junit.Test;
import prototypes.ws.proxy.soap.constantes.ApplicationConfig;
import prototypes.ws.proxy.soap.io.SoapExchange;

/**
 *
 * @author jlamande
 */
public class ProxyConfTest {

    @Test
    public void test() {
        String derbyHome = ApplicationConfig.DEFAULT_STORAGE_PATH;
        System.setProperty("derby.system.home", derbyHome);
        Properties connectionProps = new Properties();
        connectionProps.setProperty("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
        connectionProps.setProperty("javax.persistence.jdbc.url", "jdbc:derby:proxy-soap_derby.db;create=true");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProxyPU", connectionProps);
        List<SoapExchange> res = emf.createEntityManager().createQuery("select s from SoapExchange s", SoapExchange.class).getResultList();

        System.out.println(emf.getMetamodel().managedType(SoapExchange.class));
        System.out.println(((ManagedTypeImpl) emf.getMetamodel().managedType(SoapExchange.class)).getDescriptor().getTableName());

        java.util.Set<EntityType<?>> entities = emf.getMetamodel().getEntities();
        for (EntityType entity : entities) {
            //String tableName = entity.getClass().getAnnotation(SoapExchange.class).name();
            emf.getMetamodel().managedType(SoapExchange.class);
            System.out.println(entity);
            System.out.println(((EntityTypeImpl) entity).getBindableType());
            System.out.println(((EntityTypeImpl) entity).getDescriptor().getTableName());
        }
        System.out.println(res);
        emf.close();

    }

}
