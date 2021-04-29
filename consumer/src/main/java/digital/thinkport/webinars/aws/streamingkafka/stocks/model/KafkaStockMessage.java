package digital.thinkport.webinars.aws.streamingkafka.stocks.model;

import java.math.BigDecimal;

public class KafkaStockMessage {
    private BigDecimal price;

    private String symbol;

    public KafkaStockMessage(String symbol, BigDecimal price) {
        this.price = price;
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSymbol() {
        return this.symbol;
    }

}
