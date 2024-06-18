/*
 * Copyright (c) 2024 Alexander Zagniotov
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
package io.github.azagniotov.lucene.analysis.ja.sudachi.testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * A test helper class that mimicks behavior of a few testing APIs, present in the Lucene Test
 * Framework's abstract BaseTokenStreamTestCase.
 *
 * <p>The current class meant to help the Sudachi Lucene Analyzer library consumers to assert
 * TokenStream content, when it is not possible for the consumer to extend their test classes from
 * Lucene's abstract BaseTokenStreamTestCase, because their classes already extend from a testing
 * framework base class.
 */
@GeneratedCodeClassCoverageExclusion
public final class TokenStreamTester {

    private TokenStreamTester() {}

    public static void assertTokenStreamContents(final Analyzer analyzer, final String input, final String... expected)
            throws IOException {
        final List<String> expectedList = Arrays.asList(expected).stream()
                .filter(element -> !element.trim().equals(""))
                .collect(Collectors.toList());
        ;
        final List<String> actualList = tokens(analyzer, input);

        if (expectedList.size() != actualList.size()) {
            final String err = String.format(
                    "Expected tokens list size %s is not equal to the actual tokens list size %s",
                    expectedList.size(), actualList.size());
            throw new AssertionError(err);
        }

        final List<String> expectedListDifference = expectedList.stream()
                .filter(element -> !actualList.contains(element))
                .collect(Collectors.toList());

        if (expectedListDifference.size() > 0) {
            final String err =
                    String.format("Assertion failed. Actual tokens are: [%s]", String.join(", ", actualList));
            throw new AssertionError(err);
        }

        final List<String> actualListDifference = actualList.stream()
                .filter(element -> !expectedList.contains(element))
                .collect(Collectors.toList());

        if (actualListDifference.size() > 0) {
            final String err = String.format("Assertion failed. Actual tokens are: %s", String.join(", ", actualList));
            throw new AssertionError(err);
        }
    }

    private static List<String> tokens(final Analyzer analyzer, final String input) throws IOException {
        try (final TokenStream ts = analyzer.tokenStream("field", input)) {
            final List<String> result = new ArrayList<>();
            final CharTermAttribute termAtt = ts.getAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                result.add(termAtt.toString());
            }
            ts.end();
            return result;
        }
    }
}
