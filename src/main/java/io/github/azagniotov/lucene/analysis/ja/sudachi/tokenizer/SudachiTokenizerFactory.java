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
package io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer;

import static com.worksap.nlp.sudachi.Tokenizer.SplitMode;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.cache.DictionaryCache;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.util.ResourceLoader;
import org.apache.lucene.util.ResourceLoaderAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SudachiTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SudachiTokenizerFactory.class);

    private static final String SYSTEM_DICT_ENV_VAR = "SUDACHI_SYSTEM_DICT";
    private static final String USER_DICT_ENV_VAR = "SUDACHI_USER_DICT";
    private static final String SYSTEM_DICT_LOCAL_PATH = "/tmp/sudachi/system-dict/system.dict";
    private static final String USER_DICT_LOCAL_PATH = "/tmp/sudachi/user_lexicon.dict";

    private static final String MODE = "mode";
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
        this.mode = getMode(args.remove(MODE));
        this.discardPunctuation = Boolean.parseBoolean(args.remove(DISCARD_PUNCTUATION));
        this.config = config;

        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public Tokenizer create(final AttributeFactory factory) {
        final Dictionary dictionary = DictionaryCache.INSTANCE.get();
        final com.worksap.nlp.sudachi.Tokenizer internalTokenizer = dictionary.create();

        return new SudachiTokenizer(internalTokenizer, discardPunctuation, mode);
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        if (DictionaryCache.INSTANCE.isEmpty()) {
            LOGGER.info("Sudachi: Dictionary Cache is empty");

            final Config currentConfig = this.config == null ? Config.defaultConfig() : this.config;
            final Config config = currentConfig
                    .systemDictionary(getEnv(SYSTEM_DICT_ENV_VAR, SYSTEM_DICT_LOCAL_PATH))
                    .addUserDictionary(getEnv(USER_DICT_ENV_VAR, USER_DICT_LOCAL_PATH));
            LOGGER.info("Sudachi: Created config from system and user dictionaries");

            final Dictionary dictionary = new DictionaryFactory().create(config);
            DictionaryCache.INSTANCE.cache(dictionary);
            LOGGER.info("Sudachi: Created and cached Sudachi Dictionary instance");
        }
    }

    private SplitMode getMode(final String input) {
        if (input != null) {
            if (TokenizerMode.SEARCH.desc().equalsIgnoreCase(input)) {
                return SplitMode.A;
            } else if (TokenizerMode.NORMAL.desc().equalsIgnoreCase(input)) {
                return SplitMode.B;
            } else if (TokenizerMode.EXTENDED.desc().equalsIgnoreCase(input)) {
                return SplitMode.C;
            }
        }
        throw new IllegalArgumentException("Tokenization input mode is null, was given " + input);
    }

    private static Path getEnv(final String name, final String defaultValue) {
        final Path defaultValuePath = Paths.get(defaultValue);
        try {
            final String value = System.getenv(name);
            return (value == null || value.trim().isEmpty()) ? defaultValuePath : Paths.get(value);
        } catch (final SecurityException ex) {
            // System.err.println("SecurityException when reading the env variable: " + name);
            return defaultValuePath;
        }
    }
}
