#!/bin/bash

rm -rf input output
mkdir input output

cp debug/*.pdf input

./bin/dochive input/Cooke_Dale_2012-05-25.pdf output true templates * *

