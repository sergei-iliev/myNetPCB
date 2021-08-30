package com.mynetpcb.core.capi.impex;


import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.container.UnitContainerProducer;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.CommandResult;
import com.mynetpcb.core.capi.verification.XMLRootTagVerifier;
import com.mynetpcb.core.utils.Utilities;

import java.io.BufferedReader;
import java.io.File;

import java.nio.charset.Charset;
import java.nio.file.Files;

import javax.swing.SwingUtilities;


public class XMLImportTask extends CommandResult<UnitContainer> {

    private final UnitContainerProducer producer;
    private final String targetFile;


    public XMLImportTask(CommandListener monitor, UnitContainerProducer producer, String targetFile,Class receiver) {
        super(monitor, receiver);
        this.producer = producer;
        this.targetFile = targetFile;
    }


    @Override
    public UnitContainer execute() {
        File file = new File(targetFile);
        XMLRootTagVerifier verifier = new XMLRootTagVerifier(file, "modules");
        UnitContainer container = null;
        try {
            monitor.onStart(this.receiver);  

            if (verifier.check()&&producer.exists("modules")) {
                container = producer.createUnitContainerByName("modules");
            }else{
                verifier = new XMLRootTagVerifier(file, "circuits");
                if (verifier.check()&&producer.exists("circuits")) {
                    container = producer.createUnitContainerByName("circuits");
                } else {
                    verifier = new XMLRootTagVerifier(file, "footprints");
                    if (verifier.check()&&producer.exists("footprints")) {
                        container = producer.createUnitContainerByName("footprints");
                    } else {
                        verifier = new XMLRootTagVerifier(file, "boards");
                        if(verifier.check()&&producer.exists("boards")){
                            container=producer.createUnitContainerByName("boards");
                        }else
                          throw new IllegalStateException("Unknown tag. Unable to import.");
                    }
                }
            }
            Charset charset = Charset.forName("UTF-8");
            String xml = "";
            try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    xml += line;
                }
            }
            xml = Utilities.addNode(xml, "filename", file.getName());
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

    @Override
    public void cancel() {
    }
}
