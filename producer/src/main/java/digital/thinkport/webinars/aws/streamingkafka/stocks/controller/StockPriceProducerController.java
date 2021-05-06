package digital.thinkport.webinars.aws.streamingkafka.stocks.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/stocks")
public class StockPriceProducerController {

    private static Map<String, Stock> stocks = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(StockPriceProducerController.class);

    public Collection<Stock> getCurrentStockList() {
        return stocks.values();
    }

    public StockPriceProducerController() {
        addShare("INTC");
        addShare("TSLA");
        addShare("IBM");
        addShare("SGO.PA");
        addShare("DTE.DE");
    }


    @RequestMapping("/add/{symbol}")
    public String addShare(@PathVariable("symbol") String symbol) {
        try {
            Stock st = YahooFinance.get(symbol);
            stocks.put(st.getSymbol(), st);
            LOGGER.info("{} added to the list", symbol);
            return st.getName().concat(" added to the list");
        } catch (IOException e) {
            LOGGER.error("Error adding {}", symbol);
            return "an error occurred";
        }
    }

    @RequestMapping("/list")
    public List<String> getAllRegisteredShares() {
        ArrayList<String> stocksList = new ArrayList<>();
        for (Stock st : stocks.values()) {
            stocksList.add(st.getName());
        }
        return stocksList;
    }

    @RequestMapping("/price/{symbol}")
    public BigDecimal getCurrentPrice(@PathVariable("symbol") String symbol) {
        return stocks.get(symbol).getQuote().getPrice();
    }
}
