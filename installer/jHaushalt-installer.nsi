; Taken from http://nsis.sourceforge.net/New_installer_with_JRE_check_(includes_fixes_from_'Simple_installer_with_JRE_check'_and_missing_jre.ini) by csmith

; originally taken from http://nsis.sourceforge.net/Simple_installer_with_JRE_check by weebib
; Use it as you desire.
 
; Credit given to so many people of the NSIS forum.
 
!define AppName "jHaushalt"
!define AppVersion "2.6.1"
!define ShortName "jHaushalt"
!define JRE_VERSION "1.5"
!define Vendor "Selfmade"
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=63691"
 
!include "MUI.nsh"
!include "Sections.nsh"
 
Var InstallJRE
Var JREPath
Var JAVA_HOME
Var StartMenuFolder
 
 
;--------------------------------
;Configuration
 
;General
Name "${AppName}"
OutFile "jhaushalt-${AppVersion}-setup.exe"
 
;Folder selection page
InstallDir "$PROGRAMFILES\${SHORTNAME}"
 
;Get install folder from registry if available
InstallDirRegKey HKLM "SOFTWARE\${Vendor}\${ShortName}" ""
 
 
RequestExecutionLevel user
 
; Installation types
;InstType "full"	; Uncomment if you want Installation types
 
;--------------------------------
;Pages
 
!insertmacro MUI_PAGE_WELCOME
 
; License page
!insertmacro MUI_PAGE_LICENSE "data/gpl_v3.txt"

; This page checks for JRE. It displays a dialog based on JRE.ini if it needs to install JRE
; Otherwise you won't see it.
Page custom CheckInstalledJRE
 
; Define headers for the 'Java installation successfully' page
!define MUI_INSTFILESPAGE_FINISHHEADER_TEXT "Java installation complete"
!define MUI_PAGE_HEADER_TEXT "Installing Java runtime"
!define MUI_PAGE_HEADER_SUBTEXT "Please wait while we install the Java runtime"
!define MUI_INSTFILESPAGE_FINISHHEADER_SUBTEXT "Java runtime installed successfully."
!insertmacro MUI_PAGE_INSTFILES
!define MUI_INSTFILESPAGE_FINISHHEADER_TEXT "Installation complete"
!define MUI_PAGE_HEADER_TEXT "Installing"
!define MUI_PAGE_HEADER_SUBTEXT "Please wait while ${AppName} is being installed."

; Uncomment the next line if you want optional components to be selectable
;  !insertmacro MUI_PAGE_COMPONENTS

!define MUI_PAGE_CUSTOMFUNCTION_PRE myPreInstfiles
!define MUI_PAGE_CUSTOMFUNCTION_LEAVE RestoreSections
!define MUI_FINISHPAGE_SHOWREADME ""
!define MUI_FINISHPAGE_SHOWREADME_NOTCHECKED
!define MUI_FINISHPAGE_SHOWREADME_TEXT "Desktopverkn�pfung erstellen"
!define MUI_FINISHPAGE_SHOWREADME_FUNCTION finishpageaction
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU "jHaushalt" $StartMenuFolder
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
 
;--------------------------------
;Modern UI Configuration
 
!define MUI_ABORTWARNING
 
;--------------------------------
;Languages
 
!insertmacro MUI_LANGUAGE "German"
 
;--------------------------------
;Language Strings
 
;Description
LangString DESC_SecAppFiles ${LANG_ENGLISH} "Application files copy"
 
;Header
LangString TEXT_JRE_TITLE ${LANG_ENGLISH} "Java Runtime Environment"
LangString TEXT_JRE_SUBTITLE ${LANG_ENGLISH} "Installation"
LangString TEXT_PRODVER_TITLE ${LANG_ENGLISH} "Installed version of ${AppName}"
LangString TEXT_PRODVER_SUBTITLE ${LANG_ENGLISH} "Installation cancelled"
 
;--------------------------------
;Reserve Files
 
