/*
 * Copyright (c) 2017-2023 Sho Nakamura (https://github.com/sh0nk/solr-sudachi)
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
package io.github.azagniotov.lucene.analysis.ja.sudachi.filters;

import static com.worksap.nlp.sudachi.Tokenizer.SplitMode;

import io.github.azagniotov.lucene.analysis.ja.sudachi.analyzer.SudachiAnalyzer;
import java.io.IOException;
import java.util.Collections;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.junit.Test;

public class SudachiSurfaceFormFilterTest extends BaseTokenStreamTestCase {
    private Analyzer analyzer;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        analyzer = new SudachiAnalyzer(SudachiAnalyzer.getDefaultStopSet(), true, true, SplitMode.A);
    }

    @Override
    public void tearDown() throws Exception {
        analyzer.close();
        super.tearDown();
    }

    @Test
    public void testJapanese() throws IOException {
        assertAnalyzesTo(analyzer, "昨日は学校に行った後に走って食べました。", new String[] {"昨日", "学校", "行っ", "後", "走っ", "食べ", "まし"});
    }

    @Test
    public void testEnglish() throws IOException {
        assertAnalyzesTo(analyzer, "I like reading Japanese", new String[] {"I", "like", "reading", "Japanese"});
    }

    @Test
    public void testUnicode() throws IOException {
        assertAnalyzesTo(analyzer, "\u1f77\u1ff2\u1f96\u1f50\u1fec  yirhp", new String[] {
            "\u1f77", "\u1ff2\u1f96\u1f50\u1fec", "yirhp"
        });
    }

    @Test
    public void testEmptyTerm() throws IOException {
        analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {
                Tokenizer tokenizer = new KeywordTokenizer();
                return new TokenStreamComponents(
                        tokenizer, new SudachiSurfaceFormFilterFactory(Collections.emptyMap()).create(tokenizer));
            }
        };
        checkOneTerm(analyzer, "", "");
    }
}
