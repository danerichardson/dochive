@echo on

del bin\DocHive.jar

javac org\rpr\dh\*.java
jar cfm bin\DocHive.jar org\rpr\dh\Manifest.txt org\rpr\dh\*.class
del org\rpr\dh\*.class

pause
