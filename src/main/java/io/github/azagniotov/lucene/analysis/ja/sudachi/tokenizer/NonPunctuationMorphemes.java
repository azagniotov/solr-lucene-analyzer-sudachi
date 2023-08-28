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
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.Strings;

class NonPunctuationMorphemes implements MorphemeIterator {
    private MorphemeIterator inner;

    NonPunctuationMorphemes(final MorphemeIterator inner) {
        this.inner = inner;
    }

    @Override
    public Morpheme next() {
        while (true) {
            final Morpheme next = inner.next();
            if (next == null) {
                return null;
            }
            if (!Strings.isPunctuation(next.normalizedForm())) {
                return next;
            }
        }
    }

    @Override
    public int getBaseOffset() {
        return inner.getBaseOffset();
    }
}
