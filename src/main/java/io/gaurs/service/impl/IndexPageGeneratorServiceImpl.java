package io.gaurs.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.gaurs.service.DocGeneratorService;

@Service("indexPageGenerator")
public class IndexPageGeneratorServiceImpl implements DocGeneratorService {

	@Value("${start.path}")
	private String repositoryPath;

	@Value("${output.dir}")
	private String outputDir;

	private Logger logger = Logger.getLogger(IndexPageGeneratorServiceImpl.class);

	@Override
	public File generateDocument(Element... elements) {
		try {
			Document document = generatePage();
			File indexPage = new File(outputDir + "index.html");
			FileUtils.writeStringToFile(indexPage, document.outerHtml(), "utf-8");
			logger.info("Generated new document with title : index.html");
		} catch (IOException exception) {
			logger.error("Exception occurred while writing index page", exception);
		}

		return null;
	}

	private Document generatePage() {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("index.html");

		Document page = null;

		try {
			page = Jsoup.parse(stream, "utf-8", "");

			// Set the question statement
			Element heading = page.getElementById("category");
			heading.text(repositoryPath.substring(repositoryPath.lastIndexOf("/") + 1));

			// Set the content

			// 1. Get the content div
			Element tableRows = page.getElementById("rows");

			Collection<File> htmlFiles = FileUtils.listFiles(new File(outputDir), new String[] { "html" }, true);

			int count = 1;

			for (File file : htmlFiles) {
				Element row = tableRows.appendElement("tr");
				row.appendElement("th").attr("scope", "row").text(String.valueOf(count++));
				Element data = row.appendElement("td");
				data.appendElement("a").attr("href", file.getPath()).text(file.getName().replaceAll("_", " ").replaceAll(".html", ""));
			}

		} catch (IOException exception) {
			logger.error("Exception occurred while generating .html page", exception);
		}

		return page;
	}

}