;Only useful for BZIP2 compression
 
 
ReserveFile "jre.ini"
!insertmacro MUI_RESERVEFILE_INSTALLOPTIONS
 
;--------------------------------
;Installer Sections
 
Section -installjre jre
    Push $0
    Push $1
 
    ;  MessageBox MB_OK "Inside JRE Section"
    Strcmp $InstallJRE "yes" InstallJRE JREPathStorage
    DetailPrint "Starting the JRE installation"
InstallJRE:
    ; Java 7u4: http://javadl.sun.com/webapps/download/AutoDL?BundleId=63691
    ;File /oname=$TEMP\jre_setup.exe j2re-setup.exe
    MessageBox MB_OK "Installing JRE"
    DetailPrint "Launching JRE setup"
    ;ExecWait "$TEMP\jre_setup.exe /S" $0
  ; The silent install /S does not work for installing the JRE, sun has documentation on the 
  ; parameters needed.  I spent about 2 hours hammering my head against the table until it worked
    ExecWait '"$TEMP\jre_setup.exe" /s /v\"/qn REBOOT=Suppress JAVAUPDATE=0 WEBSTARTICON=0\"' $0
    DetailPrint "Setup finished"
    Delete "$TEMP\jre_setup.exe"
    StrCmp $0 "0" InstallVerif 0
    Push "The JRE setup has been abnormally interrupted."
    Goto ExitInstallJRE
 
InstallVerif:
    DetailPrint "Checking the JRE Setup's outcome"
;  MessageBox MB_OK "Checking JRE outcome"
    Push "${JRE_VERSION}"
    Call DetectJRE  
    Pop $JAVA_HOME	  ; DetectJRE's return value

    StrCmp $JAVA_HOME "0" ExitInstallJRE 0
    StrCmp $JAVA_HOME "-1" ExitInstallJRE 0

    Call SetEnv

    Goto JavaExeVerif
    Push "The JRE setup failed"
    Goto ExitInstallJRE
 
JavaExeVerif:
    IfFileExists $0 JREPathStorage 0
    Push "The following file : $0, cannot be found."
    Goto ExitInstallJRE
 
JREPathStorage:
    ;  MessageBox MB_OK "Path Storage"
    !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "UserDefinedSection" "JREPath" $1
    StrCpy $JREPath $0
    Goto End
 
ExitInstallJRE:
    Pop $1
    MessageBox MB_OK "The setup is about to be interrupted for the following reason : $1"
    Pop $1 	; Restore $1
    Pop $0 	; Restore $0
    Abort
End:
    Pop $1	; Restore $1
    Pop $0	; Restore $0
SectionEnd



Section "Installation of ${AppName}" SecAppFiles
    SectionIn 1 RO	; Full install, cannot be unselected
	                ; If you add more sections be sure to add them here as well
    SetOutPath $INSTDIR
    File /r ".\data\*"
; If you need the path to JRE, you can either get it here for from $JREPath
;  !insertmacro MUI_INSTALLOPTIONS_READ $0 "jre.ini" "UserDefinedSection" "JREPath"
;  MessageBox MB_OK "JRE Read: $0"
  ;Store install folder
    WriteRegStr HKLM "SOFTWARE\${Vendor}\${ShortName}" "" $INSTDIR
 
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "DisplayName" "${AppName} (x86)"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "UninstallString" '"$INSTDIR\uninstall.exe"'
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "NoModify" "1"
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "NoRepair" "1"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "DisplayIcon" "$INSTDIR\library\start.ico"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}" "DisplayVersion" "${AppVersion}"
 
  ;Create uninstaller
    WriteUninstaller "$INSTDIR\Uninstall.exe"
 
