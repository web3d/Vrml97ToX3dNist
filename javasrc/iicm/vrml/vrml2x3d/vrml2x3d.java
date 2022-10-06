/*
 *
 *  This is a standalone translator for translating VRML97 file to X3D.
 *  Developed by Qiming Wang in Visualization and Usability Group in ITL,NIST.
 *  This is the version of 1.0.
 *  Updated on May 7, 2004
 *
 */


package iicm.vrml.vrml2x3d;

import iicm.SystemOut;
import iicm.vrml.pw.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;



public class vrml2x3d
{

static boolean transitional; // default is false

  public static void main (String args[])
  {

    SystemOut.println("args.length=" + args.length);
    SystemOut.println("****Vrml2 to X3d Translator, Version 1.0***");

    if ((args.length < 2) || (args.length > 3))  // 2 or 3 arguments allowed
    {
      SystemOut.println ("usage: vrml2x3d VRMLFILE.wrl X3dFILE.x3d [-transitionalDTD]");
      return;
    }

    SystemOut.print  ("Translating Vrml2 file "+args[0]+" to x3d file "+args[1]);
    if (args.length == 3) SystemOut.print  (" "+args[2]);
    SystemOut.println();

    transitional = false;
    if (args.length == 3)
    {
         if (args[2].compareToIgnoreCase("-transitionalDTD")==0)
         	transitional = true;
         else
         {
    		SystemOut.println("*** Illegal option encountered and ignored:  " + args[2]);
         }
    }

    try
    {
      FileOutputStream file = new FileOutputStream (args[1]);
      PrintStream os = new PrintStream (file);  // wrapper

      VRMLparser parser = new VRMLparser (Decompression.filterfile (args [0]));

      long time = System.currentTimeMillis ();
      GroupNode root = parser.readStream ();
      time = System.currentTimeMillis () - time;

      SystemOut.println ("*** Parsing time (ms): " + time);
      if (root != null)
      {
	SystemOut.println ("=== writing x3d data  ===");

	writeX3dHeader (os, "1.0", args[1]);
	root.writeX3dNodes (parser, os);
//	root.writeX3dNodes (parser, os, 1, 1);
//	root.writeX3dNodes (parser, os, 1, 0);
	writeX3dEnd (os);

	os.close ();
	SystemOut.println ("=== finished ===");

      }
      else
      {
        SystemOut.println ("[vrml2x3d] [Error] error on parsing " + args [0]);
        if (parser.getVersion () == 0.0f)
          SystemOut.println ("unrecognized header");
      }
    }
    catch (IOException e)
    {
      SystemOut.println ("[vrml2x3d] [Error] error on reading " + args [0]);
      SystemOut.println (e.getMessage ());  // just prints file name
    }
  }

  public static void writeX3dHeader(PrintStream w, String systemId, String x3dFileName) {
    w.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//  w.print("<?xml-stylesheet href=\"X3dToVrml97.xsl\" type=\"text/xsl\"?>\n");
//  w.print("<?cocoon-process type=\"xslt\"?>\n");

    if (transitional)
    {
	//  transitional doctype
	    w.print("<!-- Warning:  transitional DOCTYPE in source .x3d file -->\n");
	    w.print("<!DOCTYPE X3D PUBLIC \"http://www.web3d.org/specifications/x3d-3.0.dtd\" \"file:///www.web3d.org/TaskGroups/x3d/translation/x3d-3.0.dtd\">\n");
    }
    else    w.print("<!DOCTYPE X3D PUBLIC \"ISO//Web3D//DTD X3D 3.3//EN\"   \"http://www.web3d.org/specifications/x3d-3.3.dtd\">\n");

    w.print("<X3D version=\"3.3\" profile=\"Full\">\n");
    w.print(" <head>\n");
    w.print("   <meta name=\"title\" content=\"" + x3dFileName + "\"/>\n");
    w.print("   <meta name=\"description\" content=\"*enter description here, short-sentence summaries preferred*\"/>\n");
    w.print("   <meta name=\"creator\" content=\"*enter name of original author here*\"/>\n");
    w.print("   <meta name=\"translator\" content=\"*if manually translating VRML-to-X3D, enter name of person translating here*\"/>\n");
    Date time = new Date(System.currentTimeMillis());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    w.print("   <meta name=\"created\" content=\"*enter date of initial version here*\"/>\n");
    w.print("   <meta name=\"translated\" content=\"" + dateFormat.format(time) + "\"/>\n");
    w.print("   <meta name=\"modified\"    content=\"" + dateFormat.format(time) + "\"/>\n");
    w.print("   <meta name=\"version\" content=\"*enter version here*\"/>\n");
    w.print("   <meta name=\"reference\" content=\"*enter reference citation or relative/online url here*\"/>\n");
    w.print("   <meta name=\"reference\" content=\"*enter additional url/bibliographic reference information here*\"/>\n");
    w.print("   <meta name=\"requires\" content=\"*enter reference resource here if required to support function, delivery, or coherence of content*\"/>\n");
    w.print("   <meta name=\"rights\" content=\"*enter copyright information here* Example:  Copyright (c) Web3D Consortium Inc. 2006\"/>\n");
    w.print("   <meta name=\"drawing\" content=\"*enter drawing filename/url here*\"/>\n");
    w.print("   <meta name=\"image\" content=\"*enter image filename/url here*\"/>\n");
    w.print("   <meta name=\"MovingImage\" content=\"*enter movie filename/url here*\"/>\n");
    w.print("   <meta name=\"photo\" content=\"*enter photo filename/url here*\"/>\n");
    w.print("   <meta name=\"subject\" content=\"*enter subject keywords here*\"/>\n");
    w.print("   <meta name=\"accessRights\" content=\"*enter permission statements or url here*\"/>\n");
    w.print("   <meta name=\"warning\" content=\"*insert any known warnings, bugs or errors here*\"/>\n");
    w.print("   <meta name=\"identifier\" content=\"http://*enter online url address for this file here*/" + x3dFileName + "\"/>\n");
    w.print("   <meta name=\"generator\" content=\"Vrml97ToX3dNist, http://ovrt.nist.gov/v2_x3d.html\"/>\n");
    w.print("   <meta name=\"license\" content=\"../../license.html\"/>\n");
    w.print(" </head>\n");
    w.print(" <Scene>\n");
  }
  public static void writeX3dEnd(PrintStream w) {
    w.print(" </Scene>\n");
    w.print("</X3D>\n");
  }
} //vrml2x3d

