#!/bin/bash
for config in configs/*
do
	filename=${config##*/}
	fall=${filename%.*}
	#echo $fall
	java -jar JGCS.jar -s -g grammars -d datasets -c $config -e evaluation.csv -t times/$fall.csv -r 100
done
