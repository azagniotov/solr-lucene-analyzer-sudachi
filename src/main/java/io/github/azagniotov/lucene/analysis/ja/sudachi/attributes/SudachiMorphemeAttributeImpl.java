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
package io.github.azagniotov.lucene.analysis.ja.sudachi.attributes;

import com.worksap.nlp.sudachi.Morpheme;
import java.util.Optional;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class SudachiMorphemeAttributeImpl extends AttributeImpl implements SudachiMorphemeAttribute {

    private Morpheme morpheme;

    @Override
    public Optional<Morpheme> getValue() {
        return this.morpheme == null ? Optional.empty() : Optional.of(morpheme);
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
        //
        // The following code:
        // attributeReflector.reflect(SudachiMorphemeAttribute.class, "morpheme", getMorpheme());
        //
        // is commented out because of:
        // 1. We do not need to reflect on Morpheme object implementation, it is not needed for the
        // above.
        // 2. The com.worksap.nlp.sudachi.MorphemeImpl has package default visibility. Solr throws
        // because of that:
        //    Caused by: java.lang.IllegalAccessException: access violation: class
        // com.worksap.nlp.sudachi.MorphemeImpl,
        // from public Lookup
        //      at java.lang.invoke.MethodHandles$Lookup.makeAccessException(Unknown Source) ~[?:?]
        //      at java.lang.invoke.MethodHandles$Lookup.accessClass(Unknown Source) ~[?:?]
        //      at org.apache.solr.common.util.Utils.addTraditionalFieldWriters(Utils.java:953) ~[?:?]
        //      at org.apache.solr.common.util.Utils.getReflectData(Utils.java:912) ~[?:?]
        //      at org.apache.solr.common.util.Utils.getReflectWriter(Utils.java:847) ~[?:?]
        //      ... 81 more
    }

    @Override
    public void copyTo(AttributeImpl attribute) {
        final SudachiMorphemeAttribute at = (SudachiMorphemeAttribute) attribute;
        at.setMorpheme(morpheme);
    }
}
