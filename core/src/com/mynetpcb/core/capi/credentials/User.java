package com.mynetpcb.core.capi.credentials;

import java.awt.Component;

import javax.swing.JOptionPane;


public class User {
    public enum Type{
      ANONYMOUS,LOGGEDIN;  
    }
    
    public static User user;
            
    private final String username;
    
    private final String password;
    
    private Type type;
    
    static{
     //***create default anonymous user    
     createUser(null,null);    
    }
        
    public static User get(){
        return user;
    }
    
    public static void createUser(String username,String password){
      user=new User(username,password);  
    }
    
    public static void showMessageDialog(Component owner,String message){
        JOptionPane.showMessageDialog(owner,message, "Close", JOptionPane.OK_OPTION)      ;
    }
    
    private User(String username,String password) {
        if(username==null||password==null){
          type=Type.ANONYMOUS;  
        }else{
          type=Type.LOGGEDIN;  
        }
        this.username=username;
        this.password=password;        
    } 
    
    
    public Type getType(){
        return type;
    }
    
    public boolean isAnonymous(){        
        return type!=Type.ANONYMOUS;
    }
    
}

