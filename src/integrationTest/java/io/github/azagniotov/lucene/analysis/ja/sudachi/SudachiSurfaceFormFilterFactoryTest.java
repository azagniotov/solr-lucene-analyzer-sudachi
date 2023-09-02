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
package io.github.azagniotov.lucene.analysis.ja.sudachi;

import static org.apache.lucene.analysis.BaseTokenStreamTestCase.assertAnalyzesTo;
import static org.apache.lucene.analysis.BaseTokenStreamTestCase.checkOneTerm;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.solr.core.SolrResourceLoader;
import org.junit.Test;

public class SudachiSurfaceFormFilterFactoryTest extends BaseTokenStreamTestCase {
    private Analyzer analyzer;

    private Tokenizer createTokenizer(final Map<String, String> args) throws IOException, URISyntaxException {
        final Map<String, String> map = new HashMap<>(args);
        final SudachiTokenizerFactory factory = new SudachiTokenizerFactory(map);
        factory.inform(new SolrResourceLoader(Paths.get(".")));

        return factory.create(BaseTokenStreamTestCase.newAttributeFactory());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {
                try {
                    final Tokenizer tokenizer = createTokenizer(Collections.emptyMap());
                    return new TokenStreamComponents(
                            tokenizer, new SudachiSurfaceFormFilterFactory(Collections.emptyMap()).create(tokenizer));
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        checkOneTerm(analyzer, "人間", "人間");
    }

    @Override
    public void tearDown() throws Exception {
        analyzer.close();
        super.tearDown();
    }

    @Test
    public void testBasics() throws IOException {
        assertAnalyzesTo(analyzer, "吾輩は猫である。", new String[] {"吾輩", "は", "猫", "で", "ある"});
    }

    @Test
    public void testEnglish() throws IOException {
        assertAnalyzesTo(analyzer, "This is a pen.", new String[] {"This", "is", "a", "pen"});
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
        analyzer.close();
    }
}
