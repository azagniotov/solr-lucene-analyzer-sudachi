/*
 * Copyright (c) 2024 Apache Software Foundation (ASF).
 * Modifications copyright (c) 2024 Alexander Zagniotov
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.azagniotov.lucene.analysis.ja.sudachi.filters;

import io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer.SudachiTokenizerFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.NoOpResourceLoader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.tests.analysis.MockTokenizer;
import org.apache.lucene.util.AttributeFactory;

/** Tests for {@link SudachiJapaneseKatakanaUppercaseFilter} */
public class ImportedLuceneJapaneseKatakanaUppercaseFilterTest extends BaseTokenStreamTestCase {
    private Analyzer keywordAnalyzer, analyzerOnlyTokenizes;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        keywordAnalyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tokenizer = new MockTokenizer(MockTokenizer.WHITESPACE, false);
                return new TokenStreamComponents(tokenizer, new SudachiJapaneseKatakanaUppercaseFilter(tokenizer));
            }
        };
        analyzerOnlyTokenizes = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                try {
                    final Tokenizer tokenizer = createTokenizer(new HashMap<>());
                    return new TokenStreamComponents(tokenizer, new SudachiJapaneseKatakanaUppercaseFilter(tokenizer));

                } catch (IOException iox) {
                    throw new UncheckedIOException(iox);
                }
            }
        };
    }

    @Override
    public void tearDown() throws Exception {
        keywordAnalyzer.close();
        analyzerOnlyTokenizes.close();
        super.tearDown();
    }

    public void testKanaUppercase() throws IOException {
        assertAnalyzesTo(
                keywordAnalyzer, "ァィゥェォヵㇰヶㇱㇲッㇳㇴㇵㇶㇷㇷ゚ㇸㇹㇺャュョㇻㇼㇽㇾㇿヮ", new String[] {"アイウエオカクケシスツトヌハヒフプヘホムヤユヨラリルレロワ"});
        assertAnalyzesTo(keywordAnalyzer, "ストップウォッチ", new String[] {"ストツプウオツチ"});
        assertAnalyzesTo(keywordAnalyzer, "サラニㇷ゚ カムイチェㇷ゚ ㇷ゚ㇷ゚", new String[] {"サラニプ", "カムイチエプ", "ププ"});
    }

    public void testKanaUppercaseWithSurrogatePair() throws IOException {
        // 𠀋 : \uD840\uDC0B
        assertAnalyzesTo(
                keywordAnalyzer,
                "\uD840\uDC0Bストップウォッチ ストップ\uD840\uDC0Bウォッチ ストップウォッチ\uD840\uDC0B",
                new String[] {"\uD840\uDC0Bストツプウオツチ", "ストツプ\uD840\uDC0Bウオツチ", "ストツプウオツチ\uD840\uDC0B"});
    }

    public void testKanaUppercaseWithJapaneseTokenizer() throws IOException {
        assertAnalyzesTo(analyzerOnlyTokenizes, "時間をストップウォッチで測る", new String[] {"時間", "を", "ストツプウオツチ", "で", "測る"});
    }

    public void testUnsupportedHalfWidthVariants() throws IOException {
        // The below result is expected since only full-width katakana is supported
        assertAnalyzesTo(keywordAnalyzer, "ｽﾄｯﾌﾟｳｫｯﾁ", new String[] {"ｽﾄｯﾌﾟｳｫｯﾁ"});
    }

    public void testRandomData() throws IOException {
        checkRandomData(random(), keywordAnalyzer, 200 * RANDOM_MULTIPLIER);
    }

    public void testEmptyTerm() throws IOException {
        assertAnalyzesTo(keywordAnalyzer, "", new String[] {});
    }

    private Tokenizer createTokenizer(final Map<String, String> args) throws IOException {

        final Map<String, String> map = new HashMap<>(args);
        map.put("mode", "search");
        map.put("discardPunctuation", "true");
        final SudachiTokenizerFactory factory = new SudachiTokenizerFactory(map);
        factory.inform(new NoOpResourceLoader());

        return factory.create(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
    }
}
