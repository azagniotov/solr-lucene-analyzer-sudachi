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

import static org.apache.solr.client.solrj.request.AbstractUpdateRequest.ACTION;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.io.FileUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SolrFieldAnalysisTest extends SolrTestCaseJ4 {

    private static final String COLLECTION_NAME = "collection1";

    @BeforeClass
    public static void beforeClass() throws Exception {
        String tmpSolrHome = createTempDir().toFile().getAbsolutePath();
        FileUtils.copyDirectory(new File(TEST_HOME()), new File(tmpSolrHome).getAbsoluteFile());
        initCore("solrconfig.xml", "schema.xml", new File(tmpSolrHome).getAbsolutePath());
        indexDocument("1", "すもももももももものうち。");
        indexDocument("2", "ももたろうは日本のおとぎ話の一つ。");
    }

    @AfterClass
    public static void afterClass() {
        // Do nothing
    }

    @Test
    public void testPerformsSuccessfulFieldAnalysisRequest() throws Exception {
        final FieldAnalysisRequest fieldAnalysisRequest = new FieldAnalysisRequest();
        fieldAnalysisRequest.addFieldType("text_ja");
        fieldAnalysisRequest.setFieldValue("ももたろうは日本のおとぎ話の一つ。");

        final FieldAnalysisResponse fieldAnalysisResponse = fieldAnalysisRequest.process(getSolrCore());

        final Iterable<Map.Entry<String, FieldAnalysisResponse.Analysis>> allFieldTypeAnalysis =
                fieldAnalysisResponse.getAllFieldTypeAnalysis();
        assertEquals(true, allFieldTypeAnalysis.iterator().hasNext());

        final Map.Entry<String, FieldAnalysisResponse.Analysis> filedTypeAnalysis =
                allFieldTypeAnalysis.iterator().next();
        final FieldAnalysisResponse.Analysis analysis = filedTypeAnalysis.getValue();

        final List<AnalysisResponseBase.AnalysisPhase> indexPhrases = StreamSupport.stream(
                        analysis.getIndexPhases().spliterator(), false)
                .collect(Collectors.toList());

        assertFalse(indexPhrases.isEmpty());
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
