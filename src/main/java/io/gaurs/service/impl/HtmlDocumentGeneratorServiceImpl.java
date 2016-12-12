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

@Service
public class HtmlDocumentGeneratorServiceImpl implements DocGeneratorService {
	
	@Value("${output.dir}")
	private String outputDir;

	private static final Logger logger = Logger.getLogger(HtmlDocumentGeneratorServiceImpl.class);

	@Override
	public File generateDocument(Element pageTitle, Element pageContent) {

		File op = null;
		try {
			
			//Replace All The Spaces
			String resourceName = pageTitle.text().replaceAll(" ", "_");
			resourceName = resourceName.replaceAll("/", "_");
			
			//Download all the images in the pageContent Folder
			Elements images = pageContent.getElementsByTag("img");
			
			if(!images.isEmpty()){
				//Create the images folder
				String imagesDir = outputDir + resourceName + "/images";
				File dir = new File(imagesDir);
				dir.mkdirs();
				
				for(Element image : images){
					String imageUrl = image.absUrl("src");
					String imageName = fetchImage(imageUrl, dir);
					
					//Update the src to the downloaded image
					image.attr("src", resourceName + "/images" + imageName);
					
					//The parent node is the a tag for the image to be linked to GFG
					image.parentNode().attr("href", resourceName + "/images" + imageName);
				}
			}
			
			
			Document document = Jsoup.parse(""
					+ "<html xmlns='http://www.w3.org/1999/xhtml'>"
					+ "<head>"
					+ "<meta charset='utf-8' />"
					+ "<meta http-equiv='X-UA-Compatible' content='IE=edge' />"
					+ "<meta name='viewport' content='width=device-width, initial-scale=1' />"
					+ "<title>" + pageTitle.text() + "</title>"
					+ "<script type='text/javascript' src='http://code.jquery.com/jquery.min.js'></script>"
					+ "<script src='https://code.jquery.com/jquery-3.0.0.min.js'></script>"
					+ "<script src='http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.js'></script>"
					+ "<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.css' />"
					+ "</head>"
					+ ""
					+ "<body> <section>  <div class='container'>" 
					+ "<h1 align='center'>" + pageTitle.text() + "</h1>"
					+ "</div>"
					+ "<div class='container'>"
					+ pageContent.html()
					+ "</div>"
					+ "</body>");
			
			op = new File(outputDir + resourceName + ".html");
			FileUtils.writeStringToFile(op, document.outerHtml(), "utf-16");
			logger.info("Generated new document with title : " + pageTitle.text());
		} catch (IOException exception) {
			logger.error("Exception occurred while writing " + pageTitle.text(), exception);
		}

		
		return op;
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

		return name;
	}

}
