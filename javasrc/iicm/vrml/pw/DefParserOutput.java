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
 * DefParserOutput.java
 *
 * created: mpichler, 19960805
 * changed: mpichler, 19960805
 *
 * $Id: DefParserOutput.java,v 1.3 1997/05/22 15:38:02 apesen Exp $
 */

package iicm.vrml.pw;

import iicm.SystemOut;

/**
 * DefParserOutput - default: write parser messages to SystemOut
 * Copyright (c) 1996 IICM
 *
 * @author Michael Pichler, Karin Roschker
 * @version 0.1, changed:  5 Aug 96
 */


public class DefParserOutput implements ParserOutput
{
  public void error (String err)
  {
    SystemOut.println ("[DefParserOutput.error] " + err);
  }

  public void warning (String warn)
  {
    SystemOut.println ("[DefParserOutput.warning] " + warn);
  }

  public void verbose (String msg)
  {
    SystemOut.println ("[DefParserOutput] " + msg);
  }

  public void debug (String msg)
  {
    SystemOut.println ("pw: " + msg);
  }

} // DefParserOutput
