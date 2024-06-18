/*
 * Copyright (c) 2023-2024 Alexander Zagniotov
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
package io.github.azagniotov.lucene.analysis.ja.sudachi;

import io.github.azagniotov.lucene.analysis.ja.sudachi.analyzer.SudachiAnalyzer;
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiBaseFormFilterFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiKatakanaStemFilter;
import io.github.azagniotov.lucene.analysis.ja.sudachi.filters.SudachiPartOfSpeechStopFilterFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.tokenizer.SudachiTokenizerFactory;
import io.github.azagniotov.lucene.analysis.ja.sudachi.util.NoOpResourceLoader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cjk.CJKWidthFilterFactory;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.util.CharsRef;
import org.junit.Test;

public class LuceneQueryIndexTest extends BaseTokenStreamTestCase {
    private Analyzer analyzer;

    private MMapDirectory ramDirectory;

    private File indexTempDirectory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        analyzer = new SudachiAnalyzer(
                SudachiAnalyzer.getDefaultStopSet(), SudachiAnalyzer.getDefaultStopTags(), true, "search");

        indexTempDirectory = Files.createTempDirectory("tmpDirPrefix").toFile();

        ramDirectory = new MMapDirectory(indexTempDirectory.toPath());
        registerDocuments(ramDirectory, analyzer);

        indexTempDirectory.deleteOnExit();
    }

    @Override
    public void tearDown() throws Exception {
        analyzer.close();
        ramDirectory.close();
        super.tearDown();
    }

    @Test
    public void queryIndex() throws Exception {
        final DirectoryReader directoryReader = DirectoryReader.open(ramDirectory);
        final IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        final QueryParser queryParser = new QueryParser("content", analyzer);

        final Query query = queryParser.parse("もも");
        final TotalHitCountCollector totalHitCountCollector = new TotalHitCountCollector();
        indexSearcher.search(query, totalHitCountCollector);

        final int totalHits = totalHitCountCollector.getTotalHits();
        assertEquals(2, totalHits);

        final TopFieldCollector documentCollector = TopFieldCollector.create(Sort.RELEVANCE, 10, totalHits);
        indexSearcher.search(query, documentCollector);

        final ScoreDoc[] scoreDocs = documentCollector.topDocs().scoreDocs;
        assertEquals(2, scoreDocs.length);

        final Document foundOne = indexSearcher.doc(scoreDocs[0].doc);
        final Document foundTwo = indexSearcher.doc(scoreDocs[1].doc);
        assertEquals("すもももももももものうち。", foundOne.get("content"));
        assertEquals("ももたろうは日本のおとぎ話の一つ。", foundTwo.get("content"));

        final TokenStream tokenStreamOne = foundOne.getField("content").tokenStream(analyzer, null);
        // 'Full' dictionary by Sudachi does not split this properly to すもも and もも
        assertTokenStreamContents(tokenStreamOne, new String[] {"すもももももも", "もも"});

        final TokenStream tokenStreamTwo = foundTwo.getField("content").tokenStream(analyzer, null);
        // The "たろう" is removed by the Sudachi Analyzer because of:
        // 1. BaseForm filter:
        //    たろう => だ; and
        // 2. SudachiPartOfSpeechStopFilter:
        //    the auxiliary verb (助動詞) it is uncommented in the stoptags.txt,
        //    thus the token is removed from the token stream.
        assertTokenStreamContents(tokenStreamTwo, new String[] {"もも", "日本", "おとぎ話", "一"});
    }

    @Test
    public void katakanaUniqloPoloShirtQueryUniqlo() throws Exception {
        final DirectoryReader directoryReader = DirectoryReader.open(ramDirectory);
        final IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        final QueryParser queryParser = new QueryParser("content", analyzer);

        final Query query = queryParser.parse("ユニクロ");
        final TotalHitCountCollector totalHitCountCollector = new TotalHitCountCollector();
        indexSearcher.search(query, totalHitCountCollector);

        final int totalHits = totalHitCountCollector.getTotalHits();
        assertEquals(1, totalHits);

        final TopFieldCollector documentCollector = TopFieldCollector.create(Sort.RELEVANCE, 10, totalHits);
        indexSearcher.search(query, documentCollector);

        final ScoreDoc[] scoreDocs = documentCollector.topDocs().scoreDocs;
        assertEquals(1, scoreDocs.length);

        final Document found = indexSearcher.doc(scoreDocs[0].doc);
        assertEquals("ユニクロポロシャツ", found.get("content"));

        final TokenStream tokenStreamOne = found.getField("content").tokenStream(analyzer, null);
        assertTokenStreamContents(tokenStreamOne, new String[] {"ユニクロ", "ポロシャツ"});
    }

    @Test
    public void katakanaUniqloPoloShirtQueryPoloShirt() throws Exception {
        final DirectoryReader directoryReader = DirectoryReader.open(ramDirectory);
        final IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        final QueryParser queryParser = new QueryParser("content", analyzer);

        final Query query = queryParser.parse("ポロシャツ");
        final TotalHitCountCollector totalHitCountCollector = new TotalHitCountCollector();
        indexSearcher.search(query, totalHitCountCollector);

        final int totalHits = totalHitCountCollector.getTotalHits();
        assertEquals(1, totalHits);

        final TopFieldCollector documentCollector = TopFieldCollector.create(Sort.RELEVANCE, 10, totalHits);
        indexSearcher.search(query, documentCollector);

        final ScoreDoc[] scoreDocs = documentCollector.topDocs().scoreDocs;
        assertEquals(1, scoreDocs.length);

        final Document found = indexSearcher.doc(scoreDocs[0].doc);
        assertEquals("ユニクロポロシャツ", found.get("content"));

        final TokenStream tokenStreamOne = found.getField("content").tokenStream(analyzer, null);
        assertTokenStreamContents(tokenStreamOne, new String[] {"ユニクロ", "ポロシャツ"});
    }

    @Test
    public void queryIndexWithSynonyms() throws Exception {

        System.out.println("Writing index to " + indexTempDirectory.getCanonicalPath());
        final Analyzer analyzerWithSynonym = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(final String fieldName) {
                try {
                    Tokenizer tokenizer = createTokenizer(new HashMap<>());
                    TokenStream stream = tokenizer;

                    stream = new SynonymGraphFilter(stream, getSynonymMap(), true);
                    stream = new FlattenGraphFilter(stream);
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

        registerDocuments(ramDirectory, analyzerWithSynonym);

        final DirectoryReader directoryReader = DirectoryReader.open(ramDirectory);
        final IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        final QueryParser queryParser = new QueryParser("content", analyzerWithSynonym);

        ///////////////////////////////////////////////////////////////////////////////////
        // FIRST QUERY: 新生児
        ///////////////////////////////////////////////////////////////////////////////////
        final Query queryOne = queryParser.parse("新生児");
        final TotalHitCountCollector totalHitCountCollectorOne = new TotalHitCountCollector();
        indexSearcher.search(queryOne, totalHitCountCollectorOne);

        final int totalHitsOne = totalHitCountCollectorOne.getTotalHits();
        assertEquals(3, totalHitsOne);

        final TopFieldCollector documentCollector = TopFieldCollector.create(Sort.RELEVANCE, 10, totalHitsOne);
        indexSearcher.search(queryOne, documentCollector);

        final ScoreDoc[] scoreDocsOne = documentCollector.topDocs().scoreDocs;
        assertEquals(3, scoreDocsOne.length);

        assertEquals("新生児", indexSearcher.doc(scoreDocsOne[0].doc).get("content"));
        assertEquals("赤ちゃん", indexSearcher.doc(scoreDocsOne[1].doc).get("content"));
        assertEquals("児", indexSearcher.doc(scoreDocsOne[2].doc).get("content"));

        ///////////////////////////////////////////////////////////////////////////////////
        // SECOND QUERY: 赤ちゃん
        ///////////////////////////////////////////////////////////////////////////////////
        final Query queryTwo = queryParser.parse("赤ちゃん");
        final TotalHitCountCollector totalHitCountCollectorTwo = new TotalHitCountCollector();
        indexSearcher.search(queryTwo, totalHitCountCollectorTwo);

        final int totalHitsTwo = totalHitCountCollectorTwo.getTotalHits();
        assertEquals(3, totalHitsTwo);

        final TopFieldCollector documentCollectorTwo = TopFieldCollector.create(Sort.RELEVANCE, 10, totalHitsTwo);
        indexSearcher.search(queryTwo, documentCollectorTwo);

        final ScoreDoc[] scoreDocsTwo = documentCollectorTwo.topDocs().scoreDocs;
        assertEquals(3, scoreDocsTwo.length);

        assertEquals("赤ちゃん", indexSearcher.doc(scoreDocsTwo[0].doc).get("content"));
        assertEquals("児", indexSearcher.doc(scoreDocsTwo[1].doc).get("content"));
        assertEquals("新生児", indexSearcher.doc(scoreDocsTwo[2].doc).get("content"));
    }

    private SynonymMap getSynonymMap() throws IOException {

        final SynonymMap.Builder synMapBuilder = new SynonymMap.Builder(Boolean.TRUE);
        // Word => Synonym
        synMapBuilder.add(new CharsRef("赤ちゃん"), new CharsRef("児"), Boolean.TRUE);
        synMapBuilder.add(new CharsRef("赤ちゃん"), new CharsRef("新生児"), Boolean.TRUE);

        return synMapBuilder.build();
    }

    private void registerDocuments(final Directory directory, final Analyzer analyzer) throws Exception {
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        indexWriterConfig.setCodec(new SimpleTextCodec());

        try (final IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
            indexWriter.addDocument(createDocument("1", "すもももももももものうち。"));
            indexWriter.addDocument(createDocument("2", "ももたろうは日本のおとぎ話の一つ。"));
            indexWriter.addDocument(createDocument("3", "赤ちゃん"));
            indexWriter.addDocument(createDocument("4", "新生児"));
            indexWriter.addDocument(createDocument("5", "児"));
            indexWriter.addDocument(createDocument("6", "ユニクロポロシャツ"));

            indexWriter.commit();
        }
    }

    private Document createDocument(final String id, final String text) {
        final Document document = new Document();
        document.add(new StringField("id", id, Field.Store.YES));
        document.add(new TextField("terms", text, Field.Store.YES));
        document.add(new TextField("content", text, Field.Store.YES));
        return document;
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
