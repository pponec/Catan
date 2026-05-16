package Catan;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author steven
 */
public class GameSprite 
{
    public BufferedImage bg   = null;
    public BufferedImage obj  = null;
    public float         xPos = 0;
    public float         yPos = 0;
    public int           life = 0;
    public float         xInc = 0;
    public float         yInc = 0;
    
    public int           xLastPos = 0;
    public int           yLastPos = 0;

    GameSprite (BufferedImage bgBuffer, BufferedImage obj, float xPos, float yPos, int xInc, int yInc, int life)
    {        
        updateBG (bgBuffer);
        this.xPos = xPos;
        this.yPos = yPos;
        this.xInc = xInc;
        this.yInc = yInc;
        this.life = life;
        this.xLastPos = (int) xPos;
        this.yLastPos = (int) yPos;        
    }
    
    public void updateBG (BufferedImage bgBuffer)
    {
        this.bg = bgBuffer;
    }
                   
    public void updatePosition ()
    {
        xLastPos = (int) xPos;
        yLastPos = (int) yPos;        
        
        life--;
        if (life > 0)
        {
            xPos += xInc;
            yPos += yInc;
        }        
    }
        
    public void draw (Graphics2D g2)
    {
        // erase previous 
//        g2.drawImage(bg, xLastPos, yLastPos, (obj.getWidth() * 2, life, observer);
    }
    
}
