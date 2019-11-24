package com.mynetpcb.board.dialog.panel;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.container.UnitContainer;
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
import com.mynetpcb.core.capi.tree.AttachedItem;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.unit.Footprint;

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

public class FootprintsPanel extends JPanel implements
                                                       MouseListener,
                                                       CommandListener,
                                                       ChangeListener,
                                                       MouseMotionListener,
                                                       ActionListener,
                                                       TreeSelectionListener,
                                                       TreeWillExpandListener{
    
    private SearchableComboBox searchableComboBox;

    private JTree footprintTree = new JTree();

    private DefaultMutableTreeNode root = new DefaultMutableTreeNode();

    private JScrollPane moduleListScroll = new JScrollPane();

    private final UnitSelectionPanel selectionPanel;

    private JScrollPane scrollPickerViewer = new JScrollPane();

    private final BoardComponent boardComponent;

    private boolean isPressedFlag;
    
    public FootprintsPanel(BoardComponent boardComponent) {
        this.boardComponent = boardComponent;


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        searchableComboBox = new SearchableComboBox(this);
        searchableComboBox.addActionListener(this);
        searchableComboBox.setMaximumSize(new Dimension(Short.MAX_VALUE, 24));
        this.add(searchableComboBox);   
        
        moduleListScroll.setPreferredSize(new Dimension(100, 200));
        moduleListScroll.setMaximumSize(new Dimension(Short.MAX_VALUE, 200));
        moduleListScroll.setMinimumSize(new Dimension(Short.MAX_VALUE, 200));
        moduleListScroll.getViewport().add(footprintTree);
        footprintTree.setShowsRootHandles(true);
        footprintTree.setVisibleRowCount(10);
        footprintTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        footprintTree.setEditable(false);
        footprintTree.addTreeSelectionListener(this);
        footprintTree.addTreeWillExpandListener(this);
        footprintTree.setModel(new DefaultTreeModel(root));
        footprintTree.setRootVisible(false);

        this.add(moduleListScroll);
        //***add module picker
        selectionPanel=new UnitSelectionPanel();
        selectionPanel.addMouseMotionListener(this); 
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.getSelectionGrid().setBackgroundColor(Color.WHITE);
        selectionPanel.getSelectionGrid().setTextColor(Color.BLACK);
        selectionPanel.setEnabled(true);
        selectionPanel.setToolTipText("Drag and Drop selected module.");
        selectionPanel.addMouseListener(this);
        
        scrollPickerViewer = new JScrollPane();
        scrollPickerViewer.getViewport().add(selectionPanel);
        scrollPickerViewer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPickerViewer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPickerViewer);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //***investigate the source
        if (e.getSource() != selectionPanel) {
            searchableComboBox.setSearchable(!searchableComboBox.isSearchable());
            CommandExecutor.INSTANCE.cancel();
            searchableComboBox.removeAllItems();
            root.removeAllChildren();
            ((DefaultTreeModel)footprintTree.getModel()).reload();
            selectionPanel.Clear();
            if (!searchableComboBox.isSearchable()) {
                //***Read Libraries
                if (!Configuration.get().isIsApplet()) {
                    Command reader =
                        new ReadRepositoryLocal(this, Configuration.get().getFootprintsRoot(),
                                                searchableComboBox.getClass());
                    CommandExecutor.INSTANCE.addTask("ReadRepositoryLocal", reader);
                } else {
                    Command reader =
                        new ReadConnector(this, new RestParameterMap.ParameterBuilder("/footprints").build(),
                                          searchableComboBox.getClass());
                    CommandExecutor.INSTANCE.addTask("ReadProjects", reader);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == selectionPanel){
            isPressedFlag=true;
            boardComponent.getDialogFrame().setButtonGroup(Mode.COMPONENT_MODE);
            boardComponent.setMode(Mode.COMPONENT_MODE);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == selectionPanel){
            if(null==boardComponent.getContainerCursor()){
               //ESCAPE global key press
               return; 
            }
        
           //***drop must be in viewable area
//           Rectangle2D rect=Utilities.getScaleRect(boardComponent.getContainerCursor().getBoundingShape().getBounds(), boardComponent.getModel().getUnit().getScalableTransformation().getCurrentTransformation());
//           if(boardComponent.getViewportWindow().contains(rect.getCenterX(),rect.getCenterY())){
//                try {
//                    Shape shape = boardComponent.getContainerCursor().clone();
//                    boardComponent.getModel().getUnit().add(shape);
//                    boardComponent.getModel().getUnit().setSelected(false);
//                    shape.setSelected(true);
//                     
//                    boardComponent.Repaint(); 
//
//                    boardComponent.getModel().getUnit().fireShapeEvent(new ShapeEvent(shape, ShapeEvent.SELECT_SHAPE));
//                    //register create with undo mgr
//                    boardComponent.getModel().getUnit().registerMemento(shape.getState(MementoType.CREATE_MEMENTO));
//                    //register placement 
//                    boardComponent.getModel().getUnit().registerMemento(shape.getState(MementoType.MOVE_MEMENTO));                     
//                } catch (CloneNotSupportedException f) {
//                    f.printStackTrace(System.out);
//                }
//               
//           }
          //***delete cursor and reset event handler
           boardComponent.setMode(Mode.COMPONENT_MODE); 
           boardComponent.Repaint();
        
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Implement this method
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Implement this method
    }

    @Override
    public void OnStart(Class<?> reciever) {
        if (reciever==JTree.class||reciever==SearchUnitLocal.class) {
            root.removeAllChildren();
            ((DefaultTreeModel)footprintTree.getModel()).reload();
        }
        DisabledGlassPane.block(boardComponent.getDialogFrame().getRootPane(),"Loading...");  
    }

    @Override
    public void OnRecive(String result, Class<?> reciever) {
        if (reciever==SearchableComboBox.class||reciever==JTree.class||reciever==ReadUnitsLocal.class||reciever==SearchUnitLocal.class) {
            //clear selection
            selectionPanel.Clear();
            //***parse xml
            this.footprintTree.removeTreeSelectionListener(this);
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
                                Utilities.addLibraryModule(footprintTree,root,element.getAttribute("library"),element.getTextContent(),
                                                                             null,null);
                            else
                                Utilities.addLibraryModule(footprintTree,root,element.getAttribute("library"),null,
                                                                          element.getTextContent(),element.getAttribute("fullname"));
                               
                        }    
                    }else if(reciever==ReadUnitsLocal.class||reciever==SearchUnitLocal.class){
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
                    }finally{
                        footprintTree.addTreeSelectionListener(this);                    
                    }              


        }
        //***module xml!
        if (reciever==Footprint.class) {
            selectionPanel.Clear();
            try {
                UnitContainer model= new FootprintContainer();
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
    public void OnFinish(Class<?> c) {
        DisabledGlassPane.unblock(boardComponent.getDialogFrame().getRootPane()); 
    }

    @Override
    public void OnError(String error) {
        DisabledGlassPane.unblock(boardComponent.getDialogFrame().getRootPane()); 
        JOptionPane.showMessageDialog(boardComponent.getDialogFrame().getParentFrame(), error, "Error",
                                      JOptionPane.ERROR_MESSAGE); 
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        if(index==1&&searchableComboBox.getItemCount()==0){
            if (!searchableComboBox.isSearchable()) {
                //***Read Libraries
                if (!Configuration.get().isIsApplet()) {
                    Command reader =
                        new ReadRepositoryLocal(this, Configuration.get().getFootprintsRoot(),
                                                searchableComboBox.getClass());
                    CommandExecutor.INSTANCE.addTask("ReadRepositoryLocal", reader);
                } else {
                    Command reader =
                        new ReadConnector(this, new RestParameterMap.ParameterBuilder("/footprints").build(),
                                          searchableComboBox.getClass());
                    CommandExecutor.INSTANCE.addTask("ReadProjects", reader);
                }
            }           
        }
       
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() == selectionPanel) {
            MouseEvent event=createMouseEvent(e);         
            if (selectionPanel.getSelectionGrid().getModel().getUnit() == null ||
                boardComponent.getModel().getUnit() == null) {
                return;
            }           
            if(isPressedFlag&&boardComponent.getEventMgr().getTargetEventHandle()==null){
                isPressedFlag=false;

//                PCBFootprint shape = BoardMgr.getInstance().createPCBFootprint((Footprint)selectionPanel.getSelectionGrid().getModel().getUnit(),boardComponent.getModel().getUnit().getActiveSide());
//                boardComponent.getModel().getUnit().setSelected(false);
//
//            //***set chip cursor
//                shape.Move(-1 * (int)shape.getBoundingShape().getBounds().getCenterX(),
//                      -1 * (int)shape.getBoundingShape().getBounds().getCenterY());
//                boardComponent.setContainerCursor(shape);
//                boardComponent.getEventMgr().setEventHandle("cursor", shape);
          }
            
                boardComponent.mouseMoved(event);
        
        }     

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Implement this method
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().getClass() == SearchableComboBox.class){
          if(!searchableComboBox.isSearchable()) {
            if (!Configuration.get().isIsApplet()) {
                Command reader =
                    new ReadCategoriesLocal(this, Configuration.get().getFootprintsRoot(),((String) searchableComboBox.getSelectedItem()),
                                             footprintTree.getClass());
                CommandExecutor.INSTANCE.addTask("ReadCategoriesLocal", reader);
            } else {
                Command reader =
                    new ReadConnector(this, new RestParameterMap.ParameterBuilder("/footprints").addURI((String)searchableComboBox.getSelectedItem()).build(),
                                      footprintTree.getClass());
                CommandExecutor.INSTANCE.addTask("ReadModules", reader);
                
            }
         }else{
              if(searchableComboBox.getSelectedItem()!=null&&((String)searchableComboBox.getSelectedItem()).length()>2){
                  if (!Configuration.get().isIsApplet()) {
                      Command reader =
                          new SearchUnitLocal(this,new FileNameLookup(new XMLTagContentLookup(Arrays.asList("//footprint/name","//footprint/value"),(String)searchableComboBox.getSelectedItem() ),(String)searchableComboBox.getSelectedItem()) ,Configuration.get().getFootprintsRoot().toString(),SearchUnitLocal.class);
                      CommandExecutor.INSTANCE.addTask("SearchUnitLocal", reader);
                  } else {

                      
                  }
              }
              
         }
        }         
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode)footprintTree.getLastSelectedPathComponent();

        if (node == null) {
            return;
        }
        
        if (null != node && node.isLeaf()) {
            AttachedItem attachedItem=(AttachedItem)node.getUserObject();            
            if (!Configuration.get().isIsApplet()) {
                Command reader =
                    new ReadUnitLocal(this, Configuration.get().getFootprintsRoot(),attachedItem.getLibrary(),(node.getLevel()==1?null:attachedItem.getCategory()),
                                      attachedItem.getFileName(), Footprint.class);
                CommandExecutor.INSTANCE.addTask("ReadUnitLocal", reader);
            } else {                
                Command reader =
                    new ReadConnector(this, new RestParameterMap.ParameterBuilder("/footprints").addURI(attachedItem.getLibrary()).addURI((node.getLevel()==1?"null":attachedItem.getCategory())).addURI(attachedItem.getFileName()).build(),
                                      Footprint.class);
                CommandExecutor.INSTANCE.addTask("ReadFootprint", reader);
            }
        }         
    
    }
    private final  MouseEvent createMouseEvent(MouseEvent e){          
        MouseEvent event=new MouseEvent(e.getComponent(),e.getID(),e.getWhen(),e.getModifiers(),
        (boardComponent.getWidth()+e.getX()-scrollPickerViewer.getHorizontalScrollBar().getValue()),                                        
        (boardComponent.getHeight()+e.getY()-(int)selectionPanel.getVisibleRect().getHeight()-scrollPickerViewer.getVerticalScrollBar().getValue()),                                                                            
                                    e.getClickCount(),false
                                    );      
        
        
        return event; 
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
                    new ReadUnitsLocal(this, Configuration.get().getFootprintsRoot().resolve(((AttachedItem)categoryNode.getUserObject()).getLibrary()).resolve(((AttachedItem)categoryNode.getUserObject()).getCategory()),"footprints", ReadUnitsLocal.class);
                CommandExecutor.INSTANCE.addTask("ReadUnitsLocal", reader);
            } else {
                Command reader =
                    new ReadConnector(this, new RestParameterMap.ParameterBuilder("/footprints").addURI(((AttachedItem)categoryNode.getUserObject()).getLibrary()).addURI(((AttachedItem)categoryNode.getUserObject()).getCategory()).build(),
                                     ReadUnitsLocal.class);
                CommandExecutor.INSTANCE.addTask("ReadFootprints", reader);
            }            
        } 
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

    }
}
