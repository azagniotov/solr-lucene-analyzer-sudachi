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
package io.github.azagniotov.lucene.analysis.ja.sudachi.util;

import static java.util.Map.entry;

import java.io.IOException;
import java.util.Map;

/**
 *
 * Utility class for english translations of morphological data, used only for debugging.
 * Borrowed from:
 * <a href="https://github.com/apache/lucene/blob/2a3e5ca07f5df1f5080b5cb54ff104b7924e99f3/lucene/analysis/kuromoji/src/java/org/apache/lucene/analysis/ja/dict/ToStringUtil.java">apache/lucene/analysis/ja/dict/ToStringUtil.java</a>
 * */
public class Translations {
    // a translation map for parts of speech, only used for reflectWith
    private static final Map<String, String> POS_TRANSLATIONS;

    static {
        POS_TRANSLATIONS = Map.<String, String>ofEntries(
                entry("名詞", "noun"),
                entry("名詞-一般", "noun-general"),
                entry("名詞-普通名詞", "noun-common"),
                entry("名詞-固有名詞", "noun-proper"),
                entry("名詞-固有名詞-一般", "noun-proper-general"),
                entry("名詞-固有名詞-人名", "noun-proper-person"),
                entry("名詞-固有名詞-人名-一般", "noun-proper-person-general"),
                entry("名詞-固有名詞-人名-姓", "noun-proper-person-surname"),
                entry("名詞-固有名詞-人名-名", "noun-proper-person-given_name"),
                entry("名詞-固有名詞-組織", "noun-proper-organization"),
                entry("名詞-固有名詞-地域", "noun-proper-place"),
                entry("名詞-固有名詞-地域-一般", "noun-proper-place-general"),
                entry("名詞-固有名詞-地域-国", "noun-proper-place-country"),
                entry("代名詞", "pronoun"),
                entry("名詞-代名詞", "noun-pronoun"),
                entry("名詞-代名詞-一般", "noun-pronoun-general"),
                entry("名詞-代名詞-縮約", "noun-pronoun-contraction"),
                entry("名詞-副詞可能", "noun-adverbial"),
                entry("名詞-サ変接続", "noun-verbal"),
                entry("名詞-形容動詞語幹", "noun-adjective-base"),
                entry("名詞-数", "noun-numeric"),
                entry("名詞-数詞", "noun-numeric"),
                entry("名詞-非自立", "noun-affix"),
                entry("名詞-非自立-一般", "noun-affix-general"),
                entry("名詞-非自立-副詞可能", "noun-affix-adverbial"),
                entry("名詞-非自立-助動詞語幹", "noun-affix-aux"),
                entry("名詞-非自立-形容動詞語幹", "noun-affix-adjective-base"),
                entry("名詞-特殊", "noun-special"),
                entry("名詞-特殊-助動詞語幹", "noun-special-aux"),
                entry("接尾辞", "suffix"),
                entry("接尾辞-名詞的", "noun-suffix"),
                entry("名詞-接尾", "noun-suffix"),
                entry("名詞-接尾-一般", "noun-suffix-general"),
                entry("名詞-接尾-人名", "noun-suffix-person"),
                entry("名詞-接尾-地域", "noun-suffix-place"),
                entry("名詞-接尾-サ変接続", "noun-suffix-verbal"),
                entry("名詞-接尾-助動詞語幹", "noun-suffix-aux"),
                entry("名詞-接尾-形容動詞語幹", "noun-suffix-adjective-base"),
                entry("名詞-接尾-副詞可能", "noun-suffix-adverbial"),
                entry("名詞-接尾-助数詞", "noun-suffix-classifier"),
                entry("名詞-接尾-特殊", "noun-suffix-special"),
                entry("名詞-接続詞的", "noun-suffix-conjunctive"),
                entry("名詞-動詞非自立的", "noun-verbal_aux"),
                entry("名詞-引用文字列", "noun-quotation"),
                entry("名詞-ナイ形容詞語幹", "noun-nai_adjective"),
                entry("接頭詞", "prefix"),
                entry("接頭辞", "prefix"),
                entry("接頭詞-名詞接続", "prefix-nominal"),
                entry("接頭詞-動詞接続", "prefix-verbal"),
                entry("接頭詞-形容詞接続", "prefix-adjectival"),
                entry("接頭詞-数接続", "prefix-numerical"),
                entry("動詞", "verb"),
                entry("動詞-一般", "verb"),
                entry("動詞-自立", "verb-main"),
                entry("動詞-非自立", "verb-auxiliary"),
                entry("動詞-非自立可能", "verb-auxiliary"),
                entry("動詞-接尾", "verb-suffix"),
                entry("形容詞", "adjective"),
                entry("形容詞-一般", "adjective"),
                entry("形容詞-自立", "adjective-main"),
                entry("形容詞-非自立", "adjective-auxiliary"),
                entry("形容詞-接尾", "adjective-suffix"),
                entry("副詞", "adverb"),
                entry("副詞-一般", "adverb-general"),
                entry("副詞-助詞類接続", "adverb-particle_conjunction"),
                entry("連体詞", "adnominal"),
                entry("接続詞", "conjunction"),
                entry("助詞", "particle"),
                entry("助詞-格助詞", "particle-case"),
                entry("助詞-格助詞-一般", "particle-case-general"),
                entry("助詞-格助詞-引用", "particle-case-quote"),
                entry("助詞-格助詞-連語", "particle-case-compound"),
                entry("助詞-接続助詞", "particle-conjunctive"),
                entry("助詞-係助詞", "particle-dependency"),
                entry("助詞-副助詞", "particle-adverbial"),
                entry("助詞-間投助詞", "particle-interjective"),
                entry("助詞-並立助詞", "particle-coordinate"),
                entry("助詞-準体助詞", "particle-quasi"),
                entry("助詞-終助詞", "particle-final"),
                entry("助詞-副助詞／並立助詞／終助詞", "particle-adverbial/conjunctive/final"),
                entry("助詞-連体化", "particle-adnominalizer"),
                entry("助詞-副詞化", "particle-adnominalizer"),
                entry("助詞-特殊", "particle-special"),
                entry("助動詞", "auxiliary-verb"),
                entry("形状詞-助動詞語幹", "auxiliary-verb-stem-form"),
                entry("感動詞", "interjection"),
                entry("記号", "symbol"),
                entry("記号-一般", "symbol-general"),
                entry("記号-句点", "symbol-period"),
                entry("補助記号-句点", "symbol-period"),
                entry("記号-読点", "symbol-comma"),
                entry("記号-空白", "symbol-space"),
                entry("記号-括弧開", "symbol-open_bracket"),
                entry("記号-括弧閉", "symbol-close_bracket"),
                entry("記号-アルファベット", "symbol-alphabetic"),
                entry("その他", "other"),
                entry("その他-間投", "other-interjection"),
                entry("フィラー", "filler"),
                entry("非言語音", "non-verbal"),
                entry("語断片", "fragment"),
                entry("未知語", "unknown"));
    }

