package com.mynetpcb.symbol.dialog;

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
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.unit.Symbol;

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

public class SymbolLoadDialog  extends AbstractLoadDialog implements ActionListener,TreeSelectionListener,TreeWillExpandListener,CommandListener{
    private JPanel basePanel = new JPanel();
    private JPanel leftPanel = new JPanel();
    private JComboBox libraryCombo = new JComboBox();
    private JTree symbolTree = new JTree();
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    private JScrollPane symbolTreeScroll = new JScrollPane();
    private FlowLayout flow = new FlowLayout();
    private JPanel bottomPanel = new JPanel(flow);
    private JScrollPane scrollViewer = new JScrollPane();
    private JPanel CenterPanel = new JPanel();
    private JPanel TopPanel = new JPanel();
    private UnitSelectionPanel selectionPanel;
    
    private JButton LoadButton = new JButton();
    private JButton CloseButton = new JButton();
    
    private SymbolLoadDialog(Window window, String Caption,boolean enabled) {
        super(window, Caption,Dialog.ModalityType.DOCUMENT_MODAL);
        Init(enabled);
    }
    
    private void Init(boolean enabled) {
        this.setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(689, 543));
        basePanel.setLayout(new BorderLayout());
        basePanel.setPreferredSize(new Dimension(400, 300));
        leftPanel.setPreferredSize(new Dimension(150, 10));
        leftPanel.setLayout(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(10, 40));
        leftPanel.add(libraryCombo, BorderLayout.NORTH);
        leftPanel.add(symbolTreeScroll, BorderLayout.CENTER);
        basePanel.add(leftPanel, BorderLayout.WEST);
        basePanel.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(LoadButton, null);
        bottomPanel.add(CloseButton, null);
        TopPanel.setPreferredSize(new Dimension(TopPanel.getWidth(), 10));
        LoadButton.setText(" Load ");
        CloseButton.setText("Close");
        basePanel.add(TopPanel, BorderLayout.NORTH);
        selectionPanel=new UnitSelectionPanel();
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.setEnabled(enabled);
        selectionPanel.getSelectionGrid().setBackgroundColor(Color.WHITE);
        selectionPanel.getSelectionGrid().setTextColor(Color.BLACK);
        selectionPanel.getSelectionGrid().setScaleRatio(1.2);
        selectionPanel.getSelectionGrid().setScaleFactor(0);
        selectionPanel.getSelectionGrid().setMinScaleFactor(0);
        selectionPanel.getSelectionGrid().setMaxScaleFactor(10);
            
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


