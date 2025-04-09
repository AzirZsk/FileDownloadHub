package io.github.azirzsk.filedownloadhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileDownloadHubApplication {

    public static void main(String[] args) {
        System.setProperty("file.basePath", "D:\\Downloads");
        SpringApplication.run(FileDownloadHubApplication.class, args);
    }

}
