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

import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.MorphemeList;
import com.worksap.nlp.sudachi.Tokenizer;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiBaseFormAttribute;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiMorphemeAttribute;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiNormalizedFormAttribute;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiPartOfSpeechAttribute;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiReadingFormAttribute;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.util.AttributeFactory;

public final class SudachiTokenizer extends org.apache.lucene.analysis.Tokenizer {

    private MorphemeIterator morphemeIterator;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionLengthAttribute posLengthAtt;
    private final PositionIncrementAttribute posIncAtt;
    private final SudachiMorphemeAttribute morphemeAtt;
    private final SudachiPartOfSpeechAttribute posAtt;
    private final SudachiBaseFormAttribute baseFormAtt;
    private final SudachiNormalizedFormAttribute normalizedFormAtt;
    private final SudachiReadingFormAttribute readingFormAtt;
    private Tokenizer sudachiTokenizer;
    private final boolean discardPunctuation;
    private final SplitMode mode;

    public SudachiTokenizer(final Tokenizer sudachiTokenizer, final boolean discardPunctuation, final SplitMode mode) {
        this(DEFAULT_TOKEN_ATTRIBUTE_FACTORY, sudachiTokenizer, discardPunctuation, mode);
    }

    public SudachiTokenizer(
            final AttributeFactory factory,
            final Tokenizer sudachiTokenizer,
            final boolean discardPunctuation,
            final SplitMode mode) {
        super(factory);
        this.sudachiTokenizer = sudachiTokenizer;
        this.discardPunctuation = discardPunctuation;
        this.mode = mode;

        this.termAtt = addAttribute(CharTermAttribute.class);
        this.offsetAtt = addAttribute(OffsetAttribute.class);
        this.morphemeAtt = addAttribute(SudachiMorphemeAttribute.class);
        // Start: attributes holding the morphological values for the field analysis screen/API
        this.posIncAtt = addAttribute(PositionIncrementAttribute.class);
        this.posLengthAtt = addAttribute(PositionLengthAttribute.class);
        this.baseFormAtt = addAttribute(SudachiBaseFormAttribute.class);
        this.normalizedFormAtt = addAttribute(SudachiNormalizedFormAttribute.class);
        this.readingFormAtt = addAttribute(SudachiReadingFormAttribute.class);
        this.posAtt = addAttribute(SudachiPartOfSpeechAttribute.class);
        // End: attributes holding the morphological values for the field analysis screen/API

        this.morphemeIterator = MorphemeIterator.EMPTY;
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

        this.morphemeAtt.setMorpheme(morpheme);

        // Start: setting the values for the field analysis screen/API
        this.posIncAtt.setPositionIncrement(1);
        this.posLengthAtt.setPositionLength(1);
        this.baseFormAtt.setMorpheme(morpheme);
        this.normalizedFormAtt.setMorpheme(morpheme);
        this.readingFormAtt.setMorpheme(morpheme);
        this.posAtt.setMorpheme(morpheme);
        // End: setting the values for the field analysis screen/API

        final int baseOffset = morphemeIterator.getBaseOffset();
        final int startOffset = correctOffset(baseOffset + morpheme.begin());
        final int endOffset = correctOffset(baseOffset + morpheme.end());
        this.offsetAtt.setOffset(startOffset, endOffset);

        this.termAtt.append(morpheme.surface());

        return true;
    }
}
