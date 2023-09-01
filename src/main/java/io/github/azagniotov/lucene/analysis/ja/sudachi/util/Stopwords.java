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

package io.github.azagniotov.lucene.analysis.ja.sudachi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.WordlistLoader;

public class Stopwords {
    public static CharArraySet load(final boolean ignoreCase, final String filename, final String comment)
            throws IOException {
        try (final InputStream inputStream = Stopwords.class.getResourceAsStream(filename);
                final InputStreamReader inputStreamReader =
                        new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
                final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final CharArraySet result = new CharArraySet(16, ignoreCase);

            return WordlistLoader.getWordSet(bufferedReader, comment, result);
        }
    }
}
