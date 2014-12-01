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

import java.io.File;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author JL06436S
 */
public class ApplicationConfig {

    public static final String PROP_VALIDATION = "proxy.soap.validate";
    public static final String PROP_BLOCKING_MODE = "proxy.soap.blockingmode";
    // changed the value of following key from "proxy.soap.schemadir" to force
    // reconfiguration
    // on preivous installed proxies
    public static final String PROP_WSDL_DIRS = "proxy.soap.wsdls";
    public static final String PROP_MAX_EXCHANGES = "proxy.soap.maxexchanges";

    public static final String PROP_IGNORE_VALID_EXCHANGES = "proxy.soap.ignore.exchanges.valid";

    public static final String PROP_RUN_MODE = "proxy.soap.run.mode";

    public static final String DEFAULT_STORAGE_PATH_CONF = FilenameUtils.normalize(System.getProperty("user.home") + File.separator + ".proxy-soap" + File.separator);

    public static final String DEFAULT_STORAGE_PATH = FilenameUtils.normalize(System.getProperty("java.io.tmpdir") + File.separator + "proxy-soap" + File.separator);

    public static final String EXCHANGES_STORAGE_PATH = ApplicationConfig.DEFAULT_STORAGE_PATH + "exchanges" + File.separator;

    static {
        // create the default storage path
        (new File(DEFAULT_STORAGE_PATH)).mkdirs();
    }

    public static final Integer RUN_MODE_PROD = 0;
    public static final Integer RUN_MODE_DEV = 1;

    // persistence options
    public static final String PROP_PERSIST_MODE = "proxy.soap.persist.mode";
    public static final Integer PERSIST_MODE_MEMORY = 0;
    public static final Integer PERSIST_MODE_DB = 1;
    // persist xml of exchanges in db or not
    public static final String PROP_PERSIST_MODE_DB_XML = "proxy.soap.persist.mode.db.xml";
    public static final Boolean PERSIST_MODE_DB_XML_OUT = false;
    public static final Boolean PERSIST_MODE_DB_XML_IN = true;
    public static final String PROP_PERSIST_MODE_DB_DRIVER = "proxy.soap.persist.mode.db.driver";
    public static final String PROP_PERSIST_MODE_DB_URL = "proxy.soap.persist.mode.db.url";
    public static final String PROP_PERSIST_MODE_DB_USERNAME = "proxy.soap.persist.mode.db.username";
    public static final String PROP_PERSIST_MODE_DB_PASSWORD = "proxy.soap.persist.mode.db.password";
    public static final String PROP_PERSIST_MODE_DB_PROPERTIES = "proxy.soap.persist.mode.db.properties";

    public static final String PROP_EXPRESSIONS_IGNORE = "proxy.soap.expressions.ignore";
    public static final String PROP_EXPRESSIONS_CAPTURE = "proxy.soap.expressions.capture";

    private ApplicationConfig() {
    }
}
