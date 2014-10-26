package prototypes.ws.proxy.soap.validation;

import java.io.File;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;
import prototypes.ws.proxy.soap.validation.SoapValidator;
import prototypes.ws.proxy.soap.validation.SoapValidatorFactory;

public class SoapValidatorFactoryTest {

    private final String schemaPath1 = "src/test/resources/simple.wsdl";

    private final String schemaPath2 = "src/test/resources/simple2.wsdl";

    @Test
    public void newSoapValidatorReuse() {
        SoapValidator val = SoapValidatorFactory.createSoapValidator(schemaPath1);
        SoapValidator val2 = SoapValidatorFactory.createSoapValidator(schemaPath1);
        // check that val and val2 are the same object
        Assert.assertSame(val, val2);
    }

    @Test
    public void newSoapValidator() {
        SoapValidator val = SoapValidatorFactory.createSoapValidator(schemaPath1);
        SoapValidator val2 = SoapValidatorFactory.createSoapValidator(schemaPath2);
        // check that val and val2 are the same object
        Assert.assertNotSame(val, val2);
    }

    @Test
    public void newSoapValidatorObsolete() {
        SoapValidator val = SoapValidatorFactory.createSoapValidator(schemaPath1);
        // touch the file to prune the cache
        (new File(schemaPath1)).setLastModified(Calendar.getInstance()
                .getTimeInMillis());
        SoapValidator val2 = SoapValidatorFactory.createSoapValidator(schemaPath1);
        // check that val and val2 are the same object
        Assert.assertNotSame(val, val2);
    }

}
