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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
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
        // 1. The stop words うち, も, は, の, です, を, etc. are removed.
        // 2. The conjugated verbs are returned in their base/dictionary form.
        // 3. Lower case filter is applied
        // 4. Full-width Japanese Katakana (supports A-Z) are converted to Latin characters
        //

        assertAnalyzesTo(analyzer, "すもももももももものうち。", new String[] {"すもも", "もも", "もも"});

        assertAnalyzesTo(analyzer, "メガネは顔の一部です。", new String[] {"メガネ", "顔", "一部"});

        assertAnalyzesTo(analyzer, "日本経済新聞でモバゲーの記事を読んだ。", new String[] {"日本", "経済", "新聞", "モバゲ", "記事", "読む"});

        assertAnalyzesTo(analyzer, "Java, Scala, Groovy, Clojure", new String[] {"java", "scala", "groovy", "clojure"});

        assertAnalyzesTo(analyzer, "ＬＵＣＥＮＥ、ＳＯＬＲ、Lucene, Solr", new String[] {"lucene", "solr", "lucene", "solr"});

        // User dictionary fixed: さしすせそ
        assertAnalyzesTo(
                analyzer, "ｱｲｳｴｵカキクケコさしすせそABCＸＹＺ123４５６", new String[] {"アイウエオカキクケコ", "さしすせそ", "abcxyz", "123456"});

        // The "たろう" is removed by the Sudachi Analyzer because of:
        // 1. BaseForm filter:
        //    たろう => だ; and
        // 2. SudachiPartOfSpeechStopFilter:
        //    the auxiliary verb (助動詞) it is uncommented in the stoptags.txt,
        //    thus the token is removed from the token stream.
        assertAnalyzesTo(analyzer, "ももたろう", new String[] {"もも"});
    }
}
