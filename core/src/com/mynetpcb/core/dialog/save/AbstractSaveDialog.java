package com.mynetpcb.core.dialog.save;


import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;

import java.lang.ref.WeakReference;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

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


/**
 *Base class for all Save dialogs. Use template method pattern to further improve UI in subclasses
 * @author Sergey Iliev
 */
@Deprecated
public abstract class AbstractSaveDialog extends JDialog implements ActionListener,CommandListener{

protected JLabel fileName,libraryName,categoryName,overrideName;

protected JTextField fileNameText;

protected JComboBox libraryCombo,categoryCombo;

protected JCheckBox overrideCheck;

protected  final WeakReference<UnitComponent> componentRef;

protected final boolean isonline;
    
protected    JButton SaveButton;
protected    JButton CancelButton;
    
    public AbstractSaveDialog(Frame owner,UnitComponent component,String caption,boolean isonline) { 
       super(owner,caption,Dialog.ModalityType.APPLICATION_MODAL);
       this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
       this.setResizable(false);
       this.isonline=isonline;
       this.componentRef=new WeakReference<UnitComponent>(component);
    }
        
    
    protected void doLayoutManager(){
        this.setSize(new Dimension(290, 205));
        this.getContentPane().setLayout(new BorderLayout()); 
    }
    protected UnitComponent getComponent(){
        return this.componentRef.get();
    }
    protected void doHeader(){
        //JPanel panel=new JPanel();
        //panel.setPreferredSize(new Dimension(getWidth(),30));
        //this.getContentPane().add(panel,BorderLayout.NORTH);
    }

    protected void doBody(){
        JPanel panel=new JPanel();
        SpringLayout layout=new SpringLayout();
        fileName=new JLabel("Name");
        libraryName=new JLabel("Library");
        categoryName=new JLabel("Category");
        overrideName=new JLabel("Overwrite existing unit");
        fileNameText=new JTextField();
        fileNameText.setPreferredSize(new Dimension(fileNameText.getWidth(),26));
        libraryCombo=new JComboBox();
        libraryCombo.setEditable(true);
        categoryCombo=new JComboBox();
        categoryCombo.setEditable(true);
        overrideCheck=new JCheckBox();
        
        panel.setLayout(layout);
        panel.add(fileName);
        panel.add(fileNameText);
        panel.add(libraryName);
        panel.add(libraryCombo);
        panel.add(categoryName);
        panel.add(categoryCombo);
        panel.add(overrideName);
        panel.add(overrideCheck);
        layout.putConstraint(SpringLayout.WEST,fileName,
                             15,
                             SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, fileName,
                             15,
                             SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, fileNameText,
                             115,
                             SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, fileNameText,
                             15,
                             SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, fileNameText,
                             255,
                             SpringLayout.WEST, panel);
//2th row
       layout.putConstraint(SpringLayout.WEST,libraryName,
                            15,
                            SpringLayout.WEST, panel);
       layout.putConstraint(SpringLayout.NORTH, libraryName,
                            50,
                            SpringLayout.NORTH, panel);
//library combo
        layout.putConstraint(SpringLayout.WEST, libraryCombo,
                             115,
                             SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, libraryCombo,
                             45,
                             SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, libraryCombo,
                             255,
                             SpringLayout.WEST, panel);
//3th row
       layout.putConstraint(SpringLayout.WEST,categoryName,
                            15,
                            SpringLayout.WEST, panel);
       layout.putConstraint(SpringLayout.NORTH, categoryName,
                            80,
                            SpringLayout.NORTH, panel);
//category combo
       layout.putConstraint(SpringLayout.WEST, categoryCombo,
                                     115,
                                     SpringLayout.WEST, panel);
       layout.putConstraint(SpringLayout.NORTH, categoryCombo,
                                     75,
                                     SpringLayout.NORTH, panel);
       layout.putConstraint(SpringLayout.EAST, categoryCombo,
                                     255,
                                     SpringLayout.WEST, panel);
        
//4th row
       layout.putConstraint(SpringLayout.WEST,overrideName,
                            15,
                            SpringLayout.WEST, panel);
       layout.putConstraint(SpringLayout.NORTH, overrideName,
                            110,
                            SpringLayout.NORTH, panel);
//check
       //layout.putConstraint(SpringLayout.WEST, categoryCombo,
       //                              115,
       //                              SpringLayout.WEST, panel);
       layout.putConstraint(SpringLayout.NORTH, overrideCheck,
                                     105,
                                     SpringLayout.NORTH, panel);
       layout.putConstraint(SpringLayout.EAST, overrideCheck,
                                     255,
                                     SpringLayout.WEST, panel);
       
        this.getContentPane().add(panel,BorderLayout.CENTER);
    }
    public void build(){
        this.doLayoutManager();
        this.doHeader();
        this.doBody();
        this.doBottom();
        this.prepareData();   
        this.setLocationRelativeTo(null); //centers on screen
        this.setVisible(true);
    }
    protected void doBottom(){
        JPanel panel=new JPanel();
        panel.setPreferredSize(new Dimension(getWidth(),40));
        SaveButton=new JButton("Save");
        CancelButton=new JButton("Cancel");
        panel.add(SaveButton);
        panel.add(CancelButton);
        this.getContentPane().add(panel,BorderLayout.SOUTH);
    }
    
