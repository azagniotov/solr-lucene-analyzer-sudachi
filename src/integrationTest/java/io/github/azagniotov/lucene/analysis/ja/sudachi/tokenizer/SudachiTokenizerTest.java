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
import io.github.azagniotov.lucene.analysis.ja.sudachi.cache.DictionaryCache;
import io.github.azagniotov.lucene.analysis.ja.sudachi.test.TestUtils;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.charfilter.MappingCharFilter;
import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SudachiTokenizerTest extends BaseTokenStreamTestCase {

    private TestUtils testUtils;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        final Config config = Config.fromClasspath("sudachi_test_config.json");
        this.testUtils = new TestUtils(config);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        DictionaryCache.INSTANCE.invalidate();
    }

    @Test
    public void incrementTokenWithShiftJis() throws Exception {
        final Charset sjis = Charset.forName("Shift_JIS");
        final String input = new String("東京都に行った。".getBytes(sjis), sjis);
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.C);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"東京都", "に", "行っ", "た"},
                new int[] {0, 3, 4, 6},
                new int[] {3, 4, 6, 7},
                new int[] {1, 1, 1, 1},
                new int[] {1, 1, 1, 1},
                8);
    }

    @Test
    public void incrementTokenByDefaultMode() throws Exception {
        final TokenStream tokenStream = this.testUtils.tokenize("東京都に行った。", true, SplitMode.C);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"東京都", "に", "行っ", "た"},
                new int[] {0, 3, 4, 6},
                new int[] {3, 4, 6, 7},
                new int[] {1, 1, 1, 1},
                new int[] {1, 1, 1, 1},
                8);
    }

    @Test
    public void incrementTokenByPunctuationMode() throws Exception {
        final TokenStream tokenStream = this.testUtils.tokenize("東京都に行った。", false, SplitMode.C);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"東京都", "に", "行っ", "た", "。"},
                new int[] {0, 3, 4, 6, 7},
                new int[] {3, 4, 6, 7, 8},
                new int[] {1, 1, 1, 1, 1},
                new int[] {1, 1, 1, 1, 1},
                8);
    }

    @Test
    public void incrementTokenWithPunctuationsByDefaultMode() throws Exception {
        final TokenStream tokenStream = this.testUtils.tokenize("東京都に行った。東京都に行った。", true, SplitMode.C);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"東京都", "に", "行っ", "た", "東京都", "に", "行っ", "た"},
                new int[] {0, 3, 4, 6, 8, 11, 12, 14},
                new int[] {3, 4, 6, 7, 11, 12, 14, 15},
                new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1},
                new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1},
                16);
    }

    @Test
    public void incrementTokenWithPunctuationsByPunctuationMode() throws Exception {
        final TokenStream tokenStream = this.testUtils.tokenize("東京都に行った。東京都に行った。", false, SplitMode.C);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"東京都", "に", "行っ", "た", "。", "東京都", "に", "行っ", "た", "。"},
                new int[] {0, 3, 4, 6, 7, 8, 11, 12, 14, 15},
                new int[] {3, 4, 6, 7, 8, 11, 12, 14, 15, 16},
                new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                16);
    }

    @Test
    public void incrementTokenWithOOVByDefaultMode() throws Exception {
        final TokenStream tokenStream = this.testUtils.tokenize("アマゾンに行った。", true, SplitMode.C);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"アマゾン", "に", "行っ", "た"},
                new int[] {0, 4, 5, 7},
                new int[] {4, 5, 7, 8},
                new int[] {1, 1, 1, 1},
                new int[] {1, 1, 1, 1},
                9);
    }

    @Test
    public void incrementTokenWithOOVByPunctuationMode() throws Exception {
        final TokenStream tokenStream = this.testUtils.tokenize("アマゾンに行った。", false, SplitMode.C);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"アマゾン", "に", "行っ", "た", "。"},
                new int[] {0, 4, 5, 7, 8},
                new int[] {4, 5, 7, 8, 9},
                new int[] {1, 1, 1, 1, 1},
                new int[] {1, 1, 1, 1, 1},
                9);
    }

    @Test
    public void incrementTokenByAMode() throws Exception {
        final TokenStream tokenStream = this.testUtils.tokenize("東京都に行った。", true, SplitMode.A);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"東京", "都", "に", "行っ", "た"},
                new int[] {0, 2, 3, 4, 6},
                new int[] {2, 3, 4, 6, 7},
                new int[] {1, 1, 1, 1, 1},
                new int[] {1, 1, 1, 1, 1},
                8);
    }

    @Test
    public void incrementTokenByBMode() throws Exception {
        final TokenStream tokenStream = this.testUtils.tokenize("東京都に行った。", true, SplitMode.B);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"東京都", "に", "行っ", "た"},
                new int[] {0, 3, 4, 6},
                new int[] {3, 4, 6, 7},
                new int[] {1, 1, 1, 1},
                new int[] {1, 1, 1, 1},
                8);
    }

    @Test
    public void incrementTokenWithCorrectOffset() throws Exception {
        final NormalizeCharMap.Builder builder = new NormalizeCharMap.Builder();
        builder.add("東京都", "京都");
        final MappingCharFilter filter = new MappingCharFilter(builder.build(), new StringReader("東京都に行った。"));
        final Tokenizer tokenizer = this.testUtils.makeTokenizer(true, SplitMode.C);
        tokenizer.setReader(filter);
        assertTokenStreamContents(
                tokenizer,
                new String[] {"京都", "に", "行っ", "た"},
                new int[] {0, 3, 4, 6},
                new int[] {3, 4, 6, 7},
                new int[] {1, 1, 1, 1},
                new int[] {1, 1, 1, 1},
                8);
    }

    @Test
    public void testRepeatedHiraganaWord() throws Exception {
        final int limit = 500;
        final String hiraganaWord = "くよくよ";

        final StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[limit]).replace("\0", hiraganaWord));

        final List<String> nCopies = Collections.nCopies(limit, hiraganaWord);
        final TokenStream tokenStream = this.testUtils.tokenize(sb.toString(), true, SplitMode.A);

        assertTokenStreamContents(tokenStream, nCopies.toArray(new String[0]));
    }

    @Test
    public void testRepeatedKatakanaWord() throws Exception {
        // When MeCabOovProviderPlugin is in use, a repeated Katakana word string does not get tokenized
        // https://github.com/WorksApplications/Sudachi/issues/216

        final int limit = 500;
        final String katakanaWord = "テスト";

        final StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[limit]).replace("\0", katakanaWord));

        final List<String> nCopies = Collections.nCopies(limit, katakanaWord);
        final TokenStream tokenStream = this.testUtils.tokenize(sb.toString(), true, SplitMode.A);

        assertTokenStreamContents(tokenStream, nCopies.toArray(new String[0]));
    }

    @Test
    public void testRepeatedKanjiWord() throws Exception {
        final int limit = 500;
        final String kanjiWord = "令和";

        final StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[limit]).replace("\0", kanjiWord));

        final List<String> nCopies = Collections.nCopies(limit, kanjiWord);
        final TokenStream tokenStream = this.testUtils.tokenize(sb.toString(), true, SplitMode.A);

        assertTokenStreamContents(tokenStream, nCopies.toArray(new String[0]));
    }

    public void testBigDocument() throws Exception {
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

        // Because of the custom test config Config.fromClasspath("sudachi_test_config.json"),
        // the number of resulting tokens is large
        assertEquals(170500, totalTokens);
    }
}
