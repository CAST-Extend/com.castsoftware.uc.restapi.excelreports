@echo off

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
SET URL=https://demo-eu.castsoftware.com/Engineering/rest 
SET AEDDOMAINS=AED0,AED1,AED2,AED3,AED4,AED5,AED6
SET USER=CIO
SET PASSWORD=cast
SET REPORTTYPE=Metrics_FullReport
SET ENVIRONMENT=DEMO
::SET RUNSQL=-dbRunSql
SET CSSHOST=localhost
SET CSSPORT=2280
SET CSSDB=postgres
SET CSSUSER=operator
SET CSSPWD=CastAIP
SET CENTRALSCHEMAS=schema1,schema2

:: All applications
SET PROCESSAPPLICATION_FILTER=
:: Only eCommerce and Financial applications
::SET PROCESSAPPLICATION_FILTER=-processApplicationFilter "eCommerce,Financial"

::VERSIONS_LASTONE|VERSIONS_LASTTWO|VERSIONS_ALL
SET VERSION_FILTER=-versionFilter VERSIONS_ALL

SET JAVA_HOME=C:\Program Files\Java\jre1.8.0_181

For /F "tokens=1* delims==" %%A IN (version.properties) DO (
    IF "%%A"=="version" set VERSION=%%B
)

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

::Check JRE Installation
IF NOT EXIST "%JAVA_HOME%\bin" GOTO JREPathNotSet

SET CMD="%JAVA_HOME%\bin\java" -jar RestAPIReports-%VERSION%.jar -url %URL% -engDomains %AEDDOMAINS% -user %USER% -password %PASSWORD% -environment %ENVIRONMENT% -reportType %REPORTTYPE% %PROCESSAPPLICATION_FILTER% %VERSION_FILTER% %RUNSQL% -dbHost %CSSHOST% -dbPort %CSSPORT% -dbDatabaseName %CSSDB% -dbUser %CSSUSER% -dbPassword %CSSPWD% -dbSchemas %CENTRALSCHEMAS%

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