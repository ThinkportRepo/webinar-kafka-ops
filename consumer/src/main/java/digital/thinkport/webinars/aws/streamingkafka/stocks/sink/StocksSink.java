package digital.thinkport.webinars.aws.streamingkafka.stocks.sink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import digital.thinkport.webinars.aws.streamingkafka.stocks.model.KafkaStockMessage;
import digital.thinkport.webinars.aws.streamingkafka.stocks.service.StockPriceConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

public class StocksSink {

    @Autowired
    private static StockPriceConsumerService spcs;

    private static final ObjectMapper oMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(StocksSink.class);

    @KafkaListener(topics = "${stocks.kafka.topic}")
    public void handleIncomingMessage(String message, @Header(KafkaHeaders.MESSAGE_KEY) String symbol) {

        KafkaStockMessage ksm;
        try {
            ksm = oMapper.readValue(message, KafkaStockMessage.class);
            spcs.setNewPrice(ksm);
            LOGGER.info("new Kafka price for {}", symbol);
        } catch (JsonProcessingException e) {
            LOGGER.error("Kafka conversion error");
        }
    }
}
