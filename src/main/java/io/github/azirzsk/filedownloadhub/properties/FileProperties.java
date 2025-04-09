package io.github.azirzsk.filedownloadhub.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author azir
 * @since 2025/04/07
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    /**
     * 指定的初始目录
     */
    private String basePath;
}
