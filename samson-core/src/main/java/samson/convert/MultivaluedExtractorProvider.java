package samson.convert;

import samson.metadata.Element;

public interface MultivaluedExtractorProvider {

    MultivaluedExtractor get(Element element);
}
