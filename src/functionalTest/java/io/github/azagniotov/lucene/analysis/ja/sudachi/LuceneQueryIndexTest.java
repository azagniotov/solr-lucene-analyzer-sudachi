package io.github.azagniotov.lucene.analysis.ja.sudachi;

import io.github.azagniotov.lucene.analysis.ja.sudachi.analyzer.SudachiAnalyzer;
import java.io.File;
import java.nio.file.Files;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
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
        assertEquals(1, totalHits);

        final TopFieldCollector documentCollector = TopFieldCollector.create(Sort.RELEVANCE, 10, totalHits);
        indexSearcher.search(query, documentCollector);

        final ScoreDoc[] scoreDocs = documentCollector.topDocs().scoreDocs;
        assertEquals(1, scoreDocs.length);

        final Document found = indexSearcher.doc(scoreDocs[0].doc);
        assertEquals("すもももももももものうち。", found.get("content"));

        final TokenStream tokenStream = found.getField("content").tokenStream(analyzer, null);
        assertTokenStreamContents(tokenStream, new String[] {"すもも", "もも", "もも"});
    }

    private void registerDocuments(final Directory directory, final Analyzer analyzer) throws Exception {
        try (final IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
            indexWriter.addDocument(createDocument("1", "Apache Lucene 入門 ～Java・オープンソース・全文検索システムの構築"));
            indexWriter.addDocument(createDocument("2", "Apache Solr入門 ―オープンソース全文検索エンジン"));
            indexWriter.addDocument(createDocument("3", "Scalaスケーラブルプログラミング第2版"));
            indexWriter.addDocument(createDocument("4", "すもももももももものうち。"));
            indexWriter.addDocument(createDocument("5", "メガネは顔の一部です。"));
            indexWriter.addDocument(createDocument("6", "日本経済新聞でモバゲーの記事を読んだ。"));
            indexWriter.addDocument(createDocument("7", "Java, Scala, Groovy, Clojure"));
            indexWriter.addDocument(createDocument("8", "ＬＵＣＥＮＥ、ＳＯＬＲ、Lucene, Solr"));
            indexWriter.addDocument(createDocument("9", "ｱｲｳｴｵカキクケコさしすせそABCＸＹＺ123４５６"));
            indexWriter.addDocument(
                    createDocument("10", "Lucene is a full-featured text search engine library written in Java."));

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
}
