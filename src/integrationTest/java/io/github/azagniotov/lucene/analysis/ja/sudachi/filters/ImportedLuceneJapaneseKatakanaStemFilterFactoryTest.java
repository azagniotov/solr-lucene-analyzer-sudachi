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

import io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer.SudachiTokenizerFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.NoOpResourceLoader;
import java.io.StringReader;
import java.util.HashMap;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;

public class ImportedLuceneJapaneseKatakanaStemFilterFactoryTest extends BaseTokenStreamTestCase {

    public void testKatakanaStemming() throws Exception {
        final SudachiTokenizerFactory tokenizerFactory = new SudachiTokenizerFactory(new HashMap<>() {
            {
                put("mode", "search");
            }
        });
        tokenizerFactory.inform(new NoOpResourceLoader());

        final Tokenizer tokenStream = tokenizerFactory.create(newAttributeFactory());
        tokenStream.setReader(new StringReader("明後日パーティーに行く予定がある。図書館で資料をコピーしました。"));

        final SudachiKatakanaStemFilterFactory filterFactory = new SudachiKatakanaStemFilterFactory(new HashMap<>());

        assertTokenStreamContents(filterFactory.create(tokenStream), new String[] {
            // パーティー should be stemmed
            "明後日",
            "パーティ",
            "に",
            "行く",
            "予定",
            "が",
            "ある",
            // コピー should not be stemmed
            "図書",
            "館",
            "で",
            "資料",
            "を",
            "コピー",
            "し",
            "まし",
            "た"
        });
    }

    /** Test that bogus arguments result in exception */
    public void testBogusArguments() throws Exception {
        IllegalArgumentException expected = expectThrows(IllegalArgumentException.class, () -> {
            new SudachiKatakanaStemFilterFactory(new HashMap<>() {
                {
                    put("bogusArg", "bogusValue");
                }
            });
        });
        assertTrue(expected.getMessage().contains("Unknown parameters"));
    }
}
