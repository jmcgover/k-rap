#!/bin/bash
data=$1
w=$2
h=$3
species=$4

for a in 0.000 0.500 0.900 0.950 0.960 0.970 0.980 0.990 1.000; do 
   ./kgraph.sh $data 1 10 $a $w $h Precision $species
done

for a in 0.000 0.500 0.900 0.950 0.960 0.970 0.980 0.990 1.000; do 
   ./kgraph.sh $data 1 20 $a $w $h Recall $species; 
done

for a in 0.000 0.500 0.900 0.950 0.960 0.970 0.980 0.990 1.000; do 
   ./pvr.sh $data 20 10 $a $w $h  $species
done

for a in 0.000 0.500 0.900 0.950 0.960 0.970 0.980 0.990 1.000; do 
   ./fmeasure.sh $data $a $w $h  $species
done

#mkdir -pv images
#mv -v *.png images

mkdir -pv vectors
mv -v *.svg vectors