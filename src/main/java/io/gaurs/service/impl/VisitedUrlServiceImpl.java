package io.gaurs.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.gaurs.service.VisitedUrlService;

@Service
public class VisitedUrlServiceImpl implements VisitedUrlService {

	@Value("${output.dir}")
	private String path;

	@Value("${file.name}")
	private String fileName;

	private static final Logger logger = Logger.getLogger(VisitedUrlServiceImpl.class);

	private final String newLine = System.getProperty("line.separator");

	@Override
	public void saveUrlList(Set<String> urls, boolean append) {
		StringBuilder data = new StringBuilder();
		try {
			logger.info("Saving " + urls.size() + " urls to the cache");

			Path outputFile = Paths.get(path + fileName);

			if (!outputFile.toFile().exists()) {
				Files.createFile(outputFile);
			}
			
			
			//only for 1st line of this session (data available both previous time and this time)
			if(append && !urls.isEmpty()){
				data.append(newLine);
			}

			Iterator<String> urlIterator = urls.iterator();

			while (urlIterator.hasNext()) {
				String url = urlIterator.next();
				data.append(url);

				// If one more url available; add a new line character before
				// appending it
				if (urlIterator.hasNext()) {
					data.append(newLine);
				}
			}

			Files.write(outputFile, data.toString().getBytes(), StandardOpenOption.APPEND);
		} catch (IOException exception) {
			logger.error("Exception occurred while storing parsed urls ", exception);
		}

	}

	@Override
	public Set<String> getUrlList() {

		Set<String> urlCache = new HashSet<>();

		try {
			// Get the file
			Path filePath = Paths.get(path + fileName);
			File urls = filePath.toFile();

			logger.info("Checking if the file containing visited urls exist ?");

			// check if it exists ?
			if (urls.exists()) {
				logger.info("Yup !! it does. Now reading the same and populating the visited url cache");
				urlCache.addAll(Files.readAllLines(filePath));

			} else {

				// Create a new cache and return
				urlCache = new HashSet<>();
			}
		} catch (IOException exception) {
			logger.error("Exception occurred while parsing the url file", exception);
		}

		return urlCache;

	}

}
