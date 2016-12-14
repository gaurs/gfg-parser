package io.gaurs.service;

import java.io.File;

import org.jsoup.nodes.Element;

public interface DocGeneratorService {

	File generateDocument(Element... elements);

}
