/*
 * MetaNode.java
 *
 * created: Don Brutzman 17 FEB 2006
 *
 */


package iicm.vrml.pw; 

import java.io.*;
import java.util.*;


// MetaNode

public class ProfileNode extends Node
{
  public String name;

  public String nodeName ()
  {
    return Node.PROFILE_KEYWORD;
  }

  public void traverse (Traverser t)
  {
    t.tProfile (this);
  }

  ProfileNode ()
  {
    name = "FULL";
  }

  ProfileNode (String pName)
  {
    name = pName;
  }

  /**
   * the ProfileNode represents a PROFILE statement;
   * it currently only exists to be output again, voila:
   */

  public void writeNode (PrintStream os, Hashtable writtenrefs)
  {
    // a PROFILE cannot be given a name via DEF
    os.println (Node.PROFILE_KEYWORD + " " + name);
  }

  public void writeX3dNode (PrintStream os, Hashtable writtenrefs) 
  {
    // *** Profile information needs to go into X3D node
    os.println ("<!-- profle name=\""+name+"\""+"\"-->");
  }
} // MetaNode  
