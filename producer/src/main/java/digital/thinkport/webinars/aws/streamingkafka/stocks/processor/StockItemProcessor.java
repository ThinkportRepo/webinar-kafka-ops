package digital.thinkport.webinars.aws.streamingkafka.stocks.processor;

import digital.thinkport.webinars.aws.streamingkafka.stocks.model.KafkaStockMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import yahoofinance.Stock;

public class StockItemProcessor implements ItemProcessor<Stock, KafkaStockMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockItemProcessor.class);

    @Override
    public KafkaStockMessage process(Stock stock) {
        LOGGER.debug("processing {}", stock.getSymbol());
        return new KafkaStockMessage(stock.getSymbol(), stock.getQuote().getPrice());
    }
}
