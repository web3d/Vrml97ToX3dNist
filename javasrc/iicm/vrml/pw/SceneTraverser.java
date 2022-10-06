//package com.ibm.hrl.xmleditor.extension.vrml;
package iicm.vrml.pw;

// might we instead package this class in iicm.vrml.pw; ??

import iicm.SystemOut;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.CDATASection;

/**
 * Traverses VRML scene graph.  It walks through a VRML97 scene nodes hiearchy to extract
 * relevant information and store the data in a W3C DOM <code>Document</code> class.
 *
 * @author Jane Wu
 * @version 1.0 last update: 11 June 2002
 */
public class SceneTraverser extends Traverser
{
  private Document doc;
  private Element sceneElement;
  private Element parentElement;

  private Element parentProto;
  private Element parentShape; //only set for Appearance & Geometry nodes
  private Element parentAppearance; //only set for Material, TextureTransform, & Texture nodes traverses
  private Element parentText; //only set for FontStyle node traverse
  private Element parentSound; //only set for children of Sound, i.e. AudioClip, MoviesTexture or ProtoInstance.
  private Element parentOfColor; //only set for Color node traverse.
  private Element parentOfNormal; //only set for Normal node traverse.
  private Element parentOfTextureCoordinate; //only set for TextureCoordinate node traverse.
  private Element parentOfCoordinate; //only set for Coordinate node traverse.

  private Vector<String>  defNames;
  private Vector<Element> parents;

  private int currentLevel;
  private boolean parentElementSet = false; //need constant reset after it is set to true
  private boolean setFieldValue = false; //only true when setting fieldValue child for ProtoInstance element
  private boolean setExternProto = false; //only true when setting ExternProtoDeclare element

  private boolean isChildOfSound = false; //only true when setting children for Sound element
  private boolean isChildOfShape = false; //only true when setting children for Shape element
  private boolean isChildOfAppearance = false; //only true when setting children for Appearance element
  private boolean isChildOfText = false; //only true when setting children for Text element

  private boolean isColorChild = false; //true when setting Color element, could be true for ProtoInstance as well
  private boolean isNormalChild = false; //true when setting Normal element, could be true for ProtoInstance as well
  private boolean isTextureCoordinateChild = false; //true when setting TextureCoordinate, could be true for ProtoInstance as well
  private boolean isCoordinateChild = false; //true when setting Coordinate element, could be true for ProtoInstance as well

  /**
   * Constructs a <code>SceneTraverser</code>.
   * @param d W3C DOM Document
   * @param sceneRoot the root Scene element
   */
  public SceneTraverser(Document d, Element sceneRoot)
  {
    doc = d;
    sceneElement = sceneRoot;
    parentElement = sceneElement;

    defNames = new Vector<String>();
    parents = new Vector<Element>();

    parents.addElement(sceneElement);
    currentLevel = 0;
  }



  /**
   * Overrides super class's method.
   * @param node the node to traverse
   * @param element the element to check
   */
  public void checkISconnect (Node node, Element element)
  {
    try { //debug purpose only

	int IS_num = 0;
	for (Enumeration e = node.subfields.keys ();  e.hasMoreElements ();  )
	{
		String fname = (String) e.nextElement ();
		Field f = (Field) node.subfields.get(fname);
		if (f.protoIS==true) IS_num++;
	}
        if (IS_num > 0) SystemOut.println ("checkISconnect() node name=" + node.nodeName() + ", IS_count=" + IS_num);

	for (Enumeration ef = node.subfields.keys();  ef.hasMoreElements ();  )
      	{
        	String  fname = (String) ef.nextElement ();
        	Field f = (Field) node.subfields.get (fname);
        	if (f.protoIS) SystemOut.println ("checkISconnect() fname=" + fname + ", f.protoIS=" + f.protoIS + ", f.protoIScontent=" + f.protoIScontent);
      	}

      	if (IS_num>0)
      	{
    		Element ISelement = doc.createElement("IS");

		element.appendChild(ISelement);	// connect this to parent...

		for (Enumeration ef = node.subfields.keys ();  ef.hasMoreElements ();  )
		{
        		String  fname = (String) ef.nextElement ();
        		Field f = (Field) node.subfields.get (fname);
        		if (f.protoIS)
        		{
				Element connectElement = doc.createElement("connect");
				ISelement.appendChild(connectElement);
				connectElement.setAttribute ("nodeField", fname);
				if (f.protoISfield.protoISname != null)
					connectElement.setAttribute ("protoField", f.protoISfield.protoISname);
			  SystemOut.println ("checkISconnect() connectElement" + connectElement);
        		}
        	}
 	}
    }
    catch (Exception e)
    {
      e.printStackTrace(System.err);
    }
  }//end of checkISconnect()


  /**
   * Overrides super class's method.
   * @param n the node to traverse
   */
  public void tGroupNode(GroupNode n)
  {
    try { //debug purpose only
      Enumeration e = n.getChildrenEnumerator();

      while (e.hasMoreElements())
      {
   //   ((Node) e.nextElement()).traverse(this);
        Node node = ((Node) e.nextElement());

   //   checkISconnect (node, element); // no IS/connect for scene root
        node.traverse(this);
      }
      if (currentLevel > 0)
      {
        currentLevel--;
        parents.removeElementAt(currentLevel+1);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace(System.err);
    }
  }//end of tGroupNode()

  /**
   * Traverses a ProtoInstance node.
   * @param n a ProtoInstance node to traverse
   */
  public void tProtoInstance(ProtoInstance n)
  {
  	SystemOut.println("tProtoInstance start");

    Element element = doc.createElement("ProtoInstance");

//  checkISconnect (n, element); // ProtoInstance IS/connects occur under fieldValue

/*
    if (isChildOfSound)
    {
      parentSound.appendChild(element);
      isChildOfSound = false; //reset
      SystemOut.println("isChildOfSound");
    }
    else if (isChildOfShape)
    {
      parentShape.appendChild(element);
      isChildOfShape = false; //reset
      SystemOut.println("isChildOfShape");
    }
    else if (isChildOfAppearance)
    {
      parentAppearance.appendChild(element);
      isChildOfAppearance = false; //reset
      SystemOut.println("isChildOfAppearance");
    }
    else if (isChildOfText)
    {
      parentText.appendChild(element);
      isChildOfText = false; //reset
      SystemOut.println("isChildOfText");
    }
    else if (isColorChild)
    {
      parentOfColor.appendChild(element);
      isColorChild = false; //reset
      SystemOut.println("isColorChild");
    }
    else if (isNormalChild)
    {
      parentOfNormal.appendChild(element);
      isNormalChild = false; //reset
      SystemOut.println("isNormalChild");
    }
    else if (isTextureCoordinateChild)
    {
      parentOfTextureCoordinate.appendChild(element);
      isTextureCoordinateChild = false; //reset
      SystemOut.println("isTextureCoordinateChild");
    }
    else if (isCoordinateChild)
    {
      parentOfCoordinate.appendChild(element);
      isCoordinateChild = false; //reset
      SystemOut.println("isCoordinateChild");
    }
    else
    {
*/
      if (!parentElementSet)
        parentElement = (Element) parents.elementAt(currentLevel);
      else
        parentElementSet = false; //reset
      parentElement.appendChild(element);
      parentElementSet = true;
      SystemOut.println("parentElement: " + parentElement);
//  }

    parents.addElement(element);
    currentLevel = parents.size() - 1;
    SystemOut.println("added ProtoInstance.  parents: " + parents+ ", currentLevel: " + currentLevel);
    SystemOut.println("parentElementSet=" + parentElementSet);

        checkISconnect (n, element);

    element.setAttribute("name", n.nodeName());

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.subfields != null)
    {
      setFieldValue = true;
      Enumeration e = n.subfields.keys();
      while(e.hasMoreElements())
      {
        String fname = (String) e.nextElement(); // fieldValue
        Element fieldElement = getFieldElement((Field) n.subfields.get(fname), fname);
        if (fieldElement != null)
        {
          element.appendChild(fieldElement);
	//checkISconnect ((Field)n.subfields.get(fname), fieldElement);  // how to perform valid typecast?
        }
      }
      setFieldValue = false;
    }
  }//end of tProtoInstance()

