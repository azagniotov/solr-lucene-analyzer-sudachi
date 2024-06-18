package io.github.azagniotov.lucene.analysis.ja.sudachi.util;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TranslationsTest {

    @Test
    public void testPOS() {
        assertEquals("noun-suffix-person", Translations.forPos("名詞-接尾-人名"));
    }

    @Test
    public void testKunrei() {
        assertThat(Translations.toRomaji("マージャン")).isEqualTo("ma-jan");
        assertThat(Translations.toRomaji("ウーロンチャ")).isEqualTo("u-roncha");
        assertThat(Translations.toRomaji("チャーハン")).isEqualTo("cha-han");
        assertThat(Translations.toRomaji("チャーシュー")).isEqualTo("cha-shu-");
        assertThat(Translations.toRomaji("シューマイ")).isEqualTo("shu-mai");
        assertThat(Translations.toRomaji("アレックス")).isEqualTo("arekkusu");
    }
}
