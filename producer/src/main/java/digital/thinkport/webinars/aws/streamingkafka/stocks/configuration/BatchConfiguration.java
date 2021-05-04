package digital.thinkport.webinars.aws.streamingkafka.stocks.configuration;

import digital.thinkport.webinars.aws.streamingkafka.stocks.controller.StockPriceProducerController;
import digital.thinkport.webinars.aws.streamingkafka.stocks.kafkawriter.StockKafkaItemWriterBuilder;
import digital.thinkport.webinars.aws.streamingkafka.stocks.listener.JobCompletionNotificationListener;
import digital.thinkport.webinars.aws.streamingkafka.stocks.model.KafkaStockMessage;
import digital.thinkport.webinars.aws.streamingkafka.stocks.processor.StockItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import yahoofinance.Stock;

import java.util.ArrayList;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private static KafkaMSKConfiguration kafkaMSKConfiguration;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private StockPriceProducerController controller;

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfiguration.class);

    @Bean
    public Job sendPricesToKafkaJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("sendPricesToKafkaJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Stock, KafkaStockMessage> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public ItemReader<Stock> reader() {
        return new ListItemReader<>(new ArrayList<>(controller.getCurrentStockList()));
    }

    @Bean
    public StockItemProcessor processor() {
        return new StockItemProcessor();
    }

    @Bean
    public ItemWriter<KafkaStockMessage> writer() {
        return new StockKafkaItemWriterBuilder<String, KafkaStockMessage>()
                .kafkaTemplate(kafkaMSKConfiguration.kafkaTemplate())
                .build();
    }

    @Scheduled(fixedDelay=10000)
    public void runJob() {
        var listener = new JobCompletionNotificationListener();
        try {
            createJobLauncher().run(
              sendPricesToKafkaJob(listener, step1()),
              new JobParametersBuilder().toJobParameters()
            );
        } catch (JobExecutionAlreadyRunningException e) {
            LOGGER.error("Job already running {}", e.getMessage());
        } catch (JobInstanceAlreadyCompleteException e) {
            LOGGER.error("Job already completed {}", e.getMessage());
        } catch (JobRestartException e) {
            LOGGER.error("Job restart exception {}", e.getMessage());
        } catch (JobParametersInvalidException e) {
            LOGGER.error("Job invalid parameters {}", e.getMessage());
        }
    }

    protected JobLauncher createJobLauncher() {
        return new SimpleJobLauncher();
    }
}
