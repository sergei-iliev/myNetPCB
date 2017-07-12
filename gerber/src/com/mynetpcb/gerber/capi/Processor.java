package com.mynetpcb.gerber.capi;

import com.mynetpcb.board.unit.Board;

public interface Processor {
    
    public void process(Board board,int layermask);
}
