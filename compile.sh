#!/bin/sh

rm app/DocHive.jar

javac org/rpr/dh/*.java
jar cfm app/DocHive.jar org/rpr/dh/Manifest.txt org/rpr/dh/*.class
rm org/rpr/dh/*.class

