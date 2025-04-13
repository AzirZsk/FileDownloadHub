package io.github.azirzsk.filedownloadhub.entity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author azir
 * @since 2025/04/13
 */
@Data
@SuperBuilder
public class File extends AbstractFileItem {

    private long size;

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getType() {
        return "file";
    }
}