    protected  void prepareData(){
        this.CancelButton.addActionListener(this);
        this.SaveButton.addActionListener(this);
        //set file name
        fileNameText.setText((getComponent().getModel().getFormatedFileName() ==
                                 null ?
                                 "Unknown" :
                                 getComponent().getModel().getFormatedFileName())); 
        this.addWindowListener(new WindowAdapter() {
                 public void windowClosing(WindowEvent evt) {
                     AbstractSaveDialog.this.Close();
                 }
             });
        
    }
    
    public void actionPerformed(ActionEvent e) { 
        if (e.getSource() == CancelButton) {
          this.Close();
        }

    }
    
    protected void Close(){
        CommandExecutor.INSTANCE.cancel();
        this.dispose();        
    }
    
    protected void enableControls() {
        fileNameText.setEditable(true);
        SaveButton.setEnabled(true);
        categoryCombo.setEnabled(true);
        libraryCombo.setEnabled(true);
    }

    protected void disableControls() {
        fileNameText.setEditable(false);
        SaveButton.setEnabled(false);
        categoryCombo.setEnabled(false);
        libraryCombo.setEnabled(false);
    }
    
    @Override
    public void onStart(Class<?> reciever) {
        disableControls();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    @Override
    public void onError(String error) {
        enableControls();
        this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(this, error, "Error",
                                      JOptionPane.ERROR_MESSAGE);    
    }
    
    @Override
    public void onRecive(String result, Class reciever) {
        if (reciever==JComboBox.class) {
            libraryCombo.removeActionListener(this);
            try {
                Document document = Utilities.buildDocument(result);
                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();
                XPathExpression expr;  
                
                expr = xpath.compile("//library/*");
                NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node=nodes.item(i);
                    libraryCombo.addItem(node.getTextContent());                 
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace(System.out);
            } catch (SAXException e) {
                e.printStackTrace(System.out);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            } catch (XPathExpressionException e) {
                e.printStackTrace(System.out);
            }finally{
                  libraryCombo.addActionListener(this); 
            }
            libraryCombo.setSelectedItem(getComponent().getModel().getLibraryName());
        }       
        
    }
    @Override
    public void onFinish(Class<?> receiver) {
           enableControls();
           this.setCursor(Cursor.getDefaultCursor());

        if (receiver.getSimpleName().equals("WriteConnector") ||
            receiver.getSimpleName().equals("WriteUnitLocal")) {
            //***success on write -> save module name and library
            getComponent().getModel().setFileName(fileNameText.getText());
            //update name
            getComponent().fireContainerEvent(new ContainerEvent(null,ContainerEvent.RENAME_CONTAINER));
            getComponent().getModel().setLibraryName( (String)libraryCombo.getSelectedItem());
            getComponent().getModel().setCategoryName( (String)categoryCombo.getSelectedItem());
            getComponent().getModel().registerInitialState();
            Close();
        }  
    }
}



