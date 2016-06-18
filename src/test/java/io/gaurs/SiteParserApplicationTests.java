package io.gaurs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.gaurs.service.impl.CategoryParser;
import io.gaurs.service.impl.PageParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SiteParserApplication.class)
public class SiteParserApplicationTests {

	@Autowired
	private PageParser pagerParser;
	
	@Autowired
	private CategoryParser categoryParser;

	@Test
	@Ignore
	public void testPageParser() throws IOException {
		Document doc = Jsoup.connect("http://www.geeksforgeeks.org/a-search-algorithm/").get();
		List<File> allPagesInThisCategory = pagerParser.beginParsing(doc);
		
		assert(!allPagesInThisCategory.isEmpty());
	}
	
	@Test
	@Ignore
	public void testCategoryParser() throws IOException {
		Document doc = Jsoup.connect("http://www.geeksforgeeks.org/category/algorithm/").get();
		List<File> allPagesInThisCategory = categoryParser.beginParsing(doc);
		
		assert(!allPagesInThisCategory.isEmpty());
	}
	
	@Test
	public void testNextPage() throws IOException{
		Document doc = Jsoup.connect("http://www.geeksforgeeks.org/category/algorithm/").get();
		Element nextPage = categoryParser.fetchNextPage(doc);
		
		assert(null != nextPage);
	
		String nextPageUrl = nextPage.attr("href");

		assert(nextPageUrl.endsWith("page/2/"));
	}

}
