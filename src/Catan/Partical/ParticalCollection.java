/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Catan.Partical;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 *
 * @author steven
 */
public class ParticalCollection 
{
    public LinkedList<Partical> collection = new LinkedList<Partical>();
        
    public ParticalCollection()
    {        
    }
    
    public void updateAll (Graphics2D g2)
    {
        LinkedList<Partical> removals = new LinkedList<Partical>();
        for (Partical p:collection)
        {
            if (p.move() == false)
                removals.add (p);
            else
                p.draw(g2);
        }
        
        // Remove dead particals.
        for (Partical p:removals)
            collection.remove(p);
    }

    public void  buildExplosionSplit (BufferedImage srcImg, 
                                      ExplodeTypes  type, 
                                      int           xOrigin,
                                      int           yOrigin,
                                      boolean       implode,    // false
                                      float         speedDelta, // 1.5
                                      int           time2Live,  // 50
                                      int           blkSize)    // 1
    {
        Partical   p = null;
        int        idx;
        float      ySpd, xSpd;  
        int        sh = srcImg.getHeight();
        int        sw = srcImg.getWidth();        
        switch (type)
        {				
            case VERTICAL:               
                ySpd = (float)(sh/2);
                xSpd = 0;
                for (idx = sh-1; idx >= 0; idx--)
                {
                    p = new Partical (srcImg, 0, idx, sw, 1);
                    collection.add (p);
                    
                    if (implode != false)
                    {
                        p.life   = time2Live;
                        p.xSpeed = 0.0f;
                        p.ySpeed = (ySpd * speedDelta);

                        p.x = xOrigin;
                        p.y = (yOrigin + (float)idx) + (time2Live * p.ySpeed);

                        p.ySpeed = -p.ySpeed;
                    }
                    else
                    {
                        p.x      = xOrigin;
                        p.y      = yOrigin + (float)idx;
                        p.xSpeed = 0.0f;
                        p.ySpeed = (ySpd * speedDelta);
                        p.life   = time2Live;
                    }
                    ySpd--;
                }        
                break;
                
            case DIAGRIGHT:
                ySpd = (float)(sh/2);
                xSpd = -(float)(sw/2);

                for (idx = sh-1; idx >= 0; idx--)
                {
                    p = new Partical (srcImg, 0, idx, sw, 1);
                    collection.add (p);
                    
                    if (implode != false)
                    {
                        p.life   = time2Live;
                        p.xSpeed = 0.0f + (xSpd * speedDelta);
                        p.ySpeed = 0.0f + (ySpd * speedDelta);

                        p.x = (xOrigin + (float)idx) + (time2Live * xSpd);
                        p.y = (yOrigin + (float)idx) + (time2Live * ySpd);

                        p.ySpeed = -ySpd;
                        p.xSpeed = -xSpd;
                    }
                    else
                    {
                        p.x      = xOrigin;
                        p.y      = yOrigin + (float)idx;
                        p.xSpeed = (xSpd * speedDelta);
                        p.ySpeed = (ySpd * speedDelta);
                        p.life   = time2Live;
                    }
                    ySpd--;
                    xSpd++;
                }
                break;
            
            case DIAGLEFT:			
                ySpd    = (float)(sh/2);
                xSpd    = (float)(sh/2);
                for (idx = sh-1; idx >= 0; idx--)
                {
                    p = new Partical (srcImg, 0, idx, sw, 1);
                    collection.add (p);
                    
                    if (implode != false)
                    {
                        p.life   = time2Live;
                        p.xSpeed = (xSpd * speedDelta);
                        p.ySpeed = (ySpd * speedDelta);

                        p.x = (xOrigin + (float)idx) + (time2Live * xSpd);
                        p.y = (yOrigin + (float)idx) + (time2Live * ySpd);

                        p.ySpeed = -ySpd;
                        p.xSpeed = -xSpd;							
                    }
                    else
                    {
                        p.x      = xOrigin;
                        p.y      = yOrigin + (float)idx;
                        p.xSpeed = (xSpd * speedDelta);
                        p.ySpeed = (ySpd * speedDelta);
                        p.life   = time2Live;
                    }
                    ySpd--;
                    xSpd--;                        
                }
                break;
            
            case HORIZONTAL:
                ySpd    = 0;
                xSpd    = (float)(sw/2);
                for (idx = sw-1; idx >= 0; idx--)
                {
                    p = new Partical (srcImg, idx, 0, 1, sh);
                    collection.add (p);
                    
                    if (implode != false)
                    {
                        p.life   = time2Live;
                        p.ySpeed = 0.0f;
                        p.xSpeed = (xSpd * speedDelta);

                        p.x = (xOrigin + (float)idx) + (time2Live * p.xSpeed);
                        p.y = yOrigin;

                        p.xSpeed = -p.xSpeed;
                    }
                    else
                    {
                        p.x      = xOrigin + (float)idx;
                        p.y      = yOrigin;
                        p.xSpeed = (xSpd * speedDelta);
                        p.ySpeed = 0.0f;
                        p.life   = time2Live;							
                    }
                    xSpd--;
                }
                break;
            
            case EXPLODE:
                {
                    ySpd = -(float)(sh/2.0f);						
                    for (int yPos = 0; yPos < sh; yPos += blkSize)
                    {
                        xSpd = -(float)(sw/2.0f);
                        for (int xPos = 0; xPos < sw; xPos+= blkSize)
                        {							
                            p = new Partical (srcImg, xPos, yPos, blkSize, blkSize);
                            collection.add (p);
                            
                            p.life   = time2Live;
                            p.ySpeed = ySpd * speedDelta;
                            p.xSpeed = xSpd * speedDelta;

                            p.x = xOrigin + xPos;
                            p.y = yOrigin + yPos;

                            if (implode != false)
                            {
                                p.x += (p.xSpeed * time2Live);
                                p.y += (p.ySpeed * time2Live);

                                p.ySpeed = -p.ySpeed;
                                p.xSpeed = -p.xSpeed;
                            }

                            xSpd += blkSize;
                        }
                        ySpd += blkSize;
                    }
                }
                break;
        }        
    }
}
