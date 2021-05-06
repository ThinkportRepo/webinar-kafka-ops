package digital.thinkport.webinars.aws.streamingkafka.stocks.model;

import java.math.BigDecimal;

public class KafkaStockMessage {
    private BigDecimal price;

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
}
