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
package io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer;

import static com.worksap.nlp.sudachi.Tokenizer.SplitMode;
import static io.github.azagniotov.lucene.analysis.ja.sudachi.cache.DictionaryCache.SYSTEM_DICT_LOCAL_PATH;
import static io.github.azagniotov.lucene.analysis.ja.sudachi.cache.DictionaryCache.USER_DICT_LOCAL_PATH;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.cache.DictionaryCache;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

public class SudachiTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {

    private static final Logger LOGGER = LogManager.getLogger(SudachiTokenizerFactory.class.getName());

    private static final String MODE = "mode";
    private static final String MODE_SEARCH = "search";
    private static final String MODE_NORMAL = "normal";
    private static final String MODE_EXTENDED = "extended";
    private static final String DISCARD_PUNCTUATION = "discardPunctuation";
    private final SplitMode mode;
    private final boolean discardPunctuation;
    private final Config config;

    public SudachiTokenizerFactory(final Map<String, String> args) {
        // Config.defaultConfig() throws an IO exception
        // and I do not want to do this in constructor, as this makes the testing brittle.
        this(args, null);
    }

    public SudachiTokenizerFactory(final Map<String, String> args, final Config config) {
        super(args);

        this.mode = getMode(get(args, MODE));
        this.discardPunctuation = getBoolean(args, DISCARD_PUNCTUATION, true);
        this.config = config;
    }

    @Override
    public Tokenizer create(final AttributeFactory factory) {
        try {
            final Dictionary dictionary = buildOrGetCached();
            final com.worksap.nlp.sudachi.Tokenizer internalTokenizer = dictionary.create();
            return new SudachiTokenizer(internalTokenizer, discardPunctuation, mode);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to create SudachiTokenizer", e);
        }
    }

    private Dictionary buildOrGetCached() throws IOException {
        if (DictionaryCache.INSTANCE.isEmpty()) {
            LOGGER.info(" ### Dictionary Cache is empty ###");

            final Config currentConfig = this.config == null ? Config.defaultConfig() : this.config;
            final Config config = currentConfig
                    .systemDictionary(Paths.get(SYSTEM_DICT_LOCAL_PATH))
                    .addUserDictionary(Paths.get(USER_DICT_LOCAL_PATH));
            LOGGER.info(" ### Created Sudachi config from system and user dictionaries ###");

            final Dictionary dictionary = new DictionaryFactory().create(config);
            DictionaryCache.INSTANCE.cache(dictionary);
            LOGGER.info(" ### Created and cached Sudachi Dictionary instance ###");
            return dictionary;
        } else {
            LOGGER.info(" ### Got Sudachi Dictionary instance from the Cache ###");
            return DictionaryCache.INSTANCE.get();
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {}

    private SplitMode getMode(final String input) {
        if (input != null) {
            if (MODE_SEARCH.equalsIgnoreCase(input)) {
                return SplitMode.A;
            } else if (MODE_NORMAL.equalsIgnoreCase(input)) {
                return SplitMode.B;
            } else if (MODE_EXTENDED.equalsIgnoreCase(input)) {
                return SplitMode.C;
            }
        }
        throw new IllegalArgumentException("Tokenization input mode is null");
    }
}
