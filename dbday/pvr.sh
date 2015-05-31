#!/bin/bash

file=$1
output="${file%.*}.svg"
GOLD="#FADA5E"
GREEN="#0A7951"
WIDTH=$5
HEIGHT=$6
xCol=$2
yCol=$3
xLabel="Recall $( awk -F "\"*,\"*" "{print \$${xCol}; exit}" ${file})"
yLabel="Precision $( awk -F "\"*,\"*" "{print \$${yCol}; exit}" ${file})"
firstLine=$(head -1 "$file")
alpha=$4
description="Precision vs. Recall"
species=$7

#output="${file%.*}"precVSreca"${species}${alpha}.png"
output="${file%.*}"precVSreca"${species}${alpha}.svg"
echo saving $file histogram as \"$output\"
echo $output
echo $xLabel
echo $yLabel
echo $species
echo $alpha



#declare -a array=("Human" "Dog" "White Crowned Sparrow")
plotCommand="plot "
#for s in ${speciesArray[@]}; do
OLD_IFS=$IFS
IFS=,
declare -a speciesArray=(${species})
for s in ${species}; do
#   plotCommand=${plotCommand}'"'$file'" using '${xCol}':($2=='$alpha' && strcol(3) eq "'${s}'" && strcol(4) eq "16-23" && $1 == 1?$'${yCol}':NaN):1 with labels offset 1 notitle "'${s}': 16S-23S",'
#   plotCommand=${plotCommand}'"'$file'" using '${xCol}':($2=='$alpha' && strcol(3) eq "'${s}'" && strcol(4) eq "16-23" ?$'${yCol}':NaN) with linespoints title "'${s}': 16S-23S",'
#
#   plotCommand=${plotCommand}'"'$file'" using '${xCol}':($2=='$alpha' && strcol(3) eq "'${s}'" && strcol(4) eq "23-5" && $1 == 1?$'${yCol}':NaN):1 with labels offset 1 notitle "'${s}': 23S-5S",'
#   plotCommand=${plotCommand}'"'$file'" using '${xCol}':($2=='$alpha' && strcol(3) eq "'${s}'" && strcol(4) eq "23-5" ?$'${yCol}':NaN) with linespoints title "'${s}': 23S-5S",'

   temp16_23=$alpha$s"16_23"$file
   temp23_5=$alpha$s"23_5"$file

   echo "cat $file | grep "$alpha,$s,16-23" > $temp16_23"
   echo "cat $file | grep "$alpha,$s,23-5" > $temp23_5"

   cat $file | grep "$alpha,$s,16-23" > $temp16_23
   cat $file | grep "$alpha,$s,23-5" > $temp23_5

#   cat $temp16_23
#   cat $temp23_5

   plotCommand=${plotCommand}'"'$temp16_23'" using '${xCol}':($1 == 1 || $1 == 11?$'${yCol}':NaN):1 with labels offset 1 notitle "'${s}': 16S-23S",'
   plotCommand=${plotCommand}'"'$temp16_23'" using '${xCol}':($1 <= 11 ? $'${yCol}': NaN) with linespoints title "'${s}': 16S-23S",'

   plotCommand=${plotCommand}'"'$temp23_5'" using '${xCol}':($1 == 1 || $1 == 11?$'${yCol}':NaN):1 with labels offset 1 notitle "'${s}': 23S-5S",'
   plotCommand=${plotCommand}'"'$temp23_5'" using '${xCol}':($1 <= 10 ? $'${yCol}': NaN) with linespoints title "'${s}': 23S-5S",'
done
IFS=$OLD_IFS
echo $plotCommand

gnuplot -persist << EOF
   # OUTPUT
   #set terminal x11
   set terminal svg size $WIDTH,$HEIGHT
   #set terminal png size $WIDTH,$HEIGHT
   set output "$output" 

   # SEPARATOR
   set datafile separator ","

   # TITLES

   set title "${species} ${description} as \$k\$ Increases with \$\\\alpha\$ Threshold ${alpha}"
   set xlabel "$xLabel"
   set ylabel "$yLabel"

   # PLOT TYPE
   #set style histogram rowstacked

   # BORDERS & EXTRAS
   #set key left bottom 
   set key at .87,.87
   set border 3
   set style fill solid 1.0 border -1 
   set format y "%.2f"
   set ytic scale 0 autofreq .10
   set xtic scale 0 autofreq .10
   set autoscale xfixmax
   set xrange [0:1]
   set yrange [0:1]

   # PLOT
   ${plotCommand}
EOF


rm -v $temp16_23
rm -v $temp23_5