package com.mynetpcb.core.capi;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;


/*
 * Mark the implementer as worthy for save,read to external media
 */
public interface Externalizable {
    
 public String toXML();
 
 public void fromXML(Node node)throws XPathExpressionException,ParserConfigurationException;   
}
