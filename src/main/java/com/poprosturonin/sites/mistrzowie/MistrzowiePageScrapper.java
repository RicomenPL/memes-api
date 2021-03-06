package com.poprosturonin.sites.mistrzowie;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.Content;
import com.poprosturonin.data.contents.ImageContent;
import com.poprosturonin.exceptions.PageIsEmptyException;
import com.poprosturonin.sites.PageScrapper;
import com.poprosturonin.utils.ParsingUtils;
import com.poprosturonin.utils.URLUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.poprosturonin.sites.mistrzowie.MistrzowieController.ROOT_URL;

/**
 * Mistrzowie scrapper
 */
@Component
public class MistrzowiePageScrapper implements PageScrapper {
    private Pattern commentPattern = Pattern.compile("Skomentuj \\(([0-9]+)\\)");

    private Optional<Meme> parsePicture(Element mistrz) {
        Elements realImage = mistrz.select("img.pic");
        if (!realImage.isEmpty())
            return parseAsImage(mistrz);

        return Optional.empty();
    }

    private Optional<Meme> parseAsImage(Element mistrz) {
        String title, url;
        Content content;

        // Get content
        Elements contentElement = mistrz.select("img.pic");
        content = new ImageContent(ROOT_URL + contentElement.attr("src"));

        // Get title
        Element header = mistrz.select("h1.picture > a").first();
        title = header.text();

        // Get url
        url = ROOT_URL + header.attr("href");

        return Optional.of(new Meme(title, content, url, getComments(mistrz), getVotes(mistrz)));
    }

    private int getComments(Element mistrz) {
        Matcher m = commentPattern.matcher(mistrz.select("a.lcomment").text());

        if (m.find()) {
            return ParsingUtils.parseIntOrGetZero(m.group(1));
        }
        return 0;
    }

    private int getVotes(Element mistrz) {
        return ParsingUtils.parseIntOrGetZero(mistrz.select("span.total_votes_up > span.value").text());
    }

    public Page parsePage(Document document) {
        Page page = new Page();

        // Get next link page
        Elements nextPageElement = document.getElementsByClass("prefetch list_next_page_button");
        if (!nextPageElement.isEmpty())
            page.setNextPage("/mistrzowie/page" + URLUtils.cutToSecondSlash(URLUtils.cutOffParameters(nextPageElement.get(0).attr("href"))).get());

        // Get content
        Elements pictures = document.select("div.pic");
        List<Meme> memes = pictures.stream()
                .map(this::parsePicture)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(meme -> URLUtils.getPathId(meme.getUrl()).ifPresent(s -> meme.setViewUrl(String.format("/mistrzowie/%s", s))))
                .collect(Collectors.toList());

        page.getMemes().addAll(memes);

        page.setTitle(document.title());

        if (page.isEmpty())
            throw new PageIsEmptyException();

        return page;
    }
}
