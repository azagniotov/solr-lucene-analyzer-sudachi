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

import com.worksap.nlp.sudachi.IOTools;
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
import java.io.StringReader;
import java.nio.CharBuffer;
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

    //
    // https://github.com/WorksApplications/Sudachi/issues/230 (OOM)
    // https://github.com/WorksApplications/elasticsearch-sudachi/issues/131
    // Fix ported from here: https://github.com/WorksApplications/elasticsearch-sudachi/pull/132
    //
    // In case of a huge text input text, the input data will be split into chunks for the tokenize
    // function.
    // The initial chunk size is INITIAL_CHUNK_SIZE, where MAX_CHUNK_SIZE is the maximum chunk size if
    // the input is large.
    private final int MAX_CHUNK_SIZE = 1024 * 1024; // 1 MiB
    private final int INITIAL_CHUNK_SIZE = 32 * 1024; // 32 KiB
    private final int CHUNK_GROW_SCALE = 8;

    private CharBuffer inputChunk;
    private int chunkCurrentOffset;
    private int chunkEndOffset;

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

        this.inputChunk = CharBuffer.allocate(INITIAL_CHUNK_SIZE);
        this.chunkCurrentOffset = 0;
        this.chunkEndOffset = 0;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.morphemeIterator = MorphemeIterator.EMPTY;
        this.chunkCurrentOffset = 0;
        this.chunkEndOffset = 0;
    }

    @Override
    public void end() throws IOException {
        super.end();
        final int lastOffset = correctOffset(chunkCurrentOffset + morphemeIterator.getBaseOffset());
        offsetAtt.setOffset(lastOffset, lastOffset);
        this.morphemeIterator = MorphemeIterator.EMPTY;
    }

    Iterator<MorphemeList> tokenize(final Reader inputReader) throws IOException {
        return this.sudachiTokenizer.tokenizeSentences(this.mode, inputReader).iterator();
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();

        Morpheme morpheme = this.morphemeIterator.next();
        if (morpheme == null) {
            if (!read()) {
                return false;
            }
            inputChunk.flip();

            final StringReader inputReader = new StringReader(inputChunk.toString());
            final Iterator<MorphemeList> morphemeListIterator = tokenize(inputReader);

            MorphemeIterator sentenceMorphemeIterator = new SentenceMorphemeIterator(morphemeListIterator);
            if (discardPunctuation) {
                sentenceMorphemeIterator = new NonPunctuationMorphemes(sentenceMorphemeIterator);
            }
            this.morphemeIterator = sentenceMorphemeIterator;
            this.chunkCurrentOffset = this.chunkEndOffset;

            morpheme = this.morphemeIterator.next();
            if (morpheme == null) {
                return false;
            }
        }

        final int baseOffset = morphemeIterator.getBaseOffset();
        final int morphemeCorrectedStartOffset = correctOffset(chunkCurrentOffset + baseOffset + morpheme.begin());
        final int morphemeCorrectedEndOffset = correctOffset(chunkCurrentOffset + baseOffset + morpheme.end());
        this.offsetAtt.setOffset(morphemeCorrectedStartOffset, morphemeCorrectedEndOffset);

        this.chunkEndOffset = this.chunkCurrentOffset + baseOffset + morpheme.end();

        this.morphemeAtt.setMorpheme(morpheme);

        // Start: setting the values for the field analysis screen/API
        this.posIncAtt.setPositionIncrement(1);
        this.posLengthAtt.setPositionLength(1);
        this.baseFormAtt.setMorpheme(morpheme);
        this.normalizedFormAtt.setMorpheme(morpheme);
        this.readingFormAtt.setMorpheme(morpheme);
        this.posAtt.setMorpheme(morpheme);
        // End: setting the values for the field analysis screen/API

        final String surface = morpheme.surface();
        this.termAtt.copyBuffer(surface.toCharArray(), 0, surface.length());

        return true;
    }

    private boolean read() throws IOException {
        inputChunk.clear();

        while (true) {
            final int nRead = IOTools.readAsMuchAsCan(input, inputChunk);
            if (nRead < 0) {
                return inputChunk.position() > 0;
            }

            // check: chunk reads all the data from Reader. No remaining data in Reader.
            if (inputChunk.hasRemaining()) {
                return true;
            }

            // check: chunk is already max size
            if (inputChunk.capacity() == MAX_CHUNK_SIZE) {
                return true;
            }

            growChunk();
        }
    }

    private void growChunk() {
        final int newChunkSize = Math.min(inputChunk.capacity() * CHUNK_GROW_SCALE, MAX_CHUNK_SIZE);
        final CharBuffer newChunk = CharBuffer.allocate(newChunkSize);
        inputChunk.flip();
        newChunk.put(inputChunk);

        inputChunk = newChunk;
    }
}
