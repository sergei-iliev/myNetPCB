package tests.mynetpcb.core.capi.text;

import com.mynetpcb.core.capi.text.glyph.Glyph;
import com.mynetpcb.core.capi.text.glyph.GlyphManager;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

public class FontGlyphTest {

    private final static String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<font name=\"default\">" +
        "<symbol char=\"&quot;\" delta=\"12\">" + "<line>0,2,0,10</line>" + "<line>10,0,10,9</line>" + "</symbol>" +
        "</font>";


    @Test
    public void testGlyphRead() throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        Document document = builder.parse(is);

        XPathFactory xfactory = XPathFactory.newInstance();
        XPath xpath = xfactory.newXPath();
        XPathExpression expr;

        expr = xpath.compile("//symbol");
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);


        Node node = nodes.item(0);
        Glyph glyph = new Glyph();
        glyph.fromXML(node);
        Assert.assertTrue(glyph.getLinesNumber() == 4);
        Assert.assertTrue(glyph.getLinesNumber() == 4);

    }

    @Test
    public void testGlyphManagerInitialization() throws Exception {
       Assert.assertTrue(Character.valueOf(GlyphManager.INSTANCE.getGlyph('$').getChar()).equals(Character.valueOf('$')));       
    }
    

    
    
    
}
