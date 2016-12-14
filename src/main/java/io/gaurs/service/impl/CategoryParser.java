package io.gaurs.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

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
import io.gaurs.service.VisitedUrlService;

@Service
public class CategoryParser implements ParsingService {

	@Value("${start.path}")
	private String repositoryPath;

	@Autowired
	private PageParser pageParser;

	@Autowired
	private VisitedUrlService visitedUrlService;
	
	@Autowired
	private IndexPageGeneratorServiceImpl indexPageGenerator;

	private static final Logger logger = Logger.getLogger(CategoryParser.class);

	@Override
	public void beginParsing() {

		// Connect to the repository
		try {
			Document category = Jsoup.connect(repositoryPath).get();
			beginParsing(category);
		} catch (IOException exception) {
			logger.error("Exception occurred while connecting to " + repositoryPath, exception);
		}

	}

	@Override
	public List<File> beginParsing(Document category) {

		logger.info("Begin Parsing : " + repositoryPath);
		boolean isPathReset = false;

		List<File> allPagesInThisCategory = new ArrayList<File>();

		// links visited last time; won't be adding these this time to file/db
		Set<String> existingUrlCache = visitedUrlService.getUrlList();
		Set<String> newUrlCache = new HashSet<>();

		try {

			do {

				// if the variable isPathReset is set; we need to again connect
				// to the page to fetch all the urls on it.
				// The same is applicable when all the links on current page are
				// processed and we are connecting to new page
				// In such cases, repositoryPath is updated to next page url as
				// set in the code below
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
					if (!existingUrlCache.contains(url) && !newUrlCache.contains(url)) {
						newUrlCache.add(url);
						Document page = Jsoup.connect(url).get();

						// Parse the page
						List<File> output = pageParser.beginParsing(page);
						allPagesInThisCategory.addAll(output);

						// Wait sometime before fetching next page to avoid
						// network traffic
						Thread.sleep((long) (Math.random() * 1000));
					}
				}

				// Fetch the next page
				Element nextPage = fetchNextPage(category);

				// If null != next page ; fetch the next page link and assign it
				// to the repository path
				if (null != nextPage) {
					repositoryPath = nextPage.attr("href");
					isPathReset = true;
				} else {
					// Completed the cycle
					break;
				}

			} while (!StringUtils.isEmpty(repositoryPath));

			// write the url cache to file/db; if existing urlcache is empty so
			// we need not to PRE - append the urls data with newline character
			// (1st line) else we do
			visitedUrlService.saveUrlList(newUrlCache, !existingUrlCache.isEmpty());
			
			indexPageGenerator.generateDocument();

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
