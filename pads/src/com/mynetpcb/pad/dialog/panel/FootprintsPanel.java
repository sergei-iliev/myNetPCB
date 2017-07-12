package com.mynetpcb.pad.dialog.panel;


import com.mynetpcb.core.capi.TreeNodeData;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ContainerListener;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.ShapeListener;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitListener;
import com.mynetpcb.core.capi.tree.UnitTreeCellRenderer;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.component.FootprintComponent;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import java.util.UUID;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


public class FootprintsPanel extends JPanel  implements TreeSelectionListener,UnitListener,ShapeListener,ContainerListener{
    
    private final FootprintComponent footprintComponent;
    
    private PropertyInspectorPanel footprintInspector;
    
    private JPanel basePanel;
    
    private JTree footprintsTree;
    
    private JScrollPane scrollPaneTree ;
    
    private JScrollPane scrollPaneInspector;
    
    public FootprintsPanel(FootprintComponent footprintComponent) {
        super(new BorderLayout());
        this.footprintComponent=footprintComponent;
        this.footprintsTree = new JTree();
        this.scrollPaneTree = new JScrollPane(footprintsTree);
        this.basePanel =new JPanel(new BorderLayout());
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Footprints");
        footprintsTree.setShowsRootHandles(true);
        footprintsTree.setVisibleRowCount(10);
        footprintsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        footprintsTree.setEditable(false);
        footprintsTree.addTreeSelectionListener(this);
        footprintsTree.setModel(new DefaultTreeModel(root));                                                  
        footprintsTree.setCellRenderer(new UnitTreeCellRenderer(Utilities.loadImageIcon(this,"/com/mynetpcb/core/images/library.png"), Utilities.loadImageIcon(this,"/com/mynetpcb/core/images/chip_ico.png"),null));

        basePanel.add(scrollPaneTree, BorderLayout.NORTH);
        
        footprintInspector=new PropertyInspectorPanel(footprintComponent);
        scrollPaneInspector=new JScrollPane(footprintInspector);
        basePanel.add(scrollPaneInspector, BorderLayout.CENTER);
        this.add(basePanel, BorderLayout.CENTER);
    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)footprintsTree.getLastSelectedPathComponent();

        if (node == null) return;


