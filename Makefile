# Program name:	Makefile
# Description:	Build the VrmlToX3dNist translator distribution
# Location:	C:\www.web3d.org\x3d\content\Vrml97ToX3dNist
#		http://www.web3d.org/x3d/content/Vrml97ToX3dNist
# Author:	Don Brutzman
# Created:	17 March    2002
# Revised:	 2 December 2011
#
# Invocation:	make all
#
# ==============================================================================

# You may need to edit some of these drives and directories to match your machine

#JDK_CLASSPATH		= c:/j2sdk1.4.2_06
#JDK_CLASSPATH		= C:/jdk1.5.0
#JDK_CLASSPATH		= 'C:/Program Files/Java/jdk1.5.0_06'
#JDK_CLASSPATH		= 'C:/Program Files/Java/jdk1.6.0_18'
JDK_CLASSPATH		= 'C:/Program Files (x86)/Java/jdk1.6.0_29'

JAVA_DIR		= $(JDK_CLASSPATH)/bin

# ==============================================================================

help:
	@echo 'Usage:  make [all | clean | java | doc | convertExamples | help]'

clean:
	cd class; rm -f */*/*.class */*/*/*.class
	rm -f  *.bak
	rm -f  */*.bak
	rm -f  */*/*.bak
	rm -f  */*/*/*.bak
	rm -f  */*/*/*/*.bak
	rm -f  *.'$$$$$$'
	rm -f  */*.'$$$$$$'
	rm -f  */*/*.'$$$$$$'
	rm -f  */*/*/*.'$$$$$$'
	rm -f  */*/*/*/*.'$$$$$$'

