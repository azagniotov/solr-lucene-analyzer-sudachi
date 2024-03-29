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
package io.github.azagniotov.lucene.analysis.ja.sudachi.filters;

import com.worksap.nlp.sudachi.Config;
import io.github.azagniotov.lucene.analysis.ja.sudachi.test.TestUtils;
import java.io.IOException;
import java.util.Collections;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.junit.Test;

public class SudachiNormalizedFormFilterTest extends BaseTokenStreamTestCase {

    private SudachiNormalizedFormFilterFactory factory;
    private TestUtils testUtils;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.factory = new SudachiNormalizedFormFilterFactory(Collections.emptyMap());
        this.testUtils = new TestUtils(Config.defaultConfig());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testJapaneseNormalizedForm() throws IOException {
        TokenStream tokenStream = this.testUtils.tokenize("昨日は学校に行った後に走って食べました。");
        tokenStream = factory.create(tokenStream);

        assertTokenStreamContents(
                tokenStream, new String[] {"昨日", "は", "学校", "に", "行く", "た", "後", "に", "走る", "て", "食べる", "ます", "た"});
    }

    @Test
    public void testJapaneseNormalizedFormWithUnNormalizedWord() throws IOException {
        TokenStream tokenStream = this.testUtils.tokenize("昨日は学校にいった後に走って食べました。"); // いった
        tokenStream = factory.create(tokenStream);

        assertTokenStreamContents(
                tokenStream, new String[] {"昨日", "は", "学校", "に", "行く", "た", "後", "に", "走る", "て", "食べる", "ます", "た"});

        tokenStream = this.testUtils.tokenize("昨日は学校に行った後に走って食べました。"); // 行った
        tokenStream = factory.create(tokenStream);

        assertTokenStreamContents(
                tokenStream, new String[] {"昨日", "は", "学校", "に", "行く", "た", "後", "に", "走る", "て", "食べる", "ます", "た"});
    }
}
