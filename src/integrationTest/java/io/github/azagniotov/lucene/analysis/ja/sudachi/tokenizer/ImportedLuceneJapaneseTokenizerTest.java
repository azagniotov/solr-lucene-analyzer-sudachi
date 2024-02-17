/*
 * Copyright (c) 2017-2023 Works Applications Co., Ltd.
 * Modifications copyright (c) 2023 Alexander Zagniotov
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

import static com.worksap.nlp.sudachi.Tokenizer.SplitMode;

import com.worksap.nlp.sudachi.Config;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiBaseFormAttribute;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiPartOfSpeechAttribute;
import io.github.azagniotov.lucene.analysis.ja.sudachi.attributes.SudachiReadingFormAttribute;
import io.github.azagniotov.lucene.analysis.ja.sudachi.test.TestUtils;
import java.io.StringReader;
import java.util.Collections;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.tests.util.TestUtil;
import org.apache.lucene.util.UnicodeUtil;
import org.junit.Before;

public class ImportedLuceneJapaneseTokenizerTest extends BaseTokenStreamTestCase {

    private TestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.testUtils = new TestUtils(Config.defaultConfig());
    }

    public void testDecomposition_v1() throws Exception {
        final String input = "本来は、貧困層の女性や子供に医療保護を提供するために創設された制度である、アメリカ低所得者医療援助制度が、今日では、その予算の約３分の１を老人に費やしている。";
        final TokenStream tokenStream = this.testUtils.tokenize(input);

        assertTokenStreamContents(
                tokenStream,
                new String[] {
                    "本来", "は", "貧困", "層", "の", "女性", "や", "子供", "に", "医療", "保護", "を", "提供", "する", "ため", "に", "創設", "さ",
                    "れ", "た", "制度", "で", "ある", "アメリカ", "低", "所得", "者", "医療", "援助", "制度", "が", "今日", "で", "は", "その",
                    "予算", "の", "約", "３", "分", "の", "１", "を", "老人", "に", "費やし", "て", "いる"
                },
                new int[] {
                    0, 2, 4, 6, 7, 8, 10, 11, 13, 14, 16, 18, 19, 21, 23, 25, 26, 28, 29, 30, 31, 33, 34, 37, 41, 42,
                    44, 45, 47, 49, 51, 53, 55, 56, 58, 60, 62, 63, 64, 65, 66, 67, 68, 69, 71, 72, 75, 76
                },
                new int[] {
                    2, 3, 6, 7, 8, 10, 11, 13, 14, 16, 18, 19, 21, 23, 25, 26, 28, 29, 30, 31, 33, 34, 36, 41, 42, 44,
                    45, 47, 49, 51, 52, 55, 56, 57, 60, 62, 63, 64, 65, 66, 67, 68, 69, 71, 72, 75, 76, 78
                });
    }

    public void testDecomposition_v2() throws Exception {
        final String input = "麻薬の密売は根こそぎ絶やさなければならない";
        final TokenStream tokenStream = this.testUtils.tokenize(input);

        assertTokenStreamContents(
                tokenStream,
                new String[] {"麻薬", "の", "密売", "は", "根こそぎ", "絶やさ", "なけれ", "ば", "なら", "ない"},
                new int[] {0, 2, 3, 5, 6, 10, 13, 16, 17, 19},
                new int[] {2, 3, 5, 6, 10, 13, 16, 17, 19, 21});
    }

    public void testDecomposition_v3() throws Exception {
        final String input = "魔女狩大将マシュー・ホプキンス。";
        final TokenStream tokenStream = this.testUtils.tokenize(input);

        assertTokenStreamContents(
                tokenStream, new String[] {"魔女", "狩", "大将", "マシュー", "ホプキンス"}, new int[] {0, 2, 3, 5, 10}, new int[] {
                    2, 3, 5, 9, 15
                });
    }

    // The stop words will be filtered out by the Analyzer anyway
    public void testDecomposition_v4() throws Exception {
        final String input = "これは本ではない    ";
        final TokenStream tokenStream = this.testUtils.tokenize(input);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"これ", "は", "本", "で", "は", "ない"},
                new int[] {0, 2, 3, 4, 5, 6, 8},
                new int[] {2, 3, 4, 5, 6, 8, 9},
                12);
    }

    public void testDecomposition_v5() throws Exception {
        final String input = "くよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよ";
        final TokenStream tokenStream = this.testUtils.tokenize(input);

        assertTokenStreamContents(
                tokenStream,
                new String[] {"くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ"},
                new int[] {0, 4, 8, 12, 16, 20, 24, 28, 32, 36},
                new int[] {4, 8, 12, 16, 20, 24, 28, 32, 36, 40});
    }

    /**
     * Tests that sentence offset is incorporated into the resulting offsets
     */
    public void testTwoSentences() throws Exception {
        final String input = "魔女狩大将マシュー・ホプキンス。 魔女狩大将マシュー・ホプキンス。";
        final TokenStream tokenStream = this.testUtils.tokenize(input);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"魔女", "狩", "大将", "マシュー", "ホプキンス", "魔女", "狩", "大将", "マシュー", "ホプキンス"},
                new int[] {0, 2, 3, 5, 10, 17, 19, 20, 22, 27},
                new int[] {2, 3, 5, 9, 15, 19, 20, 22, 26, 32});
    }

    public void testSurrogates() throws Exception {
        final String input = "𩬅艱鍟䇹愯瀛";
        final TokenStream tokenStream = this.testUtils.tokenize(input);
        assertTokenStreamContents(tokenStream, new String[] {"𩬅", "艱", "鍟䇹", "愯瀛"});
    }

    public void testSurrogates_v2() throws Exception {
        final Analyzer analyzer = this.testUtils.makeDefaultAnalyzer();

        final int numIterations = atLeast(500);
        for (int i = 0; i < numIterations; i++) {
            final String s = TestUtil.randomUnicodeString(random(), 100);
            try (TokenStream ts = analyzer.tokenStream("foo", s)) {
                final CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
                ts.reset();
                while (ts.incrementToken()) {
                    assertTrue(UnicodeUtil.validUTF16String(termAtt));
                }
                ts.end();
            }
        }
    }

    public void testOnlyPunctuation() throws Exception {
        final Analyzer analyzerNoPunct = this.testUtils.makeNoPunctuationAnalyzer();
        try (TokenStream ts = analyzerNoPunct.tokenStream("foo", "。、。。")) {
            ts.reset();
            assertFalse(ts.incrementToken());
            ts.end();
        }
    }

    public void testOnlyPunctuationExtended() throws Exception {
        final Tokenizer tokenizer = this.testUtils.makeTokenizer(true, SplitMode.C);
        final Analyzer analyzerNoPunct = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                return new TokenStreamComponents(tokenizer, tokenizer);
            }
        };
        try (TokenStream ts = analyzerNoPunct.tokenStream("foo", "......")) {
            ts.reset();
            assertFalse(ts.incrementToken());
            ts.end();
        }
    }

    public void testSegmentation() throws Exception {
        final String inputOne = "ミシェル・クワンが優勝しました。スペースステーションに行きます。うたがわしい。";
        final TokenStream tokenStreamOne = this.testUtils.tokenize(inputOne, false, SplitMode.A);
        final String[] surfaceFormsOne = {
            "ミシェル", "・", "クワン", "が", "優勝", "し", "まし", "た", "。", "スペース", "ステーション", "に", "行き", "ます", "。", "うたがわしい", "。"
        };
        final Analyzer analyzerOne = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                return new TokenStreamComponents((Tokenizer) tokenStreamOne, tokenStreamOne);
            }
        };
        assertAnalyzesTo(analyzerOne, inputOne, surfaceFormsOne);

        final String inputTwo = "スペースステーションに行きます。うたがわしい。";
        final TokenStream tokenStreamTwo = this.testUtils.tokenize(inputTwo, false, SplitMode.A);
        final String[] surfaceFormsTwo = {"スペース", "ステーション", "に", "行き", "ます", "。", "うたがわしい", "。"};

        final Analyzer analyzerTwo = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                return new TokenStreamComponents((Tokenizer) tokenStreamTwo, tokenStreamTwo);
            }
        };
        assertAnalyzesTo(analyzerTwo, inputTwo, surfaceFormsTwo);
    }

    public void testReadings() throws Exception {
        assertReadings("寿司が食べたいです。", "スシ", "ガ", "タベ", "タイ", "デス", "。");
    }

    public void testReadings_v2() throws Exception {
        assertReadings("多くの学生が試験に落ちた。", "オオク", "ノ", "ガクセイ", "ガ", "シケン", "ニ", "オチ", "タ", "。");
    }

    public void testBasicForms() throws Exception {
        assertBaseForms("それはまだ実験段階にあります。", "それ", "は", "まだ", "実験", "段階", "に", "ある", "ます", "。");
    }

    public void testPartOfSpeech() throws Exception {
        assertPartsOfSpeech(
                "それはまだ実験段階にあります。", "代名詞", "助詞-係助詞", "副詞", "名詞-普通名詞", "名詞-普通名詞", "助詞-格助詞", "動詞-非自立可能", "助動詞", "補助記号-句点");
    }

    public void testCompoundOverPunctuation() throws Exception {
        final Tokenizer tokenizer = this.testUtils.makeTokenizer(true, SplitMode.A);
        final Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                return new TokenStreamComponents(tokenizer, tokenizer);
            }
        };
        assertAnalyzesToPositions(
                analyzer,
                "dεε϶ϢϏΎϷΞͺ羽田",
                new String[] {"d", "εε϶ϢϏΎϷΞ", "", "羽田"},
                new int[] {1, 1, 1, 1, 1},
                new int[] {1, 1, 1, 1, 1});
    }

    public void testBigDocument() throws Exception {
        // Modified version. Lucene Kuromoji test does only one iteration
        final String doc =
                "商品の購入・詳細(サイズ、画像)は商品名をクリックしてください！[L.B　CANDY　STOCK]フラワービジューベアドレス[L.B　DAILY　STOCK]ボーダーニットトップス［L.B　DAILY　STOCK］ボーダーロングニットOP［L.B　DAILY　STOCK］ロゴトートBAG［L.B　DAILY　STOCK］裏毛ロゴプリントプルオーバー【TVドラマ着用】アンゴラワッフルカーディガン【TVドラマ着用】グラフィティーバックリボンワンピース【TVドラマ着用】ボーダーハイネックトップス【TVドラマ着用】レオパードミッドカーフスカート【セットアップ対応商品】起毛ニットスカート【セットアップ対応商品】起毛ニットプルオーバー2wayサングラス33ナンバーリングニット3Dショルダーフレアードレス3周年スリッパ3周年ラグマット3周年ロックグラスキャンドルLily　Brown　2015年　福袋MIXニットプルオーバーPeckhamロゴニットアンゴラジャガードプルオーバーアンゴラタートルアンゴラチュニックアンゴラニットカーディガンアンゴラニットプルオーバーアンゴラフレアワンピースアンゴラロングカーディガンアンゴラワッフルカーディガンヴィンテージファー付コートヴィンテージボーダーニットヴィンテージレースハイネックトップスヴィンテージレースブラウスウエストシースルーボーダーワンピースオーガンジーラインフレアスカートオープンショルダーニットトップスオフショルシャーリングワンピースオフショルニットオフショルニットプルオーバーオフショルボーダーロンパースオフショルワイドコンビネゾンオルテガ柄ニットプルオーバーカシュクールオフショルワンピースカットアシンメトリードレスカットサテンプリーツフレアースカートカラースーパーハイウェストスキニーカラーブロックドレスカラーブロックニットチュニックギャザーフレアスカートキラキラストライプタイトスカートキラキラストライプドレスキルティングファーコートグラデーションベアドレスグラデーションラウンドサングラスグラフティーオフショルトップスグラフティーキュロットグリッターリボンヘアゴムクロップドブラウスケーブルハイウエストスカートコーデュロイ×スエードパネルスカートコーデュロイタイトスカートゴールドバックルベルト付スカートゴシックヒールショートブーツゴシック柄ニットワンピコンビスタジャンサイドステッチボーイズデニムパンツサスペつきショートパンツサスペンダー付プリーツロングスカートシャーリングタイトスカートジャガードタックワンピーススエードフリルフラワーパンツスエード裏毛肩空きトップススクエアショルダーBAGスクエアバックルショルダースクエアミニバッグストーンビーチサンダルストライプサスペ付きスキニーストライプバックスリットシャツスライバーシャギーコートタートル×レースタイトスカートタートルニットプルオーバータイトジャンパースカートダブルクロスチュールフレアスカートダブルストラップパンプスダブルハートリングダブルフェイスチェックストールチェーンコンビビジューネックレスチェーンコンビビジューピアスチェーンコンビビジューブレスチェーンツバ広HATチェーンビジューピアスチェックニットプルオーバーチェックネルミディアムスカートチェック柄スキニーパンツチュールコンビアシメトップスデニムフレアースカートドットオフショルフリルブラウスドットジャガードドレスドットニットプルオーバードットレーストップスニット×オーガンジースカートセットニットキャミソールワンピースニットスヌードパールコンビフープピアスハイウエストショートデニムハイウエストタイトスカートハイウエストデニムショートパンツハイウエストプリーツスカートハイウエストミッドカーフスカートハイゲージタートルニットハイゲージラインニットハイネック切り替えスウェットバタフライネックレスバタフライミニピアスバタフライリングバックタンクリブワンピースバックリボンスキニーデニムパンツバックリボン深Vワンピースビジューストラップサンダルビスチェコンビオフショルブラウスブークレジャガードニットフェイクムートンショートコートフェレットカーディガンフェレットビックタートルニットブラウジングクルーブラウスプリーツブラウスフリルニットプルオーバーフリンジニットプルオーバーフレアニットスカートブロウ型サングラスベーシックフェレットプルオーバーベルト付ガウチョパンツベルト付ショートパンツベルト付タックスカートベルト付タックパンツベルベットインヒールパンプスベロアウェッジパンプスベロアミッドカーフワンピースベロアワンピースベロア風ニットカーディガンボア付コートボーダーVネックTシャツボーダーオフショルカットソーボーダーカットソーワンピースボーダータイトカットソーボーダートップスボーダートップス×スカートセットボストンメガネマオカラーシャツニットセットミックスニットプルオーバーミッドカーフ丈ポンチスカートミリタリーギャザーショートパンツメッシュハイネックトップスメルトンPコートメルトンダッフルコートメルトンダブルコートモヘアニットカーディガンモヘアニットタートルユリ柄プリーツフレアースカートライダースデニムジャケットライナー付チェスターコートラッフルプリーツブラウスラメジャガードハイゲージニットリブニットワンピリボン×パールバレッタリボンバレッタリボンベルトハイウエストパンツリリー刺繍開襟ブラウスレースビスチェローファーサボロゴニットキャップロゴ刺繍ニットワッチロングニットガウンワッフルアンゴラプルオーバーワンショルダワーワンピース光沢ラメニットカーディガン刺繍シフォンブラウス台形ミニスカート配色ニットプルオーバー裏毛プルオーバー×オーガンジースカートセット";

        final int multiplicationFactor = 250;
        final String input = String.join("\n", Collections.nCopies(multiplicationFactor, doc));
        final Tokenizer tokenizer = this.testUtils.makeTokenizer(true, SplitMode.A);
        tokenizer.setReader(new StringReader(input));
        tokenizer.reset();

        int totalTokens = 0;
        while (tokenizer.incrementToken()) {
            totalTokens++;
        }

        assertEquals(51000, totalTokens);
    }

    private void assertReadings(final String input, String... readings) throws Exception {
        final Analyzer analyzer = this.testUtils.makeDefaultAnalyzer();
        try (final TokenStream ts = analyzer.tokenStream("ignored", input)) {
            final SudachiReadingFormAttribute<?> readingAtt = ts.addAttribute(SudachiReadingFormAttribute.class);
            ts.reset();
            for (final String readingForm : readings) {
                assertTrue(ts.incrementToken());
                assertEquals(readingAtt.getValue().orElseThrow(), readingForm);
            }
            assertFalse(ts.incrementToken());
            ts.end();
        }
    }

    private void assertBaseForms(final String input, String... baseForms) throws Exception {
        final Analyzer analyzer = this.testUtils.makeDefaultAnalyzer();
        try (final TokenStream ts = analyzer.tokenStream("ignored", input)) {
            final SudachiBaseFormAttribute baseFormAtt = ts.addAttribute(SudachiBaseFormAttribute.class);
            ts.reset();
            for (final String baseForm : baseForms) {
                assertTrue(ts.incrementToken());
                assertEquals(baseFormAtt.getValue().orElseThrow(), baseForm);
            }
            assertFalse(ts.incrementToken());
            ts.end();
        }
    }

    private void assertPartsOfSpeech(final String input, String... partsOfSpeech) throws Exception {
        final Analyzer analyzer = this.testUtils.makeDefaultAnalyzer();
        try (final TokenStream ts = analyzer.tokenStream("ignored", input)) {
            final SudachiPartOfSpeechAttribute partOfSpeechAtt = ts.addAttribute(SudachiPartOfSpeechAttribute.class);
            ts.reset();
            for (final String partOfSpeech : partsOfSpeech) {
                assertTrue(ts.incrementToken());
                assertEquals(partOfSpeechAtt.getValue().orElseThrow(), partOfSpeech);
            }
            assertFalse(ts.incrementToken());
            ts.end();
        }
    }
}
