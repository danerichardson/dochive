#!/bin/bash

rm -rf input output
mkdir input output

cp debug/*.pdf input

java -jar app/DocHive.jar input/Cooke_Dale_2012-05-25.pdf output true templates * *

