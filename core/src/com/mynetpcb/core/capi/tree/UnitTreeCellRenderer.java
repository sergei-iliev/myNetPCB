package com.mynetpcb.core.capi.tree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;


public class UnitTreeCellRenderer extends DefaultTreeCellRenderer {
    
    private final Icon rootIcon;
    private final Icon unitIcon;
    private final Icon chipIcon;
    
    public UnitTreeCellRenderer(Icon rootIcon,Icon unitIcon,Icon chipIcon) {
        this.rootIcon = rootIcon;
        this.unitIcon=unitIcon;
        this.chipIcon=chipIcon;
    }
    
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);

        DefaultMutableTreeNode node=(DefaultMutableTreeNode)value;

        if(node.isRoot()&&node.getParent()==null)
              setIcon(rootIcon);
        else if(node.getParent()==tree.getModel().getRoot())
              setIcon(unitIcon);
        else if (leaf&&!node.isRoot()) 
            setIcon(chipIcon);                        
        
        return this;
    }    
}
