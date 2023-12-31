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

package io.github.azagniotov.lucene.analysis.ja.sudachi.filters;

import com.worksap.nlp.sudachi.JapaneseDictionary;
import com.worksap.nlp.sudachi.PartialPOS;
import com.worksap.nlp.sudachi.PosMatcher;
import io.github.azagniotov.lucene.analysis.ja.sudachi.analyzer.SudachiAnalyzer;
import io.github.azagniotov.lucene.analysis.ja.sudachi.cache.DictionaryCache;
import io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer.SudachiTokenizerFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.StopTags;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenFilterFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.ResourceLoader;
import org.apache.lucene.util.ResourceLoaderAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SudachiPartOfSpeechStopFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SudachiTokenizerFactory.class);

    private final String stopTagFiles;
    private List<PartialPOS> stopTags;

    public SudachiPartOfSpeechStopFilterFactory(final Map<String, String> args) {
        super(args);
        this.stopTagFiles = get(args, "tags");
        if (stopTagFiles == null) {
            LOGGER.info("Sudachi: empty 'tags' parameter given. Loading default stop tags instead");
            this.stopTags = SudachiAnalyzer.getDefaultStopTags();
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(final TokenStream tokenStream) {

        if (!this.stopTags.isEmpty()) {
            final JapaneseDictionary japaneseDictionary = (JapaneseDictionary) DictionaryCache.INSTANCE.get();
            if (japaneseDictionary == null) {
                throw new IllegalStateException("SudachiAttribute returned null JapaneseDictionary, how so??");
            }
            final PosMatcher posMatcher = japaneseDictionary.posMatcher(this.stopTags);
            return new SudachiPartOfSpeechStopFilter(tokenStream, posMatcher);
        }

        return tokenStream;
    }

    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        if (this.stopTagFiles != null) {
            final CharArraySet tagSet = getWordSet(loader, this.stopTagFiles, false);
            if (tagSet != null) {
                final List<PartialPOS> tags = new ArrayList<>();
                for (Object element : tagSet) {
                    char[] chars = (char[]) element;
                    tags.add(StopTags.parse(new String(chars)));
                }
                LOGGER.info("Sudachi: Loaded {} stop tags from the provided files {}", tags.size(), this.stopTagFiles);
                this.stopTags = tags;
            } else {
                LOGGER.info("Sudachi: Could not load stop tags from the provided files {}", this.stopTagFiles);
                this.stopTags = Collections.emptyList();
            }
        }
    }
}
