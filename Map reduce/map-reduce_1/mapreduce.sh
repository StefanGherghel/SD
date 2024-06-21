rm result.txt
cat ./exemplu_mapreduce/input/4300-0.txt ./exemplu_mapreduce/input/pg20417.txt | python3 ./mapper/mapper.py | python3 ./reducer/reducer.py > result.txt
