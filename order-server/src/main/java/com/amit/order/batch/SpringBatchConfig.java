package com.amit.order.batch;

import com.amit.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.AbstractFileItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SpringBatchConfig {

    private final JobRepository jobRepository;

    private final MongoTemplate mongoTemplate;

    private final JobBuilderFactory jobBuilderFactory;

    private final JobCompletionListener completionListener;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean(name = "myJobLauncher")
    public JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean("dataReader")
    @StepScope
    public MongoItemReader<Order> reader(@Value("#{jobParameters[product]}") String product,
            @Value("#{jobParameters[to]}") long to, @Value("#{jobParameters[from]}") long from) {

        MongoItemReader<Order> reader = new MongoItemReader<>();

        reader.setTemplate(mongoTemplate);
        reader.setTargetType(Order.class);

        reader.setQuery(format("{productName : %s, $and : [{ orderDate : { $gte : %d%n, $lt : %d%n}}]}", product, from,
                to));

        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("_id", Sort.Direction.ASC);
        reader.setSort(sorts);

        return reader;
    }

    @Bean
    @StepScope
    public AbstractFileItemWriter<Order> writer(@Value("#{jobParameters[type]}") String type,
            @Value("#{jobParameters[path]}") String path) {

        if (Objects.equals(type, "json")) {
            JsonFileItemWriterBuilder<Order> builder = new JsonFileItemWriterBuilder<>();
            JacksonJsonObjectMarshaller<Order> marshaller = new JacksonJsonObjectMarshaller<>();
            return builder
                    .name("orderItemWriter")
                    .jsonObjectMarshaller(marshaller)
                    .resource(new FileSystemResource(path))
                    .build();
        }

        Resource outputResource = new FileSystemResource(path);
        FlatFileItemWriter<Order> writer = new FlatFileItemWriter<>();

        writer.setResource(outputResource);
        writer.setAppendAllowed(false);
        writer.setHeaderCallback(writer1 -> writer1.write("id,productName,customerName,orderDate"));

        writer.setLineAggregator(new DelimitedLineAggregator<>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<>() {
                    {
                        setNames(new String[]{"id", "productName", "customerName", "orderDate"});
                    }
                });
            }
        });
        return writer;
    }

    @Bean
    public Step fetchDatabaseStep(ItemReader<Order> dataReader, ItemWriter<Order> dataWriter) {

        return stepBuilderFactory.get("export-step").<Order, Order>chunk(100)
                .reader(dataReader)
                .writer(dataWriter)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob(ItemReader<Order> dataReader, ItemWriter<Order> dataWriter) {
        return jobBuilderFactory.get("export")
                .listener(completionListener)
                .flow(fetchDatabaseStep(dataReader, dataWriter)).end().build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
}