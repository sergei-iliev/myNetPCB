package com.mynetpcb.board.dialog.panel;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ContainerListener;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.ShapeListener;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitListener;
import com.mynetpcb.core.capi.tree.TreeDragDropHandler;
import com.mynetpcb.core.capi.tree.TreeNodeData;
import com.mynetpcb.core.capi.tree.UnitTreeCellRenderer;
import com.mynetpcb.core.capi.tree.UnitTreeDragDropListener;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import java.util.UUID;

import javax.swing.DropMode;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class BoardsPanel extends JPanel implements TreeSelectionListener, UnitListener, ShapeListener,
                                                     ContainerListener,UnitTreeDragDropListener {
    
    private final BoardComponent boardComponent;
    
    private PropertyInspectorPanel boardInspector;
    
    private JTree boardsTree = new JTree();
    
    private JPanel basePanel = new JPanel(new BorderLayout());
    
    private JScrollPane scrollPaneTree = new JScrollPane(boardsTree);

    private JScrollPane scrollPaneInspector;
    
    public BoardsPanel(BoardComponent boardComponent) {
        super(new BorderLayout());
        this.boardComponent = boardComponent;
        this.setPreferredSize(new Dimension(200, 200));
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
        boardsTree.setDragEnabled(true);
        boardsTree.setDropMode(DropMode.ON_OR_INSERT);
        boardsTree.setTransferHandler(new TreeDragDropHandler(this));
        boardsTree.setShowsRootHandles(true);
        boardsTree.setVisibleRowCount(10);
        boardsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        boardsTree.setEditable(false);
        boardsTree.addTreeSelectionListener(this);
        boardsTree.setModel(new DefaultTreeModel(root));
        boardsTree.setCellRenderer(new UnitTreeCellRenderer(Utilities.loadImageIcon(this,
                                                                                      "/com/mynetpcb/core/images/library.png"),
                                                              Utilities.loadImageIcon(this,
                                                                                      "/com/mynetpcb/core/images/circuit.png"),
                                                              Utilities.loadImageIcon(this,
                                                                                      "/com/mynetpcb/core/images/chip_ico.png")));
        basePanel.add(scrollPaneTree, BorderLayout.NORTH);
        
        boardInspector=new PropertyInspectorPanel(boardComponent);
        scrollPaneInspector=new JScrollPane(boardInspector);
        basePanel.add(scrollPaneInspector, BorderLayout.CENTER);
        this.add(basePanel, BorderLayout.CENTER);                
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)boardsTree.getLastSelectedPathComponent();
        if (node == null)
            return;
        if (node.getUserObject() instanceof TreeNodeData) {
            //***could be a click on Circuit or Chip
            TreeNodeData data = (TreeNodeData)node.getUserObject();
            if (node.getParent() != boardsTree.getModel().getRoot()) { //click on chip
                TreeNodeData boardData = (TreeNodeData)((DefaultMutableTreeNode)node.getParent()).getUserObject();
                if (boardComponent.getModel().getUnit().getUUID().compareTo(boardData.getUUID()) != 0) {
                    boardComponent.getModel().getUnit().setScrollPositionValue(boardComponent.getDialogFrame().getHorizontalScrollBar().getValue(),
                                                                                 boardComponent.getDialogFrame().getVerticalScrollBar().getValue());
                    boardComponent.getModel().setActiveUnit(boardData.getUUID());
                }

                boardComponent.getModel().getUnit().setSelected(false);
                boardComponent.getModel().getUnit().setSelected(data.getUUID(), true);
                boardComponent.componentResized(null);
                //***fire node selected
                boardInspector.selectShapeEvent(new ShapeEvent(boardComponent.getModel().getUnit().getShape(data.getUUID()),
                                                                                    ShapeEvent.SELECT_SHAPE));
                //***position on a symbol
                Rectangle symbolRect =
                    boardComponent.getModel().getUnit().getShape(data.getUUID()).getBoundingShape().getBounds();
                final Point2D position = new Point2D.Double(symbolRect.x, symbolRect.y);
                boardComponent.getModel().getUnit().getScalableTransformation().getCurrentTransformation().transform(position,
                                                                                                                       position);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        boardComponent.getDialogFrame().getHorizontalScrollBar().setValue((int)position.getX() -
                                                                                            boardComponent.getWidth() /
                                                                                            2);
                        boardComponent.getDialogFrame().getVerticalScrollBar().setValue((int)position.getY() -
                                                                                          boardComponent.getHeight() /
                                                                                          2);
                    }
                });
            } else { //click on unit
                if (boardComponent.getModel().getUnit() != null) {
                    boardComponent.getModel().getUnit().setScrollPositionValue(boardComponent.getDialogFrame().getHorizontalScrollBar().getValue(),
                                                                                 boardComponent.getDialogFrame().getVerticalScrollBar().getValue());
                }
                boardComponent.getModel().setActiveUnit(data.getUUID());
                boardComponent.getModel().getUnit().setSelected(false);
                boardComponent.componentResized(null);

                boardInspector.selectUnitEvent(new UnitEvent(boardComponent.getModel().getUnit(),
                                                                        UnitEvent.SELECT_UNIT));
                boardComponent.getDialogFrame().getHorizontalScrollBar().setValue(boardComponent.getModel().getUnit().getScrollPositionXValue());
                boardComponent.getDialogFrame().getVerticalScrollBar().setValue(boardComponent.getModel().getUnit().getScrollPositionYValue());                
            }

        } else {
            //***Root select
            boardInspector.selectContainerEvent(new ContainerEvent(null, ContainerEvent.SELECT_CONTAINER));
            return;
        }
        boardComponent.Repaint();
    }

    @Override
    public void addUnitEvent(UnitEvent e) {
        this.boardsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)boardsTree.getModel().getRoot();
            //***create circuit node
            DefaultMutableTreeNode circuit =
                new DefaultMutableTreeNode(new TreeNodeData(e.getObject().getUUID(), e.getObject().getUnitName()));

            ((DefaultTreeModel)boardsTree.getModel()).insertNodeInto(circuit, root, root.getChildCount());
        } finally {
            boardsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void deleteUnitEvent(UnitEvent e) {
        this.boardsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)boardsTree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuit = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData circuitData = (TreeNodeData)circuit.getUserObject();
                if (e.getObject().getUUID().compareTo(circuitData.getUUID()) == 0) {
                    ((DefaultTreeModel)boardsTree.getModel()).removeNodeFromParent(circuit);

                    break;
                }
            }
        } finally {
            boardsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameUnitEvent(UnitEvent e) {
        this.boardsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)boardsTree.getModel().getRoot();
            UUID circuitUUID = e.getObject().getUUID();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuit = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)circuit.getUserObject();
                if (circuitUUID.compareTo(data.getUUID()) == 0) {
                    data.setName(e.getObject().getUnitName());
                    ((DefaultTreeModel)boardsTree.getModel()).nodeChanged(circuit);
                    //circuitsTree.setSelectionPath(new TreePath(circuit.getPath()));
                }
            }

        } finally {
            boardsTree.addTreeSelectionListener(this);
        }
    }


    @Override
    public void selectUnitEvent(UnitEvent e) {
        this.boardsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)boardsTree.getModel().getRoot();
            //find circuit
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuit = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData circuitData = (TreeNodeData)circuit.getUserObject();
                if (e.getObject().getUUID().compareTo(circuitData.getUUID()) == 0) {
                    //select circuit
                    boardsTree.scrollPathToVisible(new TreePath(circuit.getPath()));
                    boardsTree.setSelectionPath(new TreePath(circuit.getPath()));
                    break;
                }
            }

        } finally {
            boardsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void propertyChangeEvent(UnitEvent unitEvent) {
        // TODO Implement this method
    }

    @Override
    public void selectShapeEvent(ShapeEvent e) {
        this.boardsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)boardsTree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuit = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)circuit.getUserObject();

                if ((e.getObject()).getOwningUnit().getUUID() == data.getUUID()) {
                    for (int j = 0; j < circuit.getChildCount(); j++) {
                        DefaultMutableTreeNode symbol = (DefaultMutableTreeNode)circuit.getChildAt(j);
                        TreeNodeData _data = (TreeNodeData)symbol.getUserObject();
                        if (_data.getUUID().equals(e.getObject().getUUID())) {
                            //***select symbol
                            boardsTree.scrollPathToVisible(new TreePath(symbol.getPath()));
                            boardsTree.setSelectionPath(new TreePath(symbol.getPath()));
                            break;
                        }

                    }
                } else {
                    //close other circuits
                    boardsTree.collapsePath(new TreePath(circuit.getPath()));
                }
            }
        } finally {
            boardsTree.addTreeSelectionListener(this);
        }

    }

    @Override
    public void deleteShapeEvent(ShapeEvent e) {
        this.boardsTree.removeTreeSelectionListener(this);
        try{  
            DefaultMutableTreeNode root=(DefaultMutableTreeNode)boardsTree.getModel().getRoot();
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
                           ((DefaultTreeModel)boardsTree.getModel()).removeNodeFromParent(symbol); 
                           //select root
                            boardsTree.setSelectionPath(new TreePath(footprintNode.getPath())); 
                            //select unit
                            boardInspector.selectUnitEvent(null);
                        break;
                        }
                    }    
                }
            }  
        }finally{
          boardsTree.addTreeSelectionListener(this);
        } 
    }

    @Override
    public void renameShapeEvent(ShapeEvent e) {
        this.boardsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)boardsTree.getModel().getRoot();
            UUID circuitUUID = (e.getObject()).getOwningUnit().getUUID();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuit = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)circuit.getUserObject();
                if (circuitUUID.compareTo(data.getUUID()) == 0) {
                    for (int j = 0; j < circuit.getChildCount(); j++) {
                        DefaultMutableTreeNode symbol = (DefaultMutableTreeNode)circuit.getChildAt(j);
                        TreeNodeData _data = (TreeNodeData)symbol.getUserObject();
                        if (_data.getUUID().equals(e.getObject().getUUID())) {
                            _data.setName((e.getObject().getDisplayName().length() == 0 ? "unknown" :
                                           e.getObject().getDisplayName()));
                            ((DefaultTreeModel)boardsTree.getModel()).nodeChanged(symbol);
                            boardsTree.setSelectionPath(new TreePath(symbol.getPath()));
                            //circuitsTree.repaint();
                            break;
                        }
                    }
                }
            }

        } finally {
            boardsTree.addTreeSelectionListener(this);
        }
    }


    @Override
    public void addShapeEvent(ShapeEvent e) {
        this.boardsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)boardsTree.getModel().getRoot();
            UUID circuitUUID = (e.getObject()).getOwningUnit().getUUID();

            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuitNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)circuitNode.getUserObject();
                if (circuitUUID.compareTo(data.getUUID()) == 0) {
                    DefaultMutableTreeNode chip =
                        new DefaultMutableTreeNode(new TreeNodeData(e.getObject().getUUID(), e.getObject().getDisplayName()));
                    ((DefaultTreeModel)boardsTree.getModel()).insertNodeInto(chip, circuitNode,
                                                                               circuitNode.getChildCount());

                    break;
                }
            }
        } finally {
            boardsTree.addTreeSelectionListener(this);
        }
        //  }
    }

    @Override
    public void propertyChangeEvent(ShapeEvent shapeEvent) {
        // TODO Implement this method
    }

    @Override
    public void selectContainerEvent(ContainerEvent e) {
        this.boardsTree.removeTreeSelectionListener(this);
        try {
            boardsTree.setSelectionPath(new TreePath(boardsTree.getModel().getRoot()));
            boardsTree.scrollPathToVisible(new TreePath(boardsTree.getModel().getRoot()));
        } finally {
            boardsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameContainerEvent(ContainerEvent containerEvent) {
            boardsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)boardsTree.getModel().getRoot();
            root.setUserObject(boardComponent.getModel().getFormatedFileName());
            ((DefaultTreeModel)boardsTree.getModel()).nodeChanged(root);
        } finally {
            boardsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void deleteContainerEvent(ContainerEvent e) {
        this.boardsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)boardsTree.getModel().getRoot();
            root.setUserObject("Boards");
            ((DefaultTreeModel)boardsTree.getModel()).nodeChanged(root);
        } finally {
            boardsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void onUnitDragDrop(int index,UUID uuid) {
        System.out.println(index); 
    }
}
