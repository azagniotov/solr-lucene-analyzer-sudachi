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

import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiBaseFormAttribute;
import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

/**
 * Replaces term text with the value of {@link com.worksap.nlp.sudachi.Morpheme#dictionaryForm()}.
 *
 * <p>This acts as a lemmatizer for verbs and adjectives.
 *
 * <p>To prevent terms from being stemmed use an instance of {@link org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter}
 * or a custom {@link TokenFilter} that sets the {@link KeywordAttribute} before this {@link TokenStream}.
 */
public final class SudachiBaseFormFilter extends TokenFilter {

    private final CharTermAttribute termAtt;
    private final SudachiBaseFormAttribute baseFormAtt;
    private final KeywordAttribute keywordAtt;

    public SudachiBaseFormFilter(TokenStream input) {
        super(input);
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.baseFormAtt = addAttribute(SudachiBaseFormAttribute.class);
        this.keywordAtt = addAttribute(KeywordAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            if (!keywordAtt.isKeyword()) {
                baseFormAtt.getValue().ifPresent(value -> termAtt.setEmpty().append(value));
            }
            return true;
        } else {
            return false;
        }
    }
}
