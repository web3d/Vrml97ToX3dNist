/*
 * UtilTest.java
 * utils test app
 * Copyright (c) 1996,97 IICM
 *
 * created: mpichler, 19960808
 * changed: mpichler, 19961001
 *
 * $Id: UtilTest.java,v 1.4 1997/05/22 16:29:45 apesen Exp $
 */

package iicm.vrml.pwutils;

import iicm.SystemOut;

class UtilTest
{
  public static void printIA (IntArray ia)
  {
    int[] data = ia.getData ();
    int num = ia.getCount ();

    for (int i = 0;  i < num;  i++)
      SystemOut.print (" " + data [i]);
    SystemOut.println ();
  }


  public static void main (String args[])
  {
    int ivals[] = { 7, 11, 13 };
    IntArray ia = new IntArray (ivals);
    FloatArray fa = new FloatArray ();
    DoubleArray da = new DoubleArray ();

    SystemOut.println ("empty array lenghts: " + fa.getCount () + ", " + da.getCount ());

    SystemOut.print ("pre-initialized array, " + ia.getCount () + " elements:");  printIA (ia);
    IntArray ia3 = new IntArray (ia);
    SystemOut.print ("cloned array, " + ia3.getCount () + " elements:");  printIA (ia3);

    ia.clearData ();  // empty again
    SystemOut.print ("empty array:");  printIA (ia);
    ia.append (27);
    SystemOut.print ("one element:");  printIA (ia);
    ia.append (5);
    SystemOut.print (" 2 elements:");  printIA (ia);
    ia.append (13);
    SystemOut.print (" 3 elements:");  printIA (ia);
    ia.append (-4);
    SystemOut.print (" 4 elements:");  printIA (ia);
    ia.append (~0 >>> 1);
    SystemOut.print (" 5 elements:");  printIA (ia);
    ia.append (1001);
    SystemOut.print (" 6 elements:");  printIA (ia);
  }
}
