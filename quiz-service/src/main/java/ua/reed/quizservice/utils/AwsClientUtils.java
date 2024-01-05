package ua.reed.quizservice.utils;

import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;

@UtilityClass
public class AwsClientUtils {

    public GetRecordsRequest createGetRecordsRequest(final String shardIterator) {
        return GetRecordsRequest.builder()
                .shardIterator(shardIterator)
                .limit(4)
                .build();
    }

    public GetShardIteratorRequest createShardIteratorRequest(final String stream, final Shard shard) {
        return GetShardIteratorRequest.builder()
                .streamName(stream)
                .shardId(shard.shardId())
                .shardIteratorType(ShardIteratorType.TRIM_HORIZON)
                .build();
    }
}
