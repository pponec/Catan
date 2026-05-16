/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Catan.Partical;

/**
 *
 * @author steven
 */
public enum ExplodeTypes {NULL(-1), VERTICAL(0), HORIZONTAL(1), DIAGLEFT(2), DIAGRIGHT(3), EXPLODE(4);
                          private int value;
                          private ExplodeTypes (int i) {value = i;}
                          public int          toValue () { return value; }
                          static public ExplodeTypes toType (int v)                            
                          { 
                              switch (v)
                              {
                                  case 0: return VERTICAL;
                                  case 1: return HORIZONTAL;
                                  case 2: return DIAGLEFT;
                                  case 3: return DIAGRIGHT;
                                  case 4: return EXPLODE;                                  
                              }
                              return NULL;
                          }
}; 