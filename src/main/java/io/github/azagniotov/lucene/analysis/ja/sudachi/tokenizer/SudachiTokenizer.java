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
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SurfaceFormAttribute;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Iterator;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.util.AttributeFactory;

public final class SudachiTokenizer extends org.apache.lucene.analysis.Tokenizer {

    private final SplitMode mode;
    private MorphemeIterator morphemeIterator;
    private Tokenizer sudachiTokenizer;
    private final boolean discardPunctuation;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posIncAtt;

    private final SurfaceFormAttribute surfaceAtt;
    private final PositionLengthAttribute posLengthAtt;
    private final Path systemDictPath;
    private final Path userDictPath;

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

        this.surfaceAtt = addAttribute(SurfaceFormAttribute.class);
        this.offsetAtt = addAttribute(OffsetAttribute.class);
        this.posIncAtt = addAttribute(PositionIncrementAttribute.class);
        this.posLengthAtt = addAttribute(PositionLengthAttribute.class);
    }

    public void createDict() throws IOException {
        final Config config =
                Config.defaultConfig().systemDictionary(this.systemDictPath).addUserDictionary(this.userDictPath);

        final Dictionary dictionary = new DictionaryFactory().create(config);
        this.sudachiTokenizer = dictionary.create();
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        MorphemeIterator sentenceMorphemeIterator = new SentenceMorphemeIterator(tokenize(input));
        if (discardPunctuation) {
            sentenceMorphemeIterator = new NonPunctuationMorphemes(sentenceMorphemeIterator);
        }
        this.morphemeIterator = sentenceMorphemeIterator;
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

        setAttributes(morpheme);

        return true;
    }

    private void setAttributes(final Morpheme morpheme) throws IOException {
        posLengthAtt.setPositionLength(1);
        posIncAtt.setPositionIncrement(1);
        setMorphemeAttributes(morpheme);
    }

    private void setMorphemeAttributes(final Morpheme morpheme) throws IOException {
        this.surfaceAtt.setMorpheme(morpheme);
        final int baseOffset = morphemeIterator.getBaseOffset();
        this.offsetAtt.setOffset(
                correctOffset(baseOffset + morpheme.begin()), correctOffset(baseOffset + morpheme.end()));
    }
}
