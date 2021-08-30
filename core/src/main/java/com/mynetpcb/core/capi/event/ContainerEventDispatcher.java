package com.mynetpcb.core.capi.event;

import java.awt.event.ContainerListener;

public interface ContainerEventDispatcher {
    public void fireContainerEvent(ContainerEvent e);

    public void addContainerListener(ContainerListener listener);

    public void removeContainerListener(ContainerListener listener);
}
