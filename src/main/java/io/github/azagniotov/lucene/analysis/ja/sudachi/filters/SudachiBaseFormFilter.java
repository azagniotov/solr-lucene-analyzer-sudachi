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
package io.github.azagniotov.lucene.analysis.ja.sudachi.filters;

import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.MorphemeConsumerAttribute;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiBaseFormAttribute;
import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class SudachiBaseFormFilter extends TokenFilter {
    private final CharTermAttribute termAtt;
    private final SudachiBaseFormAttribute baseFormAtt;
    private final MorphemeConsumerAttribute morphemeConsumerAttribute;

    public SudachiBaseFormFilter(TokenStream input) {
        super(input);
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.baseFormAtt = addAttribute(SudachiBaseFormAttribute.class);
        this.morphemeConsumerAttribute = addAttribute(MorphemeConsumerAttribute.class);
        this.morphemeConsumerAttribute.setCurrentConsumer(this);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {

            if (morphemeConsumerAttribute.shouldConsume(this)) {
                if (baseFormAtt != null
                        && baseFormAtt.getDictionaryForm() != null
                        && !baseFormAtt.getDictionaryForm().isEmpty()) {
                    termAtt.setEmpty().append(baseFormAtt.getDictionaryForm());
                }
            }
            return true;
        }
        return false;
    }
}
