import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordRandomizer {
    public static String randomize(String originalMessage) {
        List<String> words = new ArrayList<>();
        Pattern wordPattern = Pattern.compile("[a-öA-Ö0-9]+[.,:;!=]*"); // Matches any non space character.
        Matcher wordMatcher = wordPattern.matcher(originalMessage);
        while (wordMatcher.find()) {
            words.add(wordMatcher.group());
        }

        StringBuilder output = new StringBuilder(originalMessage.length());
        for (String word : words) {
            output.append(word.charAt(0));
            List<Character> lettersToShuffle = new ArrayList<>();
            int i = 1;
            while (i < word.length()-1 && Character.isAlphabetic(word.charAt(i))) {
                lettersToShuffle.add(word.charAt(i));
                i++;
            }
            Collections.shuffle(lettersToShuffle);
            for (char c : lettersToShuffle) {
                output.append(c);
            }
            for(i = lettersToShuffle.size() + 1; i < word.length(); i++) {
                output.append(word.charAt(i));
            }
            output.append(" ");
        }
        output.deleteCharAt(output.length()-1);
        return output.toString();
    }
}
