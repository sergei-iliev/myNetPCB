package tests.mynetpcb.gerber;

import com.mynetpcb.gerber.command.extended.CoordinateResolutionCommand;
import com.mynetpcb.gerber.command.extended.LevelPolarityCommand;
import com.mynetpcb.gerber.command.extended.StepAndRepeatCommand;

import org.junit.Assert;
import org.junit.Test;

public class CommandsTest {
    @Test
    public void testCoordinateCommand(){
       CoordinateResolutionCommand command=new CoordinateResolutionCommand("35");
       Assert.assertTrue(command.print().equals("%FSLAX35Y35*%"));
    }
    
    @Test
    public void testLevelPolarityCommand(){
        LevelPolarityCommand  command=new LevelPolarityCommand();       
        Assert.assertTrue(command.print().equals("%LPD*%"));
        
        command.setPolarity(LevelPolarityCommand.Polarity.CLEAR);
        Assert.assertTrue(command.print().equals("%LPC*%"));
    } 
    
    @Test
    public void testStepAndRepeatCommand(){
       StepAndRepeatCommand command=new StepAndRepeatCommand();
       Assert.assertTrue(command.print().equals("%SRX1Y1I0J0*%"));
    }
    
    
    
}