SectionEnd
 
 
Section "Start menu shortcuts" SecCreateShortcut
    SectionIn 1	; Can be unselected
    !insertmacro MUI_STARTMENU_WRITE_BEGIN "jHaushalt"
    CreateDirectory "$SMPROGRAMS\${AppName}"
    CreateShortCut "$SMPROGRAMS\${AppName}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
    CreateShortCut "$SMPROGRAMS\${AppName}\${AppName}.lnk" "$INSTDIR\jhaushalt.vbs" "" "$INSTDIR\library\start.ico" 0
    !insertmacro MUI_STARTMENU_WRITE_END
; Etc
SectionEnd
 
;--------------------------------
;Descriptions
 
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
!insertmacro MUI_DESCRIPTION_TEXT ${SecAppFiles} $(DESC_SecAppFiles)
!insertmacro MUI_FUNCTION_DESCRIPTION_END
 

;--------------------------------
;Installer Functions
 
Function .onInit
    ;Extract InstallOptions INI Files
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "jre.ini"
    Call SetupSections
FunctionEnd
 
Function myPreInstfiles 
    Call RestoreSections
    SetAutoClose true
FunctionEnd

Function GetJRE2
    MessageBox MB_OK "${AppName} ben�tigt eine Java Runtime Environment. Diese wird nun heruntergeladen und installiert."
 
    StrCpy $2 "$TEMP\Java Runtime Environment.exe"
    nsisdl::download /TIMEOUT=30000 ${JRE_URL} $2
    Pop $R0 ;Get the return value
    StrCmp $R0 "success" +3
    MessageBox MB_OK "Download failed: $R0"
    Quit
    ExecWait $2
    Delete $2
FunctionEnd
 
Function CheckInstalledJRE
    MessageBox MB_OK "Checking Installed JRE Version"
    Push "${JRE_VERSION}"
    Call DetectJRE
    Exch $JAVA_HOME	; Get return value from stack
    StrCmp $JAVA_HOME "0" NoFound
    StrCmp $JAVA_HOME "-1" FoundOld
    Call SetEnv
    Goto JREAlreadyInstalled
 
FoundOld:
    MessageBox MB_OK "Old JRE found, please update"
    !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "Field 1" "Text" "${AppName} requires a more recent version of the Java Runtime Environment than the one found on your computer. The installation of JRE ${JRE_VERSION} will start."
    !insertmacro MUI_HEADER_TEXT "$(TEXT_JRE_TITLE)" "$(TEXT_JRE_SUBTITLE)"
    !insertmacro MUI_INSTALLOPTIONS_DISPLAY_RETURN "jre.ini"
    Call GetJRE2
    Goto MustInstallJRE
 
NoFound:
    MessageBox MB_OK "JRE not found"
    !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "Field 1" "Text" "No Java Runtime Environment could be found on your computer. The installation of JRE v${JRE_VERSION} will start."
    !insertmacro MUI_HEADER_TEXT "$(TEXT_JRE_TITLE)" "$(TEXT_JRE_SUBTITLE)"
    !insertmacro MUI_INSTALLOPTIONS_DISPLAY_RETURN "jre.ini"
    Call GetJRE2
    Goto MustInstallJRE
 
MustInstallJRE:
    Exch $0	; $0 now has the installoptions page return value
    ; Do something with return value here
    Pop $0	; Restore $0
    StrCpy $InstallJRE "yes"
    Return
 
JREAlreadyInstalled:
    ;  MessageBox MB_OK "No download: ${TEMP2}"
    MessageBox MB_OK "JRE already installed"
    StrCpy $InstallJRE "no"
    !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "UserDefinedSection" "JREPath" $JREPATH
    Pop $0		; Restore $0
    Return
 
FunctionEnd
 

 
; DetectJRE. Version requested is on the stack.
; Returns (on stack)	"0" on failure (java too old or not installed), otherwise path to java interpreter
; Returns (on stack): 0 - JRE not found. -1 - JRE found but too old. Otherwise - Path to JAVA HOME
; Stack value will be overwritten!
 
