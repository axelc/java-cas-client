/*
 * Copyright 2006 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.uportal.org/license.html
 */
package org.jasig.cas.client.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Common utilities for easily parsing XML without duplicating logic.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.0
 */
public final class XmlUtils {

    /** Static instance of Commons Logging. */
    private final static Log LOG = LogFactory.getLog(XmlUtils.class);

    /**
     * Get an instance of an XML reader from the XMLReaderFactory.
     * 
     * @return the XMLReader.
     */
    public static XMLReader getXmlReader() {
        try {
            return XMLReaderFactory.createXMLReader();
        } catch (final SAXException e) {
            throw new RuntimeException("Unable to create XMLReader", e);
        }
    }

    /**
     * Retrieve the text for a group of elements. Each text element is an entry
     * in a list.
     * 
     * @param xmlAsString the xml response
     * @param element the element to look for
     * @return the list of text from the elements.
     */
    public static List getTextForElements(final String xmlAsString,
        final String element) {
        final List elements = new ArrayList();
        final XMLReader reader = getXmlReader();

        final DefaultHandler handler = new DefaultHandler(){

            private boolean foundElement = false;

            private StringBuffer buffer = new StringBuffer();

            public void startElement(final String uri, final String localName,
                final String qName, final Attributes attributes)
                throws SAXException {
                if (localName.equals(element)) {
                    this.foundElement = true;
                }
            }

            public void endElement(final String uri, final String localName,
                final String qName) throws SAXException {
                if (localName.equals(element)) {
                    this.foundElement = false;
                    elements.add(this.buffer.toString());
                    this.buffer = new StringBuffer();
                }
            }

            public void characters(char[] ch, int start, int length)
                throws SAXException {
                if (this.foundElement) {
                    this.buffer.append(ch, start, length);
                }
            }
        };

        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);

        try {
            reader.parse(new InputSource(new StringReader(xmlAsString)));
        } catch (final Exception e) {
            LOG.error(e, e);
            return null;
        }

        return elements;
    }

    /**
     * Retrieve the text for a specific element (when we know there is only
     * one).
     * 
     * @param xmlAsString the xml response
     * @param element the element to look for
     * @return the text value of the element.
     */
    public static String getTextForElement(final String xmlAsString,
        final String element) {
        final XMLReader reader = getXmlReader();
        final StringBuffer buffer = new StringBuffer();

        final DefaultHandler handler = new DefaultHandler(){

            private boolean foundElement = false;

            public void startElement(final String uri, final String localName,
                final String qName, final Attributes attributes)
                throws SAXException {
                if (localName.equals(element)) {
                    this.foundElement = true;
                }
            }

            public void endElement(final String uri, final String localName,
                final String qName) throws SAXException {
                if (localName.equals(element)) {
                    this.foundElement = false;
                }
            }

            public void characters(char[] ch, int start, int length)
                throws SAXException {
                if (this.foundElement) {
                    buffer.append(ch, start, length);
                }
            }
        };

        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);

        try {
            reader.parse(new InputSource(new StringReader(xmlAsString)));
        } catch (final Exception e) {
            LOG.error(e, e);
            return null;
        }

        return buffer.toString();
    }
}