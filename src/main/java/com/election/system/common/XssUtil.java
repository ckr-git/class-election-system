package com.election.system.common;

import java.util.regex.Pattern;

public class XssUtil {

    private static final Pattern SCRIPT_TAG = Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern SCRIPT_SRC = Pattern.compile("src\\s*=\\s*['\"]\\s*javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_HANDLERS = Pattern.compile("\\s+on\\w+\\s*=\\s*['\"][^'\"]*['\"]", Pattern.CASE_INSENSITIVE);
    private static final Pattern JAVASCRIPT_PROTO = Pattern.compile("javascript\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_TAGS = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);

    private XssUtil() {}

    public static String clean(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String result = input;
        result = SCRIPT_TAG.matcher(result).replaceAll("");
        result = SCRIPT_SRC.matcher(result).replaceAll("");
        result = EVENT_HANDLERS.matcher(result).replaceAll("");
        result = JAVASCRIPT_PROTO.matcher(result).replaceAll("");
        result = HTML_TAGS.matcher(result).replaceAll("");
        return result.trim();
    }
}