Function DetectJRE
    Exch $0	; Get version requested  
    ; Now the previous value of $0 is on the stack, and the asked for version of JDK is in $0
    Push $1	; $1 = Java version string (ie 1.5.0)
    Push $2	; $2 = Javahome
    Push $3	; $3 and $4 are used for checking the major/minor version of java
    Push $4
    ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    StrCmp $1 "" DetectTry2
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$1" "JavaHome"
    StrCmp $2 "" DetectTry2
    Goto GetJRE
 
DetectTry2:
    ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
    StrCmp $1 "" NoFound
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$1" "JavaHome"
    StrCmp $2 "" NoFound
 
GetJRE:
    ; $0 = version requested. $1 = version found. $2 = javaHome
    IfFileExists "$2\bin\java.exe" 0 NoFound
    StrCpy $3 $0 1			; Get major version. Example: $1 = 1.5.0, now $3 = 1
    StrCpy $4 $1 1			; $3 = major version requested, $4 = major version found
    MessageBox MB_OK "Want $3 , found $4"
    IntCmp $4 $3 0 FoundOld FoundNew
    StrCpy $3 $0 1 2
    StrCpy $4 $1 1 2			; Same as above. $3 is minor version requested, $4 is minor version installed
    MessageBox MB_OK "Want $3 , found $4" 
    IntCmp $4 $3 FoundNew FoundOld FoundNew
 
NoFound:
    MessageBox MB_OK "JRE not found"
    Push "0"
    Goto DetectJREEnd
FoundOld:
    MessageBox MB_OK "JRE too old: $3 is older than $4"
    Push "-1"
    Goto DetectJREEnd  
FoundNew:
    Push "$2"
    Goto DetectJREEnd

DetectJREEnd:
    ; Top of stack is return value, then r4,r3,r2,r1
	Exch	; => r4,rv,r3,r2,r1,r0
	Pop $4	; => rv,r3,r2,r1r,r0
	Exch	; => r3,rv,r2,r1,r0
	Pop $3	; => rv,r2,r1,r0
	Exch 	; => r2,rv,r1,r0
	Pop $2	; => rv,r1,r0
	Exch	; => r1,rv,r0
	Pop $1	; => rv,r0
	Exch	; => r0,rv
	Pop $0	; => rv 
FunctionEnd
 
Function RestoreSections
    !insertmacro UnselectSection ${jre}
    !insertmacro SelectSection ${SecAppFiles}
    !insertmacro SelectSection ${SecCreateShortcut}
FunctionEnd
 
Function SetupSections
    !insertmacro SelectSection ${jre}
    !insertmacro UnselectSection ${SecAppFiles}
    !insertmacro UnselectSection ${SecCreateShortcut}
FunctionEnd
 
Function SetEnv
    Push $3
    Push $4
 
    FileOpen $4 "$INSTDIR\setEnv.cmd" w
    StrCpy $3 "Set CLASSPATH='$JAVA_HOME\jre\lib\dt.jar\';'$JAVA_HOME\lib\dt.jar';%CLASSPATH%"
    FileWrite $4 "$3"
 
    FileWriteByte $4 "13"
    FileWriteByte $4 "10"
 
    StrCpy $3 "Set PATH='$JAVA_HOME\bin';%PATH%"
    FileWrite $4 "$3"
    FileClose $4
 
    Pop $4
    Pop $3
FunctionEnd

Function finishpageaction
    CreateShortcut "$desktop\jHaushalt.lnk" "$INSTDIR\jhaushalt.vbs" "" "$INSTDIR\library\start.ico"
FunctionEnd

;--------------------------------
;Uninstaller Section
 
Section "Uninstall"
    ; remove registry keys
    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${ShortName}"
    DeleteRegKey HKLM  "SOFTWARE\${Vendor}\${AppName}"
    ; remove shortcuts, if any.
    Delete "$desktop\jHaushalt.lnk"
    Delete "$SMPROGRAMS\${AppName}\*.*"
    RMDir /r "$SMPROGRAMS\${AppName}"
    ; remove files
    Delete "$INSTDIR\*.*"
    RMDir /r "$INSTDIR"
SectionEnd