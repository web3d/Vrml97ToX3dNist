@echo off

REM Filename:	Vrml97ToX3dNist.bat
REM url:	http://www.web3d.org/x3d/content/Vrml97ToX3dNist.bat
REM reference:	http://ovrt.nist.gov/v2_x3d.html by Qiming Wang
REM description	Simplify launching translator under Windows.  Also
REM		handles single filename.  Example:
REM			Vrml97ToX3dNist someScene
REM		is the same as
REM			Vrml97ToX3dNist someScene.wrl someScene.x3d
REM author:	Don Brutzman
REM created:	17 March 2002
REM revised:	 4 April 2005

REM example recompilation:
REM C:\vrml\vrml2x3d>	make all

REM set CLASSPATH=C:/vrml/vrml2x3d/v2x3d_0.1.jar
REM set CLASSPATH=/www.web3d.org/x3d/content/Vrml97ToX3dNist.jar
    set CLASSPATH=/www.web3d.org/x3d/tools/Vrml97ToX3dNist/Vrml97ToX3dNist.jar

@echo CLASSPATH=%CLASSPATH%

if "%1"=="" GOTO NO_ARGUMENT

if     EXIST "%1" if "%2"=="" GOTO ONE_ARGUMENT

if NOT EXIST "%1" if     EXIST "%1.wrl" GOTO APPEND_EXTENSIONS

if NOT EXIST "%1" if NOT EXIST "%1.wrl" GOTO NOT_FOUND

@echo on
java iicm.vrml.vrml2x3d.vrml2x3d %1 %2 %3

@echo off
echo dir %1 %2
     dir %1 %2
GOTO DONE

:ONE_ARGUMENT
@echo on
java iicm.vrml.vrml2x3d.vrml2x3d %1 %1.x3d
@echo off
echo dir %1 %1.x3d
     dir %1 %1.x3d
GOTO DONE

:APPEND_EXTENSIONS
@echo on
java iicm.vrml.vrml2x3d.vrml2x3d %1.wrl %1.x3d
@echo off
echo dir %1.wrl %1.x3d
     dir %1.wrl %1.x3d
GOTO DONE

:NOT_FOUND
@echo Not found: "%1"

:NO_ARGUMENT
@echo Usage:
@echo Vrml97ToX3dNist someScene.wrl someScene.x3d [-transitionalDTD]
@echo or
@echo Vrml97ToX3dNist someScene

:DONE

