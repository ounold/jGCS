#!/bin/bash
for config in configs/*
do
	filename=${config##*/}
	fall=${filename%.*}
	java -jar JGCS.jar -s -d train -v test -g grammars \
       	-c $config -e evals/${fall}.csv -r 10
done
