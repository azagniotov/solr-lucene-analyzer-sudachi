package io.github.azagniotov.lucene.analysis.ja.sudachi.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import io.github.azagniotov.lucene.analysis.ja.sudachi.analyzer.SudachiAnalyzer;
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiBaseFormFilterFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiKatakanaStemFilter;
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiPartOfSpeechStopFilterFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer.SudachiTokenizerFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.NoOpResourceLoader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKWidthFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.util.AttributeFactory;
import org.testng.annotations.Test;

public class TokenStreamTesterTest {

    @Test
    public void assertTokenStreamContents() throws Exception {

        final Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(final String fieldName) {
                try {
                    Tokenizer tokenizer = createTokenizer(new HashMap<>());
                    TokenStream stream = tokenizer;

                    stream = new SudachiBaseFormFilterFactory(new HashMap<>()).create(stream);
                    stream = new SudachiPartOfSpeechStopFilterFactory(new HashMap<>()).create(stream);
                    stream = new CJKWidthFilterFactory(new HashMap<>()).create(stream);
                    stream = new StopFilter(stream, SudachiAnalyzer.getDefaultStopSet());
                    stream = new SudachiKatakanaStemFilter(stream);
                    stream = new LowerCaseFilter(stream);
                    return new TokenStreamComponents(tokenizer, stream);
                } catch (IOException iox) {
                    throw new UncheckedIOException(iox);
                }
            }
        };

        // うち will be filtered out because it is a stop word
        TokenStreamTester.assertTokenStreamContents(analyzer, "すもももももももものうち。", "すもももももも", "もも");
        TokenStreamTester.assertTokenStreamContents(analyzer, "清水寺は東京都にあります。", "清水寺", "東京", "都");

        // The "たろう" is removed by the Sudachi Analyzer because of:
        // 1. BaseForm filter:
        //    たろう => だ; and
        // 2. SudachiPartOfSpeechStopFilter:
        //    the auxiliary verb (助動詞) it is uncommented in the stoptags.txt,
        //    thus the だ token is removed from the token stream.
        TokenStreamTester.assertTokenStreamContents(analyzer, "ももたろう", "もも");
    }

    @Test
    public void failsAssertingTokenStreamContents() throws Exception {

        final Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(final String fieldName) {
                try {
                    Tokenizer tokenizer = createTokenizer(new HashMap<>());
                    TokenStream stream = tokenizer;

                    stream = new SudachiBaseFormFilterFactory(new HashMap<>()).create(stream);
                    stream = new SudachiPartOfSpeechStopFilterFactory(new HashMap<>()).create(stream);
                    stream = new CJKWidthFilterFactory(new HashMap<>()).create(stream);
                    stream = new StopFilter(stream, SudachiAnalyzer.getDefaultStopSet());
                    stream = new SudachiKatakanaStemFilter(stream);
                    stream = new LowerCaseFilter(stream);
                    return new TokenStreamComponents(tokenizer, stream);
                } catch (IOException iox) {
                    throw new UncheckedIOException(iox);
                }
            }
        };

        AssertionError error = assertThrows(AssertionError.class, () -> {
            TokenStreamTester.assertTokenStreamContents(analyzer, "すもももももももものうち。", "");
        });
        assertEquals(error.getMessage(), "Expected tokens list size 0 is not equal to the actual tokens list size 2");

        error = assertThrows(AssertionError.class, () -> {
            TokenStreamTester.assertTokenStreamContents(analyzer, "", "すもももももも", "もも");
        });
        assertEquals(error.getMessage(), "Expected tokens list size 2 is not equal to the actual tokens list size 0");

        error = assertThrows(AssertionError.class, () -> {
            TokenStreamTester.assertTokenStreamContents(analyzer, "東京", "東東");
        });
        assertEquals(error.getMessage(), "Assertion failed. Actual tokens are: [東京]");

        error = assertThrows(AssertionError.class, () -> {
            TokenStreamTester.assertTokenStreamContents(analyzer, "東東", "京", "京");
        });
        assertEquals(error.getMessage(), "Assertion failed. Actual tokens are: [東, 東]");
    }

    private Tokenizer createTokenizer(final Map<String, String> args) throws IOException {
        final Map<String, String> map = new HashMap<>(args);
        map.put("mode", "search");
        map.put("discardPunctuation", "true");
        final SudachiTokenizerFactory factory = new SudachiTokenizerFactory(map);
        factory.inform(new NoOpResourceLoader());

        return factory.create(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
    }
}
