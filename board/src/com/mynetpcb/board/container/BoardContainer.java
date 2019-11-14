package com.mynetpcb.board.container;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.core.utils.VersionUtils;

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

public class BoardContainer extends UnitContainer<Board, Shape>{
    public BoardContainer() {
        setFileName("Boards");
    }

    @Override
    public StringBuffer Format() {
        //***go through all circuits and invoke format on them
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<boards identity=\"board\" designer=\"" +
                   this.getDesignerName() + "\" version=\"" + VersionUtils.BOARD_VERSION + "\">\r\n");
        for (Board board : getUnits()) {
            xml.append(board.format());
            xml.append("\r\n");
        }
        xml.append("</boards>");
        return xml;
    }

    @Override
    public void Parse(String xml) throws XPathExpressionException,
                                         ParserConfigurationException,
                                         SAXException, IOException {

        Document document = Utilities.buildDocument(xml);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr;

        String designer=document.getDocumentElement().getAttribute("designer");
        if (!designer.equals("")) {
            this.setDesignerName(designer);
        }

        expr = xpath.compile("//filename");
        String filename =
            (String)expr.evaluate(document, XPathConstants.STRING);
        if (!filename.equals("")) {
            this.setFileName(filename);
        }

        expr = xpath.compile("//library");
        String library =
            (String)expr.evaluate(document, XPathConstants.STRING);
        if (!library.equals("")) {
            this.setLibraryName(library);
        }

        expr = xpath.compile("//category");
        String category = (String)expr.evaluate(document, XPathConstants.STRING);
        if (!category.equals("")) {
            this.setCategoryName(category);
        }
        
        expr = xpath.compile("//board");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node=nodes.item(i);
            if(!((org.w3c.dom.Element)node).getTagName().equals("board")){
               continue;                        
            }  
            Board board =new Board(1,1);
            board.parse(node);
            Add(board);
            board.notifyListeners(ShapeEvent.ADD_SHAPE);
        }         
    }

    @Override
    public void Parse(String xml, int index) throws ParserConfigurationException, SAXException, IOException,
                                                XPathExpressionException {
        Document document = Utilities.buildDocument(xml);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr;
        
        expr = xpath.compile("//boards/*");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        int _index=0;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node=nodes.item(i);
            if(!((Element)node).getTagName().equals("board")){
               continue;                        
            }  
            if(_index==index){
               getUnit().parse(node);
            }
            _index++;              
        } 
    }
}
