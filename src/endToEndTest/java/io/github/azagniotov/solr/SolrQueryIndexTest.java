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
package io.github.azagniotov.solr;

import static org.apache.solr.client.solrj.request.AbstractUpdateRequest.*;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SolrQueryIndexTest extends SolrTestCaseJ4 {

    private static final String COLLECTION_NAME = "collection1";

    @BeforeClass
    public static void beforeClass() throws Exception {
        String tmpSolrHome = createTempDir().toFile().getAbsolutePath();
        FileUtils.copyDirectory(new File(TEST_HOME()), new File(tmpSolrHome).getAbsoluteFile());
        initCore("solrconfig.xml", "schema.xml", new File(tmpSolrHome).getAbsolutePath());

        indexDocument("1", "すもももももももものうち。");
        indexDocument("2", "ももたろうは日本のおとぎ話の一つ。");

        // The following content are each other synonyms, see:
        // resources/solr/collection1/lang/synonyms_ja.txt
        indexDocument("3", "赤ちゃん");
        indexDocument("4", "赤ん坊");
        indexDocument("5", "乳飲み子");

        indexDocument("6", "ユニクロポロシャツ");
    }

    @AfterClass
    public static void afterClass() {
        // Do nothing
    }

    @Test
    public void testMatchesWithSynonyms() throws Exception {
        final SolrQuery solrQuery = new SolrQuery();

        ///////////////////////////////////////////////////////////////////////////////////
        // FIRST QUERY: 赤ちゃん. '赤ん坊,乳飲み子' are synonyms
        ///////////////////////////////////////////////////////////////////////////////////
        solrQuery.setQuery("terms_ja:赤ちゃん");

        final QueryRequest queryRequestOne = new QueryRequest(solrQuery);
        final QueryResponse queryResponseOne = queryRequestOne.process(getSolrCore());

        assertEquals(3, queryResponseOne.getResults().size());
        assertEquals("赤ちゃん", queryResponseOne.getResults().get(0).get("terms_ja"));
        assertEquals("赤ん坊", queryResponseOne.getResults().get(1).get("terms_ja"));
        assertEquals("乳飲み子", queryResponseOne.getResults().get(2).get("terms_ja"));

        ///////////////////////////////////////////////////////////////////////////////////
        // SECOND QUERY: 乳飲み
        ///////////////////////////////////////////////////////////////////////////////////
        // Internally, synonym 乳飲み子 will be tokenized to '乳飲み, 子' by the provided tokenizer
        // to the SynonymGraphFilterFactory, see: resources/solr/collection1/conf/schema.xml
        // Therefore, query '乳飲み' will match its synonyms: '赤ちゃん,赤ん坊,乳飲み子'
        solrQuery.setQuery("terms_ja:乳飲み");

        final QueryRequest queryRequestTwo = new QueryRequest(solrQuery);
        final QueryResponse queryResponseTwo = queryRequestTwo.process(getSolrCore());

        assertEquals(3, queryResponseTwo.getResults().size());
        assertEquals("赤ちゃん", queryResponseTwo.getResults().get(0).get("terms_ja"));
        assertEquals("赤ん坊", queryResponseTwo.getResults().get(1).get("terms_ja"));
        assertEquals("乳飲み子", queryResponseTwo.getResults().get(2).get("terms_ja"));
    }

    @Test
    public void testMatchesAllIndexedDocumentsByCommonTerm() throws Exception {
        final SolrQuery solrQuery = new SolrQuery();

        // 'もも' term appears in both indexed documents
        solrQuery.setQuery("terms_ja:もも");

        final QueryRequest queryRequest = new QueryRequest(solrQuery);
        final QueryResponse queryResponse = queryRequest.process(getSolrCore());

        assertEquals(2, queryResponse.getResults().size());
        assertEquals("すもももももももものうち。", queryResponse.getResults().get(0).get("terms_ja"));
        assertEquals("ももたろうは日本のおとぎ話の一つ。", queryResponse.getResults().get(1).get("terms_ja"));
    }

    @Test
    public void testMatchesSingleIndexedDocumentByTerm() throws Exception {
        final SolrQuery solrQuery = new SolrQuery();

        // 'Full' dictionary by Sudachi does not split this properly to すもも and もも, thus, must pass in
        // 'すもももももも'
        solrQuery.setQuery("terms_ja:すもももももも");

        final QueryRequest queryRequest = new QueryRequest(solrQuery);
        final QueryResponse queryResponse = queryRequest.process(getSolrCore());

        assertEquals(1, queryResponse.getResults().size());
        assertEquals("すもももももももものうち。", queryResponse.getResults().get(0).get("terms_ja"));
    }

    @Test
    public void testKatakanaUniqloPoloShirtQueryUniqlo() throws Exception {
        final SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery("terms_ja:ユニクロ");

        final QueryRequest queryRequest = new QueryRequest(solrQuery);
        final QueryResponse queryResponse = queryRequest.process(getSolrCore());

        assertEquals(1, queryResponse.getResults().size());
        assertEquals("ユニクロポロシャツ", queryResponse.getResults().get(0).get("terms_ja"));
    }

    @Test
    public void testKatakanaUniqloPoloShirtQueryPoloShirt() throws Exception {
        final SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery("terms_ja:ポロシャツ");

        final QueryRequest queryRequest = new QueryRequest(solrQuery);
        final QueryResponse queryResponse = queryRequest.process(getSolrCore());

        assertEquals(1, queryResponse.getResults().size());
        assertEquals("ユニクロポロシャツ", queryResponse.getResults().get(0).get("terms_ja"));
    }

    public void testDoesNotMatchIndexedDocumentByStopWord() throws Exception {
        final SolrQuery solrQuery = new SolrQuery();
        // うち is a stop word defined in stopwords_ja.txt, thus, it is not indexed
        solrQuery.setQuery("terms_ja:うち");

        final QueryRequest queryRequest = new QueryRequest(solrQuery);
        final QueryResponse queryResponse = queryRequest.process(getSolrCore());

        assertEquals(0, queryResponse.getResults().size());
    }

    private static SolrClient getSolrCore() {
        return new EmbeddedSolrServer(h.getCoreContainer(), COLLECTION_NAME);
    }

    private static void indexDocument(final String id, final String content) throws SolrServerException, IOException {
        final SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.setField("id", id);
        solrInputDocument.setField("terms_ja", content);

        final UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setAction(ACTION.COMMIT, true, true);
        updateRequest.add(solrInputDocument);
        final UpdateResponse updateResponse = updateRequest.process(getSolrCore());
        assertEquals(0, updateResponse.getStatus());
    }
}
