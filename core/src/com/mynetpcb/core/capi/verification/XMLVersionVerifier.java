package com.mynetpcb.core.capi.verification;


import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;


public class XMLVersionVerifier implements Verifier {
    private final File file;

    private final double version;

    public XMLVersionVerifier(final File file, final double version) {
        this.file = file;
        this.version = version;
    }

    @Override
    public boolean check() throws VerificationException {
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

        String text = xmlDocument.getDocumentElement().getAttribute("version");
        if(text.equals("")){
           throw new VerificationException("File '"+file.getAbsolutePath()+"' does not have version tag in its root tag."); 
        }
        
        double v=Double.parseDouble(text);
        if(Double.compare(version,v)!=0){
          return false;
        }else
          return true;                  

    }


}

