package io.gaurs.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.gaurs.service.ParsingService;

@Service
public class CategoryParser implements ParsingService {

	@Value("${start.path}")
	private String repositoryPath;

	@Autowired
	private PageParser pageParser;

	private static final Logger logger = Logger.getLogger(CategoryParser.class);

	@Override
	public void beginParsing() {

		// Connect to the repository
		try {
			Document category = Jsoup.connect(repositoryPath).get();
			beginParsing(category);
		} catch (IOException e) {
			logger.error("Exception occurred while connecting to " + repositoryPath);
		}

	}

	@Override
	public List<File> beginParsing(Document category) {

		logger.info("Begin Parsing : " + repositoryPath);
		boolean isPathReset = false;

		List<File> allPagesInThisCategory = new ArrayList<File>();

		try {

			do {

				// Checking the first page
				if (isPathReset) {
					category = Jsoup.connect(repositoryPath).get();
				}

				logger.info(repositoryPath);

				// Find link of all the titles (all a tags after h2 of class
				// entry-title)
				Elements links = category.select("h2.entry-title > a");

				// For every link
				for (Element link : links) {
					String url = link.attr("href");
					logger.info("Parsing : " + url);

					// Connect to page
					Document page = Jsoup.connect(url).get();

					// Parse the page
					List<File> output = pageParser.beginParsing(page);
					allPagesInThisCategory.addAll(output);

					Thread.sleep(2000);
				}

				// Fetch the next page
				Element nextPage = fetchNextPage(category);

				// If null != next page ; fetch the next page link and assign it
				// to the repository path
				if (null != nextPage) {
					repositoryPath = nextPage.attr("href");
					isPathReset = true;
					Thread.sleep(2000);
				}else{
					//Completed the cycle
					break;
				}


			} while (!StringUtils.isEmpty(repositoryPath));

		} catch (IOException exception) {
			logger.error("Exception occurred while parsing page " + repositoryPath, exception);
		} catch (InterruptedException exception) {
			logger.error("Exception occurred while parsing page " + repositoryPath, exception);
		}

		return allPagesInThisCategory;

	}

	/**
	 * Fetch the next page from the current page
	 * 
	 * @param category
	 * @return
	 */
	public Element fetchNextPage(Document category) {
		return category.select("link[rel='next']").first();
	}

}
