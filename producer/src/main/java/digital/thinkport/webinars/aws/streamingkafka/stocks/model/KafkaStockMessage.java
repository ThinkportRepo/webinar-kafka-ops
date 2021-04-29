package digital.thinkport.webinars.aws.streamingkafka.stocks.model;

import digital.thinkport.webinars.aws.streamingkafka.stocks.kafkawriter.IKafkaMessageHasTopicEmbedded;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public class KafkaStockMessage implements IKafkaMessageHasTopicEmbedded {
    private BigDecimal price;

    @Value("${stocks.kafka.topic}")
    private String topic;

    private final String symbol;

    public KafkaStockMessage(String symbol, BigDecimal price) {
        this.price = price;
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getSymbol() {
        return this.symbol;
    }

    @Override
    public String toString() {
        return this.getSymbol().concat(" : ").concat(this.getPrice().toString());
    }

    @Override
    public String getTopic() {
        return topic;
    }
}
