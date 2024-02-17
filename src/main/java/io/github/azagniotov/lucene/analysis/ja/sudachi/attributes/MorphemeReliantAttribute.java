package io.github.azagniotov.lucene.analysis.ja.sudachi.attributes;

import com.worksap.nlp.sudachi.Morpheme;
import java.util.Optional;
import org.apache.lucene.util.Attribute;

public interface MorphemeReliantAttribute<T> extends Attribute {
    Optional<T> getValue();

    void setMorpheme(final Morpheme morpheme);
}
