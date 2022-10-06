package iicm.vrml.pw;

import java.lang.reflect.InvocationTargetException;

/**
 * NodeNames.java
 * Created on Jun 5, 2008
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author mike
 * @version $Id$
 * 
 * originally:
 * created: mpichler, 19960930 (pulled out of Node.java)
 * changed: krosch, 19961104
 * changed: mpichler, 19970108
 */
public class NodeNames
{
  // If class name changed, must change following
  static String thisPackage = NodeNames.class.getPackage().getName()+".";
  
  /**
   * Redo of original.  Performs a no-arg constructor call to a class of the given name,
   * in _this_ package, only if it's a descendant of the Node class in this package.
   * @param name
   * @return Node
   */
  public static Node createInstanceFromName(String name)
  {
    try {
      Class<?> cls = Class.forName(thisPackage + name);

      Class<?> superC = cls;
      while((superC=superC.getSuperclass()) != null) {
        if(superC.equals(Node.class))
          return (Node)cls.getDeclaredConstructor(cls).newInstance();
      }
      // fall out
    }
    catch(ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {} // fall through
    
    System.err.println("No support found for node " + thisPackage + name);
    return null;
  }
  
  // Grouping nodes --> GroupNode
  public final static String NODE_GROUP     = "Group";
  public final static String NODE_ANCHOR    = "Anchor";
  public final static String NODE_BILLBOARD = "Billboard";
  public final static String NODE_COLLISION = "Collision";
  public final static String NODE_TRANSFORM = "Transform";
  public final static String NODE_INLINE    = "Inline";
  public final static String NODE_LOD       = "LOD";
  public final static String NODE_SWITCH    = "Switch";

  // Common Nodes --> Common
  public final static String NODE_AUDIOCLIP        = "AudioClip";
  public final static String NODE_DIRECTIONALLIGHT = "DirectionalLight";
  public final static String NODE_POINTLIGHT       = "PointLight";
  public final static String NODE_SHAPE            = "Shape";
  public final static String NODE_SOUND            = "Sound";
  public final static String NODE_SPOTLIGHT        = "SpotLight";
  public final static String NODE_SCRIPT           = "Script";
  public final static String NODE_WORLDINFO        = "WorldInfo";

  public final static String NODE_ROUTE            = "ROUTE";
  public final static String NODE_META             = "META";
  public final static String NODE_PROFILE          = "PROFILE";

  // Sensors --> Sensor
  public final static String NODE_CYLINDERSENSOR   = "CylinderSensor";
  public final static String NODE_PLANESENSOR      = "PlaneSensor";
  public final static String NODE_PROXIMITYSENSOR  = "ProximitySensor";
  public final static String NODE_SPHERESENSOR     = "SphereSensor";
  public final static String NODE_TIMESENSOR       = "TimeSensor";
  public final static String NODE_TOUCHSENSOR      = "TouchSensor";
  public final static String NODE_VISIBILITYSENSOR = "VisibilitySensor";

  // Geometry --> Geometry
  public final static String NODE_BOX            = "Box";
  public final static String NODE_CONE           = "Cone";
  public final static String NODE_CYLINDER       = "Cylinder";
  public final static String NODE_ELEVATIONGRID  = "ElevationGrid";
  public final static String NODE_EXTRUSION      = "Extrusion";
  public final static String NODE_INDEXEDFACESET = "IndexedFaceSet";
  public final static String NODE_INDEXEDLINESET = "IndexedLineSet";
  public final static String NODE_POINTSET       = "PointSet";
  public final static String NODE_SPHERE         = "Sphere";
  public final static String NODE_TEXT           = "Text";

  // Geometric Properties --> Node
  public final static String NODE_COLOR             = "Color";
  public final static String NODE_COORDINATE        = "Coordinate";
  public final static String NODE_NORMAL            = "Normal";
  public final static String NODE_TEXTURECOORDINATE = "TextureCoordinate";

  // Appearance --> AppearNode
  public final static String NODE_APPEARANCE       = "Appearance";
  public final static String NODE_FONTSTYLE        = "FontStyle";
  public final static String NODE_IMAGETEXTURE     = "ImageTexture";
  public final static String NODE_MATERIAL         = "Material";
  public final static String NODE_MOVIETEXTURE     = "MovieTexture";
  public final static String NODE_PIXELTEXTURE     = "PixelTexture";
  public final static String NODE_TEXTURETRANSFORM = "TextureTransform";

  // Interpolators --> Interpolator
  public final static String NODE_COLORINTERPOLATOR       = "ColorInterpolator";
  public final static String NODE_COORDINATEINTERPOLATOR  = "CoordinateInterpolator";
  public final static String NODE_NORMALINTERPOLATOR      = "NormalInterpolator";
  public final static String NODE_ORIENTATIONINTERPOLATOR = "OrientationInterpolator";
  public final static String NODE_POSITIONINTERPOLATOR    = "PositionInterpolator";
  public final static String NODE_SCALARINTERPOLATOR      = "ScalarInterpolator";

  // bindable nodes --> Bindable
  public final static String NODE_BACKGROUND     = "Background";
  public final static String NODE_FOG            = "Fog";
  public final static String NODE_NAVIGATIONINFO = "NavigationInfo";
  public final static String NODE_VIEWPOINT      = "Viewpoint";

}
