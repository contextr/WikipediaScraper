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

		args = new String[] { "titles", "foo", "output" };

		List<PersistModel> jsonOut = new ArrayList<>();

		if (args.length != 3) {
			helpAndQuit();
		} else {
			String filePath = args[0];
			String profile = args[1];
			String outputPath = args[2];

			List<String> titles = fileUtils.readFile(filePath);
			List<String> jsonUrls = new ArrayList<>();
			String jsonBaseURL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&explaintext=&redirects=&titles=";
			String htmlBaseURL = "https://en.wikipedia.org/wiki/";
			int MAX_NUM_LINKS = 50;

			titles.stream().map(httpUtils::urlEncode).forEach(title -> {
				String wikipediaHTML = HTTPUtils.sendRequest("", htmlBaseURL + title, HttpMethod.GET, String.class);
				Document doc = Jsoup.parse(wikipediaHTML);
				Elements links = doc.select("a[href]"); // a with href

				boolean foundBody = false;
				for (Element link : links) {
					if (jsonUrls.size() < MAX_NUM_LINKS) {
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
						}
						else
						{
							jsonUrls.add(jsonBaseURL + linkTitle);
						}
					}
				}
			});

			jsonUrls.forEach(jsonURL -> {
				WikipediaJSONModel wikipediaPlain = HTTPUtils.sendRequest("", jsonURL, HttpMethod.GET,
						WikipediaJSONModel.class);
				String cleanText = wikipediaPlain.getText().replaceAll("\\\\n", " ").replaceAll("===", "")
						.replaceAll("==", "").replaceAll("\\s", " ").trim();
				System.out.println("+++++++");
				System.out.println(jsonURL);
				System.out.println("-------");
				System.out.println(cleanText);
				jsonOut.add(new PersistModel(profile, cleanText));
			});
			fileUtils.writeToFile(outputPath, new ObjectMapper().writeValueAsString(jsonOut));
		}

	}

	private static void helpAndQuit() {
		System.out.println("Invalid arguments!\n" + "Arguments: <filepath> <profile>\n\n"
				+ "<filepath> \t- path to file of URL List\n"
				+ "<profile> \t- profile under which to file the sources");
		System.exit(-1);
	}
}