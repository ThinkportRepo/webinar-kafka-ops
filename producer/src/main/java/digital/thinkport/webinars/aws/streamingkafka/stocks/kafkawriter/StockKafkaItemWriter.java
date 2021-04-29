package digital.thinkport.webinars.aws.streamingkafka.stocks.kafkawriter;

import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StockKafkaItemWriter<K, T extends IKafkaMessageHasTopicEmbedded> extends KafkaItemWriter<K, T> {

    private final List<ListenableFuture<SendResult<K, T>>> listenableFutures = new ArrayList<>();

    private long timeout = -1;

    @Override
    protected void writeKeyValue(K key, T value) {
        if (this.delete) {
            this.listenableFutures.add(this.kafkaTemplate.send(value.getTopic(), key, null));
        }
        else {
            this.listenableFutures.add(this.kafkaTemplate.send(value.getTopic(), key, value));
        }
    }

    @Override
    protected void flush() throws Exception{
        this.kafkaTemplate.flush();
        for(ListenableFuture<SendResult<K,T>> future: this.listenableFutures){
            if (this.timeout >= 0) {
                future.get(this.timeout, TimeUnit.MILLISECONDS);
            }
            else {
                future.get();
            }
        }
        this.listenableFutures.clear();
    }
}
