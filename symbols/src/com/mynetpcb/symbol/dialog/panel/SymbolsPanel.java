package com.mynetpcb.symbol.dialog.panel;


import com.mynetpcb.core.capi.TreeNodeData;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ContainerListener;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.ShapeListener;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitListener;
import com.mynetpcb.core.capi.tree.UnitTreeCellRenderer;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.component.SymbolComponent;

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


public class SymbolsPanel extends JPanel implements TreeSelectionListener, UnitListener, ShapeListener,
                                                    ContainerListener {

    private final SymbolComponent symbolComponent;

    private PropertyInspectorPanel symbolInspector;

    private JPanel basePanel;

    private JTree footprintsTree = new JTree();;

    private JScrollPane scrollPaneTree = new JScrollPane(footprintsTree);
    
    private JScrollPane scrollPaneInspector;

    public SymbolsPanel(SymbolComponent symbolComponent) {
        super(new BorderLayout());
        this.symbolComponent = symbolComponent;
        this.basePanel = new JPanel(new BorderLayout());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Symbols");
        footprintsTree.setShowsRootHandles(true);
        footprintsTree.setVisibleRowCount(10);
        footprintsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        footprintsTree.setEditable(false);
        footprintsTree.addTreeSelectionListener(this);
        footprintsTree.setModel(new DefaultTreeModel(root));
        footprintsTree.setCellRenderer(new UnitTreeCellRenderer(Utilities.loadImageIcon(this,"/com/mynetpcb/core/images/library.png"), Utilities.loadImageIcon(this,"/com/mynetpcb/core/images/chip_ico.png"),null));

        basePanel.add(scrollPaneTree, BorderLayout.NORTH);
        
        symbolInspector=new PropertyInspectorPanel(symbolComponent);
        scrollPaneInspector=new JScrollPane(symbolInspector);
        basePanel.add(scrollPaneInspector, BorderLayout.CENTER);
        this.add(basePanel, BorderLayout.CENTER);     

    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)footprintsTree.getLastSelectedPathComponent();

        if (node == null)
            return;


        if (node.getUserObject() instanceof TreeNodeData) {
            //***could be a click on Module or Chip
            TreeNodeData data = (TreeNodeData)node.getUserObject();
            if (node.getParent() != footprintsTree.getModel().getRoot()) { //click on chip
                TreeNodeData footprintData = (TreeNodeData)((DefaultMutableTreeNode)node.getParent()).getUserObject();
                if (symbolComponent.getModel().getUnit().getUUID().compareTo(footprintData.getUUID()) != 0) {
                    symbolComponent.getModel().getUnit().setScrollPositionValue(symbolComponent.getDialogFrame().getHorizontalScrollBar().getValue(),
                                                                                symbolComponent.getDialogFrame().getVerticalScrollBar().getValue());
                    symbolComponent.getModel().setActiveUnit(footprintData.getUUID());
                }

                symbolComponent.getModel().getUnit().setSelected(false);
                symbolComponent.getModel().getUnit().setSelected(data.getUUID(), true);
                symbolComponent.componentResized(null);
                //***fire node selected
                symbolInspector.selectShapeEvent(new ShapeEvent(symbolComponent.getModel().getUnit().getShape(data.getUUID()),
                                                                                   ShapeEvent.SELECT_SHAPE));
                //***position on a symbol
                Rectangle symbolRect = symbolComponent.getModel().getUnit().getShape(data.getUUID()).getBoundingShape().getBounds();
                symbolComponent.setScrollPosition(symbolRect.x, symbolRect.y);
            } else { //click on unit
                symbolComponent.getModel().getUnit().setScrollPositionValue(symbolComponent.getViewportWindow().x,
                                                                            symbolComponent.getViewportWindow().y);
                symbolComponent.getModel().setActiveUnit(data.getUUID());
                symbolComponent.getModel().getUnit().setSelected(false);
                symbolComponent.componentResized(null);
                
                symbolInspector.selectUnitEvent(new UnitEvent(symbolComponent.getModel().getUnit(),
                                                                       UnitEvent.SELECT_UNIT));
                symbolComponent.getDialogFrame().getHorizontalScrollBar().setValue(symbolComponent.getModel().getUnit().getScrollPositionXValue());
                symbolComponent.getDialogFrame().getVerticalScrollBar().setValue(symbolComponent.getModel().getUnit().getScrollPositionYValue());
                symbolComponent.Repaint();
            }
        } else {
            //***Root select
            symbolInspector.selectContainerEvent(new ContainerEvent(null, ContainerEvent.SELECT_CONTAINER));
            return;
        }

    }

    @Override
    public void addUnitEvent(UnitEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            //***create footprint node
            DefaultMutableTreeNode footprint =
                new DefaultMutableTreeNode(new TreeNodeData(e.getObject().getUUID(), e.getObject().getUnitName()));

            ((DefaultTreeModel)footprintsTree.getModel()).insertNodeInto(footprint, root, root.getChildCount());
        } finally {
            footprintsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void deleteUnitEvent(UnitEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode footprintNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData footprintData = (TreeNodeData)footprintNode.getUserObject();
                if (e.getObject().getUUID().compareTo(footprintData.getUUID()) == 0) {
                    ((DefaultTreeModel)footprintsTree.getModel()).removeNodeFromParent(footprintNode);

                    break;
                }
            }
        } finally {
            footprintsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameUnitEvent(UnitEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            UUID footprintUUID = e.getObject().getUUID();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode footprintNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)footprintNode.getUserObject();
                if (footprintUUID.compareTo(data.getUUID()) == 0) {
                    data.setName(e.getObject().getUnitName());
                    ((DefaultTreeModel)footprintsTree.getModel()).nodeChanged(footprintNode);
                    footprintsTree.setSelectionPath(new TreePath(footprintNode.getPath()));
                }
            }

        } finally {
            footprintsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void selectUnitEvent(UnitEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);

        //***get root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
        //find footprint
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode footprintNode = (DefaultMutableTreeNode)root.getChildAt(i);
            TreeNodeData footprintData = (TreeNodeData)footprintNode.getUserObject();
            if (e.getObject().getUUID().compareTo(footprintData.getUUID()) == 0) {
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
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode footprintNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)footprintNode.getUserObject();

                if (e.getObject().getOwningUnit().getUUID().equals(data.getUUID())) {
                    for (int j = 0; i <= footprintNode.getChildCount(); j++) {
                        DefaultMutableTreeNode symbol = (DefaultMutableTreeNode)footprintNode.getChildAt(j);
                        TreeNodeData _data = (TreeNodeData)symbol.getUserObject();
                        if (_data.getUUID().equals(e.getObject().getUUID())) {
                            //***select symbol
                            footprintsTree.scrollPathToVisible(new TreePath(symbol.getPath()));
                            footprintsTree.setSelectionPath(new TreePath(symbol.getPath()));
                            break;
                        }

                    }
                }
            }
        } finally {
            footprintsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void deleteShapeEvent(ShapeEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            UUID footprintUUID = e.getObject().getOwningUnit().getUUID();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode footprintNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)footprintNode.getUserObject();
                if (footprintUUID.compareTo(data.getUUID()) == 0) {
                    for (int j = 0; j < footprintNode.getChildCount(); j++) {
                        DefaultMutableTreeNode symbol = (DefaultMutableTreeNode)footprintNode.getChildAt(j);
                        TreeNodeData _data = (TreeNodeData)symbol.getUserObject();
                        if (_data.getUUID().equals(e.getObject().getUUID())) {
                            //delete child
                            ((DefaultTreeModel)footprintsTree.getModel()).removeNodeFromParent(symbol);
                            //select root
                            footprintsTree.setSelectionPath(new TreePath(footprintNode.getPath()));
                            //select unit
                            symbolInspector.selectUnitEvent(null);
                            break;
                        }
                    }
                }
            }
        } finally {
            footprintsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameShapeEvent(ShapeEvent e) {
    }

    @Override
    public void addShapeEvent(ShapeEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            UUID footprintUUID = e.getObject().getOwningUnit().getUUID();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode footprintNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)footprintNode.getUserObject();
                if (footprintUUID.compareTo(data.getUUID()) == 0) {
                    DefaultMutableTreeNode chip =
                        new DefaultMutableTreeNode(new TreeNodeData(e.getObject().getUUID(), e.getObject().getDisplayName()));
                    ((DefaultTreeModel)footprintsTree.getModel()).insertNodeInto(chip, footprintNode,
                                                                                 footprintNode.getChildCount());

                    break;
                }
            }
        } finally {
            footprintsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void propertyChangeEvent(ShapeEvent e) {
    }

    @Override
    public void deleteContainerEvent(ContainerEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            root.setUserObject("Modules");
            ((DefaultTreeModel)footprintsTree.getModel()).nodeChanged(root);
        } finally {
            footprintsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameContainerEvent(ContainerEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)footprintsTree.getModel().getRoot();
            root.setUserObject(symbolComponent.getModel().getFormatedFileName());
            ((DefaultTreeModel)footprintsTree.getModel()).nodeChanged(root);
        } finally {
            footprintsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void selectContainerEvent(ContainerEvent e) {
        this.footprintsTree.removeTreeSelectionListener(this);
        try {
            footprintsTree.setSelectionPath(new TreePath(footprintsTree.getModel().getRoot()));
            footprintsTree.scrollPathToVisible(new TreePath(footprintsTree.getModel().getRoot()));
        } finally {
            footprintsTree.addTreeSelectionListener(this);
        }
    }

}
