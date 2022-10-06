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

public class MetaNode extends Node
{
  public String name, content;

  public String nodeName ()
  {
    return Node.ROUTE_KEYWORD;
  }

  public void traverse (Traverser t)
  {
    t.tMeta (this);
  }

  MetaNode ()
  {
  }

  MetaNode (String pName, String pContent)
  {
    name = pName;
    content = pContent;
  }

  /**
   * the MetaNode represents a META statement;
   * it currently only exists to be output again, voila:
   */

  public void writeNode (PrintStream os, Hashtable writtenrefs)
  {
    // a META cannot be given a name via DEF
    os.println (Node.META_KEYWORD + " " + name + " " + content);
  }

  public void writeX3dNode (PrintStream os, Hashtable writtenrefs) 
  {
    os.println ("<meta  name=\""+name+"\""+
		   " content=\""+content+"\""+"\"/>");
  }
} // MetaNode  
