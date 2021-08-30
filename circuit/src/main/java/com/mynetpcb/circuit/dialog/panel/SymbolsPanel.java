package com.mynetpcb.circuit.dialog.panel;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.unit.CircuitMgr;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.gui.UnitSelectionPanel;
import com.mynetpcb.core.capi.gui.panel.DisabledGlassPane;
import com.mynetpcb.core.capi.gui.searchcombo.SearchableComboBox;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.ReadCategoriesLocal;
import com.mynetpcb.core.capi.io.ReadRepositoryLocal;
import com.mynetpcb.core.capi.io.ReadUnitLocal;
import com.mynetpcb.core.capi.io.ReadUnitsLocal;
import com.mynetpcb.core.capi.io.SearchUnitLocal;
import com.mynetpcb.core.capi.io.remote.ReadConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;
import com.mynetpcb.core.capi.io.search.FileNameLookup;
import com.mynetpcb.core.capi.io.search.XMLTagContentLookup;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.tree.AttachedItem;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.io.IOException;

import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

public class SymbolsPanel extends JPanel implements
                                                       MouseListener,
                                                       CommandListener,
                                                       ChangeListener,
                                                       MouseMotionListener,
                                                       ActionListener,
                                                       TreeSelectionListener,
                                                       TreeWillExpandListener
{

    private SearchableComboBox searchableComboBox;

    private JTree symbolTree = new JTree();

    private DefaultMutableTreeNode root = new DefaultMutableTreeNode();

    private JScrollPane moduleListScroll = new JScrollPane();

    private final UnitSelectionPanel selectionPanel;

    private JScrollPane scrollPickerViewer = new JScrollPane();

    private final CircuitComponent circuitComponent;

    private boolean isPressedFlag;
    
    public SymbolsPanel(CircuitComponent circuitComponent) {
        this.circuitComponent = circuitComponent;


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        searchableComboBox = new SearchableComboBox(this);
        searchableComboBox.addActionListener(this);        
        searchableComboBox.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));
        this.add(searchableComboBox);   
        
        moduleListScroll.setPreferredSize(new Dimension(100, 200));
        moduleListScroll.setMaximumSize(new Dimension(Short.MAX_VALUE, 200));
        moduleListScroll.setMinimumSize(new Dimension(Short.MAX_VALUE, 200));
        moduleListScroll.getViewport().add(symbolTree);
        symbolTree.setShowsRootHandles(true);
        symbolTree.setVisibleRowCount(10);
        symbolTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        symbolTree.setEditable(false);
        symbolTree.addTreeSelectionListener(this);
        symbolTree.addTreeWillExpandListener(this);
        symbolTree.setModel(new DefaultTreeModel(root));
        symbolTree.setRootVisible(false);

        this.add(moduleListScroll);
        //***add module picker
        selectionPanel=new UnitSelectionPanel();
        selectionPanel.addMouseMotionListener(this); 
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.setEnabled(true);
        selectionPanel.getSelectionGrid().setBackgroundColor(Color.WHITE);
        selectionPanel.getSelectionGrid().setTextColor(Color.BLACK);
        selectionPanel.getSelectionGrid().setScaleRatio(1.2);
        selectionPanel.getSelectionGrid().setScaleFactor(0);
        selectionPanel.getSelectionGrid().setMinScaleFactor(0);
        selectionPanel.getSelectionGrid().setMaxScaleFactor(10);
        selectionPanel.setToolTipText("Drag and Drop selected module.");
        selectionPanel.addMouseListener(this);
        
        scrollPickerViewer = new JScrollPane();
        scrollPickerViewer.getViewport().add(selectionPanel);
        scrollPickerViewer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPickerViewer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPickerViewer);
    }

    public void mouseClicked(MouseEvent e) {
        //***investigate the source
        if (e.getSource() != selectionPanel) {
            searchableComboBox.setSearchable(!searchableComboBox.isSearchable());
            CommandExecutor.INSTANCE.cancel();
            searchableComboBox.removeAllItems();
            root.removeAllChildren();
            ((DefaultTreeModel)symbolTree.getModel()).reload();
            selectionPanel.Clear();
            if (!searchableComboBox.isSearchable()) {
                //***Read Libraries
                if (!Configuration.get().isIsApplet()) {
                    Command reader =
                        new ReadRepositoryLocal(this, Configuration.get().getSymbolsRoot(),
                                                searchableComboBox.getClass());
                    CommandExecutor.INSTANCE.addTask("ReadRepositoryLocal", reader);
                } else {
                    Command reader =
                        new ReadConnector(this, new RestParameterMap.ParameterBuilder("/symbols").build(),
                                          searchableComboBox.getClass());
                    CommandExecutor.INSTANCE.addTask("ReadProjects", reader);
                }
            }
        }
    }


    public void mousePressed(MouseEvent e) {
        if (e.getSource() == selectionPanel){
            isPressedFlag=true;
            circuitComponent.getDialogFrame().setButtonGroup(Mode.COMPONENT_MODE);
            circuitComponent.setMode(Mode.COMPONENT_MODE);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == selectionPanel){
            if(null==circuitComponent.getContainerCursor()){
               //ESCAPE global key press
               return; 
            }
        
           //***drop must be in viewable area
           
            Box rect = circuitComponent.getContainerCursor().getBoundingShape();
            rect.scale( circuitComponent.getModel().getUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
           
            if(rect.intersects(circuitComponent.getViewportWindow())){
                 try {
                    Shape shape = circuitComponent.getContainerCursor().clone();
                    circuitComponent.getModel().getUnit().add(shape);
                    circuitComponent.getModel().getUnit().setSelected(false);
                    shape.setSelected(true);
                    shape.alignToGrid(circuitComponent.getParameter("snaptogrid",Boolean.class,Boolean.FALSE)); 
                     
                    circuitComponent.Repaint(); 

                    circuitComponent.getModel().getUnit().fireShapeEvent(new ShapeEvent(shape, ShapeEvent.SELECT_SHAPE));
                    //register create with undo mgr
                    circuitComponent.getModel().getUnit().registerMemento(shape.getState(MementoType.CREATE_MEMENTO));
                    //register placement 
                    circuitComponent.getModel().getUnit().registerMemento(shape.getState(MementoType.MOVE_MEMENTO));                     
                } catch (CloneNotSupportedException f) {
                    f.printStackTrace(System.out);
                }
               
           }
          //***delete cursor and reset event handler
           circuitComponent.setMode(Mode.COMPONENT_MODE); 
           circuitComponent.Repaint();
        
        }
    }
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() == selectionPanel) {
            MouseEvent event=createMouseEvent(e);         
            if (selectionPanel.getSelectionGrid().getModel().getUnit() == null ||
                circuitComponent.getModel().getUnit() == null) {
                return;
            }           
            if(isPressedFlag&&circuitComponent.getEventMgr().getTargetEventHandle()==null){
                isPressedFlag=false;

                SCHSymbol shape = CircuitMgr.getInstance().createSCHSymbol((Symbol)selectionPanel.getSelectionGrid().getModel().getUnit());

                circuitComponent.getModel().getUnit().setSelected(false);

            //***set chip cursor
                shape.move(-1 * shape.getBoundingShape().getCenter().x,
                      -1 * shape.getBoundingShape().getCenter().y);
                circuitComponent.setContainerCursor(shape);
                circuitComponent.getEventMgr().setEventHandle("cursor", shape);
          }
            
                circuitComponent.mouseMoved(event);
        
        }     
      
    }

    public void mouseMoved(MouseEvent e) {
        
    }
    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    @Override
    public void onStart(Class reciever) {
        if (reciever==JTree.class||reciever==SearchUnitLocal.class) {
            root.removeAllChildren();
            ((DefaultTreeModel)symbolTree.getModel()).reload();
        }
        DisabledGlassPane.block(circuitComponent.getDialogFrame().getRootPane(),"Loading...");      
    }
    @Override
    public void onRecive(String result,  Class reciever) {
        if (reciever==SearchableComboBox.class||reciever==JTree.class||reciever==ReadUnitsLocal.class||reciever==SearchUnitLocal.class) {
            //clear selection
            selectionPanel.Clear();
            //***parse xml
            this.symbolTree.removeTreeSelectionListener(this);
            try {
                Document document = Utilities.buildDocument(result);
                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();
                XPathExpression expr;  

                    expr = xpath.compile("//name");
                    NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
                    if (reciever==SearchableComboBox.class) {                
                        for (int i = 0; i < nodes.getLength(); i++) {
                            Node node=nodes.item(i);
                            searchableComboBox.addItem(node.getTextContent());                     
                        }    
                    }else if(reciever==JTree.class){
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
                    }else if(reciever==ReadUnitsLocal.class||reciever==SearchUnitLocal.class){
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
        if (reciever==Symbol.class) {
            selectionPanel.Clear();
            try {
                UnitContainer model= new SymbolContainer();
                model.parse(result);
                selectionPanel.getSelectionGrid().setModel(model);
            } catch (Exception e) {
                e.printStackTrace(System.out);            
                return;
            }                    
            selectionPanel.buildSelectionGrid();
            selectionPanel.repaint(); 
            selectionPanel.revalidate();     
        }
    }
    @Override
    public void onFinish(Class receiver) {
        DisabledGlassPane.unblock(circuitComponent.getDialogFrame().getRootPane());  
    }
    @Override
    public void onError(String error) {
        DisabledGlassPane.unblock(circuitComponent.getDialogFrame().getRootPane()); 
        JOptionPane.showMessageDialog(circuitComponent.getDialogFrame().getParentFrame(), error, "Error",
                                      JOptionPane.ERROR_MESSAGE);       
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().getClass() == SearchableComboBox.class){
          if(!searchableComboBox.isSearchable()) {
            if (!Configuration.get().isIsApplet()) {
                Command reader =
                    new ReadCategoriesLocal(this, Configuration.get().getSymbolsRoot(),((String) searchableComboBox.getSelectedItem()),
                                             symbolTree.getClass());
                CommandExecutor.INSTANCE.addTask("ReadCategoriesLocal", reader);
            } else {
                Command reader =
                    new ReadConnector(this, new RestParameterMap.ParameterBuilder("/symbols").addURI((String)searchableComboBox.getSelectedItem()).build(),
                                      symbolTree.getClass());
                CommandExecutor.INSTANCE.addTask("ReadModules", reader);
                
            }
         }else{
              if(searchableComboBox.getSelectedItem()!=null&&((String)searchableComboBox.getSelectedItem()).length()>2){
                  if (!Configuration.get().isIsApplet()) {
                      Command reader =
                          new SearchUnitLocal(this,new FileNameLookup(new XMLTagContentLookup(Arrays.asList("//module/name","//module/unit"),(String)searchableComboBox.getSelectedItem() ),(String)searchableComboBox.getSelectedItem()) ,Configuration.get().getSymbolsRoot().toString(),SearchUnitLocal.class);
                      CommandExecutor.INSTANCE.addTask("SearchUnitLocal", reader);
                  } else {

                      
                  }
              }
              
         }
        }  
    }

    public void valueChanged(TreeSelectionEvent e) {
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

    
    //***need to create an event that is shifted in regard to Circuit Component origin
    private final  MouseEvent createMouseEvent(MouseEvent e){          
        MouseEvent event=new MouseEvent(e.getComponent(),e.getID(),e.getWhen(),e.getModifiers(),
        (circuitComponent.getWidth()+e.getX()-scrollPickerViewer.getHorizontalScrollBar().getValue()),                                        
        (circuitComponent.getHeight()+e.getY()-(int)selectionPanel.getVisibleRect().getHeight()-scrollPickerViewer.getVerticalScrollBar().getValue()),                                                                            
                                    e.getClickCount(),false
                                    );      
        
        
        return event; 
    }
    
    //***Component tab is focused
    public void stateChanged(ChangeEvent e) {
        JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        if(index==1&&searchableComboBox.getItemCount()==0){
            if (!searchableComboBox.isSearchable()) {
                //***Read Libraries
                if (!Configuration.get().isIsApplet()) {
                    Command reader =
                        new ReadRepositoryLocal(this, Configuration.get().getSymbolsRoot(),
                                                searchableComboBox.getClass());
                    CommandExecutor.INSTANCE.addTask("ReadRepositoryLocal", reader);
                } else {
                    Command reader =
                        new ReadConnector(this, new RestParameterMap.ParameterBuilder("/symbols").build(),
                                          searchableComboBox.getClass());
                    CommandExecutor.INSTANCE.addTask("ReadProjects", reader);
                }
            }           
        }
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
}

