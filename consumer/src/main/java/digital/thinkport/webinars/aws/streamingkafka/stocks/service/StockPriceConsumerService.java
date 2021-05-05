package digital.thinkport.webinars.aws.streamingkafka.stocks.service;

import digital.thinkport.webinars.aws.streamingkafka.stocks.model.KafkaStockMessage;
import digital.thinkport.webinars.aws.streamingkafka.stocks.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class StockPriceConsumerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockPriceConsumerService.class);

    @Value("${stocks.kafka.topic}")
    private String topic;

    private static Map<String, Stock> stocks = new HashMap<>();

    public void addNewStock(Stock s) {
        if (stocks.containsKey(s.getSymbol())) {
            stocks.get(s.getSymbol()).setPrice(s.getPrice());
        } else {
            stocks.put(s.getSymbol(), s);
        }
    }

    public void setNewPrice(KafkaStockMessage msg) {
        String symbol = msg.getSymbol();
        if (stocks.containsKey(symbol)) {
            stocks.get(symbol).setPrice(msg.getPrice());
        } else {
            Stock s = new Stock(symbol, "FIXME", msg.getPrice(), "FIXME"); //FIXME
            LOGGER.info("Consumed a new price for {}", symbol);
            addNewStock(s);
        }
    }

    public Stock getStock(String symbol) {
        if (stocks.containsKey(symbol)) {
            return stocks.get(symbol);
        }
        return null;
    }

    public Collection<Stock> getAll() {
        return stocks.values();
    }
}
