#!/bin/bash
path='build/classes:lib/groovy-all.jar';
file=$1;
java -classpath $path Train $file;
