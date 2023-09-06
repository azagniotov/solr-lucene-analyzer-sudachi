package io.github.azagniotov.lucene.analysis.ja.sudachi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.JettyConfig;
import org.apache.solr.client.solrj.embedded.JettySolrRunner;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.junit.Test;

public class TestJettySolrRunner extends SolrTestCaseJ4 {

    @Test
    public void testPassSolrHomeToRunner() throws Exception {

        // We set a non-standard coreRootDirectory, create a core, and check that it has been
        // built in the correct place

        Path solrHome = createTempDir();
        Path coresDir = createTempDir("crazy_path_to_cores");

        Path configsets = TEST_PATH().resolve("configsets");

        String solrxml =
                "<solr><str name=\"configSetBaseDir\">CONFIGSETS</str><str name=\"coreRootDirectory\">COREROOT</str></solr>"
                        .replace("CONFIGSETS", configsets.toString())
                        .replace("COREROOT", coresDir.toString());
        Files.write(solrHome.resolve("solr.xml"), solrxml.getBytes(StandardCharsets.UTF_8));

        JettyConfig jettyConfig = buildJettyConfig("/src/functionalTest/resources/solr");

        JettySolrRunner runner = new JettySolrRunner(solrHome.toString(), new Properties(), jettyConfig);
        try {
            runner.start();

            try (SolrClient client = getHttpSolrClient(runner.getBaseUrl().toString())) {
                CoreAdminRequest.Create createReq = new CoreAdminRequest.Create();
                createReq.setCoreName("newcore");
                createReq.setConfigSet("minimal");

                client.request(createReq);
            }

            assertTrue(Files.exists(coresDir.resolve("newcore").resolve("core.properties")));

        } finally {
            runner.stop();
        }
    }
}
