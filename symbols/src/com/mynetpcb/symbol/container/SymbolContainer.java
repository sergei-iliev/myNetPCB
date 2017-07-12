package com.mynetpcb.symbol.container;


import com.mynetpcb.core.capi.Typeable;
import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.core.utils.VersionUtils;
import com.mynetpcb.symbol.unit.Symbol;

import java.io.IOException;

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


public class SymbolContainer extends UnitContainer<Symbol,Shape> implements Typeable{
    public SymbolContainer() {
        setFileName("Symbols");
    }
    public StringBuffer Format() {
        //***go through all circuits and invoke format on them
        StringBuffer xml=new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n<modules identity=\"Module\" type=\""+getType()+"\" version=\"" + VersionUtils.SYMBOL_VERSION+"\">\r\n");
        for(Symbol symbol:getUnits()){
          xml.append(symbol.Format());
          xml.append("\r\n");
        }
        xml.append("</modules>");
        return xml;
    }
    
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
            String category = (String)expr.evaluate(document, XPathConstants.STRING);
                    if(!category.equals("")){
                        this.setCategoryName(category);    
                    }             
        

         expr = xpath.compile("//module");
         NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        
         for (int i = 0; i < nodes.getLength(); i++) {
             Node node=nodes.item(i);
             if(!((org.w3c.dom.Element)node).getTagName().equals("module")){
                continue;                        
             }  
            Symbol module=new Symbol(1,1);
            module.Parse(node); 
            Add(module);
         }   
        
        //set the type{SYMBOL,GROUND,POWER}
        String text = document.getDocumentElement().getAttribute("type");        
        if(!text.equals("")){
          this.setType(Type.valueOf(text));
        }
            
    }
    
    @Override
    public void Parse(String xml,int index) throws XPathExpressionException,ParserConfigurationException,
                                        SAXException, IOException {
         
        Document document = Utilities.buildDocument(xml);
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr;   
        
            
        expr = xpath.compile("//module");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        int _index=0;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node=nodes.item(i);
            if(!((org.w3c.dom.Element)node).getTagName().equals("module")){
               continue;                        
            }  
            if(_index==index){
               getUnit().Parse(node);
            }
            _index++;
        }   
        
    }

    @Override
    public Typeable.Type getType() {
        if(getUnits().size()==0){
          return Type.SYMBOL;  //default
        }else{
          return getUnits().iterator().next().getType(); 
        }
    }

    @Override
    public void setType(Typeable.Type type) {
        for(Symbol module:getUnits()){
          module.setType(type);
        }     
    }

}
