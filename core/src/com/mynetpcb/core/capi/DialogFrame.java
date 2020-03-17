package com.mynetpcb.core.capi;

import java.awt.Component;

import javax.swing.JRootPane;
import javax.swing.JScrollBar;


/**
 *PCB components need to access GUI context
 * @author Sergey Iliev
 */
public interface DialogFrame {

    /**
     *Get parents frame, applets need it
     * @return
     */
    public Component getParentFrame();

    public JRootPane getRootPane();

    /**
     *Access to vertical scrollbar
     * @return
     */
    public JScrollBar getVerticalScrollBar();

    /**
     *Access to horizontal scrollbar
     * @return hbar
     */
    public JScrollBar getHorizontalScrollBar();

    //***the circuit mode is changed - recap the toggle buttons group
    public void setButtonGroup(int requestedMode);
}

