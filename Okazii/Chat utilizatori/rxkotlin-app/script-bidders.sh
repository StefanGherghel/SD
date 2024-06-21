for i in {1..10}
do
    echo "Starting $i bid"
	java -jar ./out/artifacts/BidderMicroservice_jar/BidderMicroservice.jar &
	pids[${i}]=$!
done

echo "Now we wait"
for pid in ${pids[*]}; do
    wait $pid
done
