package Catan;

import java.util.LinkedList;
/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */

// Used in buy & Sell list box
public class TraderItem
{
    public Player                   owner      = null;
    public LinkedList<ResourceCard> tradeItems = new LinkedList<ResourceCard>();        
}    
