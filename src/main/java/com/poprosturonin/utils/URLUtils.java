package com.poprosturonin.utils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtils {
    public static String CUT_URL_PATTERN = "^/([^\\s/]+)/page/(\\d+)";
    public static String FIND_ID_PATTERN = "/([0-9]+)";

    private static Pattern findIdPattern = Pattern.compile(FIND_ID_PATTERN);

    /**
     * Finds the id in the url like /wow/<b>504252</b>/that_was_id/ and
     * returns it
     *
     * @param url url to search
     * @return id in optional or empty optional if id was not found
     */
    public static Optional<String> getPathId(String url) {
        Matcher matcher = findIdPattern.matcher(url);
        if (matcher.find() && matcher.groupCount() > 0) {
            return Optional.of(matcher.group(1));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Cuts the part of the url before first slash
     * Fe. http://example.org/foo will be cut to /foo
     * <p>
     * If url doesn't contain 3 slashes (note the protocol slashes) null will be returned
     *
     * @param url url to cut
     * @return cut url or empty optional
     */
    public static Optional<String> cutToFirstSlash(String url) {
        int index = -1;
        int slash = 0;
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == '/') {
                if (slash == 2) {
                    index = i; //the 3th slash
                    break;
                } else {
                    slash++;
                }
            }
        }

        if (index == -1)
            return Optional.empty();

        return Optional.of(url.substring(index));
    }

    /**
     * Cuts the part of the url before second slash
     * Fe. http://example.org/foo/wow will be cut to /wow
     * <p>
     * If url doesn't contain 4 slashes (note the protocol slashes) null will be returned
     *
     * @param url url to cut
     * @return cut url or empty optional
     */
    public static Optional<String> cutToSecondSlash(String url) {
        int index = -1;
        int slash = 0;
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == '/') {
                if (slash == 3) {
                    index = i; //the 4th slash
                    break;
                } else {
                    slash++;
                }
            }
        }

        if (index == -1)
            return Optional.empty();

        return Optional.of(url.substring(index));
    }

    /**
     * Cuts of the GET parameters from url
     * Fe. http://example.org/wow?id=123 will be cut to http://example.org/wow
     *
     * @param url url to cut
     * @return url without parameters
     */
    public static String cutOffParameters(String url) {
        int index = url.lastIndexOf('?');
        if (index != -1)
            return url.substring(0, index);
        else
            return url;
    }
}
