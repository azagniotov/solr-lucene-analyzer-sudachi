/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.
 * Modifications copyright (c) 2023 Alexander Zagniotov
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file to You under the
 * Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.azagniotov.lucene.analysis.ja.sudachi.analyzer;

import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiBaseFormFilterFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiKatakanaStemFilter;
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiPartOfSpeechStopFilterFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer.SudachiTokenizerFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.NoOpResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.util.AttributeFactory;
import org.junit.Test;

public class SudachiAnalyzerTest extends BaseTokenStreamTestCase {

    private Analyzer analyzer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        analyzer = new SudachiAnalyzer(
                SudachiAnalyzer.getDefaultStopSet(), SudachiAnalyzer.getDefaultStopTags(), true, "search");
    }

    @Override
    public void tearDown() throws Exception {
        analyzer.close();
        super.tearDown();
    }

    @Test
    public void testLargeTextLoadTestWithUnfilteredStopWords() throws Exception {
        final Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(final String fieldName) {
                try {
                    Tokenizer tokenizer = createTokenizer(new HashMap<>());
                    TokenStream stream = tokenizer;

                    stream = new SudachiBaseFormFilterFactory(new HashMap<>()).create(stream);
                    stream = new SudachiPartOfSpeechStopFilterFactory(new HashMap<>()).create(stream);
                    stream = new StopFilter(stream, new CharArraySet(16, true));
                    stream = new SudachiKatakanaStemFilter(stream);
                    stream = new LowerCaseFilter(stream);
                    return new TokenStreamComponents(tokenizer, stream);
                } catch (IOException iox) {
                    throw new UncheckedIOException(iox);
                }
            }
        };

        assertNotNull(analyzer);

        final InputStream textInputStream = this.getClass().getResourceAsStream("/large.japanese.text.txt");
        final String japanese = new Scanner(textInputStream).useDelimiter("\\A").next();

        final TokenStream tokenStream = analyzer.tokenStream("any", japanese);

        final InputStream termsInputStream =
                this.getClass().getResourceAsStream("/large.japanese.text.unfiltered.terms.txt");
        final String terms = new Scanner(termsInputStream).useDelimiter("\\A").next();
        final String[] strings = terms.split("\\n");

        assertEquals(778, strings.length);
        assertTokenStreamContents(tokenStream, strings);
    }

    @Test
    public void testLargeTextLoadTestWithFilteredOutStopWords() throws Exception {
        final Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(final String fieldName) {
                try {
                    Tokenizer tokenizer = createTokenizer(new HashMap<>());
                    TokenStream stream = tokenizer;

                    stream = new SudachiBaseFormFilterFactory(new HashMap<>()).create(stream);
                    stream = new SudachiPartOfSpeechStopFilterFactory(new HashMap<>()).create(stream);
                    stream = new StopFilter(stream, SudachiAnalyzer.getDefaultStopSet());
                    stream = new SudachiKatakanaStemFilter(stream);
                    stream = new LowerCaseFilter(stream);
                    return new TokenStreamComponents(tokenizer, stream);
                } catch (IOException iox) {
                    throw new UncheckedIOException(iox);
                }
            }
        };

        assertNotNull(analyzer);

        final InputStream textInputStream = this.getClass().getResourceAsStream("/large.japanese.text.txt");
        final String japanese = new Scanner(textInputStream).useDelimiter("\\A").next();

        final TokenStream tokenStream = analyzer.tokenStream("any", japanese);

        final InputStream termsInputStream =
                this.getClass().getResourceAsStream("/large.japanese.text.filtered.terms.txt");
        final String terms = new Scanner(termsInputStream).useDelimiter("\\A").next();
        final String[] strings = terms.split("\\n");

        assertEquals(610, strings.length);
        assertTokenStreamContents(tokenStream, strings);
    }

    @Test
    public void testSimulateSolrAnalyzerCreation() throws Exception {
        final Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(final String fieldName) {
                try {
                    Tokenizer tokenizer = createTokenizer(new HashMap<>());
                    TokenStream stream = tokenizer;

                    stream = new SudachiBaseFormFilterFactory(new HashMap<>()).create(stream);
                    stream = new SudachiPartOfSpeechStopFilterFactory(new HashMap<>()).create(stream);
                    stream = new StopFilter(stream, new CharArraySet(16, true));
                    stream = new SudachiKatakanaStemFilter(stream);
                    stream = new LowerCaseFilter(stream);
                    return new TokenStreamComponents(tokenizer, stream);
                } catch (IOException iox) {
                    throw new UncheckedIOException(iox);
                }
            }
        };

        assertNotNull(analyzer);

        final TokenStream tokenStream = analyzer.tokenStream("any", "すもももももももものうち。");
        // 'Full' dictionary by Sudachi does not split this properly to すもも and もも
        assertTokenStreamContents(tokenStream, new String[] {"すもももももも", "もも", "うち"});
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
     * <p>
     * Note that we remove a long sound in the case of "coffee" that is required.
     * Also, "copy" (コピー) should not be stemmed.
     */
    @Test
    public void testKatakanaStemming() throws Exception {
        final Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(final String fieldName) {
                try {
                    final Tokenizer tokenizer = createTokenizer(new HashMap<>());
                    TokenStream stream = tokenizer;

                    stream = new CJKWidthFilter(stream);
                    stream = new SudachiKatakanaStemFilter(stream);
                    return new TokenStreamComponents(tokenizer, stream);
                } catch (IOException iox) {
                    throw new UncheckedIOException(iox);
                }
            }
        };

        assertNotNull(analyzer);

        final TokenStream tokenStream = analyzer.tokenStream("any", "コピー コーヒー ｺｰﾋｰ タクシー ﾀｸｼｰ パーティー パーティ ｾﾝﾀｰ センター");
        assertTokenStreamContents(
                tokenStream, new String[] {"コピー", "コーヒ", "コーヒ", "タクシ", "タクシ", "パーティ", "パーティ", "センタ", "センタ"});
    }

    @Test
    public void testRepeatedHiraganaWord() throws Exception {
        final int limit = 500;
        final String hiraganaWord = "くよくよ";

        final StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[limit]).replace("\0", hiraganaWord));

        final List<String> nCopies = Collections.nCopies(limit, hiraganaWord);

        assertAnalyzesTo(analyzer, sb.toString(), nCopies.toArray(new String[0]));
    }

    @Test
    public void testRepeatedKatakanaWord() throws Exception {
        // When MeCabOovProviderPlugin is in use, a repeated Katakana word string does not get tokenized
        // https://github.com/WorksApplications/Sudachi/issues/216

        final int limit = 8;
        final String katakanaWord = "テスト";

        final StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[limit]).replace("\0", katakanaWord));

        final List<String> nCopies = Collections.nCopies(limit, katakanaWord);
        assertAnalyzesTo(analyzer, sb.toString(), nCopies.toArray(new String[0]));
    }

    @Test
    public void testRepeatedKanjiWord() throws Exception {
        final int limit = 500;
        final String kanjiWord = "令和";

        final StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[limit]).replace("\0", kanjiWord));

        final List<String> nCopies = Collections.nCopies(limit, kanjiWord);
        assertAnalyzesTo(analyzer, sb.toString(), nCopies.toArray(new String[0]));
    }

    @Test
    public void testDecomposition() throws IOException {
        //
        // Please keep in mind that:
        //
        // 1. The stop words うち, も, は, の, です, を,　ある, ます etc. are removed.
        // 2. The conjugated verbs are returned in their base/dictionary form.
        // 3. Lower case filter is applied
        // 4. Full-width Japanese Katakana (supports A-Z) are converted to Latin characters
        //

        // 'Full' dictionary by Sudachi does not split this properly to すもも and もも
        assertAnalyzesTo(analyzer, "すもももももももものうち。", new String[] {"すもももももも", "もも"});
        assertAnalyzesTo(analyzer, "エーービ〜〜〜シ〰〰〰〰", new String[] {"エービーシ"});
        assertAnalyzesTo(analyzer, "シュミレーション", new String[] {"シュミレーション"});
        assertAnalyzesTo(analyzer, "ちゃあ", new String[] {}); // Result ちゃあ => だ got filtered out due to stopwords.txt
        assertAnalyzesTo(analyzer, "打ち込む", new String[] {"打つ", "込む"});

        assertAnalyzesTo(
                analyzer,
                "The quick 客室乗務員 brown FOXes jumps over the lazy dogs and computers 医薬品安全管理責任者",
                new String[] {
                    "the",
                    "quick",
                    "客室",
                    "乗務",
                    "員",
                    "brown",
                    "foxes",
                    "jumps",
                    "over",
                    "the",
                    "lazy",
                    "dogs",
                    "and",
                    "computers",
                    "医薬",
                    "品",
                    "安全",
                    "管理",
                    "責任",
                    "者"
                });

        assertAnalyzesTo(analyzer, "清水寺は東京都にあります。", new String[] {"清水寺", "東京", "都"});

        assertAnalyzesTo(analyzer, "メガネは顔の一部です。", new String[] {"メガネ", "顔", "一部"});

        assertAnalyzesTo(analyzer, "日本経済新聞でモバゲーの記事を読んだ。", new String[] {"日本", "経済", "新聞", "モバゲ", "記事", "読む"});

        assertAnalyzesTo(analyzer, "Java, Scala, Groovy, Clojure", new String[] {"java", "scala", "groovy", "clojure"});

        assertAnalyzesTo(analyzer, "ＬＵＣＥＮＥ、ＳＯＬＲ、Lucene, Solr", new String[] {"lucene", "solr", "lucene", "solr"});

        // Need an entry in user dictionary to fix: さしすせそ (the すせ is missing in the result)
        assertAnalyzesTo(
                analyzer, "ｱｲｳｴｵカキクケコさしすせそABCＸＹＺ123４５６", new String[] {"アイウエオカキクケコ", "さし", "そ", "abcxyz", "123456"});

        // The "たろう" is removed by the Sudachi Analyzer because of:
        // 1. BaseForm filter:
        //    たろう => だ; and
        // 2. SudachiPartOfSpeechStopFilter:
        //    the auxiliary verb (助動詞) it is uncommented in the stoptags.txt,
        //    thus the token is removed from the token stream.
        assertAnalyzesTo(analyzer, "ももたろう", new String[] {"もも"});
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
