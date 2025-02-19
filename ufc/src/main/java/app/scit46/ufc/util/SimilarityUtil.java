package app.scit46.ufc.util;

import org.apache.commons.lang3.StringUtils;

public class SimilarityUtil {

    public static int computeDistance(String title, String keyword) {
        if (title == null || keyword == null) {
            return Integer.MAX_VALUE;
        }
        return StringUtils.getLevenshteinDistance(title, keyword);
    }
}
