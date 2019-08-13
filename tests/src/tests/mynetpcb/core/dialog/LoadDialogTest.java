package tests.mynetpcb.core.dialog;

import com.mynetpcb.core.dialog.load.AbstractLoadDialog;
import com.mynetpcb.pad.dialog.FootprintLoadDialog;

import org.junit.Assert;
import org.junit.Test;

public class LoadDialogTest {
 
@Test
public void testLoadDialogBuilder() {
       AbstractLoadDialog.Builder builder=(new FootprintLoadDialog.Builder());
       builder.setCaption("LUSI").setEnabled(true).setPackaging(null);
       AbstractLoadDialog dialog=builder.build();
       Assert.assertTrue(dialog.getTitle().equals("LUSI"));
}
    
@Test
public void testLoadDialog() {
   AbstractLoadDialog dialog=(new FootprintLoadDialog.Builder()).setCaption("demo").setEnabled(false).setPackaging(null).build();      
   Assert.assertNotNull(dialog);
   Assert.assertTrue(dialog instanceof FootprintLoadDialog);
}



}
