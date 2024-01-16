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
package io.github.azagniotov.lucene.analysis.ja.sudachi.cache;

import com.worksap.nlp.sudachi.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum DictionaryCache {
    INSTANCE;

    private static final String CACHE_KEY = "dictionary";

    private final Map<String, Dictionary> dictionaryCache;

    DictionaryCache() {
        dictionaryCache = new ConcurrentHashMap<>(1);
    }

    public boolean isEmpty() {
        return !this.dictionaryCache.containsKey(CACHE_KEY);
    }

    public void cache(final Dictionary dictionary) {
        if (!this.dictionaryCache.containsKey(CACHE_KEY)) {
            this.dictionaryCache.put(CACHE_KEY, dictionary);
        }
    }

    public Dictionary get() {
        return this.dictionaryCache.get(CACHE_KEY);
    }

    public void invalidate() {
        this.dictionaryCache.clear();
    }
}
