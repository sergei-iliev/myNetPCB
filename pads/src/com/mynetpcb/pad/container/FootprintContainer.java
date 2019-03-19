package com.mynetpcb.pad.container;


import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.unit.Footprint;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;


public class FootprintContainer extends UnitContainer<Footprint,Shape>{

    public FootprintContainer() {
        super();
        setFileName("Footprints");
    }
    @Override
    public StringBuffer Format() {
        StringBuffer xml=new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n"); 
        xml.append("<footprints identity=\"Footprint\" version=\"1.0\">\r\n");
        for(Footprint footprint:getUnits()){
          xml.append(footprint.Format());
          xml.append("\r\n");
        }
        xml.append("</footprints>");
        return xml;
    }

    @Override
    public void Parse(String xml) throws XPathExpressionException,ParserConfigurationException,
                                         SAXException, IOException { 
        Document document = Utilities.buildDocument(xml);
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr; 
        
        expr = xpath.compile("//filename");
        String filename = (String)expr.evaluate(document, XPathConstants.STRING);
            if(!filename.equals("")){
                this.setFileName(filename);   
            }
        expr = xpath.compile("//library");
        String library = (String)expr.evaluate(document, XPathConstants.STRING);
                if(!library.equals("")){
                    this.setLibraryName(library);    
                }            
            expr = xpath.compile("//category");
            String folder = (String)expr.evaluate(document, XPathConstants.STRING);
                    if(!folder.equals("")){
                        this.setCategoryName(folder);    
                    }  
                    
        expr = xpath.compile("//footprint");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node=nodes.item(i);
            if(!((Element)node).getTagName().equals("footprint")){
               continue;                        
            }  
           Footprint footprint=new Footprint(1,1);
           footprint.Parse(node); 
           Add(footprint); //attach listeners
           //footprint.notifyListeners(ShapeEvent.ADD_SHAPE); 
        }  
    }

    @Override
    public void Parse(String xml, int index) throws ParserConfigurationException, SAXException, IOException,
                                                    XPathExpressionException {
        Document document = Utilities.buildDocument(xml);
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr;   
        
            
        expr = xpath.compile("//footprint");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        int _index=0;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node=nodes.item(i);
            if(!((org.w3c.dom.Element)node).getTagName().equals("footprint")){
               continue;                        
            }  
            if(_index==index){
               getUnit().Parse(node);
            }
            _index++;
        }    
    }

}

