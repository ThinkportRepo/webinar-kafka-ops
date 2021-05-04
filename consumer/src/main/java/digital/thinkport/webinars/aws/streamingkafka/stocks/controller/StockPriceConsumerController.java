package digital.thinkport.webinars.aws.streamingkafka.stocks.controller;

import digital.thinkport.webinars.aws.streamingkafka.stocks.model.Stock;
import digital.thinkport.webinars.aws.streamingkafka.stocks.service.StockPriceConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/stocks")
public class StockPriceConsumerController {

    @Autowired
    private static StockPriceConsumerService spcs;

    @RequestMapping("/get/{symbol}")
    public Stock getShare(@PathVariable("symbol") String symbol) {
        return spcs.getStock(symbol);
    }

    @RequestMapping("/list")
    public List<String> getAllRegisteredShares() {
        ArrayList<String> stocksList = new ArrayList<>();
        spcs.getAll().forEach(stock -> stocksList.add(stock.getDesignation()));
        return stocksList;
    }
}
