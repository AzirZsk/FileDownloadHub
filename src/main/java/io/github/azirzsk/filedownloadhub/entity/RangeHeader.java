package io.github.azirzsk.filedownloadhub.entity;

import lombok.Data;

import java.util.Objects;

/**
 * @author azir
 * @since 2025/04/11
 */
@Data
public class RangeHeader {

    private Long start;

    private Long end;

    @Override
    public String toString() {
        return "bytes=" + start + "-" + (Objects.isNull(end) ? "" : end);
    }
}
