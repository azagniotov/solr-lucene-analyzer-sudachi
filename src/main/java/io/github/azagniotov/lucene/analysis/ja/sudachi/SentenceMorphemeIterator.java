/*
 * Copyright (c) 2017-2023 Works Applications Co., Ltd.
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

import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.MorphemeList;
import java.util.Iterator;

public class SentenceMorphemeIterator implements MorphemeIterator {

    private Iterator<Morpheme> morphemeIterator;
    private final Iterator<MorphemeList> sentenceIterator;
    private int baseOffset = 0;
    private int currentLength = 0;

    public SentenceMorphemeIterator(final Iterator<MorphemeList> sentenceIterator) {
        this.morphemeIterator = new EmptyIterator();
        this.sentenceIterator = sentenceIterator;
    }

    @Override
    public Morpheme next() {
        final Iterator<Morpheme> currentMorphemeIterator = this.morphemeIterator;
        if (currentMorphemeIterator.hasNext()) {
            return currentMorphemeIterator.next();
        } else {
            this.baseOffset += this.currentLength;
            if (sentenceIterator.hasNext()) {
                final MorphemeList sentencesList = sentenceIterator.next();
                if (!sentenceIterator.hasNext()) {
                    currentLength = sentencesList.get(sentencesList.size() - 1).end();
                } else {
                    currentLength = 0;
                }
                this.morphemeIterator = sentencesList.iterator();
                // Try once more with a recursive call, which starts consuming
                // Morphemes from the newly assigned morpheme iterator
                return this.next();
            } else {
                currentLength = 0;
            }
        }

        return null;
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
