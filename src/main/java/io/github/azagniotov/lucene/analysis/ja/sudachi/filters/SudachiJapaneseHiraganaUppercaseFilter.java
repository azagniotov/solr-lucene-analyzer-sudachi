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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * A {@link TokenFilter} that normalizes small letters (捨て仮名) in hiragana into normal letters. For
 * instance, "ちょっとまって" will be translated to "ちよつとまつて".
 *
 * <p>This filter is useful if you want to search against old style Japanese text such as patents,
 * legal, contract policies, etc.
 */
public final class SudachiJapaneseHiraganaUppercaseFilter extends TokenFilter {
    private static final Map<Character, Character> LETTER_MAPPINGS;

    static {
        // supported characters are:
        // ぁ ぃ ぅ ぇ ぉ っ ゃ ゅ ょ ゎ ゕ ゖ
        LETTER_MAPPINGS = new HashMap<Character, Character>() {
            {
                put('ぁ', 'あ');
                put('ぃ', 'い');
                put('ぅ', 'う');
                put('ぇ', 'え');
                put('ぉ', 'お');
                put('っ', 'つ');
                put('ゃ', 'や');
                put('ゅ', 'ゆ');
                put('ょ', 'よ');
                put('ゎ', 'わ');
                put('ゕ', 'か');
                put('ゖ', 'け');
            }
        };
    }

    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);

    public SudachiJapaneseHiraganaUppercaseFilter(TokenStream input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }
        final char[] termBuffer = termAttr.buffer();
        for (int i = 0, length = termAttr.length(); i < length; i++) {
            Character c = LETTER_MAPPINGS.get(termBuffer[i]);
            if (c != null) {
                termBuffer[i] = c;
            }
        }
        return true;
    }
}
