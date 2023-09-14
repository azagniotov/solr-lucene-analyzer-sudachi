# A few useful Docker commands to build an image and run the Solr container.
#
# Build (run with '--no-cache' to ensure that Git repo new tags will be pulled down, as Docker caches RUN layers):
# docker build -t solr-sudachi . --progress=plain --no-cache
#
# Run:
# docker run -p 8983:8983 --rm solr-sudachi:latest solr-precreate example
#
# cURL requests:
# 1. curl -sS 'http://localhost:8983/solr/example/analysis/field?analysis.fieldtype=text_ja' --get --data-urlencode 'analysis.fieldvalue=ちいかわ'  | jq '.analysis.field_types.text_ja.index[1][].text'
# 2. curl -sS 'http://localhost:8983/solr/example/analysis/field?analysis.fieldtype=text_ja' --get --data-urlencode 'analysis.fieldvalue=すもももももももものうち'  | jq '.analysis.field_types.text_ja.index[1][].text'
# 3. curl -sS 'http://localhost:8983/solr/example/analysis/field?analysis.fieldtype=text_ja' --get --data-urlencode 'analysis.fieldvalue=聖川真斗'  | jq '.analysis.field_types.text_ja.index[1][].text'
#
# See: https://github.com/apache/lucene/pull/12517
#

########################################################################################
# Stage 1 : Solr Lucene Analyzer Sudachi JAR
 ########################################################################################
FROM gradle:8.1.1-jdk8@sha256:d8fafc7e609e448356880e74642debf8be212121e5f97b1eacb9f5a0cba020f5 AS BUILD_JAR_STAGE

ARG PLUGIN_GIT_TAG=6.6.6

ENV GRADLE_USER_HOME=/home/gradle
WORKDIR $GRADLE_USER_HOME

RUN git clone -b v$PLUGIN_GIT_TAG https://github.com/azagniotov/solr-lucene-analyzer-sudachi.git --depth 1 && \
    git config --global user.name "Alexander Zagniotov" && \
    git config --global user.email "azagniotov@gmail.com"

# Download the dictionary and assemble the JAR to be placed under Solr /opt/solr/server/solr-webapp/webapp/WEB-INF/lib/
RUN cd solr-lucene-analyzer-sudachi && \
    gradle configureDictionariesLocally && \
    gradle -PsolrVersion=$PLUGIN_GIT_TAG assemble && \
    ls -al ./build/libs/


########################################################################################
# Stage 2 : Run Solr
########################################################################################
FROM solr:6.6.6@sha256:44f44e509c1ecb2de3c1b9810b646f4f9d410e2fff536de4aaf43e6992b21dfb

MAINTAINER Alexander Zagniotov <azagniotov@gmail.com>

ENV SOLR_JAVA_MEM="-Xms2g -Xmx2g"
ENV SOLR_SERVER_HOME=/opt/solr/server
ENV SUDACHI_DICT_HOME=/tmp/sudachi
ENV SOLR_WEB_INF_LIB_HOME=$SOLR_SERVER_HOME/solr-webapp/webapp/WEB-INF/lib

USER root

# Removing existing Lucene Kuromoji JAR to avoid polluting the classpath with
# Japanese analysis-related classes. Also, Lucene team has renamed the packages
# from analyzers => analysis at some point, thus using a wildcard.
# RUN rm $SOLR_WEB_INF_LIB_HOME/lucene-*-kuromoji-*.jar

COPY --from=BUILD_JAR_STAGE $SUDACHI_DICT_HOME/system-dict/system_core.dic $SUDACHI_DICT_HOME/system-dict/system_core.dic
COPY --from=BUILD_JAR_STAGE $SUDACHI_DICT_HOME/user_lexicon.dic $SUDACHI_DICT_HOME/user_lexicon.dic
COPY --from=BUILD_JAR_STAGE /home/gradle/solr-lucene-analyzer-sudachi/build/libs/solr-lucene-analyzer-sudachi*.jar $SOLR_WEB_INF_LIB_HOME/
COPY schema.xml $SOLR_SERVER_HOME/solr/configsets/data_driven_schema_configs/conf/managed-schema

RUN chown -R solr:solr /tmp/sudachi

USER solr
