# Solr Lucene Analyzer Sudachi
<img align="center" src="https://cdn.jsdelivr.net/gh/WorksApplications/sudachi@develop/docs/Sudachi.png" width="3%" height="3%" /> [Sudachi](https://github.com/WorksApplications/Sudachi) を活用した日本語解析のための Lucene Solr プラグイン <img align="center" 
 src="https://cdn.jsdelivr.net/gh/WorksApplications/sudachi@develop/docs/Sudachi.png" width="3%" height="3%" /> A Lucene-based Solr plugin on [Sudachi](https://github.com/WorksApplications/Sudachi) <img align="center" src="https://cdn.jsdelivr.net/gh/WorksApplications/sudachi@develop/docs/Sudachi.png" width="3%" height="3%" />

## WiP

TBD

## Local Development

### System Requirements

- The plugin keeps Java 8 source compatibility at the moment

### Build System

The plugin uses [Gradle](https://gradle.org/) for as a build system.

#### Building

Building and packaging can be done with the following command:

```bash
./gradlew build
```

#### Downloading a Sudachi dictionary

The plugin needs a dictionary in order to run the tests, thus, it needs to be downloaded using the following command:

```bash
./gradlew downloadSystemDict
```

As per [https://github.com/WorksApplications/Sudachi#dictionaries](https://github.com/WorksApplications/Sudachi#dictionaries), the above command will download a `system_core.dic` and will place it under [src/main/resources/system-dict/](src/main/resources/system-dict)

#### Formatting

The project leverages the [Spotless Gradle plugin](https://github.com/diffplug/spotless/tree/main/plugin-gradle) and follows the [palantir-java-format](https://github.com/palantir/palantir-java-format) style guide.

To format the sources, run the following command:

```bash
./gradlew spotlessApply
```

To note: Spotless Gradle plugin is invoked implicitly when running the `./gradlew build` command.

#### Testing

To run tests, run the following command:

```bash
./gradlew test
```

#### List of Gradle tasks

For list of all the available Gradle tasks, run the following command:

```bash
./gradlew tasks
```

## Licenses

### Sudachi and Sudachi Logo

Sudachi by Works Applications Co., Ltd. is licensed under the [Apache License, Version2.0](http://www.apache.org/licenses/LICENSE-2.0.html). See [https://github.com/WorksApplications/Sudachi#licenses](https://github.com/WorksApplications/Sudachi#licenses)

Sudachi logo by Works Applications Co., Ltd. is licensed under the [Apache License, Version2.0](http://www.apache.org/licenses/LICENSE-2.0.html). See [https://github.com/WorksApplications/Sudachi#logo](https://github.com/WorksApplications/Sudachi#logo)

### Lucene

Lucene, a high-performance, full-featured text search engine library written in Java is licensed under the [Apache License, Version2.0](http://www.apache.org/licenses/LICENSE-2.0.html). See [https://lucene.apache.org/core/documentation.html](https://lucene.apache.org/core/documentation.html)


### Solr Lucene Analyzer Sudachi

The Lucene-based Solr plugin leveraging [Sudachi](https://github.com/WorksApplications/Sudachi) by Alexander Zagniotov is licensed under the [Apache License, Version2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

```
Copyright (c) 2023 Alexander Zagniotov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
