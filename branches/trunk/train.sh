#!/bin/bash
path='build/classes:lib/groovy-all.jar';
for file  in config/parameters_bp*
do
	echo $file;
	java -classpath $path Train $file;
done

