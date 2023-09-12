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

import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.MorphemeList;
import java.util.Iterator;

class SentenceMorphemeIterator implements MorphemeIterator {

    private Iterator<Morpheme> morphemeIterator;
    private final Iterator<MorphemeList> sentenceIterator;
    private int baseOffset = 0;
    private int sentencesTotalLength = 0;

    SentenceMorphemeIterator(final Iterator<MorphemeList> sentenceIterator) {
        this.morphemeIterator = new EmptyIterator();
        this.sentenceIterator = sentenceIterator;
    }

    @Override
    public Morpheme next() {
        if (this.morphemeIterator.hasNext()) {
            final Morpheme morpheme = morphemeIterator.next();
            this.sentencesTotalLength = morpheme.end();
            return morpheme;
        } else {
            // The base offset is incremented again for the last time, since we
            // need to add the length of the current sentence that we just processed.
            // This value is used as final offset by the Tokenizer.end() method
            this.baseOffset += this.sentencesTotalLength;
            if (sentenceIterator.hasNext()) {
                this.morphemeIterator = sentenceIterator.next().iterator();
                return next();
            } else {
                return null;
            }
        }
    }

    @Override
    public int getBaseOffset() {
        return baseOffset;
    }

    private static class EmptyIterator implements Iterator<Morpheme> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Morpheme next() {
            throw new IllegalStateException("EmptyIterator is invoked");
        }
    }
}
