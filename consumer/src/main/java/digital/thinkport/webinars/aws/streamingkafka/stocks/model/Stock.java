package digital.thinkport.webinars.aws.streamingkafka.stocks.model;

import java.math.BigDecimal;

public class Stock {
    private BigDecimal price;

    private String currency;

    private String designation;

    private String symbol;

    public Stock(String symbol, String designation, BigDecimal price, String currency) {
        this.designation = designation;
        this.price = price;
        this.symbol = symbol;
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDesignation() {
        return designation;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getCurrency() {
        return this.currency;
    }
}
