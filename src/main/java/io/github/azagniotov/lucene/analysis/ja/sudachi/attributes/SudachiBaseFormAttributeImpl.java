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
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class SudachiBaseFormAttributeImpl extends AttributeImpl implements SudachiBaseFormAttribute {

    private Morpheme morpheme;

    @Override
    public String getBaseForm() {
        return morpheme.dictionaryForm();
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

        attributeReflector.reflect(SudachiBaseFormAttribute.class, "baseForm", getBaseForm());
    }

    @Override
    public void copyTo(AttributeImpl attribute) {
        final SudachiBaseFormAttribute at = (SudachiBaseFormAttribute) attribute;
        at.setMorpheme(morpheme);
    }
}