package digital.thinkport.webinars.aws.streamingkafka.stocks.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaMSKConfiguration {

    @Value("${stocks.kafka.topic}")
    private String topic;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMSKConfiguration.class);
    private static final ObjectMapper oMapper = new ObjectMapper();

    @Value("${kafka_bootstrap_servers}")
    private String bootstrapServers;


    @Bean
    public ProducerFactory<String, String> producerFactoryString() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> template = new KafkaTemplate<>(producerFactoryString());
        template.setDefaultTopic(topic);
        return template;
    }

    public void sendMessages(List<KafkaStockMessage> messages) {
        for (KafkaStockMessage message : messages) {
            sendMessage(message);
        }
    }

    public void sendMessage(KafkaStockMessage message) {

        try {
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate().send(topic, message.getSymbol(), oMapper.writeValueAsString(message));
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

                @Override
                public void onSuccess(SendResult<String, String> result) {
                    LOGGER.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
                }
                @Override
                public void onFailure(Throwable ex) {
                    LOGGER.info("Unable to send message=[{}] due to : {}", message, ex.getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            LOGGER.error("JSon processing exception {}", e.getMessage());
        }
    }
}
