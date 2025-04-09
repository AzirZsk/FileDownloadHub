package io.github.azirzsk.filedownloadhub.entity;

/**
 * @author azir
 * @since 2025/04/07
 */
public interface FileItem {

    String getName();

    String getPath();

    long getSize();

    String getType();

    long getLastModified();

}
