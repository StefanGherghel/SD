#!/bin/bash
java -jar ./out/artifacts/AuctioneerMicroservice_jar/AuctioneerMicroservice.jar &
java -jar ./out/artifacts/MessageProcessorMicroservice_jar/MessageProcessorMicroservice.jar &
java -jar ./out/artifacts/BiddingProcessorMicroservice_jar/BiddingProcessorMicroservice.jar &
java -jar ./out/artifacts/ChatMicroservice_jar/ChatMicroservice.jar &