        if(node.getUserObject() instanceof TreeNodeData){
            //***could be a click on Module or Chip
            TreeNodeData data=(TreeNodeData)node.getUserObject();
            if(node.getParent()!=footprintsTree.getModel().getRoot()){   //click on chip
              TreeNodeData footprintData=(TreeNodeData)((DefaultMutableTreeNode)node.getParent()).getUserObject();
              if(footprintComponent.getModel().getUnit().getUUID().compareTo(footprintData.getUUID())!=0){                  
                  footprintComponent.getModel().getUnit().setScrollPositionValue(footprintComponent.getDialogFrame().getHorizontalScrollBar().getValue(),footprintComponent.getDialogFrame().getVerticalScrollBar().getValue());                                                                                     
                  footprintComponent.getModel().setActiveUnit(footprintData.getUUID());
              }   
              
              footprintComponent.getModel().getUnit().setSelected(false);
              footprintComponent.getModel().getUnit().setSelected(data.getUUID(),true);
              footprintComponent.componentResized(null);
                    //***fire node selected             
               footprintInspector.selectShapeEvent(new ShapeEvent(footprintComponent.getModel().getUnit().getShape(data.getUUID()), ShapeEvent.SELECT_SHAPE));              
                //***position on a symbol
                  Rectangle symbolRect=footprintComponent.getModel().getUnit().getShape(data.getUUID()).getBoundingShape().getBounds();   
                  footprintComponent.setScrollPosition(symbolRect.x, symbolRect.y);                                                                                                                                          
            }else{           //click on unit
                footprintComponent.getModel().getUnit().setScrollPositionValue(footprintComponent.getViewportWindow().x,footprintComponent.getViewportWindow().y);                 
                footprintComponent.getModel().setActiveUnit(data.getUUID());
                footprintComponent.getModel().getUnit().setSelected(false);    
                footprintComponent.componentResized(null);
               
                footprintInspector.selectUnitEvent(new UnitEvent(footprintComponent.getModel().getUnit(), UnitEvent.SELECT_UNIT));                     
                footprintComponent.getDialogFrame().getHorizontalScrollBar().setValue(footprintComponent.getModel().getUnit().getScrollPositionXValue());
                footprintComponent.getDialogFrame().getVerticalScrollBar().setValue(footprintComponent.getModel().getUnit().getScrollPositionYValue());                           
                footprintComponent.Repaint();
            }
        }else{
            //***Root select
            //***fire root node selected            
           footprintInspector.selectContainerEvent(new ContainerEvent(null, ContainerEvent.SELECT_CONTAINER)); 
            return;
        }  
    
    }

    @Override
    public void addUnitEvent(UnitEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try{
        //***get root
          DefaultMutableTreeNode root=(DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
        //***create footprint node
          DefaultMutableTreeNode footprint = new DefaultMutableTreeNode(new TreeNodeData(e.getObject().getUUID(),e.getObject().getUnitName()));
        
          ((DefaultTreeModel)footprintsTree.getModel()).insertNodeInto(footprint,root,root.getChildCount()); 
        }finally{
          footprintsTree.addTreeSelectionListener(this);
        } 
    }

    @Override
    public void deleteUnitEvent(UnitEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try{
        //***get root
        DefaultMutableTreeNode root=(DefaultMutableTreeNode)footprintsTree.getModel().getRoot();            
        for(int i=0;i<root.getChildCount();i++){
          DefaultMutableTreeNode footprintNode=(DefaultMutableTreeNode)root.getChildAt(i);
          TreeNodeData footprintData = (TreeNodeData)footprintNode.getUserObject();
            if(e.getObject().getUUID().compareTo(footprintData.getUUID())==0){               
                ((DefaultTreeModel)footprintsTree.getModel()).removeNodeFromParent(footprintNode); 
                            
              break;
            }
        }
        }finally{
          footprintsTree.addTreeSelectionListener(this);
        }    
    }

    @Override
    public void renameUnitEvent(UnitEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try{     
            DefaultMutableTreeNode root=(DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            UUID footprintUUID=e.getObject().getUUID();
            for(int i=0;i<root.getChildCount();i++){
              DefaultMutableTreeNode footprintNode=(DefaultMutableTreeNode)root.getChildAt(i);
              TreeNodeData data = (TreeNodeData)footprintNode.getUserObject();
                if(footprintUUID.compareTo(data.getUUID())==0){
                    data.setName(e.getObject().getUnitName());
                    ((DefaultTreeModel)footprintsTree.getModel()).nodeChanged(footprintNode); 
                    footprintsTree.setSelectionPath(new TreePath(footprintNode.getPath()));  
                }
            }             
            
        }finally{
          footprintsTree.addTreeSelectionListener(this);
        } 
    }

    @Override
    public void selectUnitEvent(UnitEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);

        //***get root
          DefaultMutableTreeNode root=(DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
        //find footprint
        for(int i=0;i<root.getChildCount();i++){
            DefaultMutableTreeNode footprintNode=(DefaultMutableTreeNode)root.getChildAt(i);
            TreeNodeData footprintData = (TreeNodeData)footprintNode.getUserObject();
              if(e.getObject().getUUID().compareTo(footprintData.getUUID())==0){               
               //select footprint              
               footprintsTree.scrollPathToVisible(new TreePath(footprintNode.getPath()));
               footprintsTree.setSelectionPath(new TreePath(footprintNode.getPath()));                              
                break;
              }
        }    
          footprintsTree.addTreeSelectionListener(this);   
    }

    @Override
    public void propertyChangeEvent(UnitEvent e) {
    }

    @Override
    public void selectShapeEvent(ShapeEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try{                         
            //***get root
           DefaultMutableTreeNode root=(DefaultMutableTreeNode)footprintsTree.getModel().getRoot();         
           for(int i=0;i<root.getChildCount();i++){
              DefaultMutableTreeNode footprintNode=(DefaultMutableTreeNode)root.getChildAt(i);
              TreeNodeData data = (TreeNodeData)footprintNode.getUserObject();
            
              if((e.getObject()).getOwningUnit().getUUID().equals(data.getUUID())){
                for(int j=0;i<=footprintNode.getChildCount();j++){
                    DefaultMutableTreeNode symbol=(DefaultMutableTreeNode)footprintNode.getChildAt(j); 
                    TreeNodeData _data=(TreeNodeData)symbol.getUserObject();
                    if(_data.getUUID().equals(e.getObject().getUUID())){
                        //***select symbol
                        footprintsTree.scrollPathToVisible(new TreePath(symbol.getPath()));
                        footprintsTree.setSelectionPath(new TreePath(symbol.getPath()));                              
                        break;                                                
                    }
                    
                }    
            }
          }
        }finally{
          footprintsTree.addTreeSelectionListener(this);
        }                       
    }

    @Override
    public void deleteShapeEvent(ShapeEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try{  
            DefaultMutableTreeNode root=(DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            UUID footprintUUID=e.getObject().getOwningUnit().getUUID();
            for(int i=0;i<root.getChildCount();i++){
              DefaultMutableTreeNode footprintNode=(DefaultMutableTreeNode)root.getChildAt(i);
              TreeNodeData data = (TreeNodeData)footprintNode.getUserObject();
                if(footprintUUID.compareTo(data.getUUID())==0){
                    for(int j=0;j<footprintNode.getChildCount();j++){                        
                        DefaultMutableTreeNode symbol=(DefaultMutableTreeNode)footprintNode.getChildAt(j); 
                        TreeNodeData _data=(TreeNodeData)symbol.getUserObject();
                        if(_data.getUUID().equals(e.getObject().getUUID())){      
                            //delete child
                           ((DefaultTreeModel)footprintsTree.getModel()).removeNodeFromParent(symbol); 
                           //select root
                            footprintsTree.setSelectionPath(new TreePath(footprintNode.getPath())); 
                            //select unit
                            footprintInspector.selectUnitEvent(null);
                        break;
                        }
                    }    
                }
            }  
        }finally{
          footprintsTree.addTreeSelectionListener(this);
        } 
    }

    @Override
    public void renameShapeEvent(ShapeEvent e) {
    }

    @Override
    public void addShapeEvent(ShapeEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try{
            DefaultMutableTreeNode root=(DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            UUID footprintUUID=e.getObject().getOwningUnit().getUUID();
            for(int i=0;i<root.getChildCount();i++){
              DefaultMutableTreeNode footprintNode=(DefaultMutableTreeNode)root.getChildAt(i);
              TreeNodeData data = (TreeNodeData)footprintNode.getUserObject();
                if(footprintUUID.compareTo(data.getUUID())==0){               
                    DefaultMutableTreeNode chip = new DefaultMutableTreeNode(new TreeNodeData(e.getObject().getUUID(),e.getObject().getDisplayName()));
                    ((DefaultTreeModel)footprintsTree.getModel()).insertNodeInto(chip,footprintNode,footprintNode.getChildCount());                      
                                
                  break;
                }
            } 
        }finally{
          footprintsTree.addTreeSelectionListener(this);
        } 
    }

    @Override
    public void propertyChangeEvent(ShapeEvent e) {
    }

    @Override
    public void deleteContainerEvent(ContainerEvent e){
        this.footprintsTree.removeTreeSelectionListener(this);
        try{     
            DefaultMutableTreeNode root=(DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            root.setUserObject("Modules");                
            ((DefaultTreeModel)footprintsTree.getModel()).nodeChanged(root);               
        }finally{
          footprintsTree.addTreeSelectionListener(this);
        }
    }
    
    @Override
    public void renameContainerEvent(ContainerEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try{     
            DefaultMutableTreeNode root=(DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            root.setUserObject(footprintComponent.getModel().getFormatedFileName());                
            ((DefaultTreeModel)footprintsTree.getModel()).nodeChanged(root);               
        }finally{
          footprintsTree.addTreeSelectionListener(this);
        }    
    }

    @Override
    public void selectContainerEvent(ContainerEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try{          
           footprintsTree.setSelectionPath(new TreePath(footprintsTree.getModel().getRoot())); 
           footprintsTree.scrollPathToVisible(new TreePath(footprintsTree.getModel().getRoot()));   
        }finally{
          footprintsTree.addTreeSelectionListener(this);
        }    
    }

}

