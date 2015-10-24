package io.contextr;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.contextr.model.PersistModel;
import io.contextr.model.WikipediaJSONModel;
import io.contextr.repository.Repository;
import io.contextr.utils.FileUtils;
import io.contextr.utils.HTTPUtils;

@SpringBootApplication
public class Main implements CommandLineRunner {

	private static final Logger logger = Logger.getLogger(Main.class);

	static FileUtils fileUtils = new FileUtils();

	static HTTPUtils httpUtils = new HTTPUtils();

	@Autowired
	private Repository repository;

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args).close();
	}

	@Override
	public void run(String... args) throws Exception {

		args = new String[] { "titles", "Physics", "output", "en" };

		List<PersistModel> jsonOut = new ArrayList<>();

		if (args.length != 4) {
			helpAndQuit();
		} else {
			String filePath = args[0];
			String profile = args[1];
			String outputPath = args[2];
			String langPrefix = args[3];

			List<String> titles = fileUtils.readFile(filePath);
			List<String> requestTitles = new ArrayList<>();
			String jsonBaseURL = "https://" + langPrefix +".wikipedia.org/w/api.php?action=query&prop=extracts&format=json&explaintext=&redirects=&titles=";
			String jsonFirstParagraphURL = "https://" + langPrefix +".wikipedia.org/w/api.php?action=query&prop=extracts|revisions&format=json&exintro=&explaintext=&redirects=&titles=";
			String htmlBaseURL = "https://" + langPrefix +".wikipedia.org/wiki/";

			titles.parallelStream().map(httpUtils::urlEncode).forEach(title -> {
				requestTitles.add(title);
				String wikipediaHTML = HTTPUtils.sendRequest("", htmlBaseURL + title, HttpMethod.GET, String.class);
				Document doc = Jsoup.parse(wikipediaHTML);
				Elements links = doc.select("a[href]"); // a with href

				boolean foundBody = false;
				for (Element link : links) {
					String linkTitle = link.attr("href");
					System.out.println(linkTitle);

					outerLoop: for (int i = linkTitle.length() - 1; i >= 0; i--) {

						switch (linkTitle.charAt(i)) {
						case '/':
							linkTitle = linkTitle.substring(i + 1);
						case '.':
							break outerLoop;
						}

					}

					if (!foundBody) {
						foundBody = linkTitle.trim().startsWith("#");
					} else {
						requestTitles.add(linkTitle);
					}
				}
			});

			requestTitles.parallelStream().forEach(jsonURL -> {
				WikipediaJSONModel wikipediaFullText = HTTPUtils.sendRequest("", jsonBaseURL + jsonURL, HttpMethod.GET,
						WikipediaJSONModel.class);
				WikipediaJSONModel wikipediaFirstParagraph = HTTPUtils.sendRequest("", jsonFirstParagraphURL + jsonURL,
						HttpMethod.GET, WikipediaJSONModel.class);

				String cleanText = wikipediaFullText.getText().replace(wikipediaFirstParagraph.getText(), "")
						.replaceAll("\\\\n", " ").replaceAll("===", "").replaceAll("==", "").replaceAll("\\s", " ")
						.replace("\\", "\"").trim();
				if (!cleanText.isEmpty()) {
					jsonOut.add(new PersistModel(profile, cleanText));
				}
			});
			fileUtils.writeToFile(outputPath, new ObjectMapper().writeValueAsString(jsonOut));
		}

	}

	private static void helpAndQuit() {
		System.out.println("Invalid arguments!\n" + "Arguments: <input filepath> <profile> <output filepath> <lang prefix>\n\n"
				+ "<input filepath> \t- path to file of URL List\n"
				+ "<profile> \t- profile under which to file the sources\n"
				+ "<output filepath> \t- path to file to write to\n"
				+ "<lang prefix> \t- prefix of language, as used by Wikipedia\n");
		System.exit(-1);
	}
}