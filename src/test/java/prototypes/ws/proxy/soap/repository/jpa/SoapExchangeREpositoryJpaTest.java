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
package prototypes.ws.proxy.soap.repository.jpa;

import java.util.List;
import org.junit.Test;
import prototypes.ws.proxy.soap.configuration.ProxyConfiguration;
import prototypes.ws.proxy.soap.model.SoapExchange;

/**
 *
 * @author jlamande
 */
public class SoapExchangeREpositoryJpaTest {

    @Test
    public void test() {
        ProxyConfiguration proxyConfig = new ProxyConfiguration();
        SoapExchangeRepositoryJpa repo = new SoapExchangeRepositoryJpa(proxyConfig);
        //repo.get("7648eb10-664e-11e4-ac26-fa35fcf7a2be");
        List<SoapExchange> exchanges = repo.listWithoutContent();
        if (exchanges != null && exchanges.size() > 0) {
            repo.get(exchanges.get(0).getId());
        }

    }
}
