/*
 *  
 *  This is a standalone translator for translating VRML97 file to X3D.   
 *  Developed by Qiming Wang in Visualization and Usability Group in ITL,NIST.
 *  This is the version of 0.1.
 *  Updated on July 23/2001.
 *
 */
package iicm.vrml.vrml2x3d;

import iicm.SystemOut;
import iicm.vrml.pw.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Vrml97Converter
{
  public Vrml97Converter()
  {
  }
  
  public static void main(String[] args)
  {
    SystemOut.println("args.length=" + args.length);
    SystemOut.println("****Vrml2 to X3d Translator, Version 1.0***");

    if (args.length != 2) { // 2 arguments allowed
      SystemOut.println("usage: Vrml97Converter VRMLFILE.wrl X3dFILE.x3d");
      return;
    }

    SystemOut.print("Translating Vrml2 file " + args[0] + " to x3d file " + args[1]);

    try {
      new Vrml97Converter().convert(args[0], args[1],true);
    }
    catch (Exception ex) {
      SystemOut.println("Error processing file(s): " + ex.getLocalizedMessage());
    }
  }

  public void convert(String inputFile, String outputFile) throws Exception
  {
    convert(inputFile,outputFile,false);
  }
  
  public void convert(String inputFile, String outputFile, boolean verbose) throws Exception
  {
    File outputFileObject = new File(outputFile);
    FileOutputStream file = new FileOutputStream(outputFileObject);
    PrintStream os = new PrintStream(file);

    VRMLparser parser = new VRMLparser(Decompression.filterfile(inputFile));

    long time = System.currentTimeMillis();
    
    GroupNode root = parser.readStream();
    
    if(verbose) {
      time = System.currentTimeMillis() - time;
      SystemOut.println("*** Parsing time (ms): " + time);
    }
    if (root != null) {
      if(verbose)
        SystemOut.println("=== writing x3d data  ===");
      
      writeX3dHeader(os, "1.0", outputFileObject);
      root.writeX3dNodes (parser, os);
    //root.writeX3dNodes (parser, os, 1, 1);
    //root.writeX3dNodes (parser, os, 1, 0);
      writeX3dEnd(os);

      os.close();
      
      if(verbose)
        SystemOut.println("=== finished ===");
    }
    else {
      String msg = "[vrml2x3d] [Error] error on parsing " + inputFile;
      if (parser.getVersion() == 0.0f)
        msg = "unrecognized header on parsing " + inputFile;
      throw new Exception(msg);
    }
  }

  private void writeX3dHeader(PrintStream w, String systemId, File outputFileObject)
  {
    w.print("<?xml version='1.0' encoding='UTF-8'?>\n");
    w.print("<!DOCTYPE X3D PUBLIC \"ISO//Web3D//DTD X3D 3.3//EN\" \"http://www.web3d.org/specifications/x3d-3.3.dtd\">\n");

    w.print("<X3D version='3.3' profile='Immersive'  xmlns:xsd='http://www.w3.org/2001/XMLSchema-instance' xsd:noNamespaceSchemaLocation='http://www.web3d.org/specifications/x3d-3.3.xsd'>\n");
    w.print(" <head>\n");
    w.print("   <meta name='title' content='" + outputFileObject.getName() + "'/>\n");
    w.print("   <meta name='description' content='*enter description here, short-sentence summaries preferred*'/>\n");
    w.print("   <meta name='creator' content='*enter name of original author here*'/>\n");
    w.print("   <meta name='translator' content='*if manually translating VRML-to-X3D, enter name of person translating here*'/>\n");
    Date time = new Date(System.currentTimeMillis());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    w.print("   <meta name='created' content='*enter date of initial version here*'/>\n");
    w.print("   <meta name='translated' content='"  + dateFormat.format(time) + "'/>\n");
    w.print("   <meta name='modified'    content='" + dateFormat.format(time) + "'/>\n");
    w.print("   <meta name='version' content='*enter version here*'/>\n");
    w.print("   <meta name='reference' content='*enter reference citation or relative/online url here*'/>\n");
    w.print("   <meta name='reference' content='*enter additional url/bibliographic reference information here*'/>\n");
    w.print("   <meta name='requires' content='*enter reference resource here if required to support function, delivery, or coherence of content*'/>\n");
    w.print("   <meta name='rights' content='*enter copyright information here* Example:  Copyright (c) Web3D Consortium Inc. 2006'/>\n");
    w.print("   <meta name='drawing' content='*enter drawing filename/url here*'/>\n");
    w.print("   <meta name='image' content='*enter image filename/url here*'/>\n");
    w.print("   <meta name='MovingImage' content='*enter movie filename/url here*'/>\n");
    w.print("   <meta name='photo' content='*enter photo filename/url here*'/>\n");
    w.print("   <meta name='subject' content='*enter subject keywords here*'/>\n");
    w.print("   <meta name='accessRights' content='*enter permission statements or url here*'/>\n");
    w.print("   <meta name='warning' content='*insert any known warnings, bugs or errors here*'/>\n");
    w.print("   <meta name='identifier' content='http://*enter online url address for this file here* /" + outputFileObject.getAbsolutePath() + "'/>\n");
    w.print("   <meta name='generator' content='Vrml97ToX3dNist, http://ovrt.nist.gov/v2_x3d.html'/>\n");
    // TODO boolean switch to enable/disable
    w.print("   <meta name='generator' content='X3D-Edit, https://savage.nps.edu/X3D-Edit'/>\n");
    w.print("   <meta name='license' content='../../license.html'/>\n");
    w.print(" </head>\n");
    w.print(" <Scene>\n");
  }

  private void writeX3dEnd(PrintStream w)
  {
    w.print("  </Scene>\n");
    w.print("</X3D>\n");
  }
}