        symbolTree.setShowsRootHandles(true);
        symbolTree.setVisibleRowCount(10);
        symbolTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        symbolTree.setEditable(false);
        symbolTree.addTreeSelectionListener(this);
        symbolTree.addTreeWillExpandListener(this);
        symbolTree.setModel(new DefaultTreeModel(root));
        symbolTree.setRootVisible(false);
        symbolTreeScroll.getViewport().add(symbolTree);

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
                            new ReadRepositoryLocal(SymbolLoadDialog.this, Configuration.get().getSymbolsRoot(),JComboBox.class);
                        CommandExecutor.INSTANCE.addTask("ReadRepositoryLocal", reader);
                    } else {
                        Command reader =
                            new ReadConnector(SymbolLoadDialog.this,
                                              new RestParameterMap.ParameterBuilder("/symbols").build(),
                                              JComboBox.class);
                        CommandExecutor.INSTANCE.addTask("ReadProjects", reader);
                    }

                }
            });
    }

    @Override
    public void dispose(){
      CommandExecutor.INSTANCE.cancel();
      this.selectionPanel.Release();
      System.gc();
      super.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().getClass()== JComboBox.class) {
            if (!Configuration.get().isIsApplet()) {
                Command reader =
                    new ReadCategoriesLocal(this, Configuration.get().getSymbolsRoot(),(String)libraryCombo.getSelectedItem(), symbolTree.getClass());
                CommandExecutor.INSTANCE.addTask("ReadCategoriesLocal", reader);
            } else {
                Command reader =
                    new ReadConnector(this, new RestParameterMap.ParameterBuilder("/symbols").addURI((String)libraryCombo.getSelectedItem()).build(),
                                      symbolTree.getClass());
                CommandExecutor.INSTANCE.addTask("ReadSymbols", reader);
            }
        }
        if (e.getSource() == CloseButton) {
            this.dispose();
        }
        if (e.getSource() == LoadButton) {
            if(this.getSelectedModel()!=null)
               this.setVisible(false); 
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode)symbolTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        if (null != node && node.isLeaf()) {
            AttachedItem attachedItem=(AttachedItem)node.getUserObject();
            if (!Configuration.get().isIsApplet()) {
                Command reader =
                    new ReadUnitLocal(this, Configuration.get().getSymbolsRoot(),attachedItem.getLibrary(),(node.getLevel()==1?null:attachedItem.getCategory()),
                                      attachedItem.getFileName(), Symbol.class);
                CommandExecutor.INSTANCE.addTask("ReadUnitLocal", reader);
            } else {                
                Command reader =
                    new ReadConnector(this, new RestParameterMap.ParameterBuilder("/symbols").addURI(attachedItem.getLibrary()).addURI((node.getLevel()==1?"null":attachedItem.getCategory())).addURI(attachedItem.getFileName()).build(),
                                      Symbol.class);
                CommandExecutor.INSTANCE.addTask("ReadSymbol", reader);
            }
        }    
    }

    @Override
    public void onStart(Class<?> sender) {
        this.disableControls();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (sender== JTree.class) {
            root.removeAllChildren();
            ((DefaultTreeModel)symbolTree.getModel()).reload();
        }   
    }

    @Override
    public void onRecive(String result, Class sender) {
        if (sender==JComboBox.class||sender==JTree.class||sender==ReadUnitsLocal.class) {
            //***parse xml
            this.symbolTree.removeTreeSelectionListener(this);
            try {
                Document document = Utilities.buildDocument(result);
                
                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();
                XPathExpression expr;  
                
                expr = xpath.compile("//name");
                NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
                if (sender==JComboBox.class) {                
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node node=nodes.item(i);
                        libraryCombo.addItem(node.getTextContent());                     
                    }    
                }else if(sender==JTree.class){
                    //clear selection
                    selectionPanel.Clear();
                    
                    //fill category level tree
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Element element=(Element)nodes.item(i);
                        if(element.hasAttribute("category"))
                            Utilities.addLibraryModule(symbolTree,root,element.getAttribute("library"),element.getTextContent(),
                                                                         null,null);
                        else
                            Utilities.addLibraryModule(symbolTree,root,element.getAttribute("library"),null,
                                                                      element.getTextContent(),element.getAttribute("fullname"));
                           
                    }    
                }else{
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Element element = (Element)nodes.item(i);
                            Utilities.addLibraryModule(symbolTree, root, element.getAttribute("library"),
                                                          element.getAttribute("category"), element.getTextContent(),
                                                          element.getAttribute("fullname"));
                    }
                }
                                
                symbolTree.expandPath(new TreePath(root.getPath()));                
            } catch (ParserConfigurationException e) {
                e.printStackTrace(System.out);
            } catch (SAXException e) {
                e.printStackTrace(System.out);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            } catch (XPathExpressionException e) {
                e.printStackTrace(System.out);
                }finally{
                    symbolTree.addTreeSelectionListener(this);                    
                }
        }
        //***module xml!
        if (sender==Symbol.class) {
            selectionPanel.Clear();
        try{ 
            UnitContainer model= new SymbolContainer();
            model.parse(result);
            selectionPanel.getSelectionGrid().setModel(model);
        } catch (Exception ioe) {
            ioe.printStackTrace(System.out);
            return;
        }
        selectionPanel.buildSelectionGrid();
        selectionPanel.repaint(); 
        selectionPanel.revalidate();            
        }    
    }

    @Override
    public void onFinish(Class<?> clazz) {
        this.setCursor(Cursor.getDefaultCursor());
        this.enableControls();
    }

    @Override
    public void onError(String error) {
        onFinish(null);
        JOptionPane.showMessageDialog(this, error, "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }
    private void enableControls() {
        LoadButton.setEnabled(true);
        symbolTree.setEnabled(true);
        libraryCombo.setEnabled(true);
    }

    private void disableControls() {
        LoadButton.setEnabled(false);
        symbolTree.setEnabled(false);
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
                    new ReadUnitsLocal(this, Configuration.get().getSymbolsRoot().resolve(((AttachedItem)categoryNode.getUserObject()).getLibrary()).resolve(((AttachedItem)categoryNode.getUserObject()).getCategory()),"modules", ReadUnitsLocal.class);
                CommandExecutor.INSTANCE.addTask("ReadUnitsLocal", reader);
            } else {
                Command reader =
                    new ReadConnector(this, new RestParameterMap.ParameterBuilder("/symbols").addURI(((AttachedItem)categoryNode.getUserObject()).getLibrary()).addURI(((AttachedItem)categoryNode.getUserObject()).getCategory()).build(),
                                     ReadUnitsLocal.class);
                CommandExecutor.INSTANCE.addTask("ReadSymbols", reader);
            }            
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        
    }

    public static class Builder extends AbstractLoadDialog.Builder{        
        @Override
        public AbstractLoadDialog build() {
           return new SymbolLoadDialog(window,caption,enabled);
        }
    }
    }
