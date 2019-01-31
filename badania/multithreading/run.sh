#!/bin/bash
for config in configs/*
do
	filename=${config##*/}
	fall=${filename%.*}
	#echo $fall
	for dataset in datasets/*
	do
		d_filename=${dataset##*/}
		d_fall=${d_filename%.*}
		java -jar JGCS.jar -d datasets/$d_filename -c $config -e evaluation.csv -t times/th_${fall}__len_${d_fall}.csv -r 100
	done
done