  /**
   * Traverses a Proto node.
   * @param n a Proto node to traverse
   */
  public void tProtoNode(ProtoNode n)
  {
  	// still need IS/connect for <field> connections
  try {
    SystemOut.println("tProtoNode breakpoint A");
    SystemOut.println("tProtoNode start, parents: " + parents+ ", currentLevel: " + currentLevel);
    Element element;

    if (n.external == true)
    {
      setExternProto = true;
      element = doc.createElement("ExternProtoDeclare");
      // ExternProtoDeclare should always be the first level nodes
      // sceneElement.appendChild(element);
    }
    else
    {
      setExternProto = false;
      element = doc.createElement("ProtoDeclare");
      parentProto = element;
      // ProtoDeclare should always be the first level nodes
      // sceneElement.appendChild(element);
    }
    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);
    parentElementSet = true;

    parents.addElement(element);
    currentLevel = parents.size() - 1;
    SystemOut.println("added [Extern]ProtoDeclare.  parents: " + parents+ ", currentLevel: " + currentLevel);
    SystemOut.println("parentElementSet=" + parentElementSet);

    element.setAttribute("name", n.protoname);
    SystemOut.println("tProtoNode breakpoint B");

    if (n.urls_ != null)
      element.setAttribute("url", convertToString(n.urls_.getValueData(), n.urls_.getValueCount(), true));

//  sceneElement.element([Extern]ProtoDeclare).elementProtoInterface.fieldElement*

    Element elementProtoInterface = doc.createElement("ProtoInterface");

    if (n.protofields != null)
    {
      Enumeration e = n.protofields.keys();
      if(e.hasMoreElements())
      {
        element.appendChild(elementProtoInterface);
      }

      parents.addElement(elementProtoInterface);
      currentLevel = parents.size() - 1;
      parentElement = (Element) parents.elementAt(currentLevel);
      parentElementSet = true;
      SystemOut.println("elementProtoInterface added, parents: " + parents+ ", currentLevel: " + currentLevel + ", parentElement: " + parentElement);

      while(e.hasMoreElements())
      {
        String fname = (String) e.nextElement();
        Element fieldElement = getFieldElement((Field) n.protofields.get(fname), fname);
        elementProtoInterface.appendChild(fieldElement);
      }
      parents.removeElementAt(currentLevel);
      currentLevel--;
      parentElement = (Element) parents.elementAt(currentLevel);
      parentElementSet = true;
      SystemOut.println("elementProtoInterface fields complete, parents: " + parents+ ", currentLevel: " + currentLevel + ", parentElement: " + parentElement);
    }
//  sceneElement.element([Extern]ProtoDeclare).elementProtoInterface.fieldElement*
//  sceneElement.element([Extern]ProtoDeclare).elementProtoBody.*

    Element elementProtoBody = doc.createElement("ProtoBody");
    element.appendChild(elementProtoBody);

    setExternProto = false; //reset
    SystemOut.println("tProtoNode breakpoint C");

    if (n.getChildrenEnumerator() != null)
    {
      parents.addElement(elementProtoBody);
      currentLevel = parents.size() - 1;
      parentElement = (Element) parents.elementAt(currentLevel);
      parentElementSet = true;
      SystemOut.println("elementProtoBody added, parents: " + parents+ ", currentLevel: " + currentLevel + ", parentElement: " + parentElement);
      if (n.getChildrenEnumerator().hasMoreElements())
      {
        SystemOut.println("n.getChildrenEnumerator().hasMoreElements() is true");
	tGroupNode(n);
      }
      parents.removeElementAt(currentLevel);
      currentLevel--;
      parentElement = (Element) parents.elementAt(currentLevel);
      parentElementSet = true;
      SystemOut.println("elementProtoBody complete, parents: " + parents + ", currentLevel: " + currentLevel + ", parentElement: " + parentElement);
    }
    SystemOut.println("parents: " + parents);
    SystemOut.println("parents[0]=" + parents.elementAt(0));
    SystemOut.println("element: " + element);
    SystemOut.println("parentElement: " + parentElement);
    SystemOut.println("tProtoNode breakpoint D");
  }
  catch (Exception eCatchAll)
  {
  	eCatchAll.printStackTrace(System.err);
  }
  }//end of tProtoNode()

  //Followings are Bindable nodes:

  /**
   * Traverses a Viewpoint node.
   * @param n a Viewpoint node to traverse
   */
  public void tViewpoint(Viewpoint n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.description != null)
    {
      element.setAttribute("description", n.description.getValue());
    }

    if (n.fieldOfView.getValue() != 0.785398f)
      element.setAttribute("fieldOfView", "" + n.fieldOfView.getValue());

    if (n.jump.getValue() != true)
      element.setAttribute("jump", "false");

    float[] defaultOrientation = {0, 0, 1, 0};
    if (!arraysEqual(n.orientation.getValue(), defaultOrientation))
    {
      float[] orientation = n.orientation.getValue();
      element.setAttribute("orientation", convertToString(orientation, orientation.length));
    }

    float[] defaultPosition = {0, 0, 10};
    if (!arraysEqual(n.position.getValue(), defaultPosition))
    {
      float[] position = n.position.getValue();
      element.setAttribute("position", convertToString(position, position.length));
    }
  }//end of tViewpoint()

  /**
   * Traverses a NavigationInfo node.
   * @param n a NavigationInfo node to traverse
   */
  public void tNavigationInfo(NavigationInfo n)
  {
//  SystemOut.println("tNavigationInfo: Breakpoint A");
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.type != null)
    {
      element.setAttribute("type",
        convertToString(n.type.getValueData(), n.type.getValueCount(), true));
    }

    if (n.speed.getValue() != 1)
      element.setAttribute("speed", "" + n.speed.getValue());

    if (n.headlight.getValue() != true)
    {
      boolean flag = n.headlight.getValue();
      element.setAttribute("headlight", "false");
    }

    float[] defaultAvatarSize = {0.25f, 1.6f, 0.75f};
    if (!arraysEqual(n.avatarSize.getValueData(), defaultAvatarSize))
    {
      element.setAttribute("avatarSize",
        convertToString(n.avatarSize.getValueData(), n.avatarSize.getValueCount()));
    }

    if (n.visibilityLimit.getValue() != 0)
      element.setAttribute("visibilityLimit", "" + n.visibilityLimit.getValue());
  }//end of tNavigationInfo()

  /**
   * Traverses a Fog node.
   * @param n a Fog node to traverse
   */
  public void tFog(Fog n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultcolor = {1, 1, 1};
    if (!arraysEqual(n.color.getValue(), defaultcolor))
      element.setAttribute("color", convertToString(n.color.getValue(), 3));

    if (n.fogType.getValue().equals("LINEAR"))
      element.setAttribute("fogType", n.fogType.getValue());

    if (n.visibilityRange.getValue() != 0.0f)
      element.setAttribute("visibilityRange", "" + n.visibilityRange.getValue());
  }//end of tFog()

  /**
   * Traverses a Background node.
   * @param n a Background node to traverse
   */
  public void tBackground(Background n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.backUrl != null)
    {
      element.setAttribute("backUrl",
        convertToString(n.backUrl.getValueData(), n.backUrl.getValueCount(), true));
    }

    if (n.bottomUrl != null)
    {
      element.setAttribute("bottomUrl",
        convertToString(n.bottomUrl.getValueData(), n.bottomUrl.getValueCount(), true));
    }

    if (n.frontUrl != null)
    {
      element.setAttribute("frontUrl",
        convertToString(n.frontUrl.getValueData(), n.frontUrl.getValueCount(), true));
    }

    float[] defaultgroundAngle = {0, 0, 0, 0};
    if (!arraysEqual(n.groundAngle.getValueData(), defaultgroundAngle))
    {
      element.setAttribute("groundAngle",
        convertToString(n.groundAngle.getValueData(), n.groundAngle.getValueCount()));
    }

    float[] defaultgroundColor = {0, 0, 0, 0};
    if (!arraysEqual(n.groundColor.getValueData(), defaultgroundColor))
    {
      element.setAttribute("groundColor",
        convertToString(n.groundColor.getValueData(), n.groundColor.getValueCount() * 3, 3));
    }

    if (n.leftUrl != null)
    {
      element.setAttribute("leftUrl",
        convertToString(n.leftUrl.getValueData(), n.leftUrl.getValueCount(), true));
    }

    if (n.rightUrl != null)
    {
      element.setAttribute("rightUrl",
        convertToString(n.rightUrl.getValueData(), n.rightUrl.getValueCount(), true));
    }

    float[] defaultskyAngle = {0, 0, 0, 0};
    if (!arraysEqual(n.skyAngle.getValueData(), defaultskyAngle))
    {
      element.setAttribute("skyAngle",
        convertToString(n.skyAngle.getValueData(), n.skyAngle.getValueCount()));
    }

    float[] defaultskyColor = {0, 0, 0, 0};
    if (!arraysEqual(n.skyColor.getValueData(), defaultskyColor))
    {
      element.setAttribute("skyColor",
        convertToString(n.skyColor.getValueData(), n.skyColor.getValueCount() * 3, 3));
    }

    if (n.topUrl != null)
    {
      element.setAttribute("topUrl",
        convertToString(n.topUrl.getValueData(), n.topUrl.getValueCount(), true));
    }
  }//end of tBackground()

  /**
   * Traverses a ROUTE node (statement).
   * @param n a ROUTE node to traverse
   */
  public void tRoute(RouteNode n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.fromEvent != null)
    {
      element.setAttribute("fromField", n.fromEvent);
    }

    if (n.fromNode != null)
      element.setAttribute("fromNode", "" + n.fromNode.objname);

    if (n.toEvent != null)
    {
      element.setAttribute("toField", n.toEvent);
    }

    if (n.toNode != null)
      element.setAttribute("toNode", n.toNode.objname);
  }//end of tRoute()

  /**
   * Traverses a META node (statement).
   * @param n a META node to traverse
   */
  public void tMeta(MetaNode n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.name != null)
    {
      element.setAttribute("name", n.name);
    }

    if (n.content != null)
      element.setAttribute("content", "" + n.content);
  }//end of tMeta()

  /**
   * Traverses a PROFILE node (statement).
   * @param n a PROFILE node to traverse
   */
  public void tProfile(ProfileNode n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.name != null)
    {
      element.setAttribute("name", n.name);
    }
  }//end of tProfile()

  //Followings are Interpolator nodes:

  /**
   * Traverses a ScalarInterpolator node.
   * @param n a ScalerInterpolator node to traverse
   */
  public void tScalarInterpolator(ScalarInterpolator n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //false
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultkey = {0, 0, 0, 0};
    if (!arraysEqual(n.key.getValueData(), defaultkey))
    {
      element.setAttribute("key",
        convertToString(n.key.getValueData(), n.key.getValueCount(), 6));
    }

    float[] defaultkeyValue = {0, 0, 0, 0};
    if (!arraysEqual(n.keyValue.getValueData(), defaultkeyValue))
    {
      element.setAttribute("keyValue",
        convertToString(n.keyValue.getValueData(), n.keyValue.getValueCount(), 6));
    }

    if (n.set_fraction.getValue() != 0)
      element.setAttribute("fraction", "" + n.set_fraction.getValue());

    if (n.value_changed.getValue() != 0)
      element.setAttribute("value", "" + n.value_changed.getValue());
  }//end of tScalarInterpolator()

  /**
   * Traverses a PositionInterpolator node.
   * @param n a PositionInterpolator node to traverse
   */
  public void tPositionInterpolator(PositionInterpolator n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //false
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultkey = {0, 0, 0, 0};
    if (!arraysEqual(n.key.getValueData(), defaultkey))
    {
      element.setAttribute("key",
        convertToString(n.key.getValueData(), n.key.getValueCount(), 6));
    }

    float[] defaultkeyValue = {0, 0, 0, 0};
    if (!arraysEqual(n.keyValue.getValueData(), defaultkeyValue))
    {
      element.setAttribute("keyValue",
        convertToString(n.keyValue.getValueData(), n.keyValue.getValueCount() * 3, 3));
    }

    if (n.set_fraction.getValue() != 0)
      element.setAttribute("fraction", "" + n.set_fraction.getValue());

    float[] defaultvalue = {0, 0, 0};
    if (!arraysEqual(n.value_changed.getValue(), defaultvalue))
      element.setAttribute("value", convertToString(n.value_changed.getValue(), 3));
  }//end of tPositionInterpolator()

  /**
   * Traverses an OrientationInterpolator node.
   * @param n an OrientationInterpolator node
   */
  public void tOrientationInterpolator(OrientationInterpolator n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //false
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultkey = {0, 0, 0, 0};
    if (!arraysEqual(n.key.getValueData(), defaultkey))
    {
      element.setAttribute("key",
        convertToString(n.key.getValueData(), n.key.getValueCount(), 6));
    }

    float[] defaultkeyValue = {0, 0, 0, 0};
    if (!arraysEqual(n.keyValue.getValueData(), defaultkeyValue))
    {
      element.setAttribute("keyValue", "" +
        convertToString(n.keyValue.getValueData(), n.keyValue.getValueCount() * 4, 4));
    }

    if (n.set_fraction.getValue() != 0)
      element.setAttribute("fraction", "" + n.set_fraction.getValue());

    float[] defaultvalue_changed = {0, 0, 0, 0};
    if (!arraysEqual(n.value_changed.getValue(), defaultvalue_changed))
    {
      float[] value_changed = n.value_changed.getValue();
      element.setAttribute("value", convertToString(value_changed, value_changed.length));
    }
  }//end of tOrientationInterpolator()

  /**
   * Traverses a NormalInterpolator node.
   * @param n a NormalInterpolator node to traverse
   */
  public void tNormalInterpolator(NormalInterpolator n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //false
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultkey = {0, 0, 0, 0};
    if (!arraysEqual(n.key.getValueData(), defaultkey))
    {
      element.setAttribute("key",
        convertToString(n.key.getValueData(), n.key.getValueCount(), 6));
    }

    float[] defaultkeyValue = {0, 0, 0, 0};
    if (!arraysEqual(n.keyValue.getValueData(), defaultkeyValue))
    {
      element.setAttribute("keyValue",
        convertToString(n.keyValue.getValueData(), n.keyValue.getValueCount() * 3, 3));
    }

    if (n.set_fraction.getValue() != 0)
      element.setAttribute("fraction", "" + n.set_fraction.getValue());

    float[] defaultvalue = {0, 0, 0, 0};
    if (!arraysEqual(n.value_changed.getValueData(), defaultvalue))
    {
      element.setAttribute("value",
        convertToString(n.value_changed.getValueData(), n.value_changed.getValueCount() * 3, 3));
    }
  }//end of tNormalInterpolator()

  /**
   * Traverses a CoordinateInterpolator node.
   * @param n a CoordinateInterpolator node to traverse
   */
  public void tCoordinateInterpolator(CoordinateInterpolator n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //false
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultkey = {0, 0, 0, 0};
    if (!arraysEqual(n.key.getValueData(), defaultkey))
    {
      element.setAttribute("key",
        convertToString(n.key.getValueData(), n.key.getValueCount(), 6));
    }

    float[] defaultkeyValue = {0, 0, 0, 0};
    if (!arraysEqual(n.keyValue.getValueData(), defaultkeyValue))
    {
      element.setAttribute("keyValue", "" +
        convertToString(n.keyValue.getValueData(), n.keyValue.getValueCount() * 3, 3));
    }

    if (n.set_fraction.getValue() != 0)
      element.setAttribute("fraction", "" + n.set_fraction.getValue());

    float[] defaultvalue_changed = {0, 0, 0, 0};
    if (!arraysEqual(n.value_changed.getValueData(), defaultvalue_changed))
    {
      element.setAttribute("value",
        convertToString(n.value_changed.getValueData(), n.value_changed.getValueCount() * 3, 3));
    }
  }//end of tCoordinateInterpolator()

  /**
   * Traverses a ColorInterpolator node.
   * @param n a ColorInterpolator node to traverse
   */
  public void tColorInterpolator(ColorInterpolator n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //false
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultkey = {0, 0, 0, 0};
    if (!arraysEqual(n.key.getValueData(), defaultkey))
    {
      element.setAttribute("key",
        convertToString(n.key.getValueData(), n.key.getValueCount(), 6));
    }

    float[] defaultkeyValue = {0, 0, 0, 0};
    if (!arraysEqual(n.keyValue.getValueData(), defaultkeyValue))
    {
      element.setAttribute("keyValue", "" +
        convertToString(n.keyValue.getValueData(), n.keyValue.getValueCount() * 3, 3));
    }

    if (n.set_fraction.getValue() != 0)
      element.setAttribute("fraction", "" + n.set_fraction.getValue());

    float[] defaultvalue_changed = {0, 0, 0};
    if (!arraysEqual(n.value_changed.getValue(), defaultvalue_changed))
    {
      float[] value_changed = n.value_changed.getValue();
      element.setAttribute("value", convertToString(value_changed, value_changed.length));
    }
  }//end of tColorInterpolator()

  //Following are Appearance nodes:

  /**
   * Traverses a TextureTransform node.
   * @param n a TextureTransform node to traverse
   */
  public void tTextureTransform(TextureTransform n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentAppearance.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultcenter = {0, 0};
    if (!arraysEqual(n.center.getValue(), defaultcenter))
      element.setAttribute("center", convertToString(n.center.getValue(), 2));

    if (n.rotation.getValue() != 0.0f)
      element.setAttribute("rotation", "" + n.rotation.getValue());

    float[] defaultscale = {1.0f, 1.0f};
    if (!arraysEqual(n.scale.getValue(), defaultscale))
      element.setAttribute("scale", convertToString(n.scale.getValue(), 2));

    float[] defaultTranslation = {0.0f, 0.0f};
    if (!arraysEqual(n.translation.getValue(), defaultTranslation))
      element.setAttribute("translation", convertToString(n.translation.getValue(), 2));
  }//end of tTextureTransform()

  /**
   * Traverses a PixelTexture node.
   * @param n a PixelTexture node to traverse
   */
  public void tPixelTexture(PixelTexture n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentAppearance.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    int[] defaultImage = {0, 0, 0};
    if (!arraysEqual(n.image.getValueData(), defaultImage))
    {
      element.setAttribute("image",
        convertToString(n.image.getValueData(), n.image.getValueCount(), 3));
    }

    if (n.repeatS.getValue() != true)
      element.setAttribute("repeatS", "false");

    if (n.repeatT.getValue() != true)
      element.setAttribute("repeatT", "false");
  }//end of tPixelTexture()

  /**
   * Traverses a MovieTexture node.
   * @param n a MovieTexture node to traverse
   */
  public void tMovieTexture(MovieTexture n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (isChildOfSound)
    {
      parentSound.appendChild(element);
      isChildOfSound = false; //reset
    }
    else
    {
      parentAppearance.appendChild(element);
    }

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.loop.getValue() != false)
      element.setAttribute("loop", "true");

    if (n.repeatS.getValue() != true)
      element.setAttribute("repeatS", "false");

    if (n.repeatT.getValue() != true)
      element.setAttribute("repeatT", "false");

    if (n.speed.getValue() != 1.0f)
      element.setAttribute("speed", "" + n.speed.getValue());

    if (n.startTime.getValue() != 0.0d)
      element.setAttribute("startTime", "" + n.startTime.getValue());

    if (n.stopTime.getValue() != 0.0d)
      element.setAttribute("stopTime", "" + n.stopTime.getValue());

    if (n.url != null)
      element.setAttribute("url", convertToString(n.url.getValueData(), n.url.getValueCount(), true));
  }//end of tMovieTexture()

  /**
   * Traverses an ImageTexture node.
   * @param n an ImageTexture node to traverse
   */
  public void tImageTexture(ImageTexture n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentAppearance.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.repeatS.getValue() != true)
      element.setAttribute("repeatS", "false");

    if (n.repeatT.getValue() != true)
      element.setAttribute("repeatT", "false");

    if (n.url != null)
      element.setAttribute("url", convertToString(n.url.getValueData(), n.url.getValueCount(), true));
  }//end of tImageTexture()

  /**
   * Traverses a Material node.
   * @param n a Material node
   */
  public void tMaterial(Material n)
  {
//SystemOut.println("tMaterial breakpoint A");

    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (parentAppearance != null)
      parentAppearance.appendChild(element);
    else //This is a child of a Proto node
      parentProto.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.ambientIntensity.getValue() != 0.2f)
      element.setAttribute("ambientIntensity", "" + n.ambientIntensity.getValue());

    float[] defaultdiffuseColor = {0.8f, 0.8f, 0.8f};
    if (!arraysEqual(n.diffuseColor.getValue(), defaultdiffuseColor))
    {
      float[] diffuseColor = n.diffuseColor.getValue();
      element.setAttribute("diffuseColor", convertToString(diffuseColor, diffuseColor.length));
    }

    float[] defaultemissiveColor = {0, 0, 0};
    if (!arraysEqual(n.emissiveColor.getValue(), defaultemissiveColor))
    {
      float [] emissiveColor = n.emissiveColor.getValue();
      element.setAttribute("emissiveColor", convertToString(emissiveColor, emissiveColor.length));
    }

    if (n.shininess.getValue() != 0.2f)
      element.setAttribute("shininess", "" + n.shininess.getValue());

    float[] defaultspecularColor = {0, 0, 0};
    if (!arraysEqual(n.specularColor.getValue(), defaultspecularColor))
    {
      float[] specularColor = n.specularColor.getValue();
      element.setAttribute("specularColor", convertToString(specularColor, specularColor.length));
    }

    if (n.transparency.getValue() != 0)
      element.setAttribute("transparency", "" + n.transparency.getValue());
  }//end of tMaterial()

  /**
   * Traverses a FontStyle node.
   * @param n a FontStyle node to traverse
   */
  public void tFontStyle(FontStyle n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentText.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.family.getValueCount() != 1 || !arraysEqual(n.family.getValueData(), n.defFamily))
    {
      element.setAttribute("family",
        convertToString(n.family.getValueData(), n.family.getValueCount(), false));
    }

    if (n.horizontal.getValue() != true)
      element.setAttribute("horizontal", "false");

