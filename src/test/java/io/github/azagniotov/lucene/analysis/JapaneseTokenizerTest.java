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

package io.github.azagniotov.lucene.analysis;

import static com.google.common.truth.Truth.assertThat;

import com.worksap.nlp.sudachi.dictionary.UserDictionaryBuilder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class JapaneseTokenizerTest {

    private static final Path systemDictPath;
    private static final Path userLexiconCsvPath;
    private static final Pattern WHITESPACE_REGEX = Pattern.compile("\\s+");

    static {
        final String systemDictPathStr = Objects.requireNonNull(
                        JapaneseTokenizerTest.class.getResource("/system-dict/system_core.dic"))
                .getPath();
        systemDictPath = Paths.get(systemDictPathStr);

        final String userLexiconCsvPathStr = Objects.requireNonNull(
                        JapaneseTokenizerTest.class.getResource("/user-dict/user_lexicon.csv"))
                .getPath();
        userLexiconCsvPath = Paths.get(userLexiconCsvPathStr);
    }

    private static JapaneseTokenizer japaneseTokenizer;

    @BeforeClass
    public static void beforeClass() throws Exception {
        final String currentDirectory =
                Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString();
        final String userDictFilename = String.join("/", currentDirectory, "user_lexicon.dic");
        UserDictionaryBuilder.main(
                new String[] {"-o", userDictFilename, "-s", systemDictPath.toString(), userLexiconCsvPath.toString()});
        japaneseTokenizer = JapaneseTokenizer.from(systemDictPath, Paths.get(userDictFilename));
    }

    @Test
    public void sanityCheck() {
        assertThat(japaneseTokenizer).isNotNull();
    }

    @Test
    public void isJapanesePunctuation() throws Exception {
        // http://www.rikai.com/library/kanjitables/kanji_codes.unicode.shtml
        final String punctuation = "、 。 〃 〄 々 〆 〇 〈 〉 《 》 「 」 『 』 【 】 〒 〓 〔 〕 "
                + "〖 〗 〘 〙 〚 〛 〜 〝 〞 〟 〠 〡 〢 〣 〤 〥 〦 〧 〨 〩 〪 〫 〬 〭 〮 〯  〰 〱 " + "〲 〳 〴 〵 〶 〷 〸 〹 〺 〻 〼 〽 〾 〿 ";
        for (final String punt : WHITESPACE_REGEX.split(punctuation)) {
            assertThat(japaneseTokenizer.isPunctuation(punt.trim())).isTrue();
        }
    }

    @Test
    public void isAsciiPunctuation() throws Exception {
        // !"#$%&'()*+,-./:;<=>?@[\]^_`{ }~:
        final String punctuation = "@ / ! . , + - : ; [ ] { } ~ _ ( ) # $ % & ' \" * < > ? ^ ` \\";
        for (final String punt : WHITESPACE_REGEX.split(punctuation)) {
            assertThat(japaneseTokenizer.isPunctuation(punt.trim())).isTrue();
        }
    }

    @Test
    public void isLatinOnePunctuation() throws Exception {
        final String punctuation = "· ¶ »";
        for (final String punt : WHITESPACE_REGEX.split(punctuation)) {
            assertThat(japaneseTokenizer.isPunctuation(punt.trim())).isTrue();
        }
    }

    @DataProvider(name = "queryTokens")
    public static Object[][] queryTokens() {
        return new Object[][] {
            {"令和", new Object[] {"令和"}},
            {"京都。東京.東京都。京都", new Object[] {"京都", "東京", "東京", "都", "京都"}},

            // https://raw.githubusercontent.com/WorksApplications/Sudachi/develop/docs/user_dict.md
            /* === Example: Start of custom tokenization using the user dictionary === */
            {"清水寺は東京都にあります", new Object[] {"清水寺", "は", "東京", "都", "に", "あり", "ます"}},
            {"にじさんじ", new Object[] {"にじさんじ"}},
            {"にじさんじましろ", new Object[] {"にじさんじ", "ましろ"}},
            {"にじさんじのましろ", new Object[] {"にじさんじ", "の", "ましろ"}},
            {"ちいかわ", new Object[] {"ちいかわ"}},
            {"くら寿司ちいかわ", new Object[] {"くら", "寿司", "ちいかわ"}},
            {"ぼのぼの", new Object[] {"ぼのぼの"}},
            {"ぼのぼのアニメ公式サイト", new Object[] {"ぼのぼの", "アニメ", "公式", "サイト"}},
            /* === Example: End of custom tokenization using the user dictionary === */

            {"お試し用(使い切り)", new Object[] {"お", "試し", "用", "使い", "切り"}},
            {"聖川真斗", new Object[] {"聖川", "真斗"}},
            {"IKEAの椅子", new Object[] {"IKEA", "の", "椅子"}},
            {"京都東部", new Object[] {"京都", "東部"}},
            {"水 / 化粧+水", new Object[] {"水", "化粧", "水"}},
            {"平成", new Object[] {"平成"}},
            {"Tシャツ", new Object[] {"Tシャツ"}},
            {"季節感···冬", new Object[] {"季節", "感", "冬"}},
            {"ユニクロポロシャツ", new Object[] {"ユニクロ", "ポロシャツ"}},
            {"アンパスィ", new Object[] {"アンパスィ"}},
            {"トラックボール", new Object[] {"トラック", "ボール"}},
            {"日本限定ソファ", new Object[] {"日本", "限定", "ソファ"}},
            {"吾輩は猫である", new Object[] {"吾輩", "は", "猫", "で", "ある"}},
            {"あなたが誰かを殺した", new Object[] {"あなた", "が", "誰", "か", "を", "殺し", "た"}},
            {"日本語【単話版】", new Object[] {"日本", "語", "単", "話", "版"}},
            {"日本語 単話版", new Object[] {"日本", "語", "単", "話", "版"}},
            {"アフラ・マヅダ", new Object[] {"アフラ・マヅダ"}},
            {"楷・行書", new Object[] {"楷・行書"}},
            {"日本語と「記号」の話し", new Object[] {"日本", "語", "と", "記号", "の", "話し"}},
            {"桃太郎電鉄", new Object[] {"桃太郎", "電鉄"}},
            {"甲斐田晴", new Object[] {"甲", "斐田", "晴"}},
            {"椎名ニキ", new Object[] {"椎名", "ニキ"}},
            {"シルバニア", new Object[] {"シルバニア"}},
            {"ポケカ", new Object[] {"ポケカ"}},
            {"おそ松", new Object[] {"おそ松"}},
            {"セカオワ", new Object[] {"セカオワ"}},
            {"ステラ ルー", new Object[] {"ステラ", "ルー"}},
            {"ステラルー", new Object[] {"ステラルー"}},
            {"パラライ", new Object[] {"パラライ"}},
            {"ルイヴィトン財布", new Object[] {"ルイヴィトン", "財布"}},
            {"終わりのセラフ", new Object[] {"終わり", "の", "セラフ"}},
            {"グランドセイコー", new Object[] {"グランド", "セイコー"}},
            {"神宮寺レン", new Object[] {"神宮寺", "レン"}},
            {"ストウブ", new Object[] {"ストウブ"}},
            {"マキタ", new Object[] {"マキタ"}},
            {"クロミ", new Object[] {"クロミ"}},
            {"すもももももももものうち", new Object[] {"すもも", "も", "もも", "も", "もも", "の", "うち"}},
            {"イーブイヒーローズbox未開封シュリンク", new Object[] {"イーブイヒーローズ", "box", "未", "開封", "シュリンク"}},
            {"いいいいいいいい", new Object[] {"いい", "いい", "いい", "いい"}},
        };
    }

    @Test(dataProvider = "queryTokens")
    public void testSanityCheckExcel(final Object query, final Object... expected) throws Exception {
        assertThat(japaneseTokenizer.tokenize(query.toString())).containsExactly(expected);
    }

    @Test
    public void tokenizerWithRepeatedWord() throws Exception {
        final int limit = 8;
        final String katakanaWord = "テスト";

        final StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[limit]).replace("\0", katakanaWord));

        final List<String> nCopies = Collections.nCopies(limit, katakanaWord);

        assertThat(nCopies.size()).isEqualTo(limit);
        assertThat(japaneseTokenizer.tokenize(sb.toString())).containsExactly(nCopies.toArray(new Object[0]));
    }
}
