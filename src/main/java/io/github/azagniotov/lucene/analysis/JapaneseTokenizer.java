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

import com.worksap.nlp.sudachi.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class JapaneseTokenizer {

    private final Tokenizer sudachiTokenizer;

    private JapaneseTokenizer(final Tokenizer sudachiTokenizer) {
        this.sudachiTokenizer = sudachiTokenizer;
    }

    public static JapaneseTokenizer fromSystemDict(final String fileCanonicalPath) throws IOException {
        final Config config = Config.defaultConfig().systemDictionary(Paths.get(fileCanonicalPath));
        try (final Dictionary dictionary = new DictionaryFactory().create(config)) {
            return new JapaneseTokenizer(dictionary.create());
        }
    }

    public List<String> tokenize(final String query) {
        final MorphemeList morphemeList = sudachiTokenizer.tokenize(Tokenizer.SplitMode.A, query);

        return morphemeList.stream().map(Morpheme::surface).collect(Collectors.toList());
    }
}
