/*
 * Copyright (c) 2023 Alexander Zagniotov
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

import static com.google.common.truth.Truth.assertThat;
import static org.apache.lucene.analysis.TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY;

import com.worksap.nlp.sudachi.JapaneseDictionary;
import io.github.azagniotov.lucene.analysis.ja.sudachi.cache.DictionaryCache;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.NoOpResourceLoader;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Test;

public class SudachiTokenizerFactoryTest {

    @Test
    public void sudachiTokenizerCreated() throws Exception {

        final Map<String, String> args = new HashMap<>() {
            {
                put("mode", "search");
                put("discardPunctuation", "true");
            }
        };
        final SudachiTokenizerFactory sudachiTokenizerFactory = new SudachiTokenizerFactory(args);
        sudachiTokenizerFactory.inform(new NoOpResourceLoader());
        final SudachiTokenizer sudachiTokenizer =
                (SudachiTokenizer) sudachiTokenizerFactory.create(DEFAULT_TOKEN_ATTRIBUTE_FACTORY);

        assertThat(sudachiTokenizer).isNotNull();
        assertThat(DictionaryCache.INSTANCE.get()).isNotNull();
        assertThat(DictionaryCache.INSTANCE.get()).isInstanceOf(JapaneseDictionary.class);
    }
}
