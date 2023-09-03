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

public interface MorphemeIterator {

    MorphemeIterator EMPTY = new MorphemeIterator() {

        @Override
        public Morpheme next() {
            return null;
        }

        @Override
        public int getBaseOffset() {
            return 0;
        }
    };

    Morpheme next();

    int getBaseOffset();
}
