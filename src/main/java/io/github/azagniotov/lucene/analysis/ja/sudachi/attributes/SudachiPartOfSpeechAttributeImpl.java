/*
 * Copyright (c) 2023-2024 Alexander Zagniotov
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
package io.github.azagniotov.lucene.analysis.ja.sudachi.attributes;

import com.worksap.nlp.sudachi.Morpheme;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.LuceneKuromojiStringUtils;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class SudachiPartOfSpeechAttributeImpl extends AttributeImpl implements SudachiPartOfSpeechAttribute {

    private Morpheme morpheme;

    @Override
    public String getPartOfSpeech() {
        if (morpheme.partOfSpeech().isEmpty()) {
            return null;
        } else {
            final StringBuilder posBuilder = new StringBuilder();
            posBuilder.append(morpheme.partOfSpeech().get(0));

            // See io.github.azagniotov.lucene.analysis.ja.sudachi.util.LuceneKuromojiStringUtils
            // This makes it a little more compatible with the Lucene Kuromoji behavior
            final String secondPos = morpheme.partOfSpeech().get(1);
            if (!secondPos.equals("*")) {
                posBuilder.append("-");
                posBuilder.append(secondPos);
            }
            return posBuilder.toString();
        }
    }

    @Override
    public void setMorpheme(final Morpheme morpheme) {
        this.morpheme = morpheme;
    }

    @Override
    public void clear() {
        morpheme = null;
    }

    @Override
    public void reflectWith(AttributeReflector attributeReflector) {
        // AttributeReflector is used by Solr and Elasticsearch to provide analysis output.

        final String partOfSpeech = getPartOfSpeech();
        if (partOfSpeech != null) {
            final String translation = LuceneKuromojiStringUtils.getPOSTranslation(partOfSpeech);
            final String partOfSpeechEN = translation == null ? "n/a" : translation;

            attributeReflector.reflect(SudachiPartOfSpeechAttribute.class, "partOfSpeech (ja)", partOfSpeech);
            attributeReflector.reflect(SudachiPartOfSpeechAttribute.class, "partOfSpeech (en)", partOfSpeechEN);
        } else {
            attributeReflector.reflect(SudachiPartOfSpeechAttribute.class, "partOfSpeech (ja)", "n/a");
        }
    }

    @Override
    public void copyTo(AttributeImpl attribute) {
        final SudachiPartOfSpeechAttribute at = (SudachiPartOfSpeechAttribute) attribute;
        at.setMorpheme(morpheme);
    }
}
