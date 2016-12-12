package io.gaurs.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.gaurs.service.DocGeneratorService;
import io.gaurs.service.ParsingService;

@Service
public class PageParser implements ParsingService {

	@Autowired
	private DocGeneratorService docGenerator;

	private static final Logger logger = Logger.getLogger(PageParser.class);

	private final String contributedBy = "This article is contributed by";
	private final String comments = "Please write comments if you find anything incorrect";
	private final String gate = "GATE CS Corner";
	private final String cp = "Company Wise Coding Practice";

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

		// Remove writer information
		Elements allParaGraphs = pageContent.select("p");
		Iterator<Element> paragraphIterator = allParaGraphs.iterator();

		while (paragraphIterator.hasNext()) {
			Element para = paragraphIterator.next(); 
			if (para.text().startsWith(contributedBy)
					|| para.text().startsWith(comments)) {
				paragraphIterator.remove();
				para.remove();
			}
		}

		// Some additional adds
		Elements links = pageContent.select("a");
		Iterator<Element> linksIterator = links.iterator();
		
		while (linksIterator.hasNext()) {
			Element link = linksIterator.next(); 
			if (link.text().startsWith(gate)
					|| link.text().startsWith(cp)) {
				linksIterator.remove();
				link.remove();
			}
		}
		
		logger.info("Generating the page : " + pageTitle.text().replace(" ", "_") + ".html");
		File output = docGenerator.generateDocument(pageTitle, pageContent);

		outputFile.add(output);

		return outputFile;
	}

}
