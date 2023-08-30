/*
 * Copyright (c) 2023 Alexander Zagniotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.azagniotov.lucene.analysis;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.MorphemeList;
import com.worksap.nlp.sudachi.Tokenizer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JapaneseTokenizer {

    // http://www.rikai.com/library/kanjitables/kanji_codes.unicode.shtml
    private static final Pattern JAPANESE_PUNCTUATION = Pattern.compile("[\\x{3000}-\\x{303F}]");

    // !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~:
    private static final Pattern ASCII_PUNCTUATION = Pattern.compile("\\p{Punct}");

    // https://en.wikipedia.org/wiki/Latin-1_Supplement
    private static final Pattern LATIN_1_PUNCTUATION = Pattern.compile("[\\x{0080}-\\x{00FF}]");

    private final Tokenizer sudachiTokenizer;

    private JapaneseTokenizer(final Tokenizer sudachiTokenizer) {
        this.sudachiTokenizer = sudachiTokenizer;
    }

    public static JapaneseTokenizer from(final Path systemDictPath, final Path userDictPath) throws IOException {
        final Config config =
                Config.defaultConfig().systemDictionary(systemDictPath).addUserDictionary(userDictPath);

        final Dictionary dictionary = new DictionaryFactory().create(config);
        return new JapaneseTokenizer(dictionary.create());
    }

    public List<String> tokenize(final String query) {
        final MorphemeList morphemeList = sudachiTokenizer.tokenize(Tokenizer.SplitMode.A, query);

        return morphemeList.stream()
                .map(Morpheme::surface)
                .map(String::trim)
                .filter(surface -> !surface.isEmpty())
                .filter(surface -> !isPunctuation(surface))
                .collect(Collectors.toList());
    }

    boolean isPunctuation(final String query) {
        if (query == null || query.trim().length() == 0) {
            return false;
        } else {
            return JAPANESE_PUNCTUATION.matcher(query).matches()
                    || ASCII_PUNCTUATION.matcher(query).matches()
                    || LATIN_1_PUNCTUATION.matcher(query).matches();
        }
    }
}
