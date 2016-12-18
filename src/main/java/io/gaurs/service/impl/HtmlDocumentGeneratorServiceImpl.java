package io.gaurs.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.gaurs.service.DocGeneratorService;

@Service("htmlDocGenerator")
public class HtmlDocumentGeneratorServiceImpl implements DocGeneratorService {

	@Value("${output.dir}")
	private String outputDir;

	private static final Logger logger = Logger.getLogger(HtmlDocumentGeneratorServiceImpl.class);

	@Override
	public File generateDocument(Element... elements) {

		Element pageTitle = elements[0];
		Element pageContent = elements[1];

		File op = null;
		try {

			// Replace All The Spaces
			String resourceName = pageTitle.text().replaceAll(" ", "_");
			resourceName = resourceName.replaceAll("/", "_");
			
			resourceName = resourceName.replaceAll("\\|", "_");
			resourceName = resourceName.replaceAll("\\?", "");

			// Download all the images in the pageContent Folder
			Elements images = pageContent.getElementsByTag("img");

			if (!images.isEmpty()) {
				// Create the images folder
				String imagesDir = outputDir + resourceName + "/images";
				File dir = new File(imagesDir);
				dir.mkdirs();

				for (Element image : images) {
					String imageUrl = image.absUrl("src");
					String imageName = fetchImage(imageUrl, dir);

					// Update the src to the downloaded image
					image.attr("src", resourceName + "/images" + imageName);
					image.attr("class", "img-fluid");

					// The parent node is the a tag for the image to be linked
					// to GFG
					image.parentNode().attr("href", resourceName + "/images" + imageName);
				}
			}

			Document document = generatePage(pageTitle, pageContent);

			op = new File(outputDir + resourceName + ".html");
			FileUtils.writeStringToFile(op, document.outerHtml(), "utf-8");
			logger.info("Generated new document with title : " + pageTitle.text());
		} catch (IOException exception) {
			logger.error("Exception occurred while writing " + pageTitle.text(), exception);
		}

		return op;
	}

	private Document generatePage(Element pageTitle, Element pageContent) {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("template.html");

		Document page = null;

		try {
			page = Jsoup.parse(stream, "utf-8", "");

			// Set the title of page
			Element title = page.getElementsByTag("title").first();
			title.text(pageTitle.text());

			// Set the question statement
			Element question = page.getElementById("ques");
			question.text(pageTitle.text());

			// Set the content

			// 1. Get the content div
			Element content = page.getElementById("content");

			// 2. Get all the tags from downloaded page
			Elements elements = pageContent.children();

			// 3. for every tag
			for (Element element : elements) {
				if (element.tagName().equalsIgnoreCase("div")) {
					continue;
				} else if (element.tagName().equalsIgnoreCase("p")) {
					Element paragraph = content.appendElement("p");
					paragraph.attr("class", "text-justify");
					paragraph.html(element.html());
				} else if (element.tagName().equalsIgnoreCase("pre") && !containsImage(element)) {
					Element div = content.appendElement("div");
					div.attr("class", "bg-faded");
					div.attr("style", "padding:20px;");

					Element pre = div.appendElement("pre");
					pre.html(element.html());
				} else if (element.tagName().equalsIgnoreCase("a")) {
					Element link = content.appendElement("a");
					link.attr("href", element.attr("href"));
					link.text(element.text());
				}

				else {
					Element tag = content.appendElement(element.tagName());
					tag.html(element.html());
				}
			}

		} catch (IOException exception) {
			logger.error("Exception occurred while generating .html page", exception);
		}

		return page;
	}

	private boolean containsImage(Element element) {
		return (null != element.getElementsByTag("img") && !element.getElementsByTag("img").isEmpty());
	}

	private String fetchImage(String imageUrl, File dir) throws IOException {

		// Extract the name of the image from the source attribute
		int indexname = imageUrl.lastIndexOf("/");

		if (indexname == imageUrl.length()) {
			imageUrl = imageUrl.substring(1, indexname);
		}

		indexname = imageUrl.lastIndexOf("/");
		String name = imageUrl.substring(indexname, imageUrl.length());

		// Open a URL Stream

		try {
			URL url = new URL(imageUrl);
			InputStream in = url.openStream();

			File output = new File(dir.getAbsolutePath() + name);
			output.createNewFile();

			OutputStream out = new BufferedOutputStream(new FileOutputStream(output));

			for (int b; (b = in.read()) != -1;) {
				out.write(b);
			}
			out.close();
			in.close();
		} catch (Exception exception) {
			logger.error("Exception occurred while downloading image file : ", exception);
		}
		return name;
	}

}
