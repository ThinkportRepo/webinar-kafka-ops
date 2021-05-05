package digital.thinkport.webinars.aws.streamingkafka.stocks.configuration;

import digital.thinkport.webinars.aws.streamingkafka.stocks.model.KafkaStockMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaMSKConfiguration {

    @Value("${stocks.kafka.topic}")
    private String topic;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMSKConfiguration.class);

    @Value("${kafka_bootstrap_servers}")
    private String bootstrapServers;


    @Bean
    public ProducerFactory<String, KafkaStockMessage> producerFactoryString() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, KafkaStockMessage> kafkaTemplate() {
        KafkaTemplate<String, KafkaStockMessage> template = new KafkaTemplate<>(producerFactoryString());
        template.setDefaultTopic(topic);
        return template;
    }

    public void sendMessage(KafkaStockMessage message) {

        ListenableFuture<SendResult<String, KafkaStockMessage>> future =
                kafkaTemplate().send(topic, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, KafkaStockMessage>>() {

            @Override
            public void onSuccess(SendResult<String, KafkaStockMessage> result) {
                LOGGER.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
            }
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.info("Unable to send message=[{}] due to : {}", message, ex.getMessage());
            }
        });
    }
}
