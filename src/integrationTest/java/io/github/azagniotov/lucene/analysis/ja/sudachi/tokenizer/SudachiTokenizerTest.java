package io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer;

import static com.worksap.nlp.sudachi.Tokenizer.*;

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
}
