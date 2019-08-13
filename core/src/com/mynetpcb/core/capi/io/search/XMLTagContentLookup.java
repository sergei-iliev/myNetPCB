package com.mynetpcb.core.capi.io.search;


import com.mynetpcb.core.capi.io.SearchLookup;

import java.io.File;
import java.io.IOException;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class XMLTagContentLookup extends SearchLookup {
    private final List<String> tags;
    
    public XMLTagContentLookup(SearchLookup searchLookup,List<String> tags,String term) {
        super(searchLookup,term);
        this.tags=tags;
    }
    
    public XMLTagContentLookup(List<String> tags,String term) {
        this(null,tags,term);
    }
    
    @Override
    protected boolean search(File file,String term)throws IOException {
        if(!file.isFile()){
          return false;  
        }
        DocumentBuilderFactory builderfactory = DocumentBuilderFactory.newInstance();
        builderfactory.setNamespaceAware(true);

        DocumentBuilder builder;
        try {
            builder = builderfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        }
        Document xmlDocument;

        try {
            xmlDocument = builder.parse(file);
        } catch (SAXException e) {
            throw new IOException(e);
        }       
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr;

        NodeList nodes;
        try {
            for(String tag:tags){        
            expr = xpath.compile(tag);
            nodes = (NodeList) expr.evaluate(xmlDocument, XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node=nodes.item(i); 
                    if(node.getTextContent()!=null&&node.getTextContent().toLowerCase().indexOf(term)>-1)
                      return true;  
                }
            }    
        } catch (XPathExpressionException e) {
            throw new IOException(e);
        }
        
        return false;
    }
}

