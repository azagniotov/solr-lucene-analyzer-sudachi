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
package io.github.azagniotov.lucene.analysis.ja.sudachi.util;

import static com.google.common.truth.Truth.assertThat;

import java.util.regex.Pattern;
import org.junit.Test;

public class StringsTest {

    private static final Pattern WHITESPACE_REGEX = Pattern.compile("\\s+");

    @Test
    public void isJapanesePunctuation() throws Exception {
        // http://www.rikai.com/library/kanjitables/kanji_codes.unicode.shtml
        final String punctuation = "、 。 〃 〄  〈 〉 《 》 「 」 『 』 【 】 〒 〓 〔 〕 " + "〖 〗 〘 〙 〚 〛 〜 〝 〞 〟 〠";
        for (final String punt : WHITESPACE_REGEX.split(punctuation)) {
            assertThat(Strings.isPunctuation(punt.trim())).isTrue();
        }
    }

    @Test
    public void isAsciiPunctuation() throws Exception {
        // !"#$%&'()*+,-./:;<=>?@[\]^_`{ }~:
        final String punctuation = "@ / ! . , + - : ; [ ] { } ~ _ ( ) # $ % & ' \" * < > ? ^ ` \\";
        for (final String punt : WHITESPACE_REGEX.split(punctuation)) {
            assertThat(Strings.isPunctuation(punt.trim())).isTrue();
        }
    }

    @Test
    public void isLatinOnePunctuation() throws Exception {
        final String punctuation = "· ¶ »";
        for (final String punt : WHITESPACE_REGEX.split(punctuation)) {
            assertThat(Strings.isPunctuation(punt.trim())).isTrue();
        }
    }
}
