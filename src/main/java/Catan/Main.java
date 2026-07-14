package Catan;

import javax.swing.SwingUtilities;
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
        // Swing is single-threaded: building the window, showing the modal
        // "new game" dialog and every repaint must all happen on the Event
        // Dispatch Thread (EDT). The rest of the game already runs on the EDT
        // (menu/button handlers), so driving construction and the first game
        // from the main thread let the main thread and the EDT touch the same
        // components in parallel - a race that could intermittently freeze or
        // corrupt the UI. Do the whole start-up on the EDT.
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
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
        });
    }
}
