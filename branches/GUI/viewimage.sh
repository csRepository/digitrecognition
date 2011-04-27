#!/bin/bash

#for i in `seq 1 1 100`;
#do
#if [ $i -lt 10 ]; then
#	i=00$i
#elif [ $i -lt 100 ]; then
#	i=0$i
#fi
#printf $i' '
path='build/classes:lib/groovy-all.jar';


i=0;

while read LINE
do
     #eval echo "value of a${i} = \${a${i}}"
     eval a${i}=$LINE
     i=`expr $i + 1`;
done <obrazki/number.txt

i=0;
while read LINE
do
     eval b${i}=$LINE
     i=`expr $i + 1`;
done <obrazki/label.txt

i=0;
while read LINE
do
     eval c${i}=$LINE
     i=`expr $i + 1`;
done <obrazki/answer.txt


j=0
for k in `seq 1 1 $i`;
do
	if [ $k -lt 10 ]; then
       		k=00$k
	elif [ $k -lt 100 ]; then
       		k=0$k
	fi
	eval printf "\${a${j}}' '"
    eval string="\${b${j}}'=\>'\${c${j}}"
    eval number="\${a${j}}"
    string1=$string'\n'$number
	eval java -classpath $path database/MNISTtoPPM \${a${j}} | convert -negate -comment $string1 - GIF:obrazki/$k.gif 
	j=`expr $j + 1`;


done

#montage -label '%c' obrazki/*.gif -geometry 28x28\>+4+4   obrazki/montage.jpg
