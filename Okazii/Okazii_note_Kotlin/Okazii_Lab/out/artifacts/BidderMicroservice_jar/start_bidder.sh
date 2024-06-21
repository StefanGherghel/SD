#!/bin/bash

arg=$1

for i in $(seq 0 $(expr $arg - 1))
do
	( java -jar BidderMicroservice.jar & )
	sleep 0.001
done
