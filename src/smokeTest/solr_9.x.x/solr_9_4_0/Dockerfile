# A few useful Docker commands to build an image and run the Solr container.
#
# Build (run with '--no-cache' to ensure that Git repo new tags will be pulled down, as Docker caches RUN layers):
# docker build -t solr-sudachi . --progress=plain --no-cache (the presence of '--progress' may give an error, depending on your version of docker)
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
FROM gradle:8.1.1-jdk11@sha256:681c18e70745546bf66949861d18019b979810ac151a5e0933d4ff83c76b4f5f AS BUILD_JAR_STAGE

ARG PLUGIN_GIT_TAG=9.4.0

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
FROM solr:9.4.0@sha256:4e9b6794895f1f6685a61d587b63e2f0caa2b6f9a5a2c0e67613d4f160d9540d

MAINTAINER Alexander Zagniotov <azagniotov@gmail.com>

ENV SOLR_JAVA_MEM="-Xms4g -Xmx4g"
ENV SOLR_INSTALL_DIR=/opt/solr
ENV SOLR_SERVER_HOME=$SOLR_INSTALL_DIR/server

# https://solr.apache.org/guide/solr/9_4/configuration-guide/libs.html#lib-directories
ENV SOLR_INSTALL_LIB_DIR=$SOLR_INSTALL_DIR/lib

ENV SUDACHI_DICT_HOME=/tmp/sudachi
ENV SUDACHI_SYSTEM_DICT=$SOLR_INSTALL_DIR/system.dict
ENV SUDACHI_USER_DICT=$SOLR_INSTALL_DIR/user_lexicon.dict

USER root

# https://solr.apache.org/guide/solr/9_4/configuration-guide/libs.html#lib-directories
COPY --from=BUILD_JAR_STAGE /home/gradle/solr-lucene-analyzer-sudachi/build/libs/solr-lucene-analyzer-sudachi*.jar $SOLR_INSTALL_LIB_DIR/
COPY --from=BUILD_JAR_STAGE $SUDACHI_DICT_HOME/system-dict/system.dict $SOLR_INSTALL_DIR/system.dict
COPY --from=BUILD_JAR_STAGE $SUDACHI_DICT_HOME/user_lexicon.dict $SOLR_INSTALL_DIR/user_lexicon.dict
COPY schema.xml $SOLR_SERVER_HOME/solr/configsets/_default/conf/managed-schema.xml

USER solr
