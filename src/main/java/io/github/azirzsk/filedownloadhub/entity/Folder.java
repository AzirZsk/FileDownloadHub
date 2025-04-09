package io.github.azirzsk.filedownloadhub.entity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author azir
 * @since 2025/04/07
 */
@Data
@SuperBuilder
public class Folder extends AbstractFileItem {

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public String getType() {
        return "folder";
    }
}
