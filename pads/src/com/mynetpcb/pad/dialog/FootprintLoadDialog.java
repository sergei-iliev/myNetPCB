package com.mynetpcb.pad.dialog;


import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.gui.UnitSelectionPanel;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.ReadCategoriesLocal;
import com.mynetpcb.core.capi.io.ReadRepositoryLocal;
import com.mynetpcb.core.capi.io.ReadUnitLocal;
import com.mynetpcb.core.capi.io.ReadUnitsLocal;
import com.mynetpcb.core.capi.io.remote.ReadConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;
import com.mynetpcb.core.capi.tree.AttachedItem;
import com.mynetpcb.core.capi.tree.IconListCellRenderer;
import com.mynetpcb.core.dialog.load.AbstractLoadDialog;
import com.mynetpcb.core.pad.Packaging;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

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


public class FootprintLoadDialog extends AbstractLoadDialog implements ActionListener, TreeSelectionListener,TreeWillExpandListener, CommandListener {

    private JPanel basePanel = new JPanel();
    private JPanel leftPanel = new JPanel();
    private JComboBox libraryCombo = new JComboBox();
    private JTree footprintTree;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    private JScrollPane footprintTreeScroll = new JScrollPane();
    private FlowLayout flow = new FlowLayout();
    private JPanel bottomPanel = new JPanel(flow);
    private JScrollPane scrollViewer = new JScrollPane();
    private JPanel CenterPanel = new JPanel();
    private JPanel TopPanel = new JPanel();
    private UnitSelectionPanel selectionPanel;

    private JButton LoadButton = new JButton();
    private JButton CloseButton = new JButton();

    private Packaging packaging;
    
    private FootprintLoadDialog(Window d, String Caption, boolean enabled) {
        super(d, Caption, Dialog.ModalityType.DOCUMENT_MODAL);
        Init(enabled);
    }
/**
 * Load Dialog with selected footprint on it 
 **/
    private FootprintLoadDialog(Window f, String Caption,Packaging packaging) {
        super(f,Caption, Dialog.ModalityType.DOCUMENT_MODAL);
        this.packaging=packaging;
        Init(true);
        LoadButton.setText("Assign");
    }
    private void Init(boolean enabled) {
        this.setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(489, 543));
        basePanel.setLayout(new BorderLayout());
        basePanel.setPreferredSize(new Dimension(400, 300));
        leftPanel.setPreferredSize(new Dimension(150, 10));
        leftPanel.setLayout(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(10, 40));
        leftPanel.add(libraryCombo, BorderLayout.NORTH);
        leftPanel.add(footprintTreeScroll, BorderLayout.CENTER);
        basePanel.add(leftPanel, BorderLayout.WEST);
        basePanel.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(LoadButton, null);
        bottomPanel.add(CloseButton, null);
        TopPanel.setPreferredSize(new Dimension(TopPanel.getWidth(), 10));
        LoadButton.setText(" Load ");
        CloseButton.setText("Close");
        basePanel.add(TopPanel, BorderLayout.NORTH);
        
        selectionPanel = new UnitSelectionPanel();
        selectionPanel.setBackground(Color.BLACK);
        selectionPanel.setEnabled(enabled);
        selectionPanel.setPreferredSize(new Dimension(400, 500));
        scrollViewer = new JScrollPane();
        scrollViewer.getViewport().add(selectionPanel);
        scrollViewer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollViewer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.add(scrollViewer, BorderLayout.CENTER);
        basePanel.add(CenterPanel, BorderLayout.CENTER);

        libraryCombo.setRenderer(new IconListCellRenderer(Utilities.loadImageIcon(this,
                                                                                  "/com/mynetpcb/core/images/library.png")));
        libraryCombo.addActionListener(this);


        //UIManager.put("Tree.rendererFillBackground", false);
        footprintTree = new JTree();
        footprintTree.setShowsRootHandles(true);
        footprintTree.setVisibleRowCount(10);
        footprintTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        footprintTree.setEditable(false);
        footprintTree.addTreeSelectionListener(this);
        footprintTree.addTreeWillExpandListener(this);
        footprintTree.setModel(new DefaultTreeModel(root));
        footprintTree.setRootVisible(false);
        footprintTreeScroll.getViewport().add(footprintTree);

        flow.setHgap(80);
        LoadButton.addActionListener(this);
        CloseButton.addActionListener(this);


        this.getContentPane().add(basePanel, null);

