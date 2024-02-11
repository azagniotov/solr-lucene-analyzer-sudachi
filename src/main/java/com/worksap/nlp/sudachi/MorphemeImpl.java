/*
 * Copyright (c) 2017-2022 Works Applications Co., Ltd.
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

package com.worksap.nlp.sudachi;

import com.worksap.nlp.sudachi.dictionary.WordInfo;
import java.util.List;

/**
 * Recreated {@link com.worksap.nlp.sudachi.MorphemeImpl} as-is locally but with a public access modifer,
 * because Solr (for some reason unknown to me as of v9.4.0 incl.) tries to apply reflection on to the original MorphemeImpl from the Works Applications Co., Ltd,
 * which has package default visibility. In other words, the issue is not the package default visibility, but why Solr tries to reflect on MorphemeImpl?!
 *
 * See https://github.com/WorksApplications/Sudachi/issues/220 for the stacktrace example.
 */
public class MorphemeImpl implements Morpheme {

    final MorphemeList list;
    final int index;
    WordInfo wordInfo;

    MorphemeImpl(MorphemeList list, int index) {
        this.list = list;
        this.index = index;
    }

    @Override
    public int begin() {
        return list.getBegin(index);
    }

    @Override
    public int end() {
        return list.getEnd(index);
    }

    @Override
    public String surface() {
        return list.getSurface(index);
    }

    @Override
    public List<String> partOfSpeech() {
        WordInfo wi = getWordInfo();
        return list.grammar.getPartOfSpeechString(wi.getPOSId());
    }

    @Override
    public short partOfSpeechId() {
        WordInfo wi = getWordInfo();
        return wi.getPOSId();
    }

    @Override
    public String dictionaryForm() {
        WordInfo wi = getWordInfo();
        return wi.getDictionaryForm();
    }

    @Override
    public String normalizedForm() {
        WordInfo wi = getWordInfo();
        return wi.getNormalizedForm();
    }

    @Override
    public String readingForm() {
        WordInfo wi = getWordInfo();
        return wi.getReadingForm();
    }

    @Override
    public List<Morpheme> split(Tokenizer.SplitMode mode) {
        return list.split(mode, index);
    }

    @Override
    public boolean isOOV() {
        return list.isOOV(index);
    }

    @Override
    public int getWordId() {
        return list.getWordId(index);
    }

    @Override
    public int getDictionaryId() {
        return list.getDictionaryId(index);
    }

    @Override
    public int[] getSynonymGroupIds() {
        WordInfo wi = getWordInfo();
        return wi.getSynonymGoupIds();
    }

    WordInfo getWordInfo() {
        if (wordInfo == null) {
            wordInfo = list.getWordInfo(index);
        }
        return wordInfo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MorphemeImpl{");
        sb.append("begin=").append(begin());
        sb.append(", end=").append(end());
        sb.append(", surface=").append(surface());
        sb.append(", pos=").append(partOfSpeechId()).append('/').append(partOfSpeech());
        int wordId = getWordId();
        sb.append(", wid=(").append(WordId.dic(wordId)).append(',').append(WordId.word(wordId));
        sb.append(")}");
        return sb.toString();
    }
}
