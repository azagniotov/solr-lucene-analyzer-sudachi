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
 * A {@link TokenFilter} that normalizes small letters (捨て仮名) in katakana into normal letters. For
 * instance, "ストップウォッチ" will be translated to "ストツプウオツチ".
 *
 * <p>This filter is useful if you want to search against old style Japanese text such as patents,
 * legal, contract policies, etc.
 */
public final class SudachiJapaneseKatakanaUppercaseFilter extends TokenFilter {
    private static final Map<Character, Character> LETTER_MAPPINGS;

    static {
        // supported characters are:
        // ァ ィ ゥ ェ ォ ヵ ㇰ ヶ ㇱ ㇲ ッ ㇳ ㇴ ㇵ ㇶ ㇷ ㇷ゚ ㇸ ㇹ ㇺ ャ ュ ョ ㇻ ㇼ ㇽ ㇾ ㇿ ヮ
        LETTER_MAPPINGS = new HashMap<Character, Character>() {
            {
                put('ァ', 'ア');
                put('ィ', 'イ');
                put('ゥ', 'ウ');
                put('ェ', 'エ');
                put('ォ', 'オ');
                put('ヵ', 'カ');
                put('ㇰ', 'ク');
                put('ヶ', 'ケ');
                put('ㇱ', 'シ');
                put('ㇲ', 'ス');
                put('ッ', 'ツ');
                put('ㇳ', 'ト');
                put('ㇴ', 'ヌ');
                put('ㇵ', 'ハ');
                put('ㇶ', 'ヒ');
                put('ㇷ', 'フ');
                put('ㇸ', 'ヘ');
                put('ㇹ', 'ホ');
                put('ㇺ', 'ム');
                put('ャ', 'ヤ');
                put('ュ', 'ユ');
                put('ョ', 'ヨ');
                put('ㇻ', 'ラ');
                put('ㇼ', 'リ');
                put('ㇽ', 'ル');
                put('ㇾ', 'レ');
                put('ㇿ', 'ロ');
                put('ヮ', 'ワ');
            }
        };
    }

    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);

    public SudachiJapaneseKatakanaUppercaseFilter(TokenStream input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }
        final char[] termBuffer = termAttr.buffer();
        int newLength = termAttr.length();
        for (int from = 0, to = 0, length = newLength; from < length; from++, to++) {
            char c = termBuffer[from];
            if (c == 'ㇷ' && from + 1 < length && termBuffer[from + 1] == '゚') {
                // ㇷ゚detected, replace it by プ.
                termBuffer[to] = 'プ';
                from++;
                newLength--;
            } else {
                Character mappedChar = LETTER_MAPPINGS.get(c);
                termBuffer[to] = mappedChar == null ? c : mappedChar;
            }
        }
        termAttr.setLength(newLength);
        return true;
    }
}
