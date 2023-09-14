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

package io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer;

import static com.worksap.nlp.sudachi.Tokenizer.SplitMode;

import com.worksap.nlp.sudachi.Config;
import io.github.azagniotov.lucene.analysis.ja.sudachi.test.TestUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.junit.Before;

public class ImportedLuceneJapaneseTokenizerTest extends BaseTokenStreamTestCase {

    private TestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.testUtils = new TestUtils(Config.defaultConfig());
    }

    public void testDecomposition_v1() throws Exception {
        final String input = "本来は、貧困層の女性や子供に医療保護を提供するために創設された制度である、アメリカ低所得者医療援助制度が、今日では、その予算の約３分の１を老人に費やしている。";
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.A);

        assertTokenStreamContents(
                tokenStream,
                new String[] {
                    "本来", "は", "貧困", "層", "の", "女性", "や", "子供", "に", "医療", "保護", "を", "提供", "する", "ため", "に", "創設", "さ",
                    "れ", "た", "制度", "で", "ある", "アメリカ", "低", "所得", "者", "医療", "援助", "制度", "が", "今日", "で", "は", "その",
                    "予算", "の", "約", "３", "分", "の", "１", "を", "老人", "に", "費やし", "て", "いる"
                },
                new int[] {
                    0, 2, 4, 6, 7, 8, 10, 11, 13, 14, 16, 18, 19, 21, 23, 25, 26, 28, 29, 30, 31, 33, 34, 37, 41, 42,
                    44, 45, 47, 49, 51, 53, 55, 56, 58, 60, 62, 63, 64, 65, 66, 67, 68, 69, 71, 72, 75, 76
                },
                new int[] {
                    2, 3, 6, 7, 8, 10, 11, 13, 14, 16, 18, 19, 21, 23, 25, 26, 28, 29, 30, 31, 33, 34, 36, 41, 42, 44,
                    45, 47, 49, 51, 52, 55, 56, 57, 60, 62, 63, 64, 65, 66, 67, 68, 69, 71, 72, 75, 76, 78
                });
    }

    public void testDecomposition_v2() throws Exception {
        final String input = "麻薬の密売は根こそぎ絶やさなければならない";
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.A);

        assertTokenStreamContents(
                tokenStream,
                new String[] {"麻薬", "の", "密売", "は", "根こそぎ", "絶やさ", "なけれ", "ば", "なら", "ない"},
                new int[] {0, 2, 3, 5, 6, 10, 13, 16, 17, 19},
                new int[] {2, 3, 5, 6, 10, 13, 16, 17, 19, 21});
    }

    public void testDecomposition_v3() throws Exception {
        final String input = "くよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよ";
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.A);

        assertTokenStreamContents(
                tokenStream,
                new String[] {"くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ"},
                new int[] {0, 4, 8, 12, 16, 20, 24, 28, 32, 36},
                new int[] {4, 8, 12, 16, 20, 24, 28, 32, 36, 40});
    }

    public void testSurrogates() throws Exception {
        final String input = "𩬅艱鍟䇹愯瀛";
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.A);
        assertTokenStreamContents(tokenStream, new String[] {"𩬅", "艱", "鍟䇹", "愯瀛"});
    }

    // The stop words will be filtered out by the Analyzer anyway
    public void testStopWords() throws Exception {
        final String input = "これは本ではない    ";
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.A);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"これ", "は", "本", "で", "は", "ない"},
                new int[] {0, 2, 3, 4, 5, 6, 8},
                new int[] {2, 3, 4, 5, 6, 8, 9},
                12);
    }
}
