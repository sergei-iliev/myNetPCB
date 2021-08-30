package com.mynetpcb.gerber.aperture;

import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.attribute.DeleteAttribute;
import com.mynetpcb.gerber.capi.Printable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class ApertureDictionary  implements Printable{
   private final Map<AbstractAttribute,Set<ApertureDefinition>> repository; 
   private int index;
   
   public ApertureDictionary(){
       repository=new HashMap<>();
       index=10;
   }
   
   public void reset(){
       repository.clear();
       index=10;
   }
   
   public void add(ApertureDefinition apertureDefinition) {
        apertureDefinition.setCode(index);
        //check if definition exists
        
        //1.by attribute
        if(apertureDefinition.getAttribute()!=null){
            if(!repository.containsKey(apertureDefinition.getAttribute())){
               //create mapping                
               repository.put(apertureDefinition.getAttribute(),new LinkedHashSet<>()); 
            }
            
            if(repository.get(apertureDefinition.getAttribute()).add(apertureDefinition)){
                index++;    
            }            
        }else{   //attribute ==null
            if(!repository.containsKey(null)){
                repository.put(null,new LinkedHashSet<>()); 
            }
            if(repository.get(null).add(apertureDefinition)){
                index++;
            }
                        
        }
    }
    
    public ApertureDefinition findCircle(AbstractAttribute.Type type,double diameter){
        Optional<Set<ApertureDefinition>> o = repository.entrySet()
                              .stream()
                              .filter(e->e.getKey()!=null&&e.getKey().getType()==type )
                              .map(Map.Entry::getValue)
                              .findFirst();
        if(o.isPresent()){
           Optional<ApertureDefinition> aperture= o.get().stream().filter(d->{if(d.getShape()==ApertureDefinition.ApertureShape.CIRCLE&&(Utils.EQ(((CircleAperture)d).getDiameter(),diameter)))
                                          return true;
                                       else
                                          return false;}).findFirst();
            return aperture.orElse(null);   
        }
        return null;
    }
    
    public ApertureDefinition findCircle(double diameter){
        Optional<ApertureDefinition> o= repository.get(null).stream().filter(d->{if(d instanceof CircleAperture&&(Utils.EQ(((CircleAperture)d).getDiameter(),diameter)))
                                                                                                                       return true;
                                                                                                               else
                                                                                                                       return false;}).findFirst();        
        
        return o.orElse(null);   
    }
    
    /*
     * get apperture by its code
     */
    public ApertureDefinition get(int code){
        if(repository.get(null)==null){
            return null;
        }
        Optional<ApertureDefinition> o= repository.get(null).stream().filter(d->{if(d.getCode()==code)
                                                                                                                       return true;
                                                                                                               else
                                                                                                                       return false;}).findFirst();        
        
        return o.orElse(null);   
       
    }
/*
 * Use contour region
 */
//    public ApertureDefinition findRectangle(AbstractAttribute.Type type,int x,int y){
//        
//        Optional<Set<ApertureDefinition>> o = repository.entrySet()
//                              .stream()
//                              .filter(e->e.getKey()!=null&&e.getKey().getType()==type )
//                              .map(Map.Entry::getValue)
//                              .findFirst();
//        if(o.isPresent()){
//           Optional<ApertureDefinition> aperture= o.get().stream().filter(d->{if((d instanceof RectangleAperture)&&(((RectangleAperture)d).getX()==x&&((RectangleAperture)d).getY()==y))
//                                          return true;
//                                       else
//                                          return false;}).findFirst();
//            return aperture.orElse(null);   
//        }
//        return null;
//    }
/*
 * Use contour region
 */    
//    public ApertureDefinition findPolygon(AbstractAttribute.Type type,int diameter,int vertices){
//        Optional<Set<ApertureDefinition>> o = repository.entrySet()
//                              .stream()
//                              .filter(e->e.getKey()!=null&&e.getKey().getType()==type )
//                              .map(Map.Entry::getValue)
//                              .findFirst();
//        if(o.isPresent()){
//           Optional<ApertureDefinition> aperture= o.get().stream().filter(d->{if((d instanceof PolygonAperture)&&(((PolygonAperture)d).getDiameter()==diameter&&((PolygonAperture)d).getVerticesNumber()==vertices))
//                                          return true;
//                                       else
//                                          return false;}).findFirst();
//            return aperture.orElse(null);   
//        }
//        return null;       
//    }
/*
 * Use line
 */    
//    public ApertureDefinition findObround(AbstractAttribute.Type type,int x,int y){
//        Optional<Set<ApertureDefinition>> o = repository.entrySet()
//                              .stream()
//                              .filter(e->e.getKey()!=null&&e.getKey().getType()==type )
//                              .map(Map.Entry::getValue)
//                              .findFirst();
//        if(o.isPresent()){
//           Optional<ApertureDefinition> aperture= o.get().stream().filter(d->{if((d instanceof ObroundAperture)&&(((ObroundAperture)d).getX()==x&&((ObroundAperture)d).getY()==y))
//                                          return true;
//                                       else
//                                          return false;}).findFirst();
//            return aperture.orElse(null);   
//        }
//        return null;
//    }
    
//    public ApertureDefinition findRectangle(int x,int y){
//        Optional<ApertureDefinition> o= this.repository.get(null).stream().filter(d->{if(d instanceof RectangleAperture&&((RectangleAperture)d).getX()==x&&((RectangleAperture)d).getY()==y)
//                                                                                                                       return true;
//                                                                                                               else
//                                                                                                                       return false;}).findFirst();        
//        
//        return o.orElse(null);
//        
//    }
//    public ApertureDefinition findObround(int x,int y){
//        Optional<ApertureDefinition> o= this.repository.get(null).stream().filter(d->{if(d instanceof ObroundAperture&&((ObroundAperture)d).getX()==x&&((ObroundAperture)d).getY()==y)
//                                                                                                                       return true;
//                                                                                                               else
//                                                                                                                       return false;}).findFirst();        
//        
//        return o.orElse(null);
//        
//    }   
    
//    public ApertureDefinition findPolygon(int diameter,int vertices){
//          
//        Optional<ApertureDefinition> o= this.repository.get(null).stream().filter(d->{if(d instanceof PolygonAperture&&((PolygonAperture)d).getDiameter()==diameter&&((PolygonAperture)d).getVerticesNumber()==vertices)
//                                                                                                                       return true;
//                                                                                                               else
//                                                                                                                       return false;}).findFirst();        
//        
//        return o.orElse(null);
//    }
    public String print(){        
        AbstractAttribute currentAttribute=new DeleteAttribute();  //default attribute
        StringBuilder sb=new StringBuilder();
        for(Map.Entry<AbstractAttribute,Set<ApertureDefinition>> entry:repository.entrySet()){
            if(entry.getKey()!=null){
                currentAttribute=entry.getKey();
                sb.append(entry.getKey().print()+"\n");    
            }else{
                if(!(currentAttribute instanceof DeleteAttribute)){                
                    currentAttribute=new DeleteAttribute();
                    sb.append(currentAttribute.print()+"\n"); 
                }
            }
            for(ApertureDefinition a:entry.getValue()){
                sb.append(a.print()+"\n");   
            }
//        for(ApertureDefinition a:this){
//            if(a.getAttribute()!=null){
//                 if(!Objects.equals(a.getAttribute(), currentAttribute)){
//                   currentAttribute=a.getAttribute();
//                   sb.append(a.getAttribute().print()+"\n");                     
//                 }
//            }else{   //null 
//                if(!(currentAttribute instanceof DeleteAttribute)){                
//                  currentAttribute=new DeleteAttribute();
//                  sb.append(currentAttribute.print()+"\n"); 
//                }
//            }
//            sb.append(a.print()+"\n");
//        };
        }
        return sb.toString();
    }
}
