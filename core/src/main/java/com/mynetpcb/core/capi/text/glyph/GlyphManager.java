package com.mynetpcb.core.capi.text.glyph;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;


public enum GlyphManager {
    INSTANCE;
    
    
    private Map<Character,Glyph> glyphs=new HashMap<>();
    
    private GlyphManager(){
        try{
           initialize();
        }catch(Exception e){
           e.printStackTrace(System.out); 
        }
    }
    
    private void initialize() throws Exception{

        try(InputStream istream =getClass().getClassLoader().getResourceAsStream("fonts/defaultfont.xml")){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(istream);
        Document document = builder.parse(is);

        XPathFactory xfactory = XPathFactory.newInstance();
        XPath xpath = xfactory.newXPath();
        XPathExpression expr;

        expr = xpath.compile("//symbol");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
         for (int i = 0; i < nodes.getLength(); i++) {
            Node node=nodes.item(i);
            Glyph glyph = new Glyph();
            glyph.fromXML(node);
            //scale up
            //glyph.scale(1);  //1mm 
            glyphs.put(new Character(glyph.getChar()), glyph);
         }  
        }        
    }
    
    public Glyph getGlyph(char symbol){
      Glyph glyph= glyphs.get(Character.valueOf(symbol));    
      if(glyph!=null){
          try {
              return glyph.clone();
          } catch (CloneNotSupportedException e) {
            e.printStackTrace();
          }  
      }
      return null;
    }
    public Glyph getGlyph(Character symbol){
        Glyph glyph= glyphs.get(symbol);    
        if(glyph!=null){

            try {
                return glyph.clone();
            } catch (CloneNotSupportedException e) {
              e.printStackTrace();
            }
        }
        return null;

    }
    
}
