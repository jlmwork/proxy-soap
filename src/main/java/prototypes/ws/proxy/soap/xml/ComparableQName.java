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
package prototypes.ws.proxy.soap.xml;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 *
 * @author jlamande
 */
public class ComparableQName extends QName implements Comparable<Object> {

    public ComparableQName(QName qName) {
        this(qName.getNamespaceURI(), qName.getLocalPart(), qName.getPrefix());
    }

    public ComparableQName(String localPart) {
        super(localPart);
    }

    public ComparableQName(final String namespaceURI, final String localPart) {
        super(namespaceURI, localPart, XMLConstants.DEFAULT_NS_PREFIX);
    }

    public ComparableQName(String namespaceURI, String localPart, String prefix) {
        super(namespaceURI, localPart, prefix);
    }

    @Override
    public int compareTo(Object o) {
        QName other = (QName) o;
        return toString().compareTo(other.toString());
    }

}
