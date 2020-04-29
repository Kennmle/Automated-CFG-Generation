#!/bin/bash
java_files=( Cal CountPositive FindLast FindVal LastZero OddorPos Power Sum TriangleType TwoPred )
coverage_types=( ep )
for i in "${java_files[@]}"
do
    python3 parse.py write mujava/src/$i.java mujava/json/$i.json
done
for i in "${java_files[@]}"
do
    python3 parse.py read mujava/src/$i.java > mujava/graphs-auto/$i-graph.txt
done
for j in "${coverage_types[@]}"
do
    for i in "${java_files[@]}"
    do
        make run coverage=$j json_file=mujava/json/$i.json > mujava/test-requirements/$i-$j.txt
    done
done
exit 0