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

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

public class MorphemeConsumerAttributeImpl extends AttributeImpl implements MorphemeConsumerAttribute {

    private Object consumer;

    @Override
    public Object getCurrentConsumer() {
        return consumer;
    }

    @Override
    public void setCurrentConsumer(Object consumer) {
        if (consumer != null) {
            this.consumer = consumer;
        }
    }

    @Override
    public void clear() {
        // Nothing to do here
    }

    @Override
    public void reflectWith(AttributeReflector attributeReflector) {
        attributeReflector.reflect(
                SudachiAttribute.class, "consumer", this.getClass().getName());
    }

    @Override
    public void copyTo(AttributeImpl attribute) {
        // Nothing to do here
    }
}
