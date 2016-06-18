package io.gaurs.service;

import java.io.File;
import java.util.List;

import org.jsoup.nodes.Document;

/**
 * The Parent Parsing service that denotes various parsers like CategoryParser;
 * PageParser etc
 * 
 * @author gaurs
 *
 */
public interface ParsingService {

	/**
	 * Begin the parsing based on the page as mentioned in the
	 * application.properties
	 */
	default void beginParsing() {

	}

	/**
	 * Begin the parsing of the document as denoted by the input parameter
	 * 
	 * @param document
	 */
	default List<File> beginParsing(Document document) {
		return null;
	}

}
