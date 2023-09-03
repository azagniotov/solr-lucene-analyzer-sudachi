/*
 * Copyright (c) 2017-2023 Works Applications Co., Ltd.
 * Modifications copyright (c) 2023 Alexander Zagniotov
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

package io.github.azagniotov.lucene.analysis.ja.sudachi.analyzer;

import com.worksap.nlp.sudachi.PartialPOS;
import com.worksap.nlp.sudachi.Tokenizer.SplitMode;
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiBaseFormFilter;
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiSurfaceFormFilter;
import io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer.SudachiTokenizer;
import io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer.SudachiTokenizerFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.StopTags;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.StopWords;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.util.AttributeFactory;

/**
 * Analyzer which uses Sudachi as internal tokenizer. It also applies
 * BaseFormFilter and stop word/stop POS filtering.
 *
 * @see SudachiTokenizer
 */
public class SudachiAnalyzer extends StopwordAnalyzerBase {

    private final SplitMode mode;
    private final boolean discardPunctuation;
    private boolean useSurfaceFilter;

    public SudachiAnalyzer(
            final CharArraySet stopwords,
            final boolean discardPunctuation,
            final boolean useSurfaceFilter,
            final SplitMode mode) {
        super(stopwords);
        this.discardPunctuation = discardPunctuation;
        this.useSurfaceFilter = useSurfaceFilter;
        this.mode = mode;
    }

    public static CharArraySet getDefaultStopSet() {
        return Defaults.STOP_WORDS;
    }

    public static List<PartialPOS> getDefaultStopTags() {
        return Defaults.STOP_TAGS;
    }

    private static class Defaults {
        static final CharArraySet STOP_WORDS;
        static final List<PartialPOS> STOP_TAGS;

        static {
            try {
                STOP_WORDS = StopWords.load(true, "stopwords.txt", "#");
                final CharArraySet tagset = StopWords.load(false, "stoptags.txt", "#");
                List<PartialPOS> tags = new ArrayList<>();
                for (Object element : tagset) {
                    char[] chars = (char[]) element;
                    tags.add(StopTags.parse(new String(chars)));
                }
                STOP_TAGS = Collections.unmodifiableList(tags);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private Defaults() {}
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        Tokenizer tokenizer = createTokenizer(new HashMap<>());
        TokenStream stream = tokenizer;

        if (this.useSurfaceFilter) {
            stream = new SudachiSurfaceFormFilter(stream);
        } else {
            stream = new SudachiBaseFormFilter(stream);
        }

        stream = new StopFilter(stream, this.stopwords);
        return new TokenStreamComponents(tokenizer, stream);
    }

    private Tokenizer createTokenizer(final Map<String, String> args) {

        final Map<String, String> map = new HashMap<>(args);
        map.put("mode", String.valueOf(this.mode));
        map.put("discardPunctuation", String.valueOf(this.discardPunctuation));
        final SudachiTokenizerFactory factory = new SudachiTokenizerFactory(map);

        return factory.create(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
    }
}
