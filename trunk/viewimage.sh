#!/bin/sh
for i in `seq 1 1 100`;
do
printf $i' '
java -classpath build/classes/ database/MNISTtoPPM $i label #| convert - GIF:image$i.gif 
done

