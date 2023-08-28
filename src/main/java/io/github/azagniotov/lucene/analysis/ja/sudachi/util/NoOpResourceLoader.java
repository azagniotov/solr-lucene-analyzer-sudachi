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

package io.github.azagniotov.lucene.analysis.ja.sudachi.util;

import java.io.InputStream;
import org.apache.lucene.analysis.util.ResourceLoader;

public final class NoOpResourceLoader implements ResourceLoader {

    @Override
    public InputStream openResource(final String resource) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T> Class<? extends T> findClass(String cname, Class<T> expectedType) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T> T newInstance(String cname, Class<T> expectedType) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
