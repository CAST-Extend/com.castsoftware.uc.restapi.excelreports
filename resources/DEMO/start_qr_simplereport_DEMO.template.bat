@echo off

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET URL=https://demo-eu.castsoftware.com/Health/rest 
SET AADDOMAIN=AAD
SET USER=CIO
SET PASSWORD=cast
SET REPORTTYPE=QR_SimpleReport
SET ENVIRONMENT=DEMO

:: All applications
::SET PROCESSAPPLICATION_FILTER=
:: Only Arizona and Big Ben application
SET PROCESSAPPLICATION_FILTER=-processApplicationFilter "Arizona,Big Ben"

::VERSIONS_LASTONE|VERSIONS_LASTTWO|VERSIONS_ALL
SET VERSION_FILTER=-versionFilter VERSIONS_LASTONE

SET JAVA_HOME=C:\Program Files\Java\jre1.8.0_181

For /F "tokens=1* delims==" %%A IN (version.properties) DO (
    IF "%%A"=="version" set VERSION=%%B
)

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

::Check JRE Installation
IF NOT EXIST "%JAVA_HOME%\bin" GOTO JREPathNotSet

SET CMD="%JAVA_HOME%\bin\java" -jar RestAPIReports-%VERSION%.jar -url %URL% -hDomain %AADDOMAIN% -user %USER% -password %PASSWORD% -environment %ENVIRONMENT% -reportType %REPORTTYPE% %PROCESSAPPLICATION_FILTER%

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

ECHO running %CMD%
%CMD%
ECHO ========================================
SET RETURNCODE=%ERRORLEVEL%
IF NOT %RETURNCODE%==0 GOTO execError
GOTO end


:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:JREPathNotSet
ECHO The JRE Path %JAVA_HOME% is not correct
GOTO end

:execError
ECHO Error executing the command line
GOTO end

:end

PAUSE