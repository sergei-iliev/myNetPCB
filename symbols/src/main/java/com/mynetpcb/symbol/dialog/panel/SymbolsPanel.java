package com.mynetpcb.symbol.dialog.panel;

import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ContainerListener;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.ShapeListener;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitListener;
import com.mynetpcb.core.capi.tree.TreeNodeData;
import com.mynetpcb.core.capi.tree.UnitTreeCellRenderer;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.symbol.component.SymbolComponent;

import java.awt.BorderLayout;

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

public class SymbolsPanel extends JPanel implements TreeSelectionListener, UnitListener, ShapeListener,
                                                    ContainerListener {

    private final SymbolComponent symbolComponent;

    private PropertyInspectorPanel symbolInspector;

    private JPanel basePanel;

    private JTree symbolsTree = new JTree();;

    private JScrollPane scrollPaneTree = new JScrollPane(symbolsTree);
    
    private JScrollPane scrollPaneInspector;

    public SymbolsPanel(SymbolComponent symbolComponent) {
        super(new BorderLayout());
        this.symbolComponent = symbolComponent;
        this.basePanel = new JPanel(new BorderLayout());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Symbols");
        symbolsTree.setShowsRootHandles(true);
        symbolsTree.setVisibleRowCount(10);
        symbolsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        symbolsTree.setEditable(false);
        symbolsTree.addTreeSelectionListener(this);
        symbolsTree.setModel(new DefaultTreeModel(root));
        symbolsTree.setCellRenderer(new UnitTreeCellRenderer(Utilities.loadImageIcon(this,"images/library.png"), Utilities.loadImageIcon(this,"images/chip_ico.png"),null));

        basePanel.add(scrollPaneTree, BorderLayout.NORTH);
        
        symbolInspector=new PropertyInspectorPanel(symbolComponent);
        scrollPaneInspector=new JScrollPane(symbolInspector);
        basePanel.add(scrollPaneInspector, BorderLayout.CENTER);
        this.add(basePanel, BorderLayout.CENTER);     

    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)symbolsTree.getLastSelectedPathComponent();

        if (node == null)
            return;


        if (node.getUserObject() instanceof TreeNodeData) {
            //***could be a click on Module or Chip
            TreeNodeData data = (TreeNodeData)node.getUserObject();
            if (node.getParent() != symbolsTree.getModel().getRoot()) { //click on chip
                TreeNodeData footprintData = (TreeNodeData)((DefaultMutableTreeNode)node.getParent()).getUserObject();
                if (symbolComponent.getModel().getUnit().getUUID().compareTo(footprintData.getUUID()) != 0) {
                    symbolComponent.getModel().getUnit().setViewportPositionValue(symbolComponent.getViewportWindow().getX(),
                                                                                symbolComponent.getViewportWindow().getY());
                    symbolComponent.getModel().setActiveUnit(footprintData.getUUID());
                }

                symbolComponent.getModel().getUnit().setSelected(false);
                symbolComponent.getModel().getUnit().setSelected(data.getUUID(), true);
                symbolComponent.componentResized(null);
                //***fire node selected
                symbolInspector.selectShapeEvent(new ShapeEvent(symbolComponent.getModel().getUnit().getShape(data.getUUID()),
                                                                                   ShapeEvent.SELECT_SHAPE));
                //***position on a symbol
                Box symbolRect=symbolComponent.getModel().getUnit().getShape(data.getUUID()).getBoundingShape();   
                symbolComponent.setViewportPosition(symbolRect.min.x, symbolRect.min.y);
            } else { //click on unit
                symbolComponent.getModel().getUnit().setViewportPositionValue(symbolComponent.getViewportWindow().getX(),symbolComponent.getViewportWindow().getY());                 

                symbolComponent.getModel().setActiveUnit(data.getUUID());
                symbolComponent.getModel().getUnit().setSelected(false);
                symbolComponent.componentResized(null);
                
                symbolInspector.selectUnitEvent(new UnitEvent(symbolComponent.getModel().getUnit(),
                                                                       UnitEvent.SELECT_UNIT));
            	this.symbolComponent.getViewportWindow().setX(this.symbolComponent.getModel().getUnit().getViewportPositionX());
            	this.symbolComponent.getViewportWindow().setY(this.symbolComponent.getModel().getUnit().getViewportPositionY());    			    	              
            }
        } else {
            //***Root select
            symbolInspector.selectContainerEvent(new ContainerEvent(null, ContainerEvent.SELECT_CONTAINER));
            return;
        }
        symbolComponent.Repaint();
    }

    @Override
    public void addUnitEvent(UnitEvent e) {
        this.symbolsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)symbolsTree.getModel().getRoot();
            //***create footprint node
            DefaultMutableTreeNode footprint =
                new DefaultMutableTreeNode(new TreeNodeData(e.getObject().getUUID(), e.getObject().getUnitName()));

            ((DefaultTreeModel)symbolsTree.getModel()).insertNodeInto(footprint, root, root.getChildCount());
        } finally {
            symbolsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void deleteUnitEvent(UnitEvent e) {
        this.symbolsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)symbolsTree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode footprintNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData footprintData = (TreeNodeData)footprintNode.getUserObject();
                if (e.getObject().getUUID().compareTo(footprintData.getUUID()) == 0) {
                    ((DefaultTreeModel)symbolsTree.getModel()).removeNodeFromParent(footprintNode);

                    break;
                }
            }
        } finally {
            symbolsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameUnitEvent(UnitEvent e) {
        this.symbolsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)symbolsTree.getModel().getRoot();
            UUID footprintUUID = e.getObject().getUUID();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode footprintNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)footprintNode.getUserObject();
                if (footprintUUID.compareTo(data.getUUID()) == 0) {
                    data.setName(e.getObject().getUnitName());
                    ((DefaultTreeModel)symbolsTree.getModel()).nodeChanged(footprintNode);
                    symbolsTree.setSelectionPath(new TreePath(footprintNode.getPath()));
                }
            }

        } finally {
            symbolsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void selectUnitEvent(UnitEvent e) {
        this.symbolsTree.removeTreeSelectionListener(this);

        //***get root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)symbolsTree.getModel().getRoot();
        //find footprint
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode footprintNode = (DefaultMutableTreeNode)root.getChildAt(i);
            TreeNodeData footprintData = (TreeNodeData)footprintNode.getUserObject();
            if (e.getObject().getUUID().compareTo(footprintData.getUUID()) == 0) {
                //select footprint
                symbolsTree.scrollPathToVisible(new TreePath(footprintNode.getPath()));
                symbolsTree.setSelectionPath(new TreePath(footprintNode.getPath()));
                break;
            }
        }
        symbolsTree.addTreeSelectionListener(this);
    }

    @Override
    public void propertyChangeEvent(UnitEvent e) {
    }

    @Override
    public void selectShapeEvent(ShapeEvent e) {
        this.symbolsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)symbolsTree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode symbolNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)symbolNode.getUserObject();

                if (e.getObject().getOwningUnit().getUUID().equals(data.getUUID())) {
                    for (int j = 0; i <= symbolNode.getChildCount(); j++) {
                        DefaultMutableTreeNode symbol = (DefaultMutableTreeNode)symbolNode.getChildAt(j);
                        TreeNodeData _data = (TreeNodeData)symbol.getUserObject();
                        if (_data.getUUID().equals(e.getObject().getUUID())) {
                            //***select symbol
                            symbolsTree.scrollPathToVisible(new TreePath(symbol.getPath()));
                            symbolsTree.setSelectionPath(new TreePath(symbol.getPath()));
                            break;
                        }

                    }
                }
            }
        } finally {
            symbolsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void deleteShapeEvent(ShapeEvent e) {
        this.symbolsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)symbolsTree.getModel().getRoot();
            UUID footprintUUID = e.getObject().getOwningUnit().getUUID();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode symbolNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)symbolNode.getUserObject();
                if (footprintUUID.compareTo(data.getUUID()) == 0) {
                    for (int j = 0; j < symbolNode.getChildCount(); j++) {
                        DefaultMutableTreeNode symbol = (DefaultMutableTreeNode)symbolNode.getChildAt(j);
                        TreeNodeData _data = (TreeNodeData)symbol.getUserObject();
                        if (_data.getUUID().equals(e.getObject().getUUID())) {
                            //delete child
                            ((DefaultTreeModel)symbolsTree.getModel()).removeNodeFromParent(symbol);
                            //select root
                            symbolsTree.setSelectionPath(new TreePath(symbolNode.getPath()));
                            //select unit
                            symbolInspector.selectUnitEvent(null);
                            break;
                        }
                    }
                }
            }
        } finally {
            symbolsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameShapeEvent(ShapeEvent e) {
    }

    @Override
    public void addShapeEvent(ShapeEvent e) {
        this.symbolsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)symbolsTree.getModel().getRoot();
            UUID footprintUUID = e.getObject().getOwningUnit().getUUID();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode symbolNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)symbolNode.getUserObject();
                if (footprintUUID.compareTo(data.getUUID()) == 0) {
                    DefaultMutableTreeNode chip =
                        new DefaultMutableTreeNode(new TreeNodeData(e.getObject().getUUID(), e.getObject().getDisplayName()));
                    ((DefaultTreeModel)symbolsTree.getModel()).insertNodeInto(chip, symbolNode,
                                                                                 symbolNode.getChildCount());

                    break;
                }
            }
        } finally {
            symbolsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void propertyChangeEvent(ShapeEvent e) {
    }

    @Override
    public void deleteContainerEvent(ContainerEvent e) {
        this.symbolsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)symbolsTree.getModel().getRoot();
            root.setUserObject("Modules");
            ((DefaultTreeModel)symbolsTree.getModel()).nodeChanged(root);
        } finally {
            symbolsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameContainerEvent(ContainerEvent e) {
        this.symbolsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)symbolsTree.getModel().getRoot();
            root.setUserObject(symbolComponent.getModel().getFormatedFileName());
            ((DefaultTreeModel)symbolsTree.getModel()).nodeChanged(root);
        } finally {
            symbolsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void selectContainerEvent(ContainerEvent e) {
        this.symbolsTree.removeTreeSelectionListener(this);
        try {
            symbolsTree.setSelectionPath(new TreePath(symbolsTree.getModel().getRoot()));
            symbolsTree.scrollPathToVisible(new TreePath(symbolsTree.getModel().getRoot()));
        } finally {
            symbolsTree.addTreeSelectionListener(this);
        }
    }
}

