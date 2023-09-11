/*
 * Copyright (c) 2017-2023 Works Applications Co., Ltd.
 * Modifications copyright (c) 2023 Alexander Zagniotov
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

package io.github.azagniotov.lucene.analysis.ja.sudachi.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.lucene.util.ResourceLoader;

public class StringResourceLoader implements ResourceLoader {

    private final String text;

    public StringResourceLoader(final String text) {
        this.text = text;
    }

    @Override
    public InputStream openResource(final String resource) throws IOException {
        // Disregarding the given parameter resource
        return new ByteArrayInputStream(this.text.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public <T> Class<? extends T> findClass(String cname, Class<T> expectedType) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public <T> T newInstance(String cname, Class<T> expectedType) {
        throw new UnsupportedOperationException("not implemented");
    }
}
