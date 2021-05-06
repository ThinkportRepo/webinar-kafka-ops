package digital.thinkport.webinars.aws.streamingkafka.stocks.configuration;

import digital.thinkport.webinars.aws.streamingkafka.stocks.controller.StockPriceProducerController;
import digital.thinkport.webinars.aws.streamingkafka.stocks.model.KafkaStockMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import yahoofinance.Stock;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class KafkaTimerTask {

    @Autowired
    private StockPriceProducerController controller;

    @Autowired
    private KafkaMSKConfiguration kafkaMSKConfiguration;


    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTimerTask.class);

    @Scheduled(fixedDelay=10000)
    public void run() {
        List<KafkaStockMessage> messages = new ArrayList<>();
        for (Stock st : controller.getCurrentStockList()) {
            messages.add(new KafkaStockMessage(st.getSymbol(), st.getQuote().getPrice()));
        }
        try {
            LOGGER.info("writing messages");
            kafkaMSKConfiguration.sendMessages(messages);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