        //***register on open listener
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                //***Read Libraries
                if (!Configuration.get().isIsApplet()) {
                    Command reader =
                        new ReadRepositoryLocal(FootprintLoadDialog.this, Configuration.get().getFootprintsRoot(),
                                                JComboBox.class);
                    CommandExecutor.INSTANCE.addTask("ReadRepositoryLocal", reader);
                } else {
                    Command reader =
                        new ReadConnector(FootprintLoadDialog.this, new RestParameterMap.ParameterBuilder("/footprints").addURI("libraries").build(),
                                          JComboBox.class);
                    CommandExecutor.INSTANCE.addTask("ReadProjects", reader);
                }

            }
        });
    }


    @Override
    public void dispose() {
        CommandExecutor.INSTANCE.cancel();
        this.selectionPanel.Release();
        System.gc();
        super.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().getClass() == JComboBox.class) {
            if (!Configuration.get().isIsApplet()) {
                Command reader =
                    new ReadCategoriesLocal(this, Configuration.get().getFootprintsRoot(),(String)libraryCombo.getSelectedItem(), footprintTree.getClass());
                CommandExecutor.INSTANCE.addTask("ReadCategoriesLocal", reader);
            } else {
                Command reader =
                    new ReadConnector(this, new RestParameterMap.ParameterBuilder("/footprints").addURI("libraries").addURI((String)libraryCombo.getSelectedItem()).addURI("categories").build(),
                                      footprintTree.getClass());
                CommandExecutor.INSTANCE.addTask("ReadFootprints", reader);
            }
        }
        if (e.getSource() == CloseButton) {
            this.dispose();
        }
        if (e.getSource() == LoadButton) {
            if (this.getSelectedModel() != null)
                this.setVisible(false);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)footprintTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        if (null != node && node.isLeaf()) { 
            if (null != node && node.isLeaf()) {
                AttachedItem attachedItem=(AttachedItem)node.getUserObject();
                if (!Configuration.get().isIsApplet()) {
                    Command reader =
                        new ReadUnitLocal(this, Configuration.get().getFootprintsRoot(),attachedItem.getLibrary(),(node.getLevel()==1?null:attachedItem.getCategory()),
                                          attachedItem.getFileName(), Footprint.class);
                    CommandExecutor.INSTANCE.addTask("ReadUnitLocal", reader);
                } else {                
                    Command reader =
                        new ReadConnector(this, new RestParameterMap.ParameterBuilder("/footprints").addURI("libraries").addURI(attachedItem.getLibrary()).addURI("categories").addURI((node.getLevel()==1?"null":attachedItem.getCategory())).addURI(attachedItem.getFileName()).build(),
                                          Footprint.class);
                    CommandExecutor.INSTANCE.addTask("ReadFootprint", reader);
                }
            } 
        }
        
    }

    @Override
    public void onStart(Class<?> sender) {
        this.disableControls();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (sender==JTree.class) {
            root.removeAllChildren();
            ((DefaultTreeModel)footprintTree.getModel()).reload();
        }
    }


    @Override
    public void onRecive(String result, Class sender) {
        if (sender==JComboBox.class) {
            //***parse xml
            this.libraryCombo.removeActionListener(this);
            try {
                Document document = Utilities.buildDocument(result);

                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();
                XPathExpression expr;

                expr = xpath.compile("//name");
                NodeList nodes = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                        Node node = nodes.item(i);
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
            } finally {
                libraryCombo.addActionListener(this);
                //make selection if asignning package to symbol
                if(packaging!=null&&packaging.getFootprintLibrary()!=null){
                   libraryCombo.setSelectedItem(packaging.getFootprintLibrary());
                }else{
                    if(libraryCombo.getItemCount()>0){  
                       libraryCombo.setSelectedIndex(0);
                    }
                }
            }
        }
        if(sender==JTree.class||sender==ReadUnitsLocal.class){
            //***parse xml
            this.footprintTree.removeTreeSelectionListener(this);
            try {
                Document document = Utilities.buildDocument(result);

                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();
                XPathExpression expr;

                expr = xpath.compile("//name");
                NodeList nodes = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
                if(sender==JTree.class){
                                    //clear selection
                                    selectionPanel.Clear();
                                    
                                    //fill category level tree
                                    for (int i = 0; i < nodes.getLength(); i++) {
                                        Element element=(Element)nodes.item(i);
                                        if(element.hasAttribute("category"))
                                            Utilities.addLibraryModule(footprintTree,root,element.getAttribute("library"),element.getTextContent(),
                                                                                         null,null);
                                        else
                                            Utilities.addLibraryModule(footprintTree,root,element.getAttribute("library"),null,
                                                                                      element.getTextContent(),element.getAttribute("fullname"));
                                           
                                    }    
                }else{
                                    for (int i = 0; i < nodes.getLength(); i++) {
                                        Element element = (Element)nodes.item(i);
                                            Utilities.addLibraryModule(footprintTree, root, element.getAttribute("library"),
                                                                          element.getAttribute("category"), element.getTextContent(),
                                                                          element.getAttribute("fullname"));
                                    }
                }
                footprintTree.expandPath(new TreePath(root.getPath()));
            } catch (ParserConfigurationException e) {
                e.printStackTrace(System.out);
            } catch (SAXException e) {
                e.printStackTrace(System.out);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            } catch (XPathExpressionException e) {
                e.printStackTrace(System.out);
            } finally {
                footprintTree.addTreeSelectionListener(this);
                if(packaging!=null){
                    if(sender==JTree.class){
                       
                      if(packaging.getFootprintCategory()==null){ 
                          //category level file; select file
                        Utilities.selectTreeLeaf(footprintTree,new AttachedItem.Builder(packaging.getFootprintName()).setLibrary(packaging.getFootprintLibrary()).setCategory(packaging.getFootprintCategory()).setFileName(packaging.getFootprintFileName()).build());
                      }else{
                          //category level; lazy load files
                          Utilities.selectTreeNode(footprintTree,new AttachedItem.Builder(packaging.getFootprintName()).setLibrary(packaging.getFootprintLibrary()).setCategory(packaging.getFootprintCategory()).build());
                            try {
                                treeWillExpand(new TreeExpansionEvent(footprintTree, footprintTree.getSelectionPath()));
                            } catch (ExpandVetoException e) {
                            }
                        }
                    }else{
                     //module level; select file
                       Utilities.selectTreeLeaf(footprintTree,new AttachedItem.Builder(packaging.getFootprintName()).setLibrary(packaging.getFootprintLibrary()).setCategory(packaging.getFootprintCategory()).setFileName(packaging.getFootprintFileName()).build());                        
                    }
                }
            }
            
        }
        //***module xml!
        if (sender==Footprint.class) {
            selectionPanel.Clear();
            try {
                UnitContainer model = new FootprintContainer();
                model.parse(result);
                selectionPanel.getSelectionGrid().setModel(model);
            } catch (Exception ioe) {
                ioe.printStackTrace(System.out);
                return;
            }
            
            //preselect if symbol to footprint assignment
            if(packaging!=null){
               selectionPanel.getSelectionGrid().getModel().setActiveUnitByName(packaging.getFootprintName()); 
                //no need to triger it again
               packaging=null;
            }
            selectionPanel.buildSelectionGrid();
            selectionPanel.repaint();
            selectionPanel.revalidate();
        }
    }

    @Override
    public void onFinish(Class<?> receiver) {
        this.setCursor(Cursor.getDefaultCursor());
        this.enableControls();
    }

    @Override
    public void onError(String error) {
        onFinish(null);
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void enableControls() {
        LoadButton.setEnabled(true);
        footprintTree.setEnabled(true);
        libraryCombo.setEnabled(true);
    }

    private void disableControls() {
        LoadButton.setEnabled(false);
        footprintTree.setEnabled(false);
        libraryCombo.setEnabled(false);
    }
    @Override
    public UnitContainer getSelectedModel() {
        return this.selectionPanel.getSelectionGrid().getModel();
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        if(((DefaultMutableTreeNode)event.getPath().getLastPathComponent()).getUserObject()==null){
            return;
        }
        DefaultMutableTreeNode categoryNode=(DefaultMutableTreeNode)event.getPath().getLastPathComponent();
        if(categoryNode.getChildCount()==0){
            if (!Configuration.get().isIsApplet()) {
                Command reader =
                    new ReadUnitsLocal(this, Configuration.get().getFootprintsRoot().resolve(((AttachedItem)categoryNode.getUserObject()).getLibrary()).resolve(((AttachedItem)categoryNode.getUserObject()).getCategory()),
                                       "footprints", ReadUnitsLocal.class);
               
                CommandExecutor.INSTANCE.addTask("ReadUnitsLocal", reader);
            } else {   
                Command reader =new ReadConnector(this, new RestParameterMap.ParameterBuilder("/footprints").addURI("libraries").addURI(((AttachedItem)categoryNode.getUserObject()).getLibrary()).addURI("categories").addURI(((AttachedItem)categoryNode.getUserObject()).getCategory()).build(),
                                ReadUnitsLocal.class);
                CommandExecutor.INSTANCE.addTask("ReadFootprints", reader);
            }          
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

    }

    public static class Builder extends AbstractLoadDialog.Builder{
        @Override
        public AbstractLoadDialog build() {
            if(this.packaging!=null){
              return new FootprintLoadDialog(this.window,this.caption,this.packaging);
            }else{
              return new FootprintLoadDialog(this.window,this.caption,this.enabled);    
            }
        }
    }
}

