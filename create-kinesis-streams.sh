#!/bin/bash
aws kinesis create-stream --stream-name questions-stream --shard-count 1 --profile dummy  --endpoint-url http://localhost:4566
aws kinesis create-stream --stream-name answers-stream --shard-count 1 --profile dummy  --endpoint-url http://localhost:4566
aws kinesis create-stream --stream-name stats-stream --shard-count 1 --profile dummy  --endpoint-url http://localhost:4566
