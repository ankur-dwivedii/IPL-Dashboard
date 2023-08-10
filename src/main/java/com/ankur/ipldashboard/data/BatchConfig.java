package com.ankur.ipldashboard.data;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.ankur.ipldashboard.model.Match;

// This is Batch job file where we tell spring to run MatchDataProcessor while starting the app
// Matchinput is reader file, MatchDataProcessor is processor implementation and Match is writer entity. 
// while in BatchConfig we define the 3 type beans and then define a job which excutes these 3 steps/beans

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final String[] FIELD_NAMES = new String[] { "id", "city", "date", "player_of_match", "venue",
            "neutral_venue", "team1", "team2", "toss_winner", "toss_decision", "winner", "result", "result_margin", "eliminator",
            "method", "umpire1", "umpire2" };

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    // This will read the data from csv file and load in into the MatchInput Entity
    @Bean
    public FlatFileItemReader<MatchInput> reader() {
        return new FlatFileItemReaderBuilder<MatchInput>()
                .name("MatchItemReader")
                .resource(new ClassPathResource("match-data.csv"))
                .linesToSkip(1)//So we dont read the 1st row which is column names
                .delimited()
                .names(FIELD_NAMES)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<MatchInput>() {
                    {
                        setTargetType(MatchInput.class);
                    }
                })
                .build();
    }

    // Creates a bean of Processor which will convert MatchInput into Match
    @Bean
    public MatchDataProcessor processor() {
        return new MatchDataProcessor();
    }

    // Here we are writing the data into the in memory database HSQLDB
    @Bean
    public JdbcBatchItemWriter<Match> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Match>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO match (id, city, date, player_of_match, venue, team1, team2, toss_winner, toss_decision, match_winner, result, result_margin, umpire1, umpire2) "
                        + " VALUES (:id, :city, :date, :playerOfMatch, :venue, :team1, :team2, :tossWinner, :tossDecision, :matchWinner, :result, :resultMargin, :umpire1, :umpire2)")
                .dataSource(dataSource)
                .build();
    }

    // This is the actual spring job, here we define it 
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {//Listener to tell us when the job is finished
        return jobBuilderFactory
                .get("importUserJob")
                .incrementer(new RunIdIncrementer())//This is to increment through each id/record
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    // This is the steps that above spring job needs to follow
    @Bean
    public Step step1(JdbcBatchItemWriter<Match> writer) {
        return stepBuilderFactory
                .get("step1")
                .<MatchInput, Match>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }
}
