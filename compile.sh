#!/bin/sh

rm -f bin/DocHive.jar

javac org/rpr/dh/*.java
jar cfm bin/DocHive.jar org/rpr/dh/Manifest.txt org/rpr/dh/*.class
rm org/rpr/dh/*.class

