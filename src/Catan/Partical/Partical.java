package Catan.Partical;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author steven
 */
public class Partical 
{
    public BufferedImage bm     = null;
    public float         x      = 0;
    public float         y      = 0;
    public int           life   = 0;
    public float         xSpeed = 0;
    public float         ySpeed = 0;
    
    Partical (BufferedImage srcBM, int xPos, int yPos, int srcWidth, int srcHeight)
    {
        bm = new BufferedImage (srcWidth, srcHeight, BufferedImage.TYPE_INT_ARGB);        
        (bm.getGraphics()).drawImage(srcBM.getSubimage(xPos, yPos, srcWidth, srcHeight), 0, 0, null);        
    }
                           
    public boolean move  ()
    {
        life--;
        if (life > 0)
        {
            x += xSpeed;
            y += ySpeed;
        }
        else 
            return false;        
        return true;
    }
        
    public void draw (Graphics2D g2)
    {
        g2.drawImage(bm, (int)x, (int)y, null);
    }    
}
