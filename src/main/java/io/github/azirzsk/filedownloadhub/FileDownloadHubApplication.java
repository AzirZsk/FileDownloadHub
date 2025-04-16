package io.github.azirzsk.filedownloadhub;

import io.github.azirzsk.filedownloadhub.properties.FileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@Slf4j
@SpringBootApplication
public class FileDownloadHubApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(FileDownloadHubApplication.class, args);
        FileProperties fileProperties = applicationContext.getBean(FileProperties.class);
        log.info("基础路径为：{}", fileProperties.getBasePath());
    }

}
