package com.opdev.metadata;

import com.opdev.model.metadata.LinkMetadata;

public interface MetadataScraperService {

    LinkMetadata extract(String url);

}
