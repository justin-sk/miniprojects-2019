package com.wootecobook.turkey.file.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileFeature {

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private long size;

    @Builder
    private FileFeature(final String path, final String originalName, final String type, final long size) {
        this.path = path;
        this.originalName = originalName;
        this.type = type;
        this.size = size;
    }

}

