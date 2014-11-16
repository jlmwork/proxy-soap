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
package prototypes.ws.proxy.soap.validation;

import java.io.File;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;

public class SoapValidatorFactoryTest {

    private final String schemaPath1 = "src/test/resources/samples/definitions/SampleService.wsdl";

    private final String schemaPath2 = "src/test/resources/simple.wsdl";

    @Test
    public void newSoapValidatorReuse() {
        SoapValidator val = SoapValidatorFactory.getInstance().createSoapValidator(schemaPath1);
        SoapValidator val2 = SoapValidatorFactory.getInstance().createSoapValidator(schemaPath1);
        // check that val and val2 are the same object
        Assert.assertSame(val, val2);
    }

    @Test
    public void newSoapValidator() {
        SoapValidator val = SoapValidatorFactory.getInstance().createSoapValidator(schemaPath1);
        SoapValidator val2 = SoapValidatorFactory.getInstance().createSoapValidator(schemaPath2);
        // check that val and val2 are the same object
        Assert.assertNotSame(val, val2);
    }

    @Test
    public void newSoapValidatorObsolete() {
        SoapValidator val = SoapValidatorFactory.getInstance().createSoapValidator(schemaPath1);
        // touch the file to prune the cache
        (new File(schemaPath1)).setLastModified(Calendar.getInstance()
                .getTimeInMillis());
        SoapValidator val2 = SoapValidatorFactory.getInstance().createSoapValidator(schemaPath1);
        // check that val and val2 are not the same object
        Assert.assertNotSame(val, val2);
    }

}
