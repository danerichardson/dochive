@echo on

rd input /q /s
mkdir input

rd output /q /s
mkdir output

copy debug\*.pdf input

bin\dochive input\Cooke_Dale_2012-05-25.pdf output true templates * *

pause
