package digital.thinkport.webinars.aws.streamingkafka.stocks.kafkawriter;

import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.Assert;

public class StockKafkaItemWriterBuilder<K, V extends IKafkaMessageHasTopicEmbedded> {

    protected KafkaTemplate<K, V> kafkaTemplate;

    protected Converter<V, K> itemKeyMapper;

    protected boolean delete;

    protected long timeout = -1;


    /**
     * Establish the KafkaTemplate to be used by the KafkaItemWriter.
     * @param kafkaTemplate the template to be used
     * @return this instance for method chaining
     * @see KafkaItemWriter#setKafkaTemplate(KafkaTemplate)
     */
    public StockKafkaItemWriterBuilder<K, V> kafkaTemplate(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        return this;
    }

    /**
     * Set the {@link Converter} to use to derive the key from the item.
     * @param itemKeyMapper the Converter to use.
     * @return The current instance of the builder.
     * @see KafkaItemWriter#setItemKeyMapper(Converter)
     */
    public StockKafkaItemWriterBuilder<K, V> itemKeyMapper(Converter<V, K> itemKeyMapper) {
        this.itemKeyMapper = itemKeyMapper;
        return this;
    }

    /**
     * Indicate if the items being passed to the writer are all to be sent as delete events to the topic. A delete
     * event is made of a key with a null value. If set to false (default), the items will be sent with provided value
     * and key converter by the itemKeyMapper. If set to true, the items will be sent with the key converter from the
     * value by the itemKeyMapper and a null value.
     * @param delete removal indicator.
     * @return The current instance of the builder.
     * @see KafkaItemWriter#setDelete(boolean)
     */
    public StockKafkaItemWriterBuilder<K, V> delete(boolean delete) {
        this.delete = delete;
        return this;
    }

    /**
     * The time limit to wait when flushing items to Kafka.
     *
     * @param timeout milliseconds to wait, defaults to -1 (no timeout).
     * @return The current instance of the builder.
     * @see KafkaItemWriter#setTimeout(long)
     * @since 4.3.2
     */
    public StockKafkaItemWriterBuilder<K, V> timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public KafkaItemWriter<K, V> build() {
        Assert.notNull(this.kafkaTemplate, "kafkaTemplate is required.");
        Assert.notNull(this.itemKeyMapper, "itemKeyMapper is required.");

        KafkaItemWriter<K, V> writer = new StockKafkaItemWriter<>();
        writer.setKafkaTemplate(this.kafkaTemplate);
        writer.setItemKeyMapper(this.itemKeyMapper);
        writer.setDelete(this.delete);
        writer.setTimeout(this.timeout);
        return writer;
    }
}
