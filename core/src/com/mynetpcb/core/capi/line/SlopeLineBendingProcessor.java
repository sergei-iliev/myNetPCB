package com.mynetpcb.core.capi.line;


/**
 * Start bending with slope first -> board wireing
 */
public class SlopeLineBendingProcessor extends LineSlopeBendingProcessor{

    public SlopeLineBendingProcessor(){
        type=StartLineType.START_SLOPE;
    }
        
//    @Override
//    public boolean addLinePoint(Point point) {
//        if(getLine().getLinePoints().size()==0){
//             getLine().Reset(point);
//        }
//        boolean result=false;
//        if(!isOverlappedPoint(getLine(),point)){
//            if(!isPointOnLine(getLine(),point)) {
//                Point midP,endP;
//               
//                if(this.isGridAlignable){
//                  midP=getLine().getOwningUnit().getGrid().positionOnGrid(getLine().getFloatingMidPoint().x,getLine().getFloatingMidPoint().y);
//                  endP=getLine().getOwningUnit().getGrid().positionOnGrid(getLine().getFloatingEndPoint().x,getLine().getFloatingEndPoint().y);
//                }else{
//                  midP=new Point(getLine().getFloatingMidPoint().x,getLine().getFloatingMidPoint().y);
//                  endP=new Point(getLine().getFloatingEndPoint().x,getLine().getFloatingEndPoint().y);
//                  
//                }
//                if(isOverlappedPoint(getLine(), midP)){
//                  getLine().addPoint(endP);
//                  result=true;  
//                }else if(!isPointOnLine(getLine(),midP)){
//                  getLine().addPoint(midP);
//                  result=true;
//                } 
//            }  
//        }  
//        
//        getLine().shiftFloatingPoints(); 
//        return result;
//    }

//    @Override
//    public void moveLinePoint(int x, int y) {
//        Trackable line=getLine();
//        line.getFloatingEndPoint().setLocation(x,y);
//        
//        Utilities.QUADRANT quadrant = Utilities.getQuadrantLocation(line.getFloatingStartPoint(),line.getFloatingEndPoint());
//        int dx=Math.abs(line.getFloatingStartPoint().x-line.getFloatingEndPoint().x);
//        int dy=Math.abs(line.getFloatingStartPoint().y-line.getFloatingEndPoint().y);   
//        
//        if(dx>=dy){
//            if(quadrant==Utilities.QUADRANT.FIRST||quadrant==Utilities.QUADRANT.FORTH){
//                if(line.getLinePoints().size()>1){
//                    Point lastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-1);  
//                    Point lastlastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-2); 
//                    if(isSlopeInterval(lastPoint, lastlastPoint)){
//                       line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x-dy,line.getFloatingStartPoint().y);   
//                    }else{
//                       line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x+dy,line.getFloatingEndPoint().y);                                           
//                    }                 
//                }else{
//                    line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x+dy,line.getFloatingEndPoint().y);    
//                }
//            }else{
//                if(line.getLinePoints().size()>1){
//                    Point lastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-1);  
//                    Point lastlastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-2); 
//                    if(isSlopeInterval(lastPoint, lastlastPoint)){
//                       line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x+dy,line.getFloatingStartPoint().y);         
//                    }else{
//                       line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x-dy,line.getFloatingEndPoint().y);           
//                    }                }else{
//                    line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x-dy,line.getFloatingEndPoint().y);    
//                }                
//            }
//        }else{
//            if(quadrant==Utilities.QUADRANT.FIRST||quadrant==Utilities.QUADRANT.SECOND){
//                if(line.getLinePoints().size()>1){
//                    Point lastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-1);
//                    Point lastlastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-2); 
//                                        
//                    if(isSlopeInterval( lastPoint, lastlastPoint)){
//                          line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x,line.getFloatingEndPoint().y+dx);   
//                    }else{          
//                         line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x,line.getFloatingStartPoint().y-dx);    
//                    }                  }else{
//                  line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x,line.getFloatingStartPoint().y-dx);      
//                }
//            }else{
//                if(line.getLinePoints().size()>1){
//                    Point lastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-1);
//                    Point lastlastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-2); 
//                    if(isSlopeInterval( lastPoint, lastlastPoint)){
//                       line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x,line.getFloatingEndPoint().y-dx);
//                    }else{
//                     line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x,line.getFloatingStartPoint().y+dx);   
//                    }                }else{
//                  line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x,line.getFloatingStartPoint().y+dx);      
//                }
//                
//            }
//        }
//        
//
//    }

    @Override
    public String getActionCommand() {
        return "slopelinebend";
    }
}
