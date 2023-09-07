# A few useful Docker commands to build an image and run the Solr container.
#
# Build (run with '--no-cache' to ensure that Git repo new tags will be pulled down, as Docker caches RUN layers):
# 'docker build -t solr-8.11.1 . --progress=plain --no-cache'
#
# Run:
# docker run -p 8983:8983 --rm solr-8.11.1:latest solr-precreate example
#
# cURL requests:
# 1. curl -sS 'http://localhost:8983/solr/example/analysis/field?analysis.fieldtype=text_ja' --get --data-urlencode 'analysis.fieldvalue=ちいかわ'  | jq '.analysis.field_types.text_ja.index[1][].text'
# 2. curl -sS 'http://localhost:8983/solr/example/analysis/field?analysis.fieldtype=text_ja' --get --data-urlencode 'analysis.fieldvalue=すもももももももものうち'  | jq '.analysis.field_types.text_ja.index[1][].text'
# 3. curl -sS 'http://localhost:8983/solr/example/analysis/field?analysis.fieldtype=text_ja' --get --data-urlencode 'analysis.fieldvalue=聖川真斗'  | jq '.analysis.field_types.text_ja.index[1][].text'
#
# See: https://github.com/apache/lucene/pull/12517
#

FROM solr:8.11.1

MAINTAINER Alexander Zagniotov <azagniotov@gmail.com>

ENV SOLR_JAVA_MEM="-Xms2g -Xmx2g"
ENV SOLR_WEB_INF_LIB_HOME=/opt/solr/server/solr-webapp/webapp/WEB-INF/lib
ENV SOLR_CONFIG_HOME=/opt/solr/server/solr/conf

USER root
RUN mkdir -p $SOLR_CONFIG_HOME
RUN rm $SOLR_WEB_INF_LIB_HOME/lucene-analysis-kuromoji-*.jar
# COPY ./lucene/analysis/kuromoji/build/libs/lucene-analysis-kuromoji-9.7.0-SNAPSHOT.jar $SOLR_WEB_INF_LIB_HOME/
COPY schema.xml $SOLR_CONFIG_HOME/
# RUN ls -al $SOLR_WEB_INF_LIB_HOME/

USER solr
