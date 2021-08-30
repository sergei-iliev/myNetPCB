package com.mynetpcb.core.capi.tree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/*
 * https://stackoverflow.com/questions/4588109/drag-and-drop-nodes-in-jtree
 */
public class TreeDragDropHandler extends TransferHandler {
       private final DataFlavor[] flavors = new DataFlavor[1];
       private DefaultMutableTreeNode toRemove;
       private UnitTreeDragDropListener listener;
       
       public TreeDragDropHandler(UnitTreeDragDropListener listener) {
           this.listener=listener;
           try {
               String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
                                 ";class=\"" +
                   javax.swing.tree.DefaultMutableTreeNode.class.getName() +
                                 "\"";
               flavors[0] = new DataFlavor(mimeType);
           } catch(ClassNotFoundException e) {
               System.out.println("ClassNotFound: " + e.getMessage());
           }
       }
       
       @Override 
       public boolean canImport(TransferHandler.TransferSupport support) {
           if(!support.isDrop()) {
               return false;
           }           
           support.setShowDropLocation(true);
           if(!support.isDataFlavorSupported(flavors[0])) {
               return false;
           }
           // Do not allow a drop on the drag source selections.
           JTree.DropLocation dl =
                   (JTree.DropLocation)support.getDropLocation();
           //no drop to outer level
           if(dl.getChildIndex()==-1){
               return false;
           }
           
           JTree tree = (JTree)support.getComponent();
           int dropRow = tree.getRowForPath(dl.getPath());
           int[] selRows = tree.getSelectionRows();
           for(int i = 0; i < selRows.length; i++) {
               if(selRows[i] == dropRow) {
                   return false;
               }
           }           
           //allow reorder only -> no drop on any node
           if(dropRow!=0){
               return false;
           }
           // Do not allow MOVE-action drops if a non-leaf node is
           // selected unless all of its children are also selected.
           int action = support.getDropAction();           
           if(action != MOVE) {
               return false;
           }
           // Do not allow a non-leaf node to be copied to a level
           // which is less than its source level.
//           TreePath dest = dl.getPath();
//           DefaultMutableTreeNode target =
//               (DefaultMutableTreeNode)dest.getLastPathComponent();
//           TreePath path = tree.getPathForRow(selRows[0]);
//           DefaultMutableTreeNode firstNode =
//               (DefaultMutableTreeNode)path.getLastPathComponent();
//           if(firstNode.getChildCount() > 0 &&
//                  target.getLevel() < firstNode.getLevel()) {
//               return false;
//           }
           return true;
       }

//       private boolean haveCompleteNode(JTree tree) {
//           int[] selRows = tree.getSelectionRows();
//           TreePath path = tree.getPathForRow(selRows[0]);
//           DefaultMutableTreeNode first =
//               (DefaultMutableTreeNode)path.getLastPathComponent();
//           int childCount = first.getChildCount();
//           // first has children and no children are selected.
//           if(childCount > 0 && selRows.length == 1)
//               return false;
//           // first may have children.
//           for(int i = 1; i < selRows.length; i++) {
//               path = tree.getPathForRow(selRows[i]);
//               DefaultMutableTreeNode next =
//                   (DefaultMutableTreeNode)path.getLastPathComponent();
//               if(first.isNodeChild(next)) {
//                   // Found a child of first.
//                   if(childCount > selRows.length-1) {
//                       // Not all children of first are selected.
//                       return false;
//                   }
//               }
//           }
//           return true;
//       }
       @Override
       protected Transferable createTransferable(JComponent c) {
           JTree tree = (JTree)c;
           TreePath[] paths = tree.getSelectionPaths();
           if(paths != null) {
               // Make up a node array of copies for transfer and
               // another for/of the nodes that will be removed in
               // exportDone after a successful drop.
               DefaultMutableTreeNode node =
                   (DefaultMutableTreeNode)paths[0].getLastPathComponent();
               DefaultMutableTreeNode target = copy(node);
               //add children too
               int count=node.getChildCount();
               for(int i=0;i<count;i++){                   
                   target.add(copy(node.getChildAt(i)));               
               }
               toRemove=node;
               return new NodesTransferable(target);
           }
           return null;
       }

       /** Defensive copy used in createTransferable. */
       private DefaultMutableTreeNode copy(TreeNode node) {
           TreeNodeData userObject=(TreeNodeData)((DefaultMutableTreeNode)node).getUserObject();           
           return new DefaultMutableTreeNode(new TreeNodeData(userObject.getUUID(),userObject.getName()));
       }

       protected void exportDone(JComponent source, Transferable data, int action) {
           if((action & MOVE) == MOVE) {
               JTree tree = (JTree)source;
               DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
               // Remove nodes saved in nodesToRemove in createTransferable.
               toRemove.removeAllChildren();
               model.removeNodeFromParent(toRemove);               
           }
       }

       public int getSourceActions(JComponent c) {
           return COPY_OR_MOVE;
       }
       
       @Override
       public boolean importData(TransferHandler.TransferSupport support) {
           if(!canImport(support)) {
               return false;
           }
           // Extract transfer data.
           DefaultMutableTreeNode node = null;
           try {
               Transferable t = support.getTransferable();
               node = (DefaultMutableTreeNode)t.getTransferData(flavors[0]);
           } catch(UnsupportedFlavorException | IOException e) {
               e.printStackTrace(System.out);
           } 
           // Get drop location info.
           JTree.DropLocation dl =
                   (JTree.DropLocation)support.getDropLocation();
           int childIndex = dl.getChildIndex();
           TreePath dest = dl.getPath();
           DefaultMutableTreeNode parent =
               (DefaultMutableTreeNode)dest.getLastPathComponent();
           JTree tree = (JTree)support.getComponent();
           DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
           // Configure for drop mode.
           int index = childIndex;    // DropMode.INSERT
           if(childIndex == -1) {     // DropMode.ON
               index = parent.getChildCount();
           }
           // Add data to model.
           model.insertNodeInto(node, parent, index);
           TreeNodeData data = (TreeNodeData)node.getUserObject();
           listener.onUnitDragDrop(index,data.getUUID());
           return true;
       }

       public class NodesTransferable implements Transferable {
           private final DefaultMutableTreeNode node;

           public NodesTransferable(DefaultMutableTreeNode node) {
               this.node = node;
            }

           public Object getTransferData(DataFlavor flavor)
                                    throws UnsupportedFlavorException {
               if(!isDataFlavorSupported(flavor))
                   throw new UnsupportedFlavorException(flavor);
               return node;
           }

           public DataFlavor[] getTransferDataFlavors() {
               return flavors;
           }

           public boolean isDataFlavorSupported(DataFlavor flavor) {
               return flavors[0].equals(flavor);
           }
       }}
