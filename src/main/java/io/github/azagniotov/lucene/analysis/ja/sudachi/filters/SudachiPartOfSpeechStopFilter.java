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

import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.PosMatcher;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiMorphemeAttribute;
import java.io.IOException;
import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenStream;

public class SudachiPartOfSpeechStopFilter extends FilteringTokenFilter {

    private final PosMatcher posMatcher;
    private final SudachiMorphemeAttribute morphemeAtt = addAttribute(SudachiMorphemeAttribute.class);

    public SudachiPartOfSpeechStopFilter(final TokenStream tokenStream, final PosMatcher posMatcher) {
        super(tokenStream);
        this.posMatcher = posMatcher;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
    }

    @Override
    protected boolean accept() throws IOException {
        final Morpheme morpheme = this.morphemeAtt.getMorpheme();

        boolean currentTokenPoSMatchesDefinedPartOfSpeechTags = posMatcher.test(this.morphemeAtt.getMorpheme());

        // Any token with a part-of-speech tag that exactly matches those
        // defined in the stoptags.txt file are removed from the token stream.
        return morpheme == null || !currentTokenPoSMatchesDefinedPartOfSpeechTags;
    }
}
