/*
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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.tests.analysis.MockTokenizer;

public class ImportedLuceneJapaneseKatakanaStemFilterTest extends BaseTokenStreamTestCase {

    private Analyzer analyzer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                // Use a MockTokenizer here since this filter doesn't really depend on Kuromoji
                final Tokenizer source = new MockTokenizer(MockTokenizer.WHITESPACE, false);
                return new TokenStreamComponents(source, new SudachiKatakanaStemFilter(source));
            }
        };
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test a few common katakana spelling variations.
     *
     * <p>English translations are as follows:
     *
     * <ul>
     *   <li>copy
     *   <li>coffee
     *   <li>taxi
     *   <li>party
     *   <li>party (without long sound)
     *   <li>center
     * </ul>
     *
     * <p>Note that we remove a long sound in the case of "coffee" that is required.
     */
    public void testStemVariants() throws Exception {
        assertAnalyzesTo(
                analyzer,
                "コピー コーヒー タクシー パーティー パーティ センター",
                new String[] {"コピー", "コーヒ", "タクシ", "パーティ", "パーティ", "センタ"},
                new int[] {0, 4, 9, 14, 20, 25},
                new int[] {3, 8, 13, 19, 24, 29});
    }

    public void testKeyword() throws Exception {
        final CharArraySet exclusionSet = new CharArraySet(asSet("コーヒー"), false);
        Analyzer a = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer source = new MockTokenizer(MockTokenizer.WHITESPACE, false);
                TokenStream sink = new SetKeywordMarkerFilter(source, exclusionSet);
                return new TokenStreamComponents(source, new SudachiKatakanaStemFilter(sink));
            }
        };
        checkOneTerm(a, "コーヒー", "コーヒー");
        a.close();
    }

    public void testUnsupportedHalfWidthVariants() throws Exception {
        // The below result is expected since only full-width katakana is supported
        assertAnalyzesTo(analyzer, "ﾀｸｼｰ", new String[] {"ﾀｸｼｰ"});
    }

    public void testRandomData() throws Exception {
        checkRandomData(random(), analyzer, 200 * RANDOM_MULTIPLIER);
    }

    public void testEmptyTerm() throws Exception {
        Analyzer a = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tokenizer = new KeywordTokenizer();
                return new TokenStreamComponents(tokenizer, new SudachiKatakanaStemFilter(tokenizer));
            }
        };
        checkOneTerm(a, "", "");
        a.close();
    }
}
