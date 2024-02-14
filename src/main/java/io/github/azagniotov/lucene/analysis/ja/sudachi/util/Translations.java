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

    /** Romanize Katakana with modified hepburn */
    public static String toRomaji(final String s) {
        final StringBuilder out = new StringBuilder();
        try {
            toRomaji(out, s);
        } catch (IOException bogus) {
            throw new RuntimeException(bogus);
        }
        return out.toString();
    }

    /** Romanize katakana with modified hepburn */
    // TODO: now that this is used by readingsfilter and not just for
    // debugging, fix this to really be a scheme that works best with IMEs
    private static void toRomaji(final Appendable builder, final CharSequence s) throws IOException {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            // maximum lookahead: 3
            char ch = s.charAt(i);
            char ch2 = (i < len - 1) ? s.charAt(i + 1) : 0;
            char ch3 = (i < len - 2) ? s.charAt(i + 2) : 0;

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
                        case 'パ':
                        case 'ピ':
                        case 'プ':
                        case 'ペ':
                        case 'ポ':
                            builder.append('p');
                            break main;
                    }
                    break;
                case 'ア':
                    builder.append('a');
                    break;
                case 'イ':
                    if (ch2 == 'ィ') {
                        builder.append("yi");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("ye");
                        i++;
                    } else {
                        builder.append('i');
                    }
                    break;
                case 'ウ':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("wa");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("wi");
                            i++;
                            break;
                        case 'ゥ':
                            builder.append("wu");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("we");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("wo");
                            i++;
                            break;
                        case 'ュ':
                            builder.append("wyu");
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
                    if (ch2 == 'ウ') {
                        builder.append('ō');
                        i++;
                    } else {
                        builder.append('o');
                    }
                    break;
                case 'カ':
                    builder.append("ka");
                    break;
                case 'キ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("kyō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("kyū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("kya");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("kyo");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("kyu");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("kye");
                        i++;
                    } else {
                        builder.append("ki");
                    }
                    break;
                case 'ク':
                    switch (ch2) {
                        case 'ァ':
                            builder.append("kwa");
                            i++;
                            break;
                        case 'ィ':
                            builder.append("kwi");
                            i++;
                            break;
                        case 'ェ':
                            builder.append("kwe");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("kwo");
                            i++;
                            break;
                        case 'ヮ':
                            builder.append("kwa");
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
                    if (ch2 == 'ウ') {
                        builder.append("kō");
                        i++;
                    } else {
                        builder.append("ko");
                    }
                    break;
                case 'サ':
                    builder.append("sa");
                    break;
                case 'シ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("shō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("shū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("sha");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("sho");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("shu");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("she");
                        i++;
                    } else {
                        builder.append("shi");
                    }
                    break;
                case 'ス':
                    if (ch2 == 'ィ') {
                        builder.append("si");
                        i++;
                    } else {
                        builder.append("su");
                    }
                    break;
                case 'セ':
                    builder.append("se");
                    break;
                case 'ソ':
                    if (ch2 == 'ウ') {
                        builder.append("sō");
                        i++;
                    } else {
                        builder.append("so");
                    }
                    break;
                case 'タ':
                    builder.append("ta");
                    break;
                case 'チ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("chō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("chū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("cha");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("cho");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("chu");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("che");
                        i++;
                    } else {
                        builder.append("chi");
                    }
                    break;
                case 'ツ':
                    if (ch2 == 'ァ') {
                        builder.append("tsa");
                        i++;
                    } else if (ch2 == 'ィ') {
                        builder.append("tsi");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("tse");
                        i++;
                    } else if (ch2 == 'ォ') {
                        builder.append("tso");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("tsyu");
                        i++;
                    } else {
                        builder.append("tsu");
                    }
                    break;
                case 'テ':
                    if (ch2 == 'ィ') {
                        builder.append("ti");
                        i++;
                    } else if (ch2 == 'ゥ') {
                        builder.append("tu");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("tyu");
                        i++;
                    } else {
                        builder.append("te");
                    }
                    break;
                case 'ト':
                    if (ch2 == 'ウ') {
                        builder.append("tō");
                        i++;
                    } else if (ch2 == 'ゥ') {
                        builder.append("tu");
                        i++;
                    } else {
                        builder.append("to");
                    }
                    break;
                case 'ナ':
                    builder.append("na");
                    break;
                case 'ニ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("nyō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("nyū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("nya");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("nyo");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("nyu");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("nye");
                        i++;
                    } else {
                        builder.append("ni");
                    }
                    break;
                case 'ヌ':
                    builder.append("nu");
                    break;
                case 'ネ':
                    builder.append("ne");
                    break;
                case 'ノ':
                    if (ch2 == 'ウ') {
                        builder.append("nō");
                        i++;
                    } else {
                        builder.append("no");
                    }
                    break;
                case 'ハ':
                    builder.append("ha");
                    break;
                case 'ヒ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("hyō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("hyū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("hya");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("hyo");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("hyu");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("hye");
                        i++;
                    } else {
                        builder.append("hi");
                    }
                    break;
                case 'フ':
                    if (ch2 == 'ャ') {
                        builder.append("fya");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("fyu");
                        i++;
                    } else if (ch2 == 'ィ' && ch3 == 'ェ') {
                        builder.append("fye");
                        i += 2;
                    } else if (ch2 == 'ョ') {
                        builder.append("fyo");
                        i++;
                    } else if (ch2 == 'ァ') {
                        builder.append("fa");
                        i++;
                    } else if (ch2 == 'ィ') {
                        builder.append("fi");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("fe");
                        i++;
                    } else if (ch2 == 'ォ') {
                        builder.append("fo");
                        i++;
                    } else {
                        builder.append("fu");
                    }
                    break;
                case 'ヘ':
                    builder.append("he");
                    break;
                case 'ホ':
                    if (ch2 == 'ウ') {
                        builder.append("hō");
                        i++;
                    } else if (ch2 == 'ゥ') {
                        builder.append("hu");
                        i++;
                    } else {
                        builder.append("ho");
                    }
                    break;
                case 'マ':
                    builder.append("ma");
                    break;
                case 'ミ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("myō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("myū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("mya");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("myo");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("myu");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("mye");
                        i++;
                    } else {
                        builder.append("mi");
                    }
                    break;
                case 'ム':
                    builder.append("mu");
                    break;
                case 'メ':
                    builder.append("me");
                    break;
                case 'モ':
                    if (ch2 == 'ウ') {
                        builder.append("mō");
                        i++;
                    } else {
                        builder.append("mo");
                    }
                    break;
                case 'ヤ':
                    builder.append("ya");
                    break;
                case 'ユ':
                    builder.append("yu");
                    break;
                case 'ヨ':
                    if (ch2 == 'ウ') {
                        builder.append("yō");
                        i++;
                    } else {
                        builder.append("yo");
                    }
                    break;
                case 'ラ':
                    if (ch2 == '゜') {
                        builder.append("la");
                        i++;
                    } else {
                        builder.append("ra");
                    }
                    break;
                case 'リ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("ryō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("ryū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("rya");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("ryo");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("ryu");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("rye");
                        i++;
                    } else if (ch2 == '゜') {
                        builder.append("li");
                        i++;
                    } else {
                        builder.append("ri");
                    }
                    break;
                case 'ル':
                    if (ch2 == '゜') {
                        builder.append("lu");
                        i++;
                    } else {
                        builder.append("ru");
                    }
                    break;
                case 'レ':
                    if (ch2 == '゜') {
                        builder.append("le");
                        i++;
                    } else {
                        builder.append("re");
                    }
                    break;
                case 'ロ':
                    if (ch2 == 'ウ') {
                        builder.append("rō");
                        i++;
                    } else if (ch2 == '゜') {
                        builder.append("lo");
                        i++;
                    } else {
                        builder.append("ro");
                    }
                    break;
                case 'ワ':
                    builder.append("wa");
                    break;
                case 'ヰ':
                    builder.append("i");
                    break;
                case 'ヱ':
                    builder.append("e");
                    break;
                case 'ヲ':
                    builder.append("o");
                    break;
                case 'ン':
                    switch (ch2) {
                        case 'バ':
                        case 'ビ':
                        case 'ブ':
                        case 'ベ':
                        case 'ボ':
                        case 'パ':
                        case 'ピ':
                        case 'プ':
                        case 'ペ':
                        case 'ポ':
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
                        case 'ア':
                        case 'イ':
                        case 'ウ':
                        case 'エ':
                        case 'オ':
                            builder.append("n'");
                            break main;
                        default:
                            builder.append("n");
                            break main;
                    }
                case 'ガ':
                    builder.append("ga");
                    break;
                case 'ギ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("gyō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("gyū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("gya");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("gyo");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("gyu");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("gye");
                        i++;
                    } else {
                        builder.append("gi");
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
                        case 'ェ':
                            builder.append("gwe");
                            i++;
                            break;
                        case 'ォ':
                            builder.append("gwo");
                            i++;
                            break;
                        case 'ヮ':
                            builder.append("gwa");
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
                    if (ch2 == 'ウ') {
                        builder.append("gō");
                        i++;
                    } else {
                        builder.append("go");
                    }
                    break;
                case 'ザ':
                    builder.append("za");
                    break;
                case 'ジ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("jō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("jū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("ja");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("jo");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("ju");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("je");
                        i++;
                    } else {
                        builder.append("ji");
                    }
                    break;
                case 'ズ':
                    if (ch2 == 'ィ') {
                        builder.append("zi");
                        i++;
                    } else {
                        builder.append("zu");
                    }
                    break;
                case 'ゼ':
                    builder.append("ze");
                    break;
                case 'ゾ':
                    if (ch2 == 'ウ') {
                        builder.append("zō");
                        i++;
                    } else {
                        builder.append("zo");
                    }
                    break;
                case 'ダ':
                    builder.append("da");
                    break;
                case 'ヂ':
                    // TODO: investigate all this
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("jō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("jū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("ja");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("jo");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("ju");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("je");
                        i++;
                    } else {
                        builder.append("ji");
                    }
                    break;
                case 'ヅ':
                    builder.append("zu");
                    break;
                case 'デ':
                    if (ch2 == 'ィ') {
                        builder.append("di");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("dyu");
                        i++;
                    } else {
                        builder.append("de");
                    }
                    break;
                case 'ド':
                    if (ch2 == 'ウ') {
                        builder.append("dō");
                        i++;
                    } else if (ch2 == 'ゥ') {
                        builder.append("du");
                        i++;
                    } else {
                        builder.append("do");
                    }
                    break;
                case 'バ':
                    builder.append("ba");
                    break;
                case 'ビ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("byō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("byū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("bya");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("byo");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("byu");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("bye");
                        i++;
                    } else {
                        builder.append("bi");
                    }
                    break;
                case 'ブ':
                    builder.append("bu");
                    break;
                case 'ベ':
                    builder.append("be");
                    break;
                case 'ボ':
                    if (ch2 == 'ウ') {
                        builder.append("bō");
                        i++;
                    } else {
                        builder.append("bo");
                    }
                    break;
                case 'パ':
                    builder.append("pa");
                    break;
                case 'ピ':
                    if (ch2 == 'ョ' && ch3 == 'ウ') {
                        builder.append("pyō");
                        i += 2;
                    } else if (ch2 == 'ュ' && ch3 == 'ウ') {
                        builder.append("pyū");
                        i += 2;
                    } else if (ch2 == 'ャ') {
                        builder.append("pya");
                        i++;
                    } else if (ch2 == 'ョ') {
                        builder.append("pyo");
                        i++;
                    } else if (ch2 == 'ュ') {
                        builder.append("pyu");
                        i++;
                    } else if (ch2 == 'ェ') {
                        builder.append("pye");
                        i++;
                    } else {
                        builder.append("pi");
                    }
                    break;
                case 'プ':
                    builder.append("pu");
                    break;
                case 'ペ':
                    builder.append("pe");
                    break;
                case 'ポ':
                    if (ch2 == 'ウ') {
                        builder.append("pō");
                        i++;
                    } else {
                        builder.append("po");
                    }
                    break;
                case 'ヷ':
                    builder.append("va");
                    break;
                case 'ヸ':
                    builder.append("vi");
                    break;
                case 'ヹ':
                    builder.append("ve");
                    break;
                case 'ヺ':
                    builder.append("vo");
                    break;
                case 'ヴ':
                    if (ch2 == 'ィ' && ch3 == 'ェ') {
                        builder.append("vye");
                        i += 2;
                    } else {
                        builder.append('v');
                    }
                    break;
                case 'ァ':
                    builder.append('a');
                    break;
                case 'ィ':
                    builder.append('i');
                    break;
                case 'ゥ':
                    builder.append('u');
                    break;
                case 'ェ':
                    builder.append('e');
                    break;
                case 'ォ':
                    builder.append('o');
                    break;
                case 'ヮ':
                    builder.append("wa");
                    break;
                case 'ャ':
                    builder.append("ya");
                    break;
                case 'ュ':
                    builder.append("yu");
                    break;
                case 'ョ':
                    builder.append("yo");
                    break;
                case 'ー':
                    break;
                default:
                    builder.append(ch);
            }
        }
    }
}
