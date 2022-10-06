/*
 * <copyright>
 *
 * Copyright (c) 1996,97
 * Institute for Information Processing and Computer Supported New Media (IICM),
 * Graz University of Technology, Austria.
 *
 * This file is part of the `pw' VRML 2.0 parser.
 *
 * </copyright>
 */
/*
 * ProtoInstance.java - instance of a PROTO node
 *
 * created: mpichler, 19961001
 * changed: mpichler, 19970108
 *
 * $Id: ProtoInstance.java,v 1.5 1997/05/22 16:11:45 apesen Exp $
 */


package iicm.vrml.pw; 

import java.io.*;
import java.util.*;


/**
 * ProtoInstance - instance of PROTO or EXTERNPROTO node
 * Copyright (c) 1996,97 IICM
 *
 * @author Michael Pichler, Karin Roschker
 * @version 0.1, changed:  8 Jan 97
 */

public class ProtoInstance extends Node
{
  ProtoNode bap;  // the PROTO node that describes this instance

  // TODO: for traversal of this PROTO instance we need a copy
  // of the children of the PROTO node with the current field values

  public String nodeName ()
  {
    return bap.protoname;
  }

  public void traverse (Traverser t)
  {
    t.tProtoInstance (this);
  }

  ProtoInstance (ProtoNode parent)
  {
    // parent should be non-null
    bap = parent;

    // create a copy of each protofield

    // (subfields = parent.protofields; would overwrite proto fields)
    Hashtable bapfields = parent.protofields;
    Enumeration e = bapfields.keys ();
    while (e.hasMoreElements ())
    {
      String fname = (String) e.nextElement ();
      Field f = (Field) bapfields.get (fname);
      //subfields.put (fname, f.newFieldInstance ());
      // following are modified 4/6/2000 by wang. 
      if (f.getFieldClass() == Field.F_EVENTIN || 
	  f.getFieldClass() == Field.F_EVENTOUT)
	subfields.put (fname, f);
      else {
	subfields.put (fname, f.newFieldInstance ());
      }

      //subfields.put (fname, f);
    }
  }

  public ProtoNode getNode() {
    return bap;
  }

  // the current implementation allows reading the fields
  // and writes them back to the output (writeNode works)

  // for efficient traversal of the nodes,
  // a copy of the children subtree will have to be made,
  // where protofields (protoISfield refers to bap's field)
  // are substituted by the field values of this instance
  
  public void writeX3dNode(VRMLparser parser, PrintStream os, Hashtable writtenrefs, int depth) {

    tab(os, depth);

    // trap GeoVrml and H-Anim prototypes here for native X3D node support
    // don't forget to screen ExternProtos and convert geoSystem attribute values

// os.println("<!-- ... starting writeX3dNode() -->\n");

    if (nodeName().startsWith("Geo"))  // Treat GeoSpatial prototypes as native nodes
    {    
    	os.print("<"+nodeName());
    }
    else
    {
    	os.print("<ProtoInstance name=\""+nodeName()+"\"");
    }
    if (objname != null) 
    {
      if (((Node) writtenrefs.get (objname)) != this)  // first time
      {
	os.print(" DEF=\""+objname+"\"");
        //writtenrefs.put (objname, this);
      }
      else  // already written this instance
      {
	os.println(" USE=\""+objname+"\"/>\n");
        return;
      }
    }

    if (nodeName().startsWith("Geo"))  // Treat GeoSpatial prototypes as native nodes
    {
	  // nothing else needed for form attribute="value"
    }
    else
    {
	  os.print(">\n");
    }
    
    Enumeration e = subfields.keys ();
    while (e.hasMoreElements ())
    {
      String fname = (String) e.nextElement ();
      Field f = (Field) subfields.get (fname);
      
      if (f instanceof SFNode) {
	if (f.wasChanged() && ((SFNode) f).node !=null) {
    if (nodeName().startsWith("Geo"))  // Treat GeoSpatial prototypes as native nodes
    {
	  os.print(" "+fname+"=");
    }
    else
    {
	  tab(os, depth+1);
	  os.print("<fieldValue name=\""+fname+"\">\n");
    }
	  ((SFNode) f).writeX3dValue(os, writtenrefs, depth+2);
    if (nodeName().startsWith("Geo"))  // Treat GeoSpatial prototypes as native nodes
    {
	  // nothing else needed for form attribute="value"
    }
    else
    {
	  tab(os, depth+1);
	  os.print("</fieldValue>\n");
    }
	}
      }
      else if (f instanceof MFNode) {
	if (f.wasChanged() && ((MFNode) f).nodes.size()>0) {
    if (nodeName().startsWith("Geo"))  // Treat GeoSpatial prototypes as native nodes
    {
	  os.print(" "+fname+"=");
    }
    else
    {
	  tab(os, depth+1);
	  os.print("<fieldValue name=\""+fname+"\">\n");
    }
	  //tab(os, depth+2);
	  ((MFNode) f).writeX3dValue(os, writtenrefs, depth+2);
    if (nodeName().startsWith("Geo"))
    {
	  // nothing else needed for form attribute="value"
    }
    else
    {
	  tab(os, depth+1);
	  os.print("</fieldValue>\n");
    }
	}
      }
      else {
	if (f.wasChanged() && ((f.getFieldClass() & Field.F_FIELD) != 0))
	  {
    if (nodeName().startsWith("Geo"))  // Treat GeoSpatial prototypes as native nodes
    {
	  os.print(" "+fname+"=");
    }
    else
    {
	    tab(os, depth+1);
	    os.print("<fieldValue name=\""+fname+"\"");
    }
	    if ((f.fieldName()).equals("MFString")==true) 
    if (nodeName().startsWith("Geo"))
    {
	      os.print("\'");
    }
    else
    {
	      os.print(" value=\'");
    }
	    else
    if (nodeName().startsWith("Geo"))
    {
	      os.print("\"");
    }
    else
    {
	      os.print(" value=\"");
    }
	    f.writeX3dValue (os, writtenrefs);  // no IS allowed here (?!)	
	    if ((f.fieldName()).equals("MFString")==true) 
	      os.print("\'");
	    else
	      os.print("\"");	
    if (nodeName().startsWith("Geo"))
    {
	  // nothing else needed for form attribute="value"
    }
    else
    {
	    os.print("/>\n");
    }
	  }
	}
    } // end fieldValues
    tab(os, depth);
    
    if (nodeName().startsWith("Geo"))
    {    
    	os.print("</"+nodeName()+">\n");
    }
    else
    {
    	os.print("</ProtoInstance>\n");
    }
    
  // os.println("<!-- ... finished writeX3dNode() -->\n");
  }
  
} // ProtoInstance

