package io.github.azirzsk.filedownloadhub.service;

import io.github.azirzsk.filedownloadhub.entity.FileItem;
import io.github.azirzsk.filedownloadhub.entity.Folder;
import io.github.azirzsk.filedownloadhub.properties.FileProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author azir
 * @since 2025/04/07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileProperties fileProperties;

    public List<? extends FileItem> list(String path) {
        String basePath = fileProperties.getBasePath();
        File file = new File(basePath + path);
        return Arrays.stream(file.listFiles())
                .map(v -> {
                    Folder folder = Folder.builder()
                            .name(v.getName())
                            .path(v.getPath())
                            .lastModified(v.lastModified())
                            .build();
                    return folder;
                }).toList();

    }

    public void download(String path, HttpServletResponse response) {
        File file = new File(fileProperties.getBasePath() + path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.transferTo(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
