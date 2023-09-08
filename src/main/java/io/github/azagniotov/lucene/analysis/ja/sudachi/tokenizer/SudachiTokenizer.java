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
package io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer;

import static com.worksap.nlp.sudachi.Tokenizer.SplitMode;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.MorphemeList;
import com.worksap.nlp.sudachi.Tokenizer;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiAttribute;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiMorphemeAttribute;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.util.AttributeFactory;

public final class SudachiTokenizer extends org.apache.lucene.analysis.Tokenizer {

    private static final Logger LOGGER = LogManager.getLogger(SudachiTokenizer.class.getName());

    private MorphemeIterator morphemeIterator;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionLengthAttribute posLengthAtt;
    private final PositionIncrementAttribute posIncAtt;
    private final SudachiMorphemeAttribute morphemeAtt;

    private final SudachiAttribute sudachiAttribute;
    private Tokenizer sudachiTokenizer;
    private final boolean discardPunctuation;
    private final SplitMode mode;
    private final Path systemDictPath;
    private final Path userDictPath;
    private Dictionary dictionary;

    public SudachiTokenizer(
            final boolean discardPunctuation,
            final SplitMode mode,
            final Path systemDictPath,
            final Path userDictPath) {
        this(DEFAULT_TOKEN_ATTRIBUTE_FACTORY, discardPunctuation, mode, systemDictPath, userDictPath);
    }

    public SudachiTokenizer(
            final AttributeFactory factory,
            final boolean discardPunctuation,
            final SplitMode mode,
            final Path systemDictPath,
            final Path userDictPath) {
        super(factory);
        this.discardPunctuation = discardPunctuation;
        this.mode = mode;
        this.systemDictPath = systemDictPath;
        this.userDictPath = userDictPath;

        this.termAtt = addAttribute(CharTermAttribute.class);
        this.offsetAtt = addAttribute(OffsetAttribute.class);
        this.posLengthAtt = addAttribute(PositionLengthAttribute.class);
        this.posIncAtt = addAttribute(PositionIncrementAttribute.class);
        this.morphemeAtt = addAttribute(SudachiMorphemeAttribute.class);
        this.sudachiAttribute = addAttribute(SudachiAttribute.class);

        this.morphemeIterator = MorphemeIterator.EMPTY;
    }

    public void createDict(final Config defaultConfig) throws IOException {
        final Config config =
                defaultConfig.systemDictionary(this.systemDictPath).addUserDictionary(this.userDictPath);
        LOGGER.info(" ### Created Sudachi config ###");

        this.dictionary = new DictionaryFactory().create(config);
        LOGGER.info(" ### Created Sudachi Dictionary using the factory ###");

        this.sudachiTokenizer = this.dictionary.create();
        LOGGER.info(" ### Created Sudachi Tokenizer using the Dictionary ###");

        this.sudachiAttribute.setDictionary(this.dictionary);

        LOGGER.info(" ### Sudachi Dictionary is null: " + (this.dictionary == null ? "true" : "false") + " ###");
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        MorphemeIterator sentenceMorphemeIterator = new SentenceMorphemeIterator(tokenize(input));
        if (discardPunctuation) {
            sentenceMorphemeIterator = new NonPunctuationMorphemes(sentenceMorphemeIterator);
        }
        this.morphemeIterator = sentenceMorphemeIterator;
        this.sudachiAttribute.setDictionary(this.dictionary);
    }

    @Override
    public void end() throws IOException {
        super.end();
        final int lastOffset = correctOffset(morphemeIterator.getBaseOffset());
        offsetAtt.setOffset(lastOffset, lastOffset);
        this.morphemeIterator = MorphemeIterator.EMPTY;
    }

    Iterator<MorphemeList> tokenize(final Reader inputReader) throws IOException {
        return this.sudachiTokenizer.tokenizeSentences(this.mode, inputReader).iterator();
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        final Morpheme morpheme = this.morphemeIterator.next();
        if (morpheme == null) {
            return false;
        }

        this.posIncAtt.setPositionIncrement(1);
        this.posLengthAtt.setPositionLength(1);
        this.morphemeAtt.setMorpheme(morpheme);
        final int baseOffset = morphemeIterator.getBaseOffset();
        final int startOffset = correctOffset(baseOffset + morpheme.begin());
        final int endOffset = correctOffset(baseOffset + morpheme.end());
        this.offsetAtt.setOffset(startOffset, endOffset);

        this.termAtt.append(morpheme.surface());

        return true;
    }
}
