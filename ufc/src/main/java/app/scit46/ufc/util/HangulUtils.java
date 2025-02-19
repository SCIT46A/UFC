package app.scit46.ufc.util;

public class HangulUtils {

    private static final String[] CHOSEONG_LIST = {
            "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ",
            "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
    };

    private static final String[] JUNGSEONG_LIST = {
            "ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ",
            "ㅘ", "ㅙ", "ㅚ", "ㅛ", "ㅜ", "ㅝ", "ㅞ", "ㅟ", "ㅠ",
            "ㅡ", "ㅢ", "ㅣ"
    };

    /**
     * 한글 문자열을 초성+중성 조합의 문자열로 변환
     * (예: "간다" → "ㄱㅏㄷㅏ")
     */
    public static String decomposeHangul(String input) {
        if (input == null) return "";
        StringBuilder result = new StringBuilder();

        for (char ch : input.toCharArray()) {
            // 한글 음절이면 분해 (가 ~ 힣: U+AC00 ~ U+D7A3)
            if (ch >= 0xAC00 && ch <= 0xD7A3) {
                int syllableIndex = ch - 0xAC00;
                int choseongIndex = syllableIndex / (21 * 28);
                int jungseongIndex = (syllableIndex % (21 * 28)) / 28;
                result.append(CHOSEONG_LIST[choseongIndex]).append(JUNGSEONG_LIST[jungseongIndex]);
            } else if (!Character.isWhitespace(ch)) {
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * 한글 문자열에서 초성만 추출
     * (예: "간다" → "ㄱㄷ")
     * 띄어쓰기는 제거합니다.
     */
    public static String extractInitialConsonants(String input) {
        if (input == null) return "";
        StringBuilder result = new StringBuilder();
        // 먼저 띄어쓰기 제거
        String normalized = input.replaceAll("\\s+", "");
        for (char ch : normalized.toCharArray()) {
            if (ch >= 0xAC00 && ch <= 0xD7A3) {
                int syllableIndex = ch - 0xAC00;
                int choseongIndex = syllableIndex / (21 * 28);
                result.append(CHOSEONG_LIST[choseongIndex]);
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
