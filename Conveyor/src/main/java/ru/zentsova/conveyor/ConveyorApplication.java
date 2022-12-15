package ru.zentsova.conveyor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zalando.logbook.DefaultHttpLogWriter;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.JsonHttpLogFormatter;

@SpringBootApplication
public class ConveyorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConveyorApplication.class, args);
	}

	@Bean
	public Logbook logbook() {
		Logbook logbook = Logbook.builder()
				.sink(new DefaultSink(
						new JsonHttpLogFormatter(),
						new DefaultHttpLogWriter()
				))
				.build();
		return logbook;
	}
}
