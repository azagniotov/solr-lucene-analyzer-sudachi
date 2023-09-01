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

package io.github.azagniotov.lucene.analysis.ja.sudachi;

import static com.worksap.nlp.sudachi.Tokenizer.SplitMode;

import com.codahale.metrics.Gauge;
import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.MorphemeList;
import com.worksap.nlp.sudachi.Tokenizer;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.Strings;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.util.AttributeFactory;

public final class SudachiTokenizer extends org.apache.lucene.analysis.Tokenizer {

    private final SplitMode mode;
    private Tokenizer sudachiTokenizer;
    private final boolean discardPunctuation;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final PositionIncrementAttribute posIncAtt;

    private final SurfaceFormAttribute surfaceAtt;
    private final PositionLengthAttribute posLengthAtt;
    private final Path systemDictPath;
    private final Path userDictPath;

    private int baseOffset = 0;

    private Iterator<Morpheme> iterator;

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
        this.baseOffset = 0;
        this.iterator = null;

        this.iterator = tokenize(input);
    }

    @Override
    public void end() throws IOException {
        super.end();
        final int lastOffset = correctOffset(this.baseOffset);
        this.offsetAtt.setOffset(lastOffset, lastOffset);
        this.iterator = null;
    }

    Iterator<Morpheme> tokenize(final Reader inputReader) throws IOException {
        final Iterable<MorphemeList> sentenceList = this.sudachiTokenizer.tokenizeSentences(this.mode, inputReader);

        final Stream<Morpheme> morphemeIterable = concat(sentenceList);
        if (discardPunctuation) {
            return morphemeIterable
                    .filter(morpheme -> !Strings.isPunctuation(morpheme.normalizedForm()))
                    .filter(morpheme -> !morpheme.surface().isEmpty())
                    .iterator();
        } else {
            return morphemeIterable
                    .filter(morpheme -> !morpheme.surface().isEmpty())
                    .iterator();
        }
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        if (this.iterator == null || !this.iterator.hasNext()) {
            return false;
        }

        final Morpheme morpheme = this.iterator.next();
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
        this.offsetAtt.setOffset(
                correctOffset(baseOffset + morpheme.begin()), correctOffset(baseOffset + morpheme.end()));
        this.termAtt.append(morpheme.surface());
        this.baseOffset += morpheme.end();
    }

    private static <T> Stream<T> concat(Iterable<? extends Iterable<T>> foo) {
        Gauge<Stream<T>> streamGauge = () -> StreamSupport.stream(foo.spliterator(), false)
                .flatMap(i -> StreamSupport.stream(i.spliterator(), false));

        return streamGauge.getValue();
    }
}
