#!/bin/sh
java -classpath build/classes/ database/MNISTtoPPM  $1 | convert - GIF:image.gif 
gpicview image.gif

