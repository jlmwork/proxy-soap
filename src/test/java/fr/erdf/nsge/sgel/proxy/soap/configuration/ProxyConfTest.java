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
package fr.erdf.nsge.sgel.proxy.soap.configuration;

import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProxyPU");
        List<SoapExchange> res = emf.createEntityManager().createQuery("select monitor from SoapRequestMonitor as monitor", SoapExchange.class).getResultList();
        System.out.println(res);
        emf.close();

    }

}
