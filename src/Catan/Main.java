package Catan;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */
public class Main
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // Set the Look-and-Feel to the cross platform.
        try 
        {
              UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
              // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (InstantiationException e)          {}
        catch (IllegalAccessException e)          {}
        catch (UnsupportedLookAndFeelException e) {}
        catch (ClassNotFoundException e )         {}

        CatanJFrame catan = new CatanJFrame();
        
        catan.setVisible (true);
        catan.newGame();
    }
}
