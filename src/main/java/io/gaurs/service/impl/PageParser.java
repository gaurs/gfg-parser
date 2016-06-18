package io.gaurs.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.gaurs.service.DocGeneratorService;
import io.gaurs.service.ParsingService;

@Service
public class PageParser implements ParsingService {

	@Autowired
	private DocGeneratorService docGenerator;

	private static final Logger logger = Logger.getLogger(PageParser.class);

	@Override
	public List<File> beginParsing(Document doc) {

		List<File> outputFile = new ArrayList<File>(1);

		// Remove unwanted elements
		doc.select("script").remove();
		doc.select("ins.adsbygoogle").remove();

		// Fetch the page title
		Element pageTitle = doc.select("h1.entry-title").first();
		logger.info("Parsing the page : " + pageTitle.text());

		// Fetch page content
		Element pageContent = doc.select("div.entry-content").first();

		logger.info("Generating the page : " + pageTitle.text().replace(" ", "_") + ".html");
		File output = docGenerator.generateDocument(pageTitle, pageContent);

		outputFile.add(output);

		return outputFile;
	}

}
