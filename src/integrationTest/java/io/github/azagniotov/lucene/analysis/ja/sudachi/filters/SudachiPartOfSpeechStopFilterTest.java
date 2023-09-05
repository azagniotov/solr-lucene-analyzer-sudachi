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

package io.github.azagniotov.lucene.analysis.ja.sudachi.filters;

import com.worksap.nlp.sudachi.Config;
import io.github.azagniotov.lucene.analysis.ja.sudachi.analyzer.SudachiAnalyzer;
import io.github.azagniotov.lucene.analysis.ja.sudachi.test.TestUtils;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.StringResourceLoader;
import java.io.IOException;
import java.util.HashMap;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.junit.Test;

public class SudachiPartOfSpeechStopFilterTest extends BaseTokenStreamTestCase {

    private SudachiPartOfSpeechStopFilterFactory factory;

    private TokenStream tokenStream;

    private TestUtils testUtils;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.testUtils = new TestUtils(Config.defaultConfig());
        factory = new SudachiPartOfSpeechStopFilterFactory(new HashMap<String, String>() {
            {
                put("tags", "stoptags.txt");
            }
        });
        tokenStream = this.testUtils.tokenize("東京都に行った。");
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testAllPOS() throws IOException {
        String tags = "動詞,非自立可能\n名詞,固有名詞,地名,一般\n";

        factory.inform(new StringResourceLoader(tags));
        final TokenStream localTokenStream = factory.create(tokenStream);
        assertTokenStreamContents(localTokenStream, new String[] {"都", "に", "た"});
    }

    @Test
    public void testPrefix() throws IOException {
        String tags = "動詞\n名詞,固有名詞\n";
        factory.inform(new StringResourceLoader(tags));
        final TokenStream localTokenStream = factory.create(tokenStream);
        assertTokenStreamContents(localTokenStream, new String[] {"都", "に", "た"});
    }

    @Test
    public void testConjugationType() throws IOException {
        String tags = "*,*,*,*,五段-カ行\n";
        factory.inform(new StringResourceLoader(tags));
        final TokenStream localTokenStream = factory.create(tokenStream);
        assertTokenStreamContents(localTokenStream, new String[] {"東京", "都", "に", "た"});
    }

    @Test
    public void testConjugationTypeAndForm() throws IOException {
        String tags = "*,*,*,*,五段-カ行,終止形-一般\n";
        factory.inform(new StringResourceLoader(tags));
        final TokenStream localTokenStream = factory.create(tokenStream);
        assertTokenStreamContents(localTokenStream, new String[] {"東京", "都", "に", "行っ", "た"});
    }

    @Test
    public void testConjugationForm() throws IOException {
        String tags = "*,*,*,*,*,終止形-一般\n";
        factory.inform(new StringResourceLoader(tags));
        final TokenStream localTokenStream = factory.create(tokenStream);
        assertTokenStreamContents(localTokenStream, new String[] {"東京", "都", "に", "行っ"});
    }

    @Test
    public void testPrefixWithUnmatchedSubcategory() throws IOException {
        String tags = "助詞,格助詞\n助詞,格助詞,引用\n";
        factory.inform(new StringResourceLoader(tags));
        final TokenStream localTokenStream = factory.create(tokenStream);
        assertTokenStreamContents(localTokenStream, new String[] {"東京", "都", "行っ", "た"});
    }

    @Test
    public void testTooLongCategory() throws IOException {
        String tags = "名詞,固有名詞,地名,一般,一般\n";
        factory.inform(new StringResourceLoader(tags));
        final TokenStream localTokenStream = factory.create(tokenStream);
        assertTokenStreamContents(localTokenStream, new String[] {"東京", "都", "に", "行っ", "た"});
    }

    /** If we don't specify "tags", then load the default stop tags. */
    public void testNoTagsSpecified() throws IOException {
        TokenStream localTokenStream = this.testUtils.tokenize("私は制限スピードを超える。");

        SudachiPartOfSpeechStopFilterFactory factory = new SudachiPartOfSpeechStopFilterFactory(new HashMap<>());
        factory.inform(new ClasspathResourceLoader(SudachiAnalyzer.class));
        localTokenStream = factory.create(localTokenStream);

        assertTokenStreamContents(localTokenStream, new String[] {"私", "制限", "スピード", "超える"});
    }
}
