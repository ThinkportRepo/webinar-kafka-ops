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
    private static final Logger LOGGER = LoggerFactory.getLogger(StockPriceProducerController.class);

    private static Map<String, Stock> stocks = new HashMap<>();


    public StockPriceProducerController() {
    }

    public Collection<Stock> getCurrentStockList() {
        return stocks.values();
    }


    @RequestMapping("/add/{symbol}")
    public String addShare(@PathVariable("symbol") String symbol) {
        try {
            Stock st = YahooFinance.get(symbol);
            stocks.put(st.getSymbol(), st);
            return st.getName().concat(" added to the list");
        } catch (IOException e) {
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
