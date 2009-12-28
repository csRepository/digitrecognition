#!/bin/bash
path='build/classes:lib/groovy-all.jar';
for file  in config/*xml
do
	echo $file;
	java -classpath $path Test $file 
done
