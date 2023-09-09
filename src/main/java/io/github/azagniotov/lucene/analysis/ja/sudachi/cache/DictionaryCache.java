package io.github.azagniotov.lucene.analysis.ja.sudachi.cache;

import com.worksap.nlp.sudachi.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum DictionaryCache {
    INSTANCE;

    private static final String CACHE_KEY = "dictionary";
    public static final String SYSTEM_DICT_LOCAL_PATH = "/tmp/sudachi/system-dict/system_core.dic";
    public static final String USER_DICT_LOCAL_PATH = "/tmp/sudachi/user_lexicon.dic";

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
