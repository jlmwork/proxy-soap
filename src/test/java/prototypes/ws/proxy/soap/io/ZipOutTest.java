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
package prototypes.ws.proxy.soap.io;

import java.io.File;
import java.io.FileOutputStream;
import org.junit.Assert;
import org.junit.Test;
import prototypes.ws.proxy.soap.constants.ApplicationConfig;

/**
 *
 * @author jlamande
 */
public class ZipOutTest {

    public ZipOutTest() {
    }

    @Test
    public void hello() throws Exception {
        File file = new File("test.zip");
        FileOutputStream fos = new FileOutputStream(file);
        ZipOut zipOut = new ZipOut(fos);
        zipOut.addDirToZipStream(ApplicationConfig.EXCHANGES_STORAGE_PATH, new String[]{"xml", "xml.gz"});
        zipOut.finish();
        Assert.assertTrue(file.exists());
    }
}