    /** Get the english form of a POS tag */
    public static String forPos(final String s) {
        return POS_TRANSLATIONS.get(s);
    }

    /**
     * Romanize Katakana using Kunrei: <a href="https://en.wikipedia.org/wiki/Kunrei-shiki_romanization">Kunrei</a>
     * */
    public static String toRomaji(final String s) {
        final StringBuilder out = new StringBuilder();
        try {
            toRomaji(out, s);
        } catch (IOException bogus) {
            throw new RuntimeException(bogus);
        }
        return out.toString();
    }

    /**
     *  Copyright (c) 2018 Works Applications Co., Ltd.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     * <p>
     *     http://www.apache.org/licenses/LICENSE-2.0
     * <p>
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     * <p>
     * Romanize Katakana using Kunrei: <a href="https://en.wikipedia.org/wiki/Kunrei-shiki_romanization">Kunrei</a>
     * The code adopted from:
     * 1. https://github.com/WorksApplications/elasticsearch-sudachi/issues/29
     * 2. https://github.com/WorksApplications/elasticsearch-sudachi/pull/32
     * */
    private static void toRomaji(final Appendable builder, final CharSequence s) throws IOException {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            char ch2 = (i < len - 1) ? s.charAt(i + 1) : 0;

            main:
            switch (ch) {
                case 'ッ':
                    switch (ch2) {
                        case 'カ':
                        case 'キ':
                        case 'ク':
                        case 'ケ':
                        case 'コ':
                            builder.append('k');
                            break main;
                        case 'サ':
                        case 'シ':
                        case 'ス':
                        case 'セ':
                        case 'ソ':
                            builder.append('s');
                            break main;
                        case 'タ':
                        case 'チ':
                        case 'ツ':
                        case 'テ':
                        case 'ト':
                            builder.append('t');
                            break main;
                        case 'ナ':
                        case 'ニ':
                        case 'ヌ':
                        case 'ネ':
                        case 'ノ':
                            builder.append('n');
                            break main;
                        case 'ハ':
                        case 'ヒ':
                        case 'フ':
                        case 'ヘ':
                        case 'ホ':
                            builder.append('h');
                            break main;
                        case 'マ':
                        case 'ミ':
                        case 'ム':
                        case 'メ':
                        case 'モ':
                            builder.append('m');
                            break main;
                        case 'ヤ':
                        case 'ユ':
                        case 'ヨ':
                            builder.append('y');
                            break main;
                        case 'ワ':
                            builder.append('w');
                            break main;
                        case 'ガ':
                        case 'ギ':
                        case 'グ':
                        case 'ゲ':
                        case 'ゴ':
                            builder.append('g');
                            break main;
                        case 'ザ':
                        case 'ジ':
                        case 'ズ':
                        case 'ゼ':
                        case 'ゾ':
                            builder.append('z');
                            break main;
                        case 'ダ':
                        case 'ヂ':
                        case 'ヅ':
                        case 'デ':
                        case 'ド':
                            builder.append('d');
                            break main;
                        case 'バ':
                        case 'ビ':
                        case 'ブ':
                        case 'ベ':
                        case 'ボ':
                            builder.append('b');
                            break main;
                        case 'パ':
                        case 'ピ':
                        case 'プ':
                        case 'ペ':
                        case 'ポ':
                            builder.append('p');
                            break main;
                        default:
                            builder.append("ltu");
                    }
                    break;
                case 'ア':
                    builder.append('a');
                    break;
                case 'イ':
                    builder.append('i');
                    break;
                case 'ウ':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("wha");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("whi");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("whe");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("who");
                            i++;
                            break;
                        default:
                            builder.append('u');
                            break;
                    }
                    break;
                case 'エ':
                    builder.append('e');
                    break;
                case 'オ':
                    builder.append('o');
                    break;
                case 'カ':
                    builder.append("ka");
                    break;
                case 'キ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("kya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("kyi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("kyu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("kye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("kyo");
                            i++;
                            break;
                        default:
                            builder.append("ki");
                            break;
                    }
                    break;
                case 'ク':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("qwa");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("qwi");
                            i++;
                            break;
                        case 'ゥ':
                            builder.append("qwu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("qwe");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("qwo");
                            i++;
                            break;
                        default:
                            builder.append("ku");
                            break;
                    }
                    break;
                case 'ケ':
                    builder.append("ke");
                    break;
                case 'コ':
                    builder.append("ko");
                    break;
                case 'サ':
                    builder.append("sa");
                    break;
                case 'シ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("sya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("syi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("syu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("sye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("syo");
                            i++;
                            break;
                        default:
                            builder.append("si");
                            break;
                    }
                    break;
                case 'ス':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("swa");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("swi");
                            i++;
                            break;
                        case 'ゥ':
                            builder.append("swu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("swe");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("swo");
                            i++;
                            break;
                        default:
                            builder.append("su");
                            break;
                    }
                    break;
                case 'セ':
                    builder.append("se");
                    break;
                case 'ソ':
                    builder.append("so");
                    break;
                case 'タ':
                    builder.append("ta");
                    break;
                case 'チ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("tya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("tyi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("tyu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("tye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("tyo");
                            i++;
                            break;
                        default:
                            builder.append("ti");
                            break;
                    }
                    break;
                case 'ツ':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("tsa");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("tsi");
                            i++;
                            break;
                        case 'ゥ':
                            builder.append("tsu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("tse");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("tso");
                            i++;
                            break;
                        default:
                            builder.append("tu");
                            break;
                    }
                    break;
                case 'テ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("tha");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("thi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("thu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("the");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("tho");
                            i++;
                            break;
                        default:
                            builder.append("te");
                            break;
                    }
                    break;
                case 'ト':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("twa");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("twi");
                            i++;
                            break;
                        case 'ゥ':
                            builder.append("twu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("twe");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("two");
                            i++;
                            break;
                        default:
                            builder.append("to");
                            break;
                    }
                    break;
                case 'ナ':
                    builder.append("na");
                    break;
                case 'ニ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("nya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("nyi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("nyu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("nye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("nyo");
                            i++;
                            break;
                        default:
                            builder.append("ni");
                            break;
                    }
                    break;
                case 'ヌ':
                    builder.append("nu");
                    break;
                case 'ネ':
                    builder.append("ne");
                    break;
                case 'ノ':
                    builder.append("no");
                    break;
                case 'ハ':
                    builder.append("ha");
                    break;
                case 'ヒ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("hya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("hyi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("hyu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("hye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("hyo");
                            i++;
                            break;
                        default:
                            builder.append("hi");
                            break;
                    }
                    break;
                case 'フ':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("fwa");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("fwi");
                            i++;
                            break;
                        case 'ゥ':
                            builder.append("fwu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("fwe");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("fwo");
                            i++;
                            break;
                        case 'ャ':
                            builder.append("fya");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("fyu");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("fyo");
                            i++;
                            break;
                        default:
                            builder.append("ho");
                            break;
                    }
                    break;
                case 'ヘ':
                    builder.append("he");
                    break;
                case 'ホ':
                    builder.append("ho");
                    break;
                case 'マ':
                    builder.append("ma");
                    break;
                case 'ミ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("mya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("myi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("myu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("mye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("myo");
                            i++;
                            break;
                        default:
                            builder.append("mi");
                            break;
                    }
                    break;
                case 'ム':
                    builder.append("mu");
                    break;
                case 'メ':
                    builder.append("me");
                    break;
                case 'モ':
                    builder.append("mo");
                    break;
                case 'ヤ':
                    builder.append("ya");
                    break;
                case 'ユ':
                    builder.append("yu");
                    break;
                case 'ヨ':
                    builder.append("yo");
                    break;
                case 'ラ':
                    builder.append("ra");
                    break;
                case 'リ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("rya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("ryi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("ryu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("rye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("ryo");
                            i++;
                            break;
                        default:
                            builder.append("ri");
                            break;
                    }
                    break;
                case 'ル':
                    builder.append("ru");
                    break;
                case 'レ':
                    builder.append("re");
                    break;
                case 'ロ':
                    builder.append("ro");
                    break;
                case 'ワ':
                    builder.append("wa");
                    break;
                case 'ヰ':
                    builder.append("wi");
                    break;
                case 'ヱ':
                    builder.append("we");
                    break;
                case 'ヲ':
                    builder.append("wo");
                    break;
                case 'ン':
                    switch (ch2) {
                        case 'ア':
                        case 'イ':
                        case 'ウ':
                        case 'エ':
                        case 'オ':
                        case 'ヤ':
                        case 'ユ':
                        case 'ヨ':
                            builder.append("nn");
                            break;
                        default:
                            builder.append('n');
                            break;
                    }
                    break;
                case 'ガ':
                    builder.append("ga");
                    break;
                case 'ギ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("gya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("gyi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("gyu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("gye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("gyo");
                            i++;
                            break;
                        default:
                            builder.append("gi");
                            break;
                    }
                    break;
                case 'グ':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("gwa");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("gwi");
                            i++;
                            break;
                        case 'ゥ':
                            builder.append("qwu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("gwe");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("gwo");
                            i++;
                            break;
                        default:
                            builder.append("gu");
                            break;
                    }
                    break;
                case 'ゲ':
                    builder.append("ge");
                    break;
                case 'ゴ':
                    builder.append("go");
                    break;
                case 'ザ':
                    builder.append("za");
                    break;
                case 'ジ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("zya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("zyi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("zyu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("zye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("zyo");
                            i++;
                            break;
                        default:
                            builder.append("zi");
                            break;
                    }
                    break;
                case 'ズ':
                    builder.append("zu");
                    break;
                case 'ゼ':
                    builder.append("ze");
                    break;
                case 'ゾ':
                    builder.append("zo");
                    break;
                case 'ダ':
                    builder.append("da");
                    break;
                case 'ヂ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("dya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("dyi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("dyu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("dye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("dyo");
                            i++;
                            break;
                        default:
                            builder.append("di");
                            break;
                    }
                    break;
                case 'ヅ':
                    builder.append("zu");
                    break;
                case 'デ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("dha");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("dhi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("dhu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("dhe");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("dho");
                            i++;
                            break;
                        default:
                            builder.append("de");
                            break;
                    }
                    break;
                case 'ド':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("dwa");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("dwi");
                            i++;
                            break;
                        case 'ゥ':
                            builder.append("dwu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("dwe");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("dwo");
                            i++;
                            break;
                        default:
                            builder.append("do");
                            break;
                    }
                    break;
                case 'バ':
                    builder.append("ba");
                    break;
                case 'ビ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("bya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("byi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("byu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("bye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("byo");
                            i++;
                            break;
                        default:
                            builder.append("bi");
                            break;
                    }
                    break;
                case 'ブ':
                    builder.append("bu");
                    break;
                case 'ベ':
                    builder.append("be");
                    break;
                case 'ボ':
                    builder.append("bo");
                    break;
                case 'パ':
                    builder.append("pa");
                    break;
                case 'ピ':
                    switch (ch2) {
                        case 'ャ':
                            builder.append("pya");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("pyi");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("pyu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("pye");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("pyo");
                            i++;
                            break;
                        default:
                            builder.append("pi");
                            break;
                    }
                    break;
                case 'プ':
                    builder.append("pu");
                    break;
                case 'ペ':
                    builder.append("pe");
                    break;
                case 'ポ':
                    builder.append("po");
                    break;
                case 'ヴ':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("va");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("vi");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("ve");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("vo");
                            i++;
                            break;
                        case 'ャ':
                            builder.append("vya");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("vyu");
                            i++;
                            break;
                        case 'ョ':
                            builder.append("vyo");
                            i++;
                            break;
                        default:
                            builder.append("vu");
                            break;
                    }
                    break;
                case 'ァ':
                    builder.append("la");
                    break;
                case 'ィ':
                    builder.append("li");
                    break;
                case 'ゥ':
                    builder.append("lu");
                    break;
                case 'ェ':
                    builder.append("le");
                    break;
                case 'ォ':
                    builder.append("lo");
                    break;
                case 'ヵ':
                    builder.append("lka");
                    break;
                case 'ヶ':
                    builder.append("lke");
                    break;
                case 'ャ':
                    builder.append("lya");
                    break;
                case 'ュ':
                    builder.append("lyu");
                    break;
                case 'ョ':
                    builder.append("lyo");
                    break;
                case 'ヮ':
                    builder.append("lwa");
                    break;
                case 'ー':
                    builder.append('-');
                    break;
                case '・':
                case '＝':
                    /* drop these characters */
                    break;
                default:
                    builder.append(ch);
                    break;
            }
        }
    }
}
