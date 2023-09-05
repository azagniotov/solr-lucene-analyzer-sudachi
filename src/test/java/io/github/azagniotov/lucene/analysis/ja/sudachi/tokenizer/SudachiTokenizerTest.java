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
package io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer;

import static com.google.common.truth.Truth.assertThat;
import static org.apache.lucene.analysis.TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.MorphemeList;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.Strings;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SudachiTokenizerTest {

    private static SudachiTokenizer sudachiTokenizer;
    private static final boolean DISCARD_PUNCTUATION = true;

    @BeforeClass
    public static void beforeClass() throws Exception {
        final Map<String, String> args = new HashMap<String, String>() {
            {
                put("mode", "search");
                put("discardPunctuation", String.valueOf(DISCARD_PUNCTUATION));
            }
        };
        final SudachiTokenizerFactory sudachiTokenizerFactory =
                new SudachiTokenizerFactory(args, Config.defaultConfig());
        sudachiTokenizer = (SudachiTokenizer) sudachiTokenizerFactory.create(DEFAULT_TOKEN_ATTRIBUTE_FACTORY);
    }

    @Test
    public void sanityCheck() {
        assertThat(sudachiTokenizer).isNotNull();
    }

    @DataProvider(name = "querySurfaces")
    public static Object[][] querySurfaces() {
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

    @Test(dataProvider = "querySurfaces")
    public void testTokenizeSurfaces(final Object query, final Object... expected) throws Exception {
        final Reader stringReader = new StringReader(query.toString());
        assertThat(tokens(sudachiTokenizer.tokenize(stringReader))).containsExactly(expected);
    }

    private List<String> tokens(final Iterator<MorphemeList> morphemeList) {
        final List<Morpheme> result = new ArrayList<>();

        for (final Iterator<MorphemeList> it = morphemeList; it.hasNext(); ) {
            final MorphemeList sentence = it.next();

            sentence.iterator().forEachRemaining(morpheme -> {
                if (DISCARD_PUNCTUATION) {
                    if (!Strings.isPunctuation(morpheme.normalizedForm())) {
                        result.add(morpheme);
                    }
                } else {
                    result.add(morpheme);
                }
            });
        }

        return result.stream().map(Morpheme::surface).map(String::trim).collect(Collectors.toList());
    }
}
