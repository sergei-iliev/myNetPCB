package com.mynetpcb.core.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCodeGenerator {
    public static final int MAX_LIMIT=10000000;                        
    
    private final ZonedDateTime today;
    private int maxLimit; 
    private String formatter;

    public RandomCodeGenerator(int randomLength) {
        today = ZonedDateTime.now(ZoneId.systemDefault());        
        this.maxLimit=(int)Math.pow(10,randomLength);
        this.formatter="%0"+String.valueOf(randomLength)+"d";
    }
    
    public RandomCodeGenerator() {
        this(7);
    }
    
    public int createRandomNumber(){                
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        return  rnd.nextInt(0,maxLimit);                        
    }
    

    public int getMaxLimit() {
       
        return maxLimit;
    }
    
    /*
     * Warning:
     * Highest possible number 11 digit (99999999999)
     * 11x9 =99 two digit result
     * 2 divides by 9
     */
    private long calculateDigitalRoot(long n) {
        if (n == 0) {
            return 0;
        }
        if (n % 9 == 0) {
            return 9;
        }
        return n % 9;
    }
          
    public String generateTicketCode(int number) {
        
        String paddedNumber=String.format(this.formatter, number);
        return calculateRandomCode(paddedNumber);
    }       
    
    private String calculateRandomCode(String randomNumber){
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(today.getYear() % 10));
        sb.append(today.getMonthValue() < 10
            ? "0" + String.valueOf(today.getMonthValue()) : String.valueOf(today.getMonthValue()));
        
        //4th last digit of the day
        sb.append(today.getDayOfMonth() % 10);
        
        //7 random digits left padded with 0
        sb.append(randomNumber);
        
        //11th - calculate digital root of all digits            
        long digitalRoot=this.calculateDigitalRoot(Long.parseLong(sb.toString()));
        sb.append(String.valueOf(digitalRoot));
        

        return sb.toString();            
    }             

}
