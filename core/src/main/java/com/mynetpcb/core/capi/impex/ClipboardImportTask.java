package com.mynetpcb.core.capi.impex;

import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.container.UnitContainerProducer;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.CommandResult;
import com.mynetpcb.core.utils.Utilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import javax.swing.SwingUtilities;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

public class ClipboardImportTask extends CommandResult<UnitContainer> {

    private final UnitContainerProducer producer;
    private final String content;


    public ClipboardImportTask(CommandListener monitor, UnitContainerProducer producer, String content,Class receiver) {
        super(monitor, receiver);
        this.producer = producer;
        this.content = content;
    }


    @Override
    public UnitContainer execute() {
        UnitContainer container = null;
        try {
            monitor.onStart(this.receiver);  
            String rootTag=this.getRootTag(this.content); 
            if (rootTag.equalsIgnoreCase("modules")) {
                container = producer.createUnitContainerByName("modules");
            }else{                
                if (rootTag.equalsIgnoreCase("circuits")) {
                    container = producer.createUnitContainerByName("circuits");
                } else {                    
                    if (rootTag.equalsIgnoreCase("footprints")) {
                        container = producer.createUnitContainerByName("footprints");
                    } else {                        
                        if(rootTag.equalsIgnoreCase("boards")){
                            container=producer.createUnitContainerByName("boards");
                        }else
                          throw new IllegalStateException("Unknown tag. Unable to import.");
                    }
                }
            }
            
            String xml = Utilities.addNode(content, "filename",null);
            container.parse(xml);
        } catch (Exception e) {
            e.printStackTrace();
            invokeErrorDialog(e.getMessage());
        } finally {
            SwingUtilities.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        monitor.onFinish(receiver);   
                    }
                });      
        }
        return container;
    }

    private String getRootTag(String content) throws ParserConfigurationException, SAXException, IOException {        
        DocumentBuilderFactory fact = 
        DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = fact.newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        Document doc = builder.parse(stream);
        Node node = doc.getDocumentElement();
        return node.getNodeName();        
    }
    
    @Override
    public void cancel() {
    }
}
