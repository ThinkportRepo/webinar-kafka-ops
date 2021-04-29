package digital.thinkport.webinars.aws.streamingkafka.stocks.processor;

import digital.thinkport.webinars.aws.streamingkafka.stocks.model.KafkaStockMessage;
import org.springframework.batch.item.ItemProcessor;
import yahoofinance.Stock;

public class StockItemProcessor implements ItemProcessor<Stock, KafkaStockMessage> {
    @Override
    public KafkaStockMessage process(Stock stock) {
        return new KafkaStockMessage(stock.getSymbol(), stock.getQuote().getPrice());
    }
}
