package io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer;

import static com.worksap.nlp.sudachi.Tokenizer.SplitMode;

import com.worksap.nlp.sudachi.Config;
import io.github.azagniotov.lucene.analysis.ja.sudachi.test.TestUtils;
import java.nio.charset.Charset;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
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

    public void testDecompositionPortedFromLucene_v1() throws Exception {
        final String input = "本来は、貧困層の女性や子供に医療保護を提供するために創設された制度である、アメリカ低所得者医療援助制度が、今日では、その予算の約３分の１を老人に費やしている。";
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.A);

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

    public void testDecompositionPortedFromLucene_v2() throws Exception {
        final String input = "麻薬の密売は根こそぎ絶やさなければならない";
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.A);

        assertTokenStreamContents(
                tokenStream,
                new String[] {"麻薬", "の", "密売", "は", "根こそぎ", "絶やさ", "なけれ", "ば", "なら", "ない"},
                new int[] {0, 2, 3, 5, 6, 10, 13, 16, 17, 19},
                new int[] {2, 3, 5, 6, 10, 13, 16, 17, 19, 21});
    }

    public void testDecompositionPortedFromLucene_v3() throws Exception {
        final String input = "くよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよくよ";
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.A);

        assertTokenStreamContents(
                tokenStream,
                new String[] {"くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ", "くよくよ"},
                new int[] {0, 4, 8, 12, 16, 20, 24, 28, 32, 36},
                new int[] {4, 8, 12, 16, 20, 24, 28, 32, 36, 40});
    }

    public void testSurrogatesPortedFromLucene() throws Exception {
        final String input = "𩬅艱鍟䇹愯瀛";
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.A);
        assertTokenStreamContents(tokenStream, new String[] {"𩬅", "艱", "鍟䇹愯瀛"});
    }

    // The stop words will be filtered out by the Analyzer anyway
    public void testStopWords() throws Exception {
        final String input = "これは本ではない    ";
        final TokenStream tokenStream = this.testUtils.tokenize(input, true, SplitMode.A);
        assertTokenStreamContents(
                tokenStream,
                new String[] {"これ", "は", "本", "で", "は", "ない"},
                new int[] {0, 2, 3, 4, 5, 6, 8},
                new int[] {2, 3, 4, 5, 6, 8, 9},
                12);
    }
}
