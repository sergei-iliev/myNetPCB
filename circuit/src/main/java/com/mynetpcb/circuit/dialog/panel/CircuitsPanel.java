package com.mynetpcb.circuit.dialog.panel;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHJunction;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ContainerListener;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.ShapeListener;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitListener;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.tree.TreeDragDropHandler;
import com.mynetpcb.core.capi.tree.TreeNodeData;
import com.mynetpcb.core.capi.tree.UnitTreeCellRenderer;
import com.mynetpcb.core.capi.tree.UnitTreeDragDropListener;
import com.mynetpcb.core.capi.unit.Unitable;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

public class CircuitsPanel extends JPanel implements TreeSelectionListener, UnitListener, ShapeListener,
                                                     ContainerListener,UnitTreeDragDropListener {
    private final CircuitComponent circuitComponent;

    private PropertyInspectorPanel circuitInspector;

    private JPanel basePanel = new JPanel(new BorderLayout());

    private JTree circuitsTree = new JTree();

    private JScrollPane scrollPaneTree = new JScrollPane(circuitsTree);

    private JScrollPane scrollPaneInspector;
        
    public CircuitsPanel(CircuitComponent circuitComponent) {
        super(new BorderLayout());
        this.circuitComponent = circuitComponent;
        this.setPreferredSize(new Dimension(200, 200));
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
        circuitsTree.setDragEnabled(true);
        circuitsTree.setDropMode(DropMode.ON_OR_INSERT);
        circuitsTree.setTransferHandler(new TreeDragDropHandler(this));
        circuitsTree.setShowsRootHandles(true);
        circuitsTree.setVisibleRowCount(10);
        circuitsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        circuitsTree.setEditable(false);
        circuitsTree.addTreeSelectionListener(this);
        circuitsTree.setModel(new DefaultTreeModel(root));
        circuitsTree.setCellRenderer(new UnitTreeCellRenderer(Utilities.loadImageIcon(this,
                                                                                      "images/library.png"),
                                                              Utilities.loadImageIcon(this,
                                                                                      "images/circuit.png"),
                                                              Utilities.loadImageIcon(this,
                                                                                      "images/chip_ico.png")));
        basePanel.add(scrollPaneTree, BorderLayout.NORTH);
        
        circuitInspector=new PropertyInspectorPanel(circuitComponent);
        scrollPaneInspector=new JScrollPane(circuitInspector);
        basePanel.add(scrollPaneInspector, BorderLayout.CENTER);
        this.add(basePanel, BorderLayout.CENTER);
    }


    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)circuitsTree.getLastSelectedPathComponent();
        if (node == null)
            return;
        if (node.getUserObject() instanceof TreeNodeData) {
            //***could be a click on Circuit or Chip
            TreeNodeData data = (TreeNodeData)node.getUserObject();
            if (node.getParent() != circuitsTree.getModel().getRoot()) { //click on chip
                TreeNodeData circuitData = (TreeNodeData)((DefaultMutableTreeNode)node.getParent()).getUserObject();
                if (circuitComponent.getModel().getUnit().getUUID().compareTo(circuitData.getUUID()) != 0) {
                    //circuitComponent.getModel().getUnit().setScrollPositionValue(circuitComponent.getDialogFrame().getHorizontalScrollBar().getValue(),
                    //                                                             circuitComponent.getDialogFrame().getVerticalScrollBar().getValue());
                    circuitComponent.getModel().setActiveUnit(circuitData.getUUID());
                }

                circuitComponent.getModel().getUnit().setSelected(false);
                circuitComponent.getModel().getUnit().setSelected(data.getUUID(), true);
                circuitComponent.componentResized(null);
                //***fire node selected
               circuitInspector.selectShapeEvent(new ShapeEvent(circuitComponent.getModel().getUnit().getShape(data.getUUID()),
                                                                                    ShapeEvent.SELECT_SHAPE));
                //***position on a symbol
                Box symbolRect=circuitComponent.getModel().getUnit().getShape(data.getUUID()).getBoundingShape();   
                final Point2D position = new Point2D.Double(symbolRect.min.x, symbolRect.min.y);
                circuitComponent.getModel().getUnit().getScalableTransformation().getCurrentTransformation().transform(position,
                                                                                                                       position);
                //SwingUtilities.invokeLater(new Runnable() {
                //    public void run() {
                //        circuitComponent.getDialogFrame().getHorizontalScrollBar().setValue((int)position.getX() -
                //                                                                            circuitComponent.getWidth() /
                //                                                                            2);
                //        circuitComponent.getDialogFrame().getVerticalScrollBar().setValue((int)position.getY() -
                //                                                                          circuitComponent.getHeight() /
                //                                                                          2);
                //    }
                //});
            } else { //click on unit
                //if (circuitComponent.getModel().getUnit() != null) {
                //    circuitComponent.getModel().getUnit().setScrollPositionValue(circuitComponent.getDialogFrame().getHorizontalScrollBar().getValue(),
                //                                                                 circuitComponent.getDialogFrame().getVerticalScrollBar().getValue());
                //}
                circuitComponent.getModel().setActiveUnit(data.getUUID());
                circuitComponent.getModel().getUnit().setSelected(false);
                circuitComponent.componentResized(null);

                circuitInspector.selectUnitEvent(new UnitEvent(circuitComponent.getModel().getUnit(),
                                                                        UnitEvent.SELECT_UNIT));
                //circuitComponent.getDialogFrame().getHorizontalScrollBar().setValue(circuitComponent.getModel().getUnit().getScrollPositionXValue());
                //circuitComponent.getDialogFrame().getVerticalScrollBar().setValue(circuitComponent.getModel().getUnit().getScrollPositionYValue());

            }

        } else {
            //***Root select
            circuitInspector.selectContainerEvent(new ContainerEvent(null, ContainerEvent.SELECT_CONTAINER));
            return;
        }
        circuitComponent.Repaint();
    }

    //***********UnitListener

    @Override
    public void selectShapeEvent(ShapeEvent e) {
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)circuitsTree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuit = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)circuit.getUserObject();

                if ((e.getObject()).getOwningUnit().getUUID() == data.getUUID()) {
                    for (int j = 0; j < circuit.getChildCount(); j++) {
                        DefaultMutableTreeNode symbol = (DefaultMutableTreeNode)circuit.getChildAt(j);
                        TreeNodeData _data = (TreeNodeData)symbol.getUserObject();
                        if (_data.getUUID().equals(e.getObject().getUUID())) {
                            //***select symbol
                            circuitsTree.scrollPathToVisible(new TreePath(symbol.getPath()));
                            circuitsTree.setSelectionPath(new TreePath(symbol.getPath()));
                            break;
                        }

                    }
                } else {
                    //close other circuits
                    circuitsTree.collapsePath(new TreePath(circuit.getPath()));
                }
            }
        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }

    }

    @Override
    public void deleteShapeEvent(ShapeEvent e) {
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)circuitsTree.getModel().getRoot();
            UUID circuitUUID = ((Unitable)e.getObject()).getOwningUnit().getUUID();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuit = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)circuit.getUserObject();
                if (circuitUUID.compareTo(data.getUUID()) == 0) {
                    for (int j = 0; j < circuit.getChildCount(); j++) {
                        DefaultMutableTreeNode symbol = (DefaultMutableTreeNode)circuit.getChildAt(j);
                        TreeNodeData _data = (TreeNodeData)symbol.getUserObject();
                        if (_data.getUUID().equals(e.getObject().getUUID())) {
                            //delete symbol
                            ((DefaultTreeModel)circuitsTree.getModel()).removeNodeFromParent(symbol);
                            //select root
                            circuitsTree.setSelectionPath(new TreePath(circuit.getPath()));
                            //select unit
                            circuitInspector.selectUnitEvent(null);
                            break;
                        }
                    }
                }
            }
        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameShapeEvent(ShapeEvent e) {
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)circuitsTree.getModel().getRoot();
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
                            ((DefaultTreeModel)circuitsTree.getModel()).nodeChanged(symbol);
                            circuitsTree.setSelectionPath(new TreePath(symbol.getPath()));
                            //circuitsTree.repaint();
                            break;
                        }
                    }
                }
            }

        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void addShapeEvent(ShapeEvent e) {
        if((e.getObject()instanceof Trackable)|| (e.getObject() instanceof SCHJunction)/*|| (e.getObject() instanceof SCHNoConnector)*/){
          return; 
        }
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)circuitsTree.getModel().getRoot();
            UUID circuitUUID = (e.getObject()).getOwningUnit().getUUID();

            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuitNode = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)circuitNode.getUserObject();
                if (circuitUUID.compareTo(data.getUUID()) == 0) {
                    DefaultMutableTreeNode chip =
                        new DefaultMutableTreeNode(new TreeNodeData(e.getObject().getUUID(), e.getObject().getDisplayName()));
                    ((DefaultTreeModel)circuitsTree.getModel()).insertNodeInto(chip, circuitNode,
                                                                               circuitNode.getChildCount());

                    break;
                }
            }
        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }
        //  }
    }

    @Override
    public void propertyChangeEvent(ShapeEvent e) {
    }

    //*******************ComponentListener

    @Override
    public void addUnitEvent(UnitEvent e) {
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)circuitsTree.getModel().getRoot();
            //***create circuit node
            DefaultMutableTreeNode circuit =
                new DefaultMutableTreeNode(new TreeNodeData(e.getObject().getUUID(), e.getObject().getUnitName()));

            ((DefaultTreeModel)circuitsTree.getModel()).insertNodeInto(circuit, root, root.getChildCount());
        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void selectUnitEvent(UnitEvent e) {
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)circuitsTree.getModel().getRoot();
            //find circuit
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuit = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData circuitData = (TreeNodeData)circuit.getUserObject();
                if (e.getObject().getUUID().compareTo(circuitData.getUUID()) == 0) {
                    //select circuit
                    circuitsTree.scrollPathToVisible(new TreePath(circuit.getPath()));
                    circuitsTree.setSelectionPath(new TreePath(circuit.getPath()));
                    break;
                }
            }

        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void deleteUnitEvent(UnitEvent e) {
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            //***get root
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)circuitsTree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuit = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData circuitData = (TreeNodeData)circuit.getUserObject();
                if (e.getObject().getUUID().compareTo(circuitData.getUUID()) == 0) {
                    ((DefaultTreeModel)circuitsTree.getModel()).removeNodeFromParent(circuit);

                    break;
                }
            }
        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameUnitEvent(UnitEvent e) {
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)circuitsTree.getModel().getRoot();
            UUID circuitUUID = e.getObject().getUUID();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode circuit = (DefaultMutableTreeNode)root.getChildAt(i);
                TreeNodeData data = (TreeNodeData)circuit.getUserObject();
                if (circuitUUID.compareTo(data.getUUID()) == 0) {
                    data.setName(e.getObject().getUnitName());
                    ((DefaultTreeModel)circuitsTree.getModel()).nodeChanged(circuit);
                    //circuitsTree.setSelectionPath(new TreePath(circuit.getPath()));
                }
            }

        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void propertyChangeEvent(UnitEvent e) {
    }

    @Override
    public void selectContainerEvent(ContainerEvent e) {
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            circuitsTree.setSelectionPath(new TreePath(circuitsTree.getModel().getRoot()));
            circuitsTree.scrollPathToVisible(new TreePath(circuitsTree.getModel().getRoot()));
        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void renameContainerEvent(ContainerEvent e) {
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)circuitsTree.getModel().getRoot();
            root.setUserObject(circuitComponent.getModel().getFormatedFileName());
            ((DefaultTreeModel)circuitsTree.getModel()).nodeChanged(root);
        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void deleteContainerEvent(ContainerEvent e) {
        this.circuitsTree.removeTreeSelectionListener(this);
        try {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)circuitsTree.getModel().getRoot();
            root.setUserObject("Circuits");
            ((DefaultTreeModel)circuitsTree.getModel()).nodeChanged(root);
        } finally {
            circuitsTree.addTreeSelectionListener(this);
        }
    }

    @Override
    public void onUnitDragDrop(int index,UUID uuid) {
        circuitComponent.getModel().reorder(index, uuid);
    }
}
