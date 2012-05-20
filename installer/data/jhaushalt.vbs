Set WshShell = CreateObject("WScript.Shell")
cmds=WshShell.RUN("jhaushalt.bat", 0, True)
Set WshShell = Nothing