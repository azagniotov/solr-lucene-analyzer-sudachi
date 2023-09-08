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

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.dictionary.UserDictionaryBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

public class SudachiTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {

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
            final String systemDictPath = "/tmp/sudachi/system-dict/system_core.dic";
            final String userLexiconCsvPath = "/tmp/sudachi/user_lexicon.csv";

            final InputStream userLexiconCsvStream = Objects.requireNonNull(
                    SudachiTokenizerFactory.class.getResourceAsStream("/user-dict/user_lexicon.csv"));

            final String csv = new Scanner(userLexiconCsvStream, StandardCharsets.UTF_8.name())
                    .useDelimiter("\\A")
                    .next();
            Files.write(Paths.get(userLexiconCsvPath), csv.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

            final String currentDirectory =
                    Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString();
            final String userDictFilename = String.join("/", currentDirectory, "user_lexicon.dic");
            UserDictionaryBuilder.main(new String[] {"-o", userDictFilename, "-s", systemDictPath, userLexiconCsvPath});

            final SudachiTokenizer sudachiTokenizer = new SudachiTokenizer(
                    discardPunctuation, mode, Paths.get(systemDictPath), Paths.get(userDictFilename));

            if (this.config == null) {
                sudachiTokenizer.createDict(Config.defaultConfig());
            } else {
                sudachiTokenizer.createDict(this.config);
            }

            return sudachiTokenizer;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to create SudachiTokenizer", e);
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
