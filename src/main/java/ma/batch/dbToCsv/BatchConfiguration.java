package ma.batch.dbToCsv;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import ma.batch.dbToCsv.model.User;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/springBatch");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}

	@Bean
	public JdbcCursorItemReader<User> reader() {
		JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql("SELECT * FROM user");
		reader.setRowMapper(new UserRowMapper());
		return reader;
	}

	public UserIemProcessor process() {
		return new UserIemProcessor();
	}

	public class UserRowMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet res, int rowNum) throws SQLException {
			User user = new User(res.getInt("id"), res.getString("name"));
			System.out.println("################## user ################### :" + user.getName());
			return user;
		}

	}

	@Bean
	public FlatFileItemWriter<User> writer() {

		FlatFileItemWriter<User> writer = new FlatFileItemWriter<>();
		writer.setResource(new ClassPathResource("user.csv"));

		writer.setLineAggregator(new DelimitedLineAggregator<User>() {
			{
				setDelimiter(",");
				setFieldExtractor(new BeanWrapperFieldExtractor<User>() {
					{
						setNames(new String[] { "id", "name" });
					}
				});

			}
		});

		return writer;
	}

	@Bean
	public Step step() {
		return stepBuilderFactory.get("step1").<User, User>chunk(10).reader(reader()).processor(process())
				.writer(writer()).build();

	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job1").incrementer(new RunIdIncrementer()).flow(step()).end().build();
	}

}
