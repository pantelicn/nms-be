package com.opdev.metadata;

import com.opdev.exception.ApiBadRequestException;
import com.opdev.model.metadata.LinkMetadata;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Service
public class MetadataScraperServiceImpl implements MetadataScraperService {

    @Override
    public LinkMetadata extract(String url) {
        String contentType = fetchContentType(url);

        if (contentType == null) {
            return buildEmptyLinkMetadata(url);
        } else if (contentType.contains("image")) {
            return buildImageLinkMetadata(url);
        } else if (contentType.contains("text/html")) {
            Document fetchedDocument = fetchDocument(url);
            return extractLinkMetadata(fetchedDocument);
        }

        return buildEmptyLinkMetadata(url);
    }

    private String fetchContentType(String url) {
        URL createdUrl = createUrl(url);
        HttpURLConnection urlConnection;

        try {
            urlConnection = (HttpURLConnection) createdUrl.openConnection();
        } catch (IOException e) {
            throw new ApiBadRequestException(e.getMessage());
        }

        return urlConnection.getHeaderField("content-type");
    }

    private LinkMetadata buildEmptyLinkMetadata(String url) {
        return LinkMetadata
                .builder()
                .title("")
                .description("")
                .imageUrl("")
                .url(url)
                .build();
    }

    private LinkMetadata buildImageLinkMetadata(String url) {
        return LinkMetadata
                .builder()
                .title("")
                .description("")
                .imageUrl(url)
                .url(url)
                .build();
    }

    private Document fetchDocument(String url) {
        Document fetchedDocument;

        try {
            fetchedDocument = Jsoup.connect(url).get();
        } catch (Exception e) {
            throw new ApiBadRequestException("Metadata scraper failed to connect to a given URL");
        }

        return fetchedDocument;
    }


    private URL createUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new ApiBadRequestException("Malformed URL");
        }
    }

    private LinkMetadata extractLinkMetadata(Document document) {
        return LinkMetadata
                .builder()
                .title(document.title())
                .url(document.location())
                .description(findDescription(document))
                .imageUrl(findImageUrl(document))
                .build();
    }

    private String findDescription(Document document) {
        return findDescriptionInMetaElements(document);
    }

    private String findDescriptionInMetaElements(Document document) {
        String description = "";

        for (Element metaTag : document.select("meta")) {
            description = findDescriptionInAttributes(metaTag.attributes());
            if (!description.isEmpty()) {
                break;
            }
        }

        return description;
    }

    private String findImageUrl(Document document) {
        String imageUrl = findImageInMetaElements(document);

        if (imageUrl.isEmpty()) {
            imageUrl = findLargestIcon(document);
        }
        if (imageUrl.isEmpty()) {
            imageUrl = findLogoElement(document);
        }

        return imageUrl;
    }


    private String findDescriptionInAttributes(Attributes attributes) {
        for (Attribute attribute : attributes) {
            if (attribute.getValue().contains("description") && !attribute.getKey().equals("content")) {
                return attributes.get("content");
            }
        }

        return "";
    }

    private String findImageInMetaElements(Document document) {
        String imageUrl = "";

        for (Element metaTag : document.select("meta")) {
            imageUrl = findImageUrlInAttributes(metaTag.attributes());
            if (!imageUrl.isEmpty()) {
                break;
            }
        }
        return imageUrl;
    }

    private String findImageUrlInAttributes(Attributes attributes) {
        UrlValidator urlValidator = new UrlValidator();

        for (Attribute attribute : attributes) {
            if (attribute.getValue().contains("image") && urlValidator.isValid(attributes.get("content"))) {
                return attributes.get("content");
            }
        }

        return "";
    }

    private String findLargestIcon(Document document) {
        Elements elementsWithIconAttribute = document.select("link[rel=icon]");

        if (!elementsWithIconAttribute.isEmpty()) {
            return Objects.requireNonNull(elementsWithIconAttribute.last()).attr("href");
        }

        return "";
    }

    private String findLogoElement(Document document) {
        Elements elementsWithLogoClasses = document.getElementsByAttributeValueContaining("class", "logo");
        if (!elementsWithLogoClasses.isEmpty())
            return elementsWithLogoClasses.first().attr("src");

        return "";
    }

}
