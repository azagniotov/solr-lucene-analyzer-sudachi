package io.github.azagniotov.lucene.analysis.ja.sudachi;

import static com.google.common.truth.Truth.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

/** A helper to sanitize the synonym file. */
public class SynonymCleanUpHelper {
    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9,\\s{1}-]+$");

    @Test
    @Ignore("This is not an actual test, but a utility to sanitize the synonym file")
    public void sanitize() throws Exception {
        final InputStream inputStream = this.getClass().getResourceAsStream("/original_sudachi_synonyms.txt");

        final String resultingSynonyms = "sudachi_synonyms_clean.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(resultingSynonyms, true));

        final Map<String, List<String>> original = new HashMap<>();

        try (final BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)); ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) {
                    continue;
                }

                if (isAlpha(trimmedLine)) {
                    continue;
                }

                final String[] strings = trimmedLine.split(",");
                if (strings.length != 2) {
                    throw new IllegalStateException(trimmedLine);
                }

                final String key = strings[0];
                final String synonym = strings[1];

                if (!original.containsKey(key)) {
                    original.put(key, new ArrayList<>());
                }

                original.get(key).add(synonym);
            }

            final Map<String, List<String>> filtered = original.entrySet().stream()
                    .filter(a -> a.getValue().size() > 1)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            for (Map.Entry<String, List<String>> entry : filtered.entrySet()) {
                String commaSeparatedString = String.join(",", entry.getValue());
                System.out.println(entry.getKey() + " " + commaSeparatedString);

                writer.append(commaSeparatedString);
                writer.newLine();
            }

            writer.close();

        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }
    }

    @Test
    public void regex() throws Exception {
        assertThat(isAlpha("000006,open")).isTrue();
        assertThat(isAlpha("000087,United Kingdom")).isTrue();
        assertThat(isAlpha("000064,American League")).isTrue();

        assertThat(isAlpha("001652,kmホールディングス")).isFalse();
        assertThat(isAlpha("001646,UTスターコム")).isFalse();
        assertThat(isAlpha("000006,アレックス")).isFalse();
    }

    private static boolean isAlpha(final String input) {
        return PATTERN.matcher(input).find();
    }
}
