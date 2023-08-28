package io.github.azagniotov.lucene.analysis;


import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class JapaneseTokenizerTest {

    private static final URL systemDictResource;
    static {
        systemDictResource = JapaneseTokenizerTest.class.getResource("/system-dict/system_core.dic");
    }

    private static JapaneseTokenizer japaneseTokenizer;

    @BeforeClass
    public static void beforeClass() throws Exception {
        japaneseTokenizer = JapaneseTokenizer.fromSystemDict(systemDictResource.getPath());
    }

    @Test
    public void sanityCheck() {
        assertThat(systemDictResource).isNotNull();
        assertThat(japaneseTokenizer).isNotNull();
    }

    @Test
    public void tokenizeKyoto() throws Exception {
        final List<String> tokens = japaneseTokenizer.tokenize("京都。東京.東京都。京都");

        assertThat(tokens).containsExactly("alex");
    }
}