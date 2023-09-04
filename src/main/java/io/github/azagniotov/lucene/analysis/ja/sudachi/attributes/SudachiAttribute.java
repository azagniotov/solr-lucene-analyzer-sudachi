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

import com.worksap.nlp.sudachi.Dictionary;
import org.apache.lucene.util.Attribute;

public interface SudachiAttribute extends Attribute {
    /**
     * Get current dictionary instance
     *
     * @return reloadable facade for the current dictionary
     */
    Dictionary getDictionary();

    /**
     * Set the current dictionary for the token stream. Use this method only if you
     * really know what you are doing.
     *
     * @param dictionary
     *            new instance of the dictionary
     */
    void setDictionary(Dictionary dictionary);
}
