/*
 * Copyright (c) 2023-2024 Alexander Zagniotov
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

import static com.worksap.nlp.sudachi.Tokenizer.*;
import static org.apache.lucene.analysis.TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.JapaneseDictionary;
import io.github.azagniotov.lucene.analysis.ja.sudachi.cache.DictionaryCache;
import io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer.SudachiTokenizerFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.NoOpResourceLoader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

public class TestUtils {
    private final Config config;

    public TestUtils(final Config config) {
        this.config = config;
    }

    public TokenStream tokenize(final String input) throws IOException {
        return tokenize(input, true, SplitMode.A);
    }

    public TokenStream tokenize(final String input, final boolean discardPunctuation, final SplitMode splitMode)
            throws IOException {
        final Tokenizer tokenizer = makeTokenizer(discardPunctuation, splitMode);
        tokenizer.setReader(new StringReader(input));

        return tokenizer;
    }

    public Tokenizer makeTokenizer(final boolean discardPunctuation, final SplitMode splitMode) throws IOException {
        final String mode = splitMode == SplitMode.A ? "search" : (splitMode == SplitMode.B ? "normal" : "extended");
        final Map<String, String> args = new HashMap<String, String>() {
            {
                put("mode", mode);
                put("discardPunctuation", String.valueOf(discardPunctuation));
            }
        };
        DictionaryCache.INSTANCE.invalidate();
        final SudachiTokenizerFactory sudachiTokenizerFactory = new SudachiTokenizerFactory(args, this.config);
        sudachiTokenizerFactory.inform(new NoOpResourceLoader());
        final Tokenizer tokenizer = sudachiTokenizerFactory.create(DEFAULT_TOKEN_ATTRIBUTE_FACTORY);

        assert DictionaryCache.INSTANCE.get() != null;
        assert DictionaryCache.INSTANCE.get().getClass().isAssignableFrom(JapaneseDictionary.class);

        return tokenizer;
    }

    public Analyzer makeDefaultAnalyzer() throws IOException {
        final Tokenizer tokenizer = this.makeTokenizer(false, SplitMode.A);
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                return new TokenStreamComponents(tokenizer, tokenizer);
            }
        };
    }

    public Analyzer makeNoPunctuationAnalyzer() throws IOException {
        final Tokenizer tokenizer = this.makeTokenizer(true, SplitMode.A);
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                return new TokenStreamComponents(tokenizer, tokenizer);
            }
        };
    }
}
