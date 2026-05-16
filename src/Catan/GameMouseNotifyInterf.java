package Catan;

import java.util.LinkedList;


/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */
public interface GameMouseNotifyInterf 
{   
    void GMNI_MouseSelectEvent  (Object srcObj, LinkedList objsSelected, ObjSelectType types);
}
