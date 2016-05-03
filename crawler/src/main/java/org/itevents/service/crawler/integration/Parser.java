package org.itevents.service.crawler.integration;

import org.itevents.service.crawler.interfaces.IntegrationEventData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by vaa25 on 06.04.2016.
 */
@SuppressWarnings("PMD.LawOfDemeter")
public final class Parser {

    private static final String TAG_DD = ".dd";

    private final Document document;

    public Parser(final String html) {
        this.document = Jsoup.parse(html);
    }

    public IntegrationEventData parse() {
        final IntegrationEventData integrationEventData =
            new SampleIntegrationEventData();
        integrationEventData.setTitle(getTitle());
        integrationEventData.setDate(getDate());
        integrationEventData.setTime(getTime());
        integrationEventData.setAddress(getAddress());
        integrationEventData.setCity(getCity());
        integrationEventData.setRegistrationLink(getRegistrationLink());
        integrationEventData.setDescription(getDescription());
        integrationEventData.setPrice(getPrice());
        return integrationEventData;
    }

    private String getTitle() {
        return document.head().select("title").text();
    }

    private String getDate() {
        return document.select(Parser.TAG_DD).first().text();
    }

    private String getTime() {
        return document.select(Parser.TAG_DD).get(1).text();
    }

    private String getAddress() {
        final String[] split =
            document.select(Parser.TAG_DD).get(2).text().split(",");
        return String.format("%s, %s", split[1].trim(), split[2].trim());
    }

    private String getCity() {
        return document.select(Parser.TAG_DD).get(0).text();
    }

    private String getRegistrationLink() {
        final Elements possibleLinks = document.select(":containsOwn(регистр) a");
        return possibleLinks.first().attr("href");
    }

    private String getDescription() {
        return document.select(".b-typo").first().text();
    }

    private String getPrice() {
        String price = document.select(":containsOwn(Цена)").text();
        if (price.contains("у.е")) {
            price = String.format("%s у.е.", price.split("у.е")[0].replaceAll("\\D", ""));
        }
        return price;
    }
}
