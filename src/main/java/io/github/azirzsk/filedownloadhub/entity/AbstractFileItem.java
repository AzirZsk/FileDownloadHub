package io.github.azirzsk.filedownloadhub.entity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author azir
 * @since 2025/04/07
 */
@Data
@SuperBuilder
public abstract class AbstractFileItem implements FileItem {

    private String path;

    private String name;

    private long lastModified;

}
