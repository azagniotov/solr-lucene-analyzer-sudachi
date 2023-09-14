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
        assertEquals(2, totalHits);

        final TopFieldCollector documentCollector =
                TopFieldCollector.create(Sort.RELEVANCE, 10, true, false, false, false);
        indexSearcher.search(query, documentCollector);

        final ScoreDoc[] scoreDocs = documentCollector.topDocs().scoreDocs;
        assertEquals(2, scoreDocs.length);

        final Document foundOne = indexSearcher.doc(scoreDocs[0].doc);
        final Document foundTwo = indexSearcher.doc(scoreDocs[1].doc);
        assertEquals("すもももももももものうち。", foundOne.get("content"));
        assertEquals("ももたろうは日本のおとぎ話の一つ。", foundTwo.get("content"));

        final TokenStream tokenStreamOne = foundOne.getField("content").tokenStream(analyzer, null);
        assertTokenStreamContents(tokenStreamOne, new String[] {"すもも", "もも", "もも"});

        final TokenStream tokenStreamTwo = foundTwo.getField("content").tokenStream(analyzer, null);
        // The "たろう" is removed by the Sudachi Analyzer because of:
        // 1. BaseForm filter:
        //    たろう => だ; and
        // 2. SudachiPartOfSpeechStopFilter:
        //    the auxiliary verb (助動詞) it is uncommented in the stoptags.txt,
        //    thus the token is removed from the token stream.
        assertTokenStreamContents(tokenStreamTwo, new String[] {"もも", "日本", "おとぎ話", "一"});
    }

    private void registerDocuments(final Directory directory, final Analyzer analyzer) throws Exception {
        try (final IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
            indexWriter.addDocument(createDocument("1", "すもももももももものうち。"));
            indexWriter.addDocument(createDocument("2", "ももたろうは日本のおとぎ話の一つ。"));

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
