package com.mynetpcb.core.capi.verification;


import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;


public class XMLRootTagVerifier implements Verifier{
    private final File file;
    
    private final String tagvalue;
    
    public XMLRootTagVerifier(final File file,final String tagvalue) {
      this.file=file;
      this.tagvalue=tagvalue;
    }

    @Override
    public boolean check() throws VerificationException{
        DocumentBuilderFactory builderfactory = DocumentBuilderFactory.newInstance();
        builderfactory.setNamespaceAware(true);

        DocumentBuilder builder;
        try {
            builder = builderfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new VerificationException(e);
        }
        Document xmlDocument;

        try {
            xmlDocument = builder.parse(file);
        } catch (IOException e) {
            throw new VerificationException(e);
        } catch (SAXException e) {
            throw new VerificationException(e);
        }

        String text = xmlDocument.getDocumentElement().getTagName();
        
        if(!text.equalsIgnoreCase(tagvalue)){
          return false;
        }else
          return true;
    }
}