//    if (n.justify.getValueCount() != 1 || !arraysEqual(n.justify.getValueData(), n.defJustify))
//    {
//      element.setAttribute("justify",
//        convertToString(n.justify.getValueData(), n.justify.getValueCount(), false));
//    }
    if (n.justify != null)
      element.setAttribute("justify", convertToString(n.justify.getValueData(), n.justify.getValueCount(), true));

    if (n.language.getValue() != null)
    {
      element.setAttribute("language", n.language.getValue());
    }

    if (n.leftToRight.getValue() != true)
      element.setAttribute("leftToRight", "false");

    if (n.size.getValue() != 1.0f)
      element.setAttribute("size", "" + n.size.getValue());

    if (n.spacing.getValue() != 1.0f)
      element.setAttribute("spacing", "" + n.spacing.getValue());

    if (!n.style.getValue().equals("PLAIN"))
      element.setAttribute("style", n.style.getValue());

    if (n.topToBottom.getValue() != true)
      element.setAttribute("topToBottom", "false");
  }//end of tFontStyle()

  /**
   * Traverses an Appearance node.
   * @param n an Appearance node to traverse
   */
  public void tAppearance(Appearance n)
  {
//SystemOut.println("tAppearance: Breakpoint A");
    isChildOfAppearance = true; //[Wu] Oct2002

    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (parentShape != null)
      parentShape.appendChild(element);
    else //[Wu] - Oct2002
      parentProto.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    parentAppearance = element;

    if (n.material != null)
    {
      if (n.material.getNode() != null)
      {
      	if (n.material.getNode() instanceof Material)
          tMaterial((Material) n.material.getNode());
        else
          tProtoInstance((ProtoInstance) n.material.getNode());
      }
    }

    if (n.texture != null)
    {
      if (n.texture.getNode() != null)
      {
        if (n.texture.getNode() instanceof ImageTexture)
          tImageTexture((ImageTexture) n.texture.getNode());
        else if (n.texture.getNode() instanceof MovieTexture)
          tMovieTexture((MovieTexture) n.texture.getNode());
        else if (n.texture.getNode() instanceof PixelTexture)
          tPixelTexture((PixelTexture) n.texture.getNode());
      }
    }

    if (n.textureTransform != null)
    {
      if (n.textureTransform.getNode() != null)
        tTextureTransform((TextureTransform) n.textureTransform.getNode());
    }
  }//end of tAppearance()

  //Following nodes are related to Geometric Properties:

  /**
   * Traverses a TextureCoordinate node.
   * @param n a TextureCoordinate node to traverse
   */
  public void tTextureCoordinate(TextureCoordinate n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentOfTextureCoordinate.appendChild(element);
    isTextureCoordinateChild = false; //reset

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultpoint = {0, 0, 0, 0}; //due to default MFVec2f is an array of 0's of size 4
    if (!arraysEqual(n.point.getValueData(), defaultpoint))
    {
      element.setAttribute("point",
        convertToString(n.point.getValueData(), n.point.getValueCount() * 2, 2));
    }
  }//end of tTextureCoordinate()

  /**
   * Traverses a Normal node.
   * @param n a Normal node to traverse
   */
  public void tNormal(Normal n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentOfNormal.appendChild(element);
    isNormalChild = false; //reset

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultvector = {0, 0, 0, 0}; //due to default MFVec3f is an array of 0's of size 4
    if (!arraysEqual(n.vector.getValueData(), defaultvector))
    {
      element.setAttribute("vector",
        convertToString(n.vector.getValueData(), n.vector.getValueCount() * 3, 3));
    }
  }//end of tNormal()

  /**
   * Traverses a Coordinate node.
   * @param n a Coordinate node to traverse
   */
  public void tCoordinate(Coordinate n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentOfCoordinate.appendChild(element);
    isCoordinateChild = false; //reset

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultpoint = {0, 0, 0, 0}; //due to default MFVec3f is an array of 0's of size 4
    if (!arraysEqual(n.point.getValueData(), defaultpoint))
    {
      element.setAttribute("point",
        convertToString(n.point.getValueData(), n.point.getValueCount() * 3, 3));
    }
  }//end of tCoordinate()

  /**
   * Traverses a Color node.
   * @param n a Color node to traverse
   */
  public void tColor(Color n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentOfColor.appendChild(element);
    isColorChild = false; //reset

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultcolor = {0, 0, 0, 0}; //due to default MFRotation is an array of 0's of size 4
    if (!arraysEqual(n.color.getValueData(), defaultcolor))
    {
      element.setAttribute("color", convertToString(n.color.getValueData(), n.color.getValueCount() * 3, 3));
    }
  }//end of tColor()

  //Followings are Geometry nodes

  /**
   * Traverses a Texture node.
   * @param n a Text node to traverse
   */
  public void tText(Text n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentShape.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    parentText = element;

    if (n.fontStyle != null)
    {
      if (n.fontStyle.getNode() != null)
      {
        tFontStyle((FontStyle) n.fontStyle.getNode());
      }
    }

    float[] defaultLength = {0, 0, 0, 0};
    if (!arraysEqual(n.length.getValueData(), defaultLength))
    {
      element.setAttribute("length", convertToString(n.length.getValueData(), n.length.getValueCount()));
    }

    if (n.maxExtent.getValue() != 0.0f)
      element.setAttribute("maxExtent", "" + n.maxExtent.getValue());

    if (n.string != null)
      element.setAttribute("string", convertToString(n.string.getValueData(), n.string.getValueCount(), true));
  }//end of tText()

  /**
   * Traverses a Sphere node.
   * @param n a Sphere node to traverse
   */
  public void tSphere(Sphere n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentShape.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.radius.getValue() != 1)
      element.setAttribute("radius", "" + n.radius.getValue());
  }//end of tSphere()

  /**
   * Traverses a PointSet node.
   * @param n a PointSet node to traverse
   */
  public void tPointSet(PointSet n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentShape.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.color != null)
    {
      if (n.color.getNode() != null)
      {
        parentOfColor = element;
        isColorChild = true;
        n.color.getNode().traverse(this);
//        tColor((Color) n.color.getNode());
      }
    }

    if (n.coord != null)
    {
      if (n.coord.getNode() != null)
      {
        parentOfCoordinate = element;
        isCoordinateChild = true;
        n.coord.getNode().traverse(this);
//        tCoordinate((Coordinate) n.coord.getNode());
      }
    }
  }//end of tPointSet()

  /**
   * Traverses an IndexedLineSet node.
   * @param n an IndexedLineSet node to traverse
   */
  public void tIndexedLineSet(IndexedLineSet n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentShape.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.color != null)
    {
      if (n.color.getNode() != null)
      {
        parentOfColor = element;
        isColorChild = true;
        n.color.getNode().traverse(this);
//        tColor((Color) n.color.getNode());
      }
    }

    int[] defaultcolorIndex = {0, 0, 0, 0}; //due to default MFInt32 is an array of 0's of size 4
    if (!arraysEqual(n.colorIndex.getValueData(), defaultcolorIndex))
    {
      element.setAttribute("colorIndex",
        convertToString(n.colorIndex.getValueData(), n.colorIndex.getValueCount(), 6));
    }

    if (n.colorPerVertex.getValue() != true)
      element.setAttribute("colorPerVertex", "false");

    if (n.coord != null)
    {
      if (n.coord.getNode() != null)
      {
        parentOfCoordinate = element;
        isCoordinateChild = true;
        n.coord.getNode().traverse(this);
//        tCoordinate((Coordinate) n.coord.getNode());
      }
    }

    int[] defaultcoordIndex = {0, 0, 0, 0}; //due to default MFInt32 is an array of 0's of size 4
    if (!arraysEqual(n.coordIndex.getValueData(), defaultcoordIndex))
    {
      element.setAttribute("coordIndex",
        convertToString(n.coordIndex.getValueData(), n.coordIndex.getValueCount(), 6));
    }
  }//end of tIndexedLineSet()

  /**
   * Traverses an IndexedFaceSet node.
   * @param n an IndexedFaceSet node to traverse
   */
  public void tIndexedFaceSet(IndexedFaceSet n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentShape.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.ccw.getValue() != true)
      element.setAttribute("ccw", "false");

    if (n.color != null)
    {
      if (n.color.getNode() != null)
      {
        parentOfColor = element;
        isColorChild = true;
        n.color.getNode().traverse(this);
//        tColor((Color) n.color.getNode());
      }
    }

    int[] defaultcolorIndex = {0, 0, 0, 0}; //due to default MFInt32 is an array of 0's of size 4
    if (!arraysEqual(n.colorIndex.getValueData(), defaultcolorIndex))
    {
      element.setAttribute("colorIndex",
        convertToString(n.colorIndex.getValueData(), n.colorIndex.getValueCount(), 6));
    }

    if (n.colorPerVertex.getValue() != true)
      element.setAttribute("colorPerVertex", "false");

    if (n.convex.getValue() != true)
      element.setAttribute("convex", "false");

    if (n.coord != null)
    {
      if (n.coord.getNode() != null)
      {
        parentOfCoordinate = element;
        isCoordinateChild = true;
        n.coord.getNode().traverse(this);
//        tCoordinate((Coordinate) n.coord.getNode());
      }
    }

    int[] defaultcoordIndex = {0, 0, 0, 0}; //due to default MFInt32 is an array of 0's of size 4
    if (!arraysEqual(n.coordIndex.getValueData(), defaultcoordIndex))
    {
      element.setAttribute("coordIndex",
        convertToString(n.coordIndex.getValueData(), n.coordIndex.getValueCount(), 6));
    }

    if (n.creaseAngle.getValue() != 0)
      element.setAttribute("creaseAngle", "" + n.creaseAngle.getValue());

    if (n.normal != null)
    {
      if (n.normal.getNode() != null)
      {
        parentOfNormal = element;
        isNormalChild = true;
        n.normal.getNode().traverse(this);
//        tNormal((Normal) n.normal.getNode());
      }
    }

    int[] defaultnormalIndex = {0, 0, 0, 0}; //due to default MFInt32 is an array of 0's of size 4
    if (!arraysEqual(n.normalIndex.getValueData(), defaultnormalIndex))
    {
      element.setAttribute("normalIndex",
        convertToString(n.normalIndex.getValueData(), n.normalIndex.getValueCount(), 6));
    }

    if (n.normalPerVertex.getValue() != true)
      element.setAttribute("normalPerVertex", "false");

    if (n.solid.getValue() != true)
      element.setAttribute("solid", "false");

    if (n.texCoord != null)
    {
      if (n.texCoord.getNode() != null)
      {
        parentOfTextureCoordinate = element;
        isTextureCoordinateChild = true;
        n.texCoord.getNode().traverse(this);
//        tTextureCoordinate((TextureCoordinate) n.texCoord.getNode());
      }
    }

    int[] defaulttexCoordIndex = {0, 0, 0, 0}; //due to default MFInt32 is an array of 0's of size 4
    if (!arraysEqual(n.texCoordIndex.getValueData(), defaulttexCoordIndex))
    {
      element.setAttribute("texCoordIndex",
        convertToString(n.texCoordIndex.getValueData(), n.texCoordIndex.getValueCount(), 6));
    }
  }//end of tIndexedFaceSet()

  /**
   * Traverses an Extrusion node.
   * @param n an Extrusion node to traverse
   */
  public void tExtrusion(Extrusion n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentShape.appendChild(element);
    checkISconnect (n, element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.beginCap.getValue() != true)
      element.setAttribute("beginCap", "false");

    if (n.ccw.getValue() != true)
      element.setAttribute("ccw", "false");

    if (n.convex.getValue() != true)
      element.setAttribute("convex", "false");

    if (n.creaseAngle.getValue() != 0)
      element.setAttribute("creaseAngle", "" + n.creaseAngle.getValue());

    if (!arraysEqual(n.crossSection.getValueData(), n.defCrossSection))
    {
      element.setAttribute("crossSection",
        convertToString(n.crossSection.getValueData(), n.crossSection.getValueCount() * 2, 2));
    }

    if (n.endCap.getValue() != true)
      element.setAttribute("endCap", "false");

    if (!arraysEqual(n.orientation.getValueData(), n.defOrientation))
    {
      element.setAttribute("orientation",
        convertToString(n.orientation.getValueData(), n.orientation.getValueCount() * 4, 4));
    }

    if (!arraysEqual(n.scale.getValueData(), n.defScale))
    {
      element.setAttribute("scale",
        convertToString(n.scale.getValueData(), n.scale.getValueCount() * 2, 2));
    }

    if (n.solid.getValue() != true)
      element.setAttribute("solid", "false");

    if (!arraysEqual(n.spine.getValueData(), n.defSpine))
    {
      element.setAttribute("spine",
        convertToString(n.spine.getValueData(), n.spine.getValueCount() * 3, 3));
    }
  }//end of tExtrusion()

  /**
   * Traverses an ElevationGrid node.
   * @param n an ElevationGrid node to traverse
   */
  public void tElevationGrid(ElevationGrid n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentShape.appendChild(element);
    checkISconnect (n, element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.ccw.getValue() != true)
      element.setAttribute("ccw", "false");

    if (n.color != null)
    {
      if (n.color.getNode() != null)
      {
        parentOfColor = element;
        isColorChild = true;
        n.color.getNode().traverse(this);
//        tColor((Color) n.color.getNode());
      }
    }

    if (n.colorPerVertex.getValue() != true)
      element.setAttribute("colorPerVertex", "false");

    if (n.creaseAngle.getValue() != 0)
      element.setAttribute("creaseAngle", "" + n.creaseAngle.getValue());

    float[] defaultHeight = {0, 0, 0, 0}; //default MFFloat is an array of 0's of size 4
    if (!arraysEqual(n.height.getValueData(), defaultHeight))
      element.setAttribute("height", convertToString(n.height.getValueData(), n.height.getValueCount(), 6));

    if (n.normal != null)
    {
      if (n.normal.getNode() != null)
      {
        parentOfNormal = element;
        isNormalChild = true;
        n.normal.getNode().traverse(this);
//        tNormal((Normal) n.normal.getNode());
      }
    }

    if (n.normalPerVertex.getValue() != true)
      element.setAttribute("normalPerVertex", "false");

    if (n.solid.getValue() != true)
      element.setAttribute("solid", "false");

    if (n.texCoord != null)
    {
      if (n.texCoord.getNode() != null)
      {
        parentOfTextureCoordinate = element;
        isTextureCoordinateChild = true;
        n.texCoord.getNode().traverse(this);
//        tTextureCoordinate((TextureCoordinate) n.texCoord.getNode());
      }
    }

    if (n.xDimension.getValue() != 0)
      element.setAttribute("xDimension", "" + n.xDimension.getValue());

    if (n.xSpacing.getValue() != 1.0f)
      element.setAttribute("xSpacing", "" + n.xSpacing.getValue());

    if (n.zDimension.getValue() != 0)
      element.setAttribute("zDimension", "" + n.zDimension.getValue());

    if (n.zSpacing.getValue() != 1.0f)
      element.setAttribute("zSpacing", "" + n.zSpacing.getValue());
  }//end of tElevationGrid()

  /**
   * Traverses a Cylinder node.
   * @param n a Cylinder node to traverse
   */
  public void tCylinder(Cylinder n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentShape.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.bottom.getValue() != true)
      element.setAttribute("bottom", "false");

    if (n.radius.getValue() != 1)
      element.setAttribute("radius", "" + n.radius.getValue());

    if (n.height.getValue() != 2)
      element.setAttribute("height", "" + n.height.getValue());

    if (n.side.getValue() != true)
      element.setAttribute("side", "false");

    if (n.top.getValue() != true)
      element.setAttribute("top", "false");
  }//end of tCylinder()

  /**
   * Traverses a Cone node.
   * @param n a Cone node to traverse
   */
  public void tCone(Cone n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentShape.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.bottom.getValue() != true)
      element.setAttribute("bottom", "false");

    if (n.bottomRadius.getValue() != 1)
      element.setAttribute("bottomRadius", "" + n.bottomRadius.getValue());

    if (n.height.getValue() != 2)
      element.setAttribute("height", "" + n.height.getValue());

    if (n.side.getValue() != true)
      element.setAttribute("side", "false");
  }//end of tCone()

  /**
   * Traverses a Box node.
   * @param n a Box node to traverse
   */
  public void tBox(Box n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    parentShape.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultSize = {2, 2, 2};
    if (!arraysEqual(n.size.getValue(), defaultSize))
    {
      float[] size = n.size.getValue();
      element.setAttribute("size", convertToString(size, size.length));
    }
  }//end of tBox()

  //Followings are Sensor related nodes:

  /**
   * Traverses a VisibilitySensor node.
   * @param n a VisibilitySensor node to traverse
   */
  public void tVisibilitySensor(VisibilitySensor n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultcenter = {0, 0, 0};
    if (!arraysEqual(n.center.getValue(), defaultcenter))
      element.setAttribute("center", convertToString(n.center.getValue(), 3));

    if (n.enabled.getValue() != true)
      element.setAttribute("enabled", "false");

    float[] defaultsize = {0, 0, 0};
    if (!arraysEqual(n.size.getValue(), defaultsize))
      element.setAttribute("size", convertToString(n.size.getValue(), 3));
  }//end of tVisibilitySensor()

  /**
   * Traverses a TouchSensor node.
   * @param n a TouchSensor node
   */
  public void tTouchSensor(TouchSensor n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.enabled.getValue() != true)
      element.setAttribute("enabled", "false");

    float[] defaulthitNormal = {0, 0, 1};
    if (!arraysEqual(n.hitNormal_changed.getValue(), defaulthitNormal))
      element.setAttribute("hitNormal", convertToString(n.hitNormal_changed.getValue(), 3));

    float[] defaulthitPoint = {0, 0, 0};
    if (!arraysEqual(n.hitPoint_changed.getValue(), defaulthitPoint))
      element.setAttribute("hitPoint", convertToString(n.hitPoint_changed.getValue(), 3));

    float[] defaulthitTexCoord = {0, 0};
    if (!arraysEqual(n.hitTexCoord_changed.getValue(), defaulthitTexCoord))
      element.setAttribute("hitTexCoord", convertToString(n.hitTexCoord_changed.getValue(), 2));

    if (n.isActive.getValue() != false)
      element.setAttribute("isActive", "true");

    if (n.isOver.getValue() != false)
      element.setAttribute("isOver", "true");

    if (n.touchTime.getValue() != 0.0d)
      element.setAttribute("touchTime", "" + n.touchTime.getValue());
  }//end of tTouchSensor()

  /**
   * Traverses a TimeSensor node.
   * @param n a TimeSensor node to traverse
   */
  public void tTimeSensor(TimeSensor n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.cycleInterval.getValue() != 1.0d)
      element.setAttribute("cycleInterval", "" + n.cycleInterval.getValue());

    if (n.cycleTime.getValue() != 0.0d)
      element.setAttribute("cycleTime", "" + n.cycleTime.getValue());

    if (n.enabled.getValue() != true)
      element.setAttribute("enabled", "false");

    if (n.fraction_changed.getValue() != 0)
      element.setAttribute("fraction", "" + n.fraction_changed.getValue());

    if (n.isActive.getValue() != false)
      element.setAttribute("isActive", "true");

    if (n.loop.getValue() != false)
      element.setAttribute("loop", "true");

    if (n.startTime.getValue() != 0.0d)
      element.setAttribute("startTime", "" + n.startTime.getValue());

    if (n.stopTime.getValue() != 0.0d)
      element.setAttribute("stopTime", "" + n.stopTime.getValue());

    if (n.time.getValue() != 0.0d)
      element.setAttribute("time", "" + n.time.getValue());
  }//end of tTimeSensor()

  /**
   * Traverses a SphereSensor node.
   * @param n a SphereSensor node to traverse
   */
  public void tSphereSensor(SphereSensor n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.autoOffset.getValue() != true)
      element.setAttribute("autoOffset", "false");

    if (n.enabled.getValue() != true)
      element.setAttribute("enabled", "false");

    if (n.isActive.getValue() != false)
      element.setAttribute("isActive", "true");

    float[] defaultRotation = {0, 0, 0, 0};
    if (!arraysEqual(n.rotation_changed.getValue(), defaultRotation))
      element.setAttribute("rotation", convertToString(n.rotation_changed.getValue(), 4));

    float[] defaultTrackPoint = {0, 0, 0};
    if (!arraysEqual(n.trackPoint_changed.getValue(), defaultTrackPoint))
      element.setAttribute("trackPoint", convertToString(n.trackPoint_changed.getValue(), 3));
  }//end of tSphereSensor()

  /**
   * Traverses a ProximitySensor node.
   * @param n a ProximitySensor node to traverse
   */
  public void tProximitySensor(ProximitySensor n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultcenter = {0, 0, 0};
    if (!arraysEqual(n.center.getValue(), defaultcenter))
      element.setAttribute("center", convertToString(n.center.getValue(), 3));

    if (n.enabled.getValue() != true)
      element.setAttribute("enabled", "false");

    float[] defaultsize = {0, 0, 0};
    if (!arraysEqual(n.size.getValue(), defaultsize))
      element.setAttribute("size", convertToString(n.size.getValue(), 3));
  }//end of tProximitySensor()

  /**
   * Traverses a PlaneSensor node.
   * @param n a PlaneSensor node to traverse
   */
  public void tPlaneSensor(PlaneSensor n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.autoOffset.getValue() != true)
      element.setAttribute("autoOffset", "false");

    if (n.enabled.getValue() != true)
      element.setAttribute("enabled", "false");

    if (n.isActive.getValue() != false)
      element.setAttribute("isActive", "true");

    float[] defaultMaxPos = {0, 0, 0};
    if (!arraysEqual(n.maxPosition.getValue(), defaultMaxPos))
      element.setAttribute("maxPosition", convertToString(n.maxPosition.getValue(), 3));

    float[] defaultMinPos = {0, 0, 0};
    if (!arraysEqual(n.minPosition.getValue(), defaultMinPos))
      element.setAttribute("minPosition", convertToString(n.minPosition.getValue(), 3));
  }//end of tPlaneSensor()

  /**
   * Traverses a CylinderSensor node.
   * @param n a CylinderSensor node to traverse
   */
  public void tCylinderSensor(CylinderSensor n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.autoOffset.getValue() != true)
      element.setAttribute("autoOffset", "false");

    if (n.diskAngle.getValue() != 0.262f)
      element.setAttribute("diskAngle", "" + n.diskAngle.getValue());

    if (n.enabled.getValue() != true)
      element.setAttribute("enabled", "false");

    if (n.isActive.getValue() != false)
      element.setAttribute("isActive", "true");

    if (n.maxAngle.getValue() != -1.0f)
      element.setAttribute("maxAngle", "" + n.maxAngle.getValue());

    if (n.minAngle.getValue() != 0.0f)
      element.setAttribute("minAngle", "" + n.minAngle.getValue());

    if (n.offset.getValue() != 0.0f)
      element.setAttribute("offset", "" + n.offset.getValue());

    float[] defaultRotation = {0, 0, 0, 0};
    if (!arraysEqual(n.rotation_changed.getValue(), defaultRotation))
      element.setAttribute("rotation", convertToString(n.rotation_changed.getValue(), 4));

    float[] defaultTrackPoint = {0, 0, 0};
    if (!arraysEqual(n.trackPoint_changed.getValue(), defaultTrackPoint))
      element.setAttribute("trackPoint", convertToString(n.trackPoint_changed.getValue(), 3));
  }//end of tCylinderSensor()

  //The followings are common nodes:

  /**
   * Traverses a WorldInfo node.
   * @param n a WorldInfo node to traverse
   */
  public void tWorldInfo(WorldInfo n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.info != null)
    {
      element.setAttribute("info",
        convertToString(n.info.getValueData(), n.info.getValueCount(), false));
    }

    if (n.title.getValue() != null)
      element.setAttribute("title", n.title.getValue());
  }//end of tWorldInfo()

  /**
   * Traverses a Script node.
   * @param n a Script node to traverse
   */
  public void tScript(Script n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.directOutput.getValue() != false)
      element.setAttribute("directOutput", "true");

    if (n.mustEvaluate.getValue() != false)
      element.setAttribute("mustEvaluate", "true");

    if (n.subfields != null)
    {
      Enumeration e = n.subfields.keys();
      while(e.hasMoreElements())
      {
        String fname = (String) e.nextElement();
        if (fname != Script.STR_DIRECTOUTPUT && fname != Script.STR_MUSTEVALUATE && fname != Script.STR_URL)
        {
          Element fieldElement = getFieldElement((Field) n.subfields.get(fname), fname);
          element.appendChild(fieldElement);
        } //end of every field
      }
    }//end of subfields != null

    //if the url starts with javascript or vrmlscript, wrap the values in url in a CDATA section
    if (n.url != null)
    {
      if (n.url.getValueData()[0].startsWith("javascript") ||
          n.url.getValueData()[0].startsWith("vrmlscript"))
      {
        CDATASection cdata = doc.createCDATASection(convertToString(n.url.getValueData(), n.url.getValueCount(), false));
        element.appendChild(cdata);
      }
      else
      {
        element.setAttribute("url", convertToString(n.url.getValueData(), n.url.getValueCount(), true));
      }
    }
  }//end of tScript()

  /**
   * Traverses a SpotLight node.
   * @param n a SpotLight node to traverse
   */
  public void tSpotLight(SpotLight n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.ambientIntensity.getValue() != 0.0f)
      element.setAttribute("ambientIntensity", "" + n.ambientIntensity.getValue());

    float[] defaultAttenuation = {1.0f, 0.0f, 0.0f};
    if (!arraysEqual(n.attenuation.getValue(), defaultAttenuation))
      element.setAttribute("attenuation", convertToString(n.attenuation.getValue(), 3));

    if (n.beamWidth.getValue() != 1.570796f)
      element.setAttribute("beamWidth", "" + n.beamWidth.getValue());

    float[] defaultcolor = {1.0f, 1.0f, 1.0f};
    if (!arraysEqual(n.color.getValue(), defaultcolor))
      element.setAttribute("color", convertToString(n.color.getValue(), 3));

    if (n.cutOffAngle.getValue() != 0.785398f)
      element.setAttribute("cutOffAngle", "" + n.cutOffAngle.getValue());

    float[] defaultdirection = {0.0f, 0.0f, 1.0f};
    if (!arraysEqual(n.direction.getValue(), defaultdirection))
      element.setAttribute("direction", convertToString(n.direction.getValue(), 3));

    if (n.intensity.getValue() != 1.0f)
      element.setAttribute("intensity", "" + n.intensity.getValue());

    float[] defaultlocation = {0.0f, 0.0f, 0.0f};
    if (!arraysEqual(n.location.getValue(), defaultlocation))
      element.setAttribute("location", convertToString(n.location.getValue(), 3));

    if (n.on.getValue() != true)
      element.setAttribute("on", "false");

    if (n.radius.getValue() != 100.0f)
      element.setAttribute("radius", "" + n.radius.getValue());
  }//end of tSpotLight()

  /**
   * Traverses a Sound node.
   * @param n a Sound node to traverse
   */
  public void tSound(Sound n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    parentSound = element;

    float[] defaultdirection = {0.0f, 0.0f, 1.0f};
    if (!arraysEqual(n.direction.getValue(), defaultdirection))
      element.setAttribute("direction", convertToString(n.direction.getValue(), 3));

    if (n.intensity.getValue() != 1.0f)
      element.setAttribute("intensity", "" + n.intensity.getValue());

    float[] defaultlocation = {0.0f, 0.0f, 0.0f};
    if (!arraysEqual(n.location.getValue(), defaultlocation))
      element.setAttribute("location", convertToString(n.location.getValue(), 3));

    if (n.maxBack.getValue() != 10.0f)
      element.setAttribute("maxBack", "" + n.maxBack.getValue());

    if (n.maxFront.getValue() != 10.0f)
      element.setAttribute("maxFront", "" + n.maxFront.getValue());

    if (n.minBack.getValue() != 1.0f)
      element.setAttribute("minBack", "" + n.minBack.getValue());

    if (n.minFront.getValue() != 1.0f)
      element.setAttribute("minFront", "" + n.minFront.getValue());

    if (n.priority.getValue() != 0.0f)
      element.setAttribute("priority", "" + n.priority.getValue());

    if (n.source != null)
    {
      if (n.source.getNode() != null)
      {
        isChildOfSound = true;
        n.source.getNode().traverse(this);
      }
    }

    if (n.spatialize.getValue() != true)
      element.setAttribute("spatialize", "false");
  }//end of tSound()

  /**
   * Traverses a Shape node.
   * @param n a Shape node to traverse
   */
  public void tShape(Shape n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    parentShape = element;

    if (n.appearance != null)
    {
      if (n.appearance.getNode() != null)
//        tAppearance((Appearance) n.appearance.getNode());
        n.appearance.getNode().traverse(this);
    }

    if (n.geometry != null)
    {
      if (n.geometry.getNode() != null)
      {
        n.geometry.getNode().traverse(this);
      }
    }
  }//end of tShape()

  /**
   * Traverses a PointLight node.
   * @param n a PointLight node to traverse
   */
  public void tPointLight(PointLight n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.ambientIntensity.getValue() != 0.0f)
      element.setAttribute("ambientIntensity", "" + n.ambientIntensity.getValue());

    float[] defaultAttenuation = {1.0f, 0.0f, 0.0f};
    if (!arraysEqual(n.attenuation.getValue(), defaultAttenuation))
      element.setAttribute("attenuation", convertToString(n.attenuation.getValue(), 3));

    float[] defaultcolor = {1.0f, 1.0f, 1.0f};
    if (!arraysEqual(n.color.getValue(), defaultcolor))
      element.setAttribute("color", convertToString(n.color.getValue(), 3));

    if (n.intensity.getValue() != 1.0f)
      element.setAttribute("intensity", "" + n.intensity.getValue());

    float[] defaultlocation = {0.0f, 0.0f, 0.0f};
    if (!arraysEqual(n.location.getValue(), defaultlocation))
      element.setAttribute("location", convertToString(n.location.getValue(), 3));

    if (n.on.getValue() != true)
      element.setAttribute("on", "false");

    if (n.radius.getValue() != 100.0f)
      element.setAttribute("radius", "" + n.radius.getValue());
  }//end of tPointLight()

  /**
   * Traverses a DirectionalLight node.
   * @param n a DirectionalLight node to traverse
   */
  public void tDirectionalLight(DirectionalLight n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.ambientIntensity.getValue() != 0.0f)
      element.setAttribute("ambientIntensity", "" + n.ambientIntensity.getValue());

    float[] defaultcolor = {1.0f, 1.0f, 1.0f};
    if (!arraysEqual(n.color.getValue(), defaultcolor))
      element.setAttribute("color", convertToString(n.color.getValue(), 3));

    float[] defaultdirection = {0.0f, 0.0f, -1.0f};
    if (!arraysEqual(n.direction.getValue(), defaultdirection))
      element.setAttribute("direction", convertToString(n.direction.getValue(), 3));

    if (n.intensity.getValue() != 1.0f)
      element.setAttribute("intensity", "" + n.intensity.getValue());

    if (n.on.getValue() != true)
      element.setAttribute("on", "false");
  }//end of tDirectionalLight()

  /**
   * Traverses an AudioClip node.
   * @param n an AudioClip node to traverse
   */
  public void tAudioClip(AudioClip n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (isChildOfSound)
    {
      parentSound.appendChild(element);
      isChildOfSound = false; //reset
    }
    else //must be a child of field/fieldValue element in which case parentElement should be set
    {
      parentElement.appendChild(element);
      parentElementSet = false; //reset
    }

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.description.getValue() != null)
      element.setAttribute("description", n.description.getValue());

    if (n.loop.getValue() != false)
      element.setAttribute("loop", "true");

    if (n.pitch.getValue() != 1.0f)
      element.setAttribute("pitch", "" + n.pitch.getValue());

    if (n.startTime.getValue() != 0.0d)
      element.setAttribute("startTime", "" + n.startTime.getValue());

    if (n.stopTime.getValue() != 0.0d)
      element.setAttribute("stopTime", "" + n.stopTime.getValue());

    if (n.url != null)
      element.setAttribute("url", convertToString(n.url.getValueData(), n.url.getValueCount(), true));
  }//end of tAudioClip()

  //Followings are Grouping nodes:

  /**
   * Traverses a Switch node.
   * @param n a Switch node to traverse
   */
  public void tSwitch(Switch n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    if (n.whichChoice.getValue() != -1)
      element.setAttribute("whichChoice", "" + n.whichChoice.getValue());

    if (n.choice != null)
    {
      Enumeration e = n.choice.getNodes().elements();
      while (e.hasMoreElements())
      {
        traverseGenericNode((Node) e.nextElement(), element);
      }
    }

    if (n.getChildrenEnumerator().hasMoreElements())
    {
      parents.addElement(element);
      currentLevel = parents.size() - 1;
      tGroupNode(n);
    }
  }//end of tSwitch()

  /**
   * Traverses a LOD node.
   * @param n a LOD node to traverse
   */
  public void tLOD(LOD n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultcenter = {0, 0, 0};
    if (!arraysEqual(n.center.getValue(), defaultcenter))
    {
      float[] center = n.center.getValue();
      element.setAttribute("center", convertToString(center, center.length));
    }

    float[] defaultrange = {0, 0, 0, 0}; //due to default MFFloat is an array of 0's of size 4
    if (!arraysEqual(n.range.getValueData(), defaultrange))
    {
      element.setAttribute("range", convertToString(n.range.getValueData(), n.range.getValueCount()));
    }

    if (n.level != null)
    {
      Enumeration e = n.level.getNodes().elements();
      while (e.hasMoreElements())
      {
        traverseGenericNode((Node) e.nextElement(), element);
      }
    }

    if (n.getChildrenEnumerator().hasMoreElements())
    {
      parents.addElement(element);
      currentLevel = parents.size() - 1;
      tGroupNode(n);
    }
  }//end of tLOD()

  /**
   * Traverses an Inline node.
   * @param n an Inline node
   */
  public void tInline(Inline n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false;
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultbboxCenter = {0, 0, 0};
    if (!arraysEqual(n.bboxCenter.getValue(), defaultbboxCenter))
    {
      element.setAttribute("bboxCenter", convertToString(n.bboxCenter.getValue(), 3));
    }

    float[] defaultbboxSize = {-1, -1, -1};
    if (!arraysEqual(n.bboxSize.getValue(), defaultbboxSize))
    {
      element.setAttribute("bboxSize", convertToString(n.bboxSize.getValue(), 3));
    }

    if (n.url != null)
    {
      element.setAttribute("url", convertToString(n.url.getValueData(), n.url.getValueCount(), true));
    }
  }//end of tInline()

  /**
   * Traverses a Transform node.
   * @param n a Transform node to traverse
   */
  public void tTransform(Transform n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultbboxCenter = {0, 0, 0};
    if (!arraysEqual(n.bboxCenter.getValue(), defaultbboxCenter))
    {
      element.setAttribute("bboxCenter", convertToString(n.bboxCenter.getValue(), 3));
    }

    float[] defaultbboxSize = {-1, -1, -1};
    if (!arraysEqual(n.bboxSize.getValue(), defaultbboxSize))
    {
      element.setAttribute("bboxSize", convertToString(n.bboxSize.getValue(), 3));
    }

    float[] defaultcenter = {0, 0, 0};
    if (!arraysEqual(n.center.getValue(), defaultcenter))
    {
      element.setAttribute("center", convertToString(n.center.getValue(), 3));
    }

    float[] defaultRotation = {0, 0, 1, 0};
    if (!arraysEqual(n.rotation.getValue(), defaultRotation))
    {
      element.setAttribute("rotation", convertToString(n.rotation.getValue(), 4));
    }

    float[] defaultScale = {1, 1, 1};
    if (!arraysEqual(n.scale.getValue(), defaultScale))
    {
      element.setAttribute("scale", convertToString(n.scale.getValue(), 3));
    }

    float[] defaultScaleOrientation = {0, 0, 1, 0};
    if (!arraysEqual(n.scaleOrientation.getValue(), defaultScaleOrientation))
    {
      element.setAttribute("scaleOrientation", convertToString(n.scaleOrientation.getValue(), 4));
    }

    float[] defaultTranslation = {0, 0, 0};
    if (!arraysEqual(n.translation.getValue(), defaultTranslation))
    {
      element.setAttribute("translation", convertToString(n.translation.getValue(), 3));
    }

    if (n.getChildrenEnumerator().hasMoreElements())
    {
      parents.addElement(element);
      currentLevel = parents.size() - 1;
      tGroupNode(n);
    }
  }//end of tTransform()

  /**
   * Traverses a Collision node.
   * @param n a Collision node to traverse
   */
  public void tCollision(Collision n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultbboxCenter = {0, 0, 0};
    if (!arraysEqual(n.bboxCenter.getValue(), defaultbboxCenter))
    {
      element.setAttribute("bboxCenter", convertToString(n.bboxCenter.getValue(), 3));
    }

    float[] defaultbboxSize = {-1, -1, -1};
    if (!arraysEqual(n.bboxSize.getValue(), defaultbboxSize))
    {
      element.setAttribute("bboxSize", convertToString(n.bboxSize.getValue(), 3));
    }

    if (n.enabled.getValue() != true) // formerly 'collide'
      element.setAttribute("enabled", "false"); // formerly 'collide'

    if (n.proxy != null)
    {
      traverseGenericNode(n.proxy.getNode(), element);
    }

    if (n.getChildrenEnumerator().hasMoreElements())
    {
      parents.addElement(element);
      currentLevel = parents.size() - 1;
      tGroupNode(n);
    }
  }//end of tCollision()

  /**
   * Traverses a Billboard node.
   * @param n a Billboard node to traverse
   */
  public void tBillboard(Billboard n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultAxisOfRotation = {0, 1, 0};
    if (!arraysEqual(n.axisOfRotation.getValue(), defaultAxisOfRotation))
    {
      element.setAttribute("axisOfRotation", convertToString(n.axisOfRotation.getValue(), 3));
    }

    float[] defaultbboxCenter = {0, 0, 0};
    if (!arraysEqual(n.bboxCenter.getValue(), defaultbboxCenter))
    {
      float[] bboxCenter = n.bboxCenter.getValue();
      element.setAttribute("bboxCenter", convertToString(bboxCenter, bboxCenter.length));
    }

    float[] defaultbboxSize = {-1, -1, -1};
    if (!arraysEqual(n.bboxSize.getValue(), defaultbboxSize))
    {
      float[] bboxSize = n.bboxSize.getValue();
      element.setAttribute("bboxSize", convertToString(bboxSize, bboxSize.length));
    }

    if (n.getChildrenEnumerator().hasMoreElements())
    {
      parents.addElement(element);
      currentLevel = parents.size() - 1;
      tGroupNode(n);
    }
  }//end of tBillboard()

  /**
   * Traverses an Anchor node.
   * @param n an Anchor node to traverse
   */
  public void tAnchor(Anchor n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultbboxCenter = {0, 0, 0};
    if (!arraysEqual(n.bboxCenter.getValue(), defaultbboxCenter))
    {
      float[] bboxCenter = n.bboxCenter.getValue();
      element.setAttribute("bboxCenter", convertToString(bboxCenter, bboxCenter.length));
    }

    float[] defaultbboxSize = {-1, -1, -1};
    if (!arraysEqual(n.bboxSize.getValue(), defaultbboxSize))
    {
      float[] bboxSize = n.bboxSize.getValue();
      element.setAttribute("bboxSize", convertToString(bboxSize, bboxSize.length));
    }

    if (n.description != null)
    {
      if (n.description.getValue() != null)
        element.setAttribute("description", n.description.getValue());
    }

    if (n.parameter != null)
    {
      if (n.parameter.getValueCount() > 0)
      {
        element.setAttribute("parameter",
          convertToString(n.parameter.getValueData(), n.parameter.getValueCount(), false));
      }
    }

    if (n.url != null)
    {
      if (n.url.getValueCount() > 0)
      {
        element.setAttribute("url",
          convertToString(n.url.getValueData(), n.url.getValueCount(), true));
      }
    }

    if (n.getChildrenEnumerator().hasMoreElements())
    {
      parents.addElement(element);
      currentLevel = parents.size() - 1;
      tGroupNode(n);
    }
  }//end of tAnchor()

  /**
   * Traverses a Group node.
   * @param n a Group node to traverse
   */
  public void tGroup(Group n)
  {
    Element element = doc.createElement(n.nodeName());
    checkISconnect (n, element);

    if (!parentElementSet)
      parentElement = (Element) parents.elementAt(currentLevel);
    else
      parentElementSet = false; //reset
    parentElement.appendChild(element);

    if (n.objname != null)
    {
      if (!setObjectName(element, n.objname))
        return;
    }

    float[] defaultbboxCenter = {0, 0, 0};
    if (!arraysEqual(n.bboxCenter.getValue(), defaultbboxCenter))
    {
      float[] bboxCenter = n.bboxCenter.getValue();
      element.setAttribute("bboxCenter", convertToString(bboxCenter, bboxCenter.length));
    }

    float[] defaultbboxSize = {-1, -1, -1};
    if (!arraysEqual(n.bboxSize.getValue(), defaultbboxSize))
    {
      float[] bboxSize = n.bboxSize.getValue();
      element.setAttribute("bboxSize", convertToString(bboxSize, bboxSize.length));
    }

    if (n.getChildrenEnumerator().hasMoreElements())
    {
      parents.addElement(element);
      currentLevel = parents.size() - 1;
      tGroupNode(n);
    }
  }//end of tGroup()

  /**
   * Registers object name for the node.  The ojbect name can
   * either be a DEF attribute or an USE attribute.  If the
   * name is unique, then it is for the DEF attribute, else
   * it is for the USE attribute.
   * @param element the node element
   * @param objname the object name
   * @return <code>true</code> if the object name is a DEF,
   * or <code>false</code> if it is a USE
   */
  private boolean setObjectName(Element element, String objname)
  {
    for (int i = 0; i < defNames.size(); i++)
    {
      String name = (String) defNames.elementAt(i);
      if (name.equals(objname))
      {
        element.setAttribute("USE", objname);
        return false;
      }
    }
    element.setAttribute("DEF", objname);
    defNames.addElement(objname);
    return true;
  }

  /**
   * Returns a string representation of a boolean.
   * @param bool a boolean value
   * @return a string representation of the given boolean
   */
  private String convertToString(boolean bool)
  {
    if (bool)
      return "true";
    else
      return "false";
  }

  /**
   * Returns a string representation of a integer array.
   * @param array an array containing integer elements
   * @param length number of valid integer numbers
   * @return a string representation of the given integer array
   */
  private String convertToString(int[] array, int length)
  {
    StringBuffer buf = new StringBuffer("" + array[0]);
    for (int i = 1; i < length; i++)
    {
      buf.append(" ");
      buf.append(Integer.toString(array[i]));
    }
    return buf.toString();
  }

  /**
   * Returns a string representation of a integer array.
   * @param array an array containing integer elements
   * @param length number of valid integer numbers
   * @param count number of float numbers on a single line
   * @return a string representation of the given integer array
   */
  private String convertToString(int[] array, int length, int count)
  {
    StringBuffer buf = new StringBuffer();
    int j = 1;
    for (int i = 0; i < length; i++)
    {
      if (j < count)
      {
//      if (j == 1)
//        buf.append(Integer.toString(array[i]));
//      else
//      {
          buf.append(" ");
          buf.append(Integer.toString(array[i]));
//      }
      }
      else
      {
        buf.append(" ");
        buf.append(Integer.toString(array[i]));
        if (((i + 1) % 10) == 0) buf.append("\n");
        j = 0;
      }
      j++;
    }
    return buf.toString();
  }//end of convertToString(int[],int,int)

  /**
   * Returns a string representation of a float array.
   * @param array an array containing float elements
   * @param length number of valid float numbers
   * @return a string representation of the given float array
   */
  private String convertToString(float[] array, int length)
  {
    StringBuffer buf = new StringBuffer("" + array[0]);
    for (int i = 1; i < length; i++)
    {
      buf.append(" ");
      buf.append(Float.toString(array[i]));
    }
    return buf.toString();
  }

  /**
   * Returns a string representation of a float array.
   * @param array an array containing float elements
   * @param length number of valid float numbers
   * @param count number of float numbers on a single line
   * @return a string representation of the given float array
   */
  private String convertToString(float[] array, int length, int count)
  {
    StringBuffer buf = new StringBuffer();
    int j = 1;
    for (int i = 0; i < length; i++)
    {
      if (j < count)
      {
//      if (j == 1)
//        buf.append(Float.toString(array[i]));
//      else
//      {
          buf.append(" ");
          buf.append(Float.toString(array[i]));
//      }
      }
      else
      {
        buf.append(" ");
        buf.append(Float.toString(array[i]));
        if (((i + 1) % 10) == 0) buf.append("\n");
        j = 0;
      }
      j++;
    }
    return buf.toString();
  }//end of convertToString(float[],int,int)

  /**
   * Returns a string representation of a string array.
   * @param array an array containing string elements
   * @param length number of valid strings
   * @param wrapInQuotes if <code>true</code>, wrape each string in quotes,
   * <code>false</code> otherwise
   * @return a string representation of the given string array
   */
  private String convertToString(String[] array, int length, boolean wrapInQuotes)
  {
    StringBuffer buf = new StringBuffer();
    if (array[0] != null)
    {
      if (wrapInQuotes)
      {
        buf.append("\"");
        buf.append(array[0]);
        buf.append("\" ");
      }
      else
        buf.append(array[0]);
    }

    for (int i = 1; i < length; i++)
    {
      if (array[i] != null)
      {
        if (wrapInQuotes)
        {
          buf.append("\"");
          buf.append(array[i]);
          buf.append("\" ");
          if (((i + 1) % 10) == 0) buf.append("\n");
        }
        else
        {
          buf.append(" ");
          buf.append(array[i]);
        }
      }
    }
    return buf.toString();
  }//end of convertToString(String[],int,boolean)

  /**
   * Returns a boolean value indicating whether two float arrays have equal values.  The arrays
   * are equal only if every element in one array is equal to the corresponding element in the
   * other array.
   * @param array1 first array of float elements
   * @param array2 second array of float elements
   * @return <code>true</code> if two arrays are equal, <code>false</code> otherwise
   */
  private boolean arraysEqual(float[] array1, float[] array2)
  {
    if (array1.length != array2.length)
      return false;
    for (int i = 0; i < array1.length; i++)
    {
      if (array1[i] != array2[i])
        return false;
    }
    return true;
  }

  /**
   * Returns a boolean value indicating whether two integer arrays have equal values.  The arrays
   * are equal only if every element in one array is equal to the corresponding element in the
   * other array.
   * @param array1 first array of integer elements
   * @param array2 second array of integer elements
   * @return <code>true</code> if two arrays are equal, <code>false</code> otherwise
   */
  private boolean arraysEqual(int[] array1, int[] array2)
  {
    if (array1.length != array2.length)
      return false;
    for (int i = 0; i < array1.length; i++)
    {
      if (array1[i] != array2[i])
        return false;
    }
    return true;
  }

  /**
   * Returns a boolean value indicating whether two string arrays have equal values.  The arrays
   * are equal only if every element in one array is equal to the corresponding element in the
   * other array.
   * @param array1 first array of string elements
   * @param array2 second array of string elements
   * @return <code>true</code> if two arrays are equal, <code>false</code> otherwise
   */
  private boolean arraysEqual(String[] array1, String[] array2)
  {
    if (array1.length != array2.length)
      return false;
    for (int i = 0; i < array1.length; i++)
    {
      if (!array1[i].equals(array2[i]))
        return false;
    }
    return true;
  }

  /**
   * Returns a field Element by extracting data from the given <code>Field</code> object.
   * @param f the structure that contains relevent data
   * @param name user specified field name
   * @return a field Element
   */
  private Element getFieldElement(Field f, String name)
  {
    Element fieldElement;

    if (setFieldValue)
      fieldElement = doc.createElement("fieldValue");
    else
    {
      fieldElement = doc.createElement("field");
      fieldElement.setAttribute("accessType", f.getFieldClassName());
//    fieldElement.setAttribute("accessType", f.getFieldClassNameX3dAccessType());
//    fieldElement.setAttribute("type", f.fieldNameToX3d(f.fieldName()));
      fieldElement.setAttribute("type", f.fieldName());

      SystemOut.println ("f.fieldName() type=" + f.fieldName());

      if (f.protoIScontent != null)
         SystemOut.println (f.protoIScontent);

        //[Wu] - Here's where I think we should add in the new IS/connect handling, Oct2002

      //IS attribute no longer applicable for ProtoDeclare fields
      if (!setExternProto && f.protoIScontent != null)
      {
        //fieldElement.setAttribute("IS", f.protoIScontent);
        SystemOut.println("SceneTraverser <field> protoIScontent: " + f.protoIScontent);
      }
    }

    fieldElement.setAttribute("name", name);

    if (!setExternProto && (f.getFieldClass() != Field.F_EVENTIN) && (f.getFieldClass() != Field.F_EVENTOUT) && (f.protoIScontent == null))
    {
      if (f instanceof SFBool)
        fieldElement.setAttribute("value", convertToString(((SFBool) f).getValue()));
      else if (f instanceof SFColor)
        fieldElement.setAttribute("value", convertToString(((SFColor) f).getValue(), 3));
      else if (f instanceof SFRotation)
        fieldElement.setAttribute("value", convertToString(((SFRotation) f).getValue(), 4));
      else if (f instanceof SFVec2f)
        fieldElement.setAttribute("value", convertToString(((SFVec2f) f).getValue(), 2));
      else if (f instanceof SFVec3f)
        fieldElement.setAttribute("value", convertToString(((SFVec3f) f).getValue(), 3));
      else if (f instanceof SFFloat)
        fieldElement.setAttribute("value", "" + ((SFFloat) f).getValue());
      else if (f instanceof SFInt32)
        fieldElement.setAttribute("value", "" + ((SFInt32) f).getValue());
      else if (f instanceof SFString)
        fieldElement.setAttribute("value", ((SFString) f).getValue());
      else if (f instanceof SFTime)
        fieldElement.setAttribute("value", "" + ((SFTime) f).getValue());
      else if (f instanceof MFColor)
      {
        fieldElement.setAttribute("value",
          convertToString(((MFColor) f).getValueData(), ((MFColor) f).getValueCount() * 3, 3));
      }
      else if (f instanceof MFFloat)
      {
        fieldElement.setAttribute("value",
          convertToString(((MFFloat) f).getValueData(), ((MFFloat) f).getValueCount(), 6));
      }
      else if (f instanceof MFInt32)
      {
        fieldElement.setAttribute("value",
          convertToString(((MFInt32) f).getValueData(), ((MFInt32) f).getValueCount(), 6));
      }
      else if (f instanceof MFRotation)
      {
        fieldElement.setAttribute("value",
          convertToString(((MFRotation) f).getValueData(), ((MFRotation) f).getValueCount() * 4, 4));
      }
      else if (f instanceof MFString)
      {
        fieldElement.setAttribute("value",
          convertToString(((MFString) f).getValueData(), ((MFString) f).getValueCount(), false));
      }
      else if (f instanceof MFVec2f)
      {
        fieldElement.setAttribute("value",
          convertToString(((MFVec2f) f).getValueData(), ((MFVec2f) f).getValueCount() * 2, 2));
      }
      else if (f instanceof MFVec3f)
      {
        fieldElement.setAttribute("value",
          convertToString(((MFVec3f) f).getValueData(), ((MFVec3f) f).getValueCount() * 3, 3));
      }
      else if (f instanceof SFNode)
      {
        Node node = ((SFNode) f).getNode();
        traverseGenericNode(node, fieldElement);
      }
      else if (f instanceof MFNode)
      {
        Enumeration e = ((MFNode) f).getNodes().elements();
        while (e.hasMoreElements())
        {
          traverseGenericNode((Node) e.nextElement(), fieldElement);
        }
      }
    }
    else //the field is an eventIn or an eventOut
    {
      //do not include eventIn and eventOut fields in fieldValue list
      if (setFieldValue)
        return null;
    }

    return fieldElement;
  }//end of getFieldElement()

  /**
   * Traverses a generic node with a given parent.
   * @param node the generic node to traverse
   * @param parent the parent element
   */
  private void traverseGenericNode(Node node, Element parent)
  {
    if (node != null)
    {
      if (node instanceof GroupNode ||
          node instanceof Bindable ||
          node instanceof Interpolator ||
          node instanceof Sensor ||
          node instanceof Light ||
          node instanceof Common ||
          node instanceof ProtoInstance)
      {
        parentElement = parent;
        parentElementSet = true;
      }
      else if (node instanceof AppearNode ||
               node instanceof Geometry ||
               node instanceof Texture)
      {
        parentShape = parent;
        parentText = parent;
        parentAppearance = parent;
      }
      else if (node instanceof Color)
        parentOfColor = parent;
      else if (node instanceof Normal)
        parentOfNormal = parent;
      else if (node instanceof TextureCoordinate)
        parentOfTextureCoordinate = parent;
      else if (node instanceof Coordinate)
        parentOfCoordinate = parent;

      node.traverse(this);
    }
  }//end of traverseGenericNode()
}//end of class SceneTraverser
