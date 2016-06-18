package io.gaurs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import io.gaurs.service.ParsingService;
import io.gaurs.service.impl.CategoryParser;

@SpringBootApplication
@ComponentScan(basePackages = { "io.gaurs" })
public class SiteParserApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SiteParserApplication.class);
		application.setWebEnvironment(false);

		ConfigurableApplicationContext context = application.run(args);
		ParsingService service = context.getBean(CategoryParser.class);
		service.beginParsing();

	}

}
