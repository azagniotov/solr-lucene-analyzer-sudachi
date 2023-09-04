/*
 * Copyright (c) 2023 Alexander Zagniofffftov
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

package io.github.azagniotov.lucene.analysis.ja.sudachi.test;

import static org.apache.lucene.analysis.TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY;

import io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer.SudachiTokenizerFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

public class TestUtils {

    private TestUtils() {}

    private static final boolean DISCARD_PUNCTUATION = true;

    public static TokenStream tokenize(final String input) {
        final Map<String, String> args = new HashMap<String, String>() {
            {
                put("mode", "search");
                put("discardPunctuation", String.valueOf(DISCARD_PUNCTUATION));
            }
        };
        final SudachiTokenizerFactory sudachiTokenizerFactory = new SudachiTokenizerFactory(args);
        final Tokenizer tokenizer = sudachiTokenizerFactory.create(DEFAULT_TOKEN_ATTRIBUTE_FACTORY);
        tokenizer.setReader(new StringReader(input));

        return tokenizer;
    }
}