java:
	@echo NIST make...
	pwd
	$(JAVA_DIR)/java -version
	$(JAVA_DIR)/javac -source 1.5 -target 1.5 -classpath "javasrc;$(CLASSPATH)" -d class -deprecation -g \
		javasrc/iicm/*.java \
		javasrc/iicm/ge3d/*.java \
		javasrc/iicm/utils3d/*.java \
		javasrc/iicm/vrml/pw/*.java \
		javasrc/iicm/vrml/pwutils/*.java \
		javasrc/iicm/vrml/vrml2x3d/*.java

all:
	@echo ====================
	date
	$(JAVA_DIR)/java  -version
	@echo CLASSPATH="$(CLASSPATH)"
	@echo ====================
	make -i clean
	@echo ====================
	make -i java
	@echo ====================
	make -i mkdir
	make -i doc
	@echo ====================
	cd class; jar -cvf Vrml97ToX3dNist.jar */*.class */*/*.class */*/*/*.class
	mv class/Vrml97ToX3dNist.jar .
#	@echo copy .jar for use with Vrml97ToX3dNist.bat
#	cp Vrml97ToX3dNist.jar c:/www.web3d.org/x3d/content/
#	cp Vrml97ToX3dNist.jar c:/Ibm/Xeena/lib/
#	ls -l c:/www.web3d.org/x3d/content/Vrml97ToX3dNist.jar
#	ls -l c:/Ibm/Xeena/lib/Vrml97ToX3dNist.jar
	make -i zip
#	@echo ====================
#	@echo Xeena make all:
#	cd C:/Ibm/Xeena;  pwd;  make all
	@echo ====================
	make -i convertExamples
	@echo ====================
	date
	@echo 'need to save current output as file MakeBuild.out:'
	pwd;  ls -l MakeBuild.out
	@echo 'make all complete.'

mkdir:
	mkdir class
	mkdir javadoc

doc:
	$(JAVA_DIR)/javadoc -classpath javasrc -d javadoc -author -version -private -breakiterator	\
		-windowtitle "NIST Translator: VRML 97 to X3D Javadoc"			\
		-doctitle "<center>NIST Translator: VRML 97 to X3D Javadoc</center>"	\
		iicm			\
		iicm.ge3d		\
		iicm.utils3d		\
		iicm.vrml.pw		\
		iicm.vrml.pwutils	\
		iicm.vrml.vrml2x3d

zip:
#	TODO use Ant to exclude .svn subdirectories
	rm -f Vrml97ToX3dNist.zip ../Vrml97ToX3dNist.zip
	cd ..;	jar -cvf Vrml97ToX3dNist.zip Vrml97ToX3dNist/*
	mv ../Vrml97ToX3dNist.zip .
	ls -al Vrml97ToX3dNist.zip

x3dv:
	make java
	cd test; pwd; ../Vrml97ToX3dNist.bat AddDynamicRoutes.x3dv				AddDynamicRoutes.x3d -transitionalDTD

JavaLB:
	cd JavaLBExamples;  pwd;  ../Vrml97ToX3dNist.bat AddDynamicRoutes.x3dv			AddDynamicRoutes.x3d
	cd JavaLBExamples;  pwd;  ../Vrml97ToX3dNist.bat CreateNodes.x3dv			CreateNodes.x3d
	cd JavaLBExamples;  pwd;  ../Vrml97ToX3dNist.bat CreateNodesFromPrototype.x3dv		CreateNodesFromPrototype.x3d
	cd JavaLBExamples;  pwd;  ../Vrml97ToX3dNist.bat PerFrameNotification.x3dv		PerFrameNotification.x3d
	cd JavaLBExamples;  pwd;  ../Vrml97ToX3dNist.bat TouchSensorIsOverEvent.x3dv		TouchSensorIsOverEvent.x3d


convertExamples:
	pwd; Vrml97ToX3dNist.bat 'test\TranslationTestScene.wrl'		'test\TranslationTestScene.x3d' -transitionalDTD
	pwd; Vrml97ToX3dNist.bat 'test\NewShape.wrl'				'test\NewShapeToX3D.x3d'   -transitionalDTD
	pwd; Vrml97ToX3dNist.bat 'test\ai0.wrl'					'test\ai0ToX3D.x3d'        -transitionalDTD
	pwd; Vrml97ToX3dNist.bat 'test\Rollers.wrl'				'test\RollersToX3D.x3d'    -transitionalDTD
# TODO fix path, classpath for each invocation
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\HelloWorld.wrl'	'test\HelloWorldToX3d.x3d' -transitionalDTD
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\HelloWorld.wrl'	'test\HelloWorldToX3d.x3d' -transitionalDTD
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Vrml2.0Sourcebook\Chapter31-Prototypes\Figure31.7DonutExternalPrototype.wrl'	'test\Figure31.7DonutExternalPrototypeToX3D.x3d' -transitionalDTD
#debugConversion:
# death loop is in writing out protoinstance
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Vrml2.0Sourcebook\Chapter31-Prototypes\Figure31.9SpinGroupPrototype.wrl'  'test\Figure31.9SpinGroupPrototypeToX3d.x3d' -transitionalDTD
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\KelpForestExhibit\shark2.1.wrl'					    'test\shark2.1ToX3D.x3d' -transitionalDTD
#stop:
# GeoSpatialExamples:
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\A1_GeoElevationGrid.wrl'	    'test\A1_GeoElevationGridToX3D.x3d' -transitionalDTD
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\A2_AnimatedGeoViewpoint.wrl'	    'test\A2_AnimatedGeoViewpointToX3D.x3d' -transitionalDTD
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\A3_GeoInline.wrl'		    'test\A3_GeoInlineToX3D.x3d' -transitionalDTD
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\A4_GeoLocation.wrl'		    'test\A4_GeoLocationToX3D.x3d' -transitionalDTD
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\A5_GeoOrigin.wrl'		    'test\A5_GeoOriginToX3D.x3d' -transitionalDTD

convertGeoVrmlExamplesToX3D:
	rm -f ../examples/GeoSpatial/GeoVrmlSiteExamples/conversions/*.x3d
	ls    ../examples/GeoSpatial/GeoVrmlSiteExamples/conversions/
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\Mars.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\Mars.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD000.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD000.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD001.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD001.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD002.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD002.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD003.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD003.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD004.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD004.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD005.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD005.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD006.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD006.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD007.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD007.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD008.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD008.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD009.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD009.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD010.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD010.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD011.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD011.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD012.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD012.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD013.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD013.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD014.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD015.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD015.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD015.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD016.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD016.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD017.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD017.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD018.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD018.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD019.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD019.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD020.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD020.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD021.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD021.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD022.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD022.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\mars\MarsLOD023.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\MarsLOD023.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\shapeviz\mexico.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\Mexico.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squaw.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\Squaw.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD000.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD000.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD001.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD001.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD002.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD002.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD003.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD003.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD004.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD004.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD005.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD005.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD006.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD006.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD007.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD007.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD008.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD008.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD009.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD009.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD010.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD010.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD011.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD011.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD012.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD012.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD013.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD013.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD014.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD014.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD015.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD015.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD016.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD016.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD017.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD017.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD018.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD018.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD019.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD019.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD020.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD020.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD021.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD021.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD022.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD022.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD023.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD023.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD024.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD024.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD025.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD025.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD026.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD026.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD027.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD027.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD028.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD028.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\squaw\squawLOD029.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SquawLOD029.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\sricampus\world.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SriCampus.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\sricampus\world2.wrl'	'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SriCampus2.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\temple\pretty.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\TemplePretty.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\temple\temple.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\Temple.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\trips\model.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\TripsModel.x3d'
# SpinViewPoint same as example A5
#	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\viewpoint\spin.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\SpinViewpoint.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\world\example.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\WorldExample.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\world\test.wrl'		'..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\WorldTest.x3d'
	pwd;  Vrml97ToX3dNist.bat '..\..\content\examples\Basic\GeoSpatial\GeoVrmlSiteExamples\world\world.wrl'		'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\World.x3d'
# changed names

change:
	cd ../examples/GeoSpatial/GeoVrmlSiteExamples/showgdc;		cp GTS_Demo.wrl		GeoTouchSensorExample.wrl
	cd ../examples/GeoSpatial/GeoVrmlSiteExamples/temple;		cp temple_noorigin.wrl	TempleNoOrigin.wrl
	cd ../examples/GeoSpatial/GeoVrmlSiteExamples/temple;		cp temple_origin.wrl	TempleOrigin.wrl
	cd ../examples/GeoSpatial/GeoVrmlSiteExamples/trips;		cp world_trips.wrl	TripsAroundWorld.wrl
	cd ../examples/GeoSpatial/GeoVrmlSiteExamples/world;		cp world-hi.wrl		WorldHigh.wrl
	cd ../examples/GeoSpatial/GeoVrmlSiteExamples/world;		cp world-lo.wrl		WorldLow.wrl
	cd ../examples/GeoSpatial/GeoVrmlSiteExamples/world;		cp world-test.wrl	WorldTest.wrl
	cd ../examples/GeoSpatial/GeoVrmlSiteExamples/world;		cp test-solid.wrl	WorldTestSolid.wrl
	cd ..;  pwd;  Vrml97ToX3dNist.bat 'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\showgdc\GeoTouchSensorExample.wrl'	'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\GeoTouchSensorExample.x3d'
	cd ..;  pwd;  Vrml97ToX3dNist.bat 'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\temple\TempleNoOrigin.wrl'	'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\TempleNoOrigin.x3d'
	cd ..;  pwd;  Vrml97ToX3dNist.bat 'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\temple\TempleOrigin.wrl'	'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\TempleOrigin.x3d'
	cd ..;  pwd;  Vrml97ToX3dNist.bat 'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\trips\TripsAroundWorld.wrl'	'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\TripsAroundWorld.x3d'
	cd ..;  pwd;  Vrml97ToX3dNist.bat 'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\world\WorldHigh.wrl'		'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\WorldHigh.x3d'
	cd ..;  pwd;  Vrml97ToX3dNist.bat 'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\world\WorldLow.wrl'		'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\WorldLow.x3d'
	cd ..;  pwd;  Vrml97ToX3dNist.bat 'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\world\WorldTest.wrl'		'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\WorldTest.x3d'
	cd ..;  pwd;  Vrml97ToX3dNist.bat 'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\world\WorldTestSolid.wrl'	'examples\Basic\GeoSpatial\GeoVrmlSiteExamples\conversions\WorldTestSolid.x3d'
#
# further fixes to source content:
#	eliminate duplicate GeoOrigin nodes
#	change geoSystem values "GDC" -> "GD"
#	UTM coordinate zones will need to be moved from geoCoords field to a supplementary parameter for geoSystem
#		(but that will require fixing the GeoVrml java source/classes also)
#		X3D specification, paragraphs 25.2.3 Specifying a spatial reference frame
#		and 25.2.4 Specifying geospatial coordinates
#	Integrate metadata from http://www.geovrml.org/examples


