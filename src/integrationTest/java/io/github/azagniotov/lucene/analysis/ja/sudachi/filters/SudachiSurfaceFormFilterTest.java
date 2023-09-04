/*
 * Copyright (c) 2023 Alexander Zagniotov
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

import io.github.azagniotov.lucene.analysis.ja.sudachi.test.TestUtils;
import java.io.IOException;
import java.util.Collections;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.junit.Test;

public class SudachiSurfaceFormFilterTest extends BaseTokenStreamTestCase {

    private SudachiSurfaceFormFilterFactory factory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        factory = new SudachiSurfaceFormFilterFactory(Collections.emptyMap());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testJapanese() throws IOException {
        TokenStream tokenStream = TestUtils.tokenize("昨日は学校に行った後に走って食べました。");
        tokenStream = factory.create(tokenStream);

        assertTokenStreamContents(
                tokenStream, new String[] {"昨日", "は", "学校", "に", "行っ", "た", "後", "に", "走っ", "て", "食べ", "まし", "た"});
    }

    @Test
    public void testEnglish() throws IOException {
        TokenStream tokenStream = TestUtils.tokenize("I like reading Japanese");
        tokenStream = factory.create(tokenStream);

        assertTokenStreamContents(tokenStream, new String[] {"I", "like", "reading", "Japanese"});
    }
}
