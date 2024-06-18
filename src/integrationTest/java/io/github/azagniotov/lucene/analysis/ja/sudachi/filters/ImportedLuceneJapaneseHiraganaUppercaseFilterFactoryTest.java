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
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.tests.util.StringMockResourceLoader;

/** Tests for {@link SudachiJapaneseHiraganaUppercaseFilterFactory} */
public class ImportedLuceneJapaneseHiraganaUppercaseFilterFactoryTest extends BaseTokenStreamTestCase {

    public void testBasics() throws IOException {

        Map<String, String> args = new HashMap<>();
        args.put("discardPunctuation", "true");

        final SudachiTokenizerFactory tokenizerFactory = new SudachiTokenizerFactory(args);
        tokenizerFactory.inform(new StringMockResourceLoader(""));

        TokenStream tokenStream = tokenizerFactory.create(newAttributeFactory());
        ((Tokenizer) tokenStream).setReader(new StringReader("ちょっとまって"));

        SudachiJapaneseHiraganaUppercaseFilterFactory factory =
                new SudachiJapaneseHiraganaUppercaseFilterFactory(new HashMap<>());
        tokenStream = factory.create(tokenStream);
        assertTokenStreamContents(tokenStream, new String[] {"ちよつと", "まつ", "て"});
    }

    /** Test that bogus arguments result in exception */
    public void testBogusArguments() throws Exception {
        IllegalArgumentException expected = expectThrows(
                IllegalArgumentException.class,
                () -> new SudachiJapaneseHiraganaUppercaseFilterFactory(new HashMap<>() {
                    {
                        put("bogusArg", "bogusValue");
                    }
                }));
        assertTrue(expected.getMessage().contains("Unknown parameters"));
    }
}
