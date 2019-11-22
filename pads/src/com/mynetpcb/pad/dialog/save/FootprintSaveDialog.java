package com.mynetpcb.pad.dialog.save;


import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.ReadRepositoryLocal;
import com.mynetpcb.core.capi.io.WriteUnitLocal;
import com.mynetpcb.core.capi.io.remote.ReadConnector;
import com.mynetpcb.core.capi.io.remote.WriteConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;
import com.mynetpcb.core.dialog.save.AbstractSaveDialog;
import com.mynetpcb.core.utils.Utilities;

import java.awt.Window;
import java.awt.event.ActionEvent;

import java.io.IOException;

import javax.swing.JComboBox;

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

public class FootprintSaveDialog extends AbstractSaveDialog {
    public FootprintSaveDialog(Window owner, UnitComponent component,boolean isonline) {
        super(owner, component, "Save",isonline);
    }

    @Override
    protected void prepareData() {
        super.prepareData();
        //***Read Libraries
        if (!isonline) {
            Command reader = new ReadRepositoryLocal(this, Configuration.get().getFootprintsRoot(), JComboBox.class);
            CommandExecutor.INSTANCE.addTask("ReadRepositoryLocal", reader);
        } else {
            Command reader =
                new ReadConnector(this, new RestParameterMap.ParameterBuilder("/footprints").addURI("libraries").build(), JComboBox.class);
            CommandExecutor.INSTANCE.addTask("ReadLibraries", reader);
        }
    }

    @Override
    protected void doBody() {
        super.doBody();
        libraryName.setText("Library");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() == libraryCombo) {
            //skip double invocation
            if (e.getActionCommand().equalsIgnoreCase("comboBoxEdited")) {
                return;
            }

            //***selection of a new lib
            if (libraryCombo.getSelectedItem() != null && libraryCombo.getSelectedIndex() != -1 &&
                ((String)libraryCombo.getSelectedItem()).length() > 0) {
                //skip invoking REST at typing new library
                if (!isonline) {
                    Command reader =
                        new ReadRepositoryLocal(this, Configuration.get().getFootprintsRoot().resolve((String)libraryCombo.getSelectedItem()),
                                                FootprintSaveDialog.class);
                    CommandExecutor.INSTANCE.addTask("ReadCategoriesLocal", reader);
                } else {
                    Command reader =
                        new ReadConnector(this, new RestParameterMap.ParameterBuilder("/footprints").addURI("libraries").addURI((String)libraryCombo.getSelectedItem()).addURI("categories").addAttribute("includefiles","false") .build(),
                                          FootprintSaveDialog.class);
                    CommandExecutor.INSTANCE.addTask("ReadCategories", reader);

                }

            }
        }
        if (e.getSource() == SaveButton) {
            if (fileNameText.getText() == null || fileNameText.getText().length() == 0) {
                return;
            }
            if (!isonline) {
                Command writer =
                    new WriteUnitLocal(this, getComponent().getModel().format(), Configuration.get().getFootprintsRoot(),
                                       (String)libraryCombo.getSelectedItem(), (String)categoryCombo.getSelectedItem(),
                                       fileNameText.getText(), overrideCheck.isSelected(), WriteUnitLocal.class);
                CommandExecutor.INSTANCE.addTask("WriteUnitLocal", writer);
            } else {   
                Command writer =
                    new WriteConnector(this, getComponent().getModel().format(), new RestParameterMap.ParameterBuilder("/footprints").addURI("libraries").addURI((String)libraryCombo.getSelectedItem()).addURI("categories").addURI(categoryCombo.getSelectedItem()==null||"".equals(categoryCombo.getSelectedItem())?"null":(String)categoryCombo.getSelectedItem()).addAttribute("footprintName",fileNameText.getText()).addAttribute("overwrite",String.valueOf(overrideCheck.isSelected())).build(),
                                       WriteConnector.class);
                CommandExecutor.INSTANCE.addTask("WriteUnit", writer);
            }

        }
    }


    @Override
    public void OnRecive(String result, Class reciever) {
        super.OnRecive(result, reciever);
        if (reciever == FootprintSaveDialog.class) {
            categoryCombo.setEditable(false);
            categoryCombo.removeAllItems();
            categoryCombo.getEditor().setItem(null);
            categoryCombo.setEditable(true);

            try {
                Document document = Utilities.buildDocument(result);

                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();
                XPathExpression expr;

                expr = xpath.compile("//name");
                NodeList nodes = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    categoryCombo.addItem(node.getTextContent());
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace(System.out);
            } catch (SAXException e) {
                e.printStackTrace(System.out);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            } catch (XPathExpressionException e) {
                e.printStackTrace(System.out);
            }
            if (libraryCombo.getSelectedItem().equals(getComponent().getModel().getLibraryName())) {
                categoryCombo.setSelectedItem(getComponent().getModel().getCategoryName());
            }

        }


    }


}

