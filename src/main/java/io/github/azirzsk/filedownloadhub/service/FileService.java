package io.github.azirzsk.filedownloadhub.service;

import io.github.azirzsk.filedownloadhub.DownloadException;
import io.github.azirzsk.filedownloadhub.entity.FileItem;
import io.github.azirzsk.filedownloadhub.entity.Folder;
import io.github.azirzsk.filedownloadhub.entity.RangeHeader;
import io.github.azirzsk.filedownloadhub.properties.FileProperties;
import io.github.azirzsk.filedownloadhub.utils.HeaderUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author azir
 * @since 2025/04/07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    /**
     * 一个G的大小
     */
    private static final int BYTE_SIZE = 1024 * 1024 * 1024;

    private static final String CONTENT_RANGE = "bytes %s-%s/%s";

    private final FileProperties fileProperties;

    public List<? extends FileItem> list(String path) {
        String basePath = fileProperties.getBasePath();
        File file = new File(basePath + path);
        File[] files = file.listFiles();
        if (files == null) {
            log.info("该路径下没有东西");
            return Collections.emptyList();
        }
        return Arrays.stream(files)
                .map(fileItem -> {
                    // 文件夹
                    if (fileItem.isDirectory()) {
                        return Folder.builder()
                                .name(fileItem.getName())
                                .path(getPath(fileItem.getPath()))
                                .lastModified(fileItem.lastModified())
                                .build();
                    } else if (fileItem.isFile()) {
                        return io.github.azirzsk.filedownloadhub.entity.File.builder()
                                .name(fileItem.getName())
                                .path(getPath(fileItem.getPath()))
                                .lastModified(fileItem.lastModified())
                                .size(fileItem.length())
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private String getPath(String path) {
        return path.replace(fileProperties.getBasePath(), "").replace(File.separator, "/");
    }

    /**
     * 下载文件
     *
     * @param path     下载路径
     * @param request  请求体
     * @param response 响应体
     */
    public void download(String path, HttpServletRequest request, HttpServletResponse response) {
        log.info("download-start,path:{}", path);
        File file = new File(fileProperties.getBasePath() + path);
        checkFile(file);
        setDownloadHeader(file, response);
        RangeHeader rangeHeader = HeaderUtils.getRangeHeader(request);
        if (rangeHeader != null) {
            rangeDownload(file, rangeHeader, response);
            return;
        }
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.transferTo(response.getOutputStream());
            log.info("download-end");
        } catch (IOException e) {
            log.warn("download-error:{}", e.getMessage());
        }
    }

    private void checkFile(File file) {
        if (!file.exists()) {
            log.warn("要下载的文件不存在:{}", file.getPath());
            throw new DownloadException("要下载的文件不存在");
        }
        if (file.isDirectory()) {
            log.warn("要下载的文件是文件夹");
            throw new DownloadException("要下载的文件是文件夹");
        }
    }

    /**
     * 范围下载
     *
     * @param file        下载文件
     * @param rangeHeader 范围头
     * @param response    响应体
     */
    private void rangeDownload(File file, RangeHeader rangeHeader, HttpServletResponse response) {
        log.info("范围下载：{}", rangeHeader);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        String contentRange = CONTENT_RANGE.formatted(rangeHeader.getStart(), Objects.isNull(rangeHeader.getEnd()) ? file.length() - 1 : rangeHeader.getEnd(), file.length());
        response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange);
        long contentLength = calcRangeDownloadContentLength(file, rangeHeader);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        long downloadSize = 0;
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(rangeHeader.getStart());
            ServletOutputStream outputStream = response.getOutputStream();
            // 分块读取字节数(1GB)
            while (downloadSize < contentLength) {
                if (log.isDebugEnabled()) {
                    log.debug("分块读取，开始位置：{}，当前位置：{}", rangeHeader.getStart(), rangeHeader.getStart() + downloadSize);
                }
                long remainSize = contentLength - downloadSize;
                int curReadSize;
                if (remainSize > BYTE_SIZE) {
                    curReadSize = BYTE_SIZE;
                } else {
                    curReadSize = (int) remainSize;
                }
                byte[] bytes = new byte[curReadSize];
                raf.readFully(bytes, 0, curReadSize);
                outputStream.write(bytes);
                downloadSize += curReadSize;
            }
            log.info("范围下载完成");
        } catch (Exception e) {
            log.warn("范围下载失败：{}", e.getMessage(), e);
            throw new DownloadException("范围下载失败" + e.getMessage(), e);
        }
    }

    /**
     * 计算范围下载内容长度
     *
     * @param file        下载文件
     * @param rangeHeader 范围头
     * @return 内容长度
     */
    private long calcRangeDownloadContentLength(File file, RangeHeader rangeHeader) {
        long contentLength;
        if (rangeHeader.getEnd() == null) {
            contentLength = file.length() - rangeHeader.getStart();
        } else {
            contentLength = rangeHeader.getEnd() - rangeHeader.getStart();
        }
        return contentLength;
    }

    /**
     * 设置下载响应头信息
     *
     * @param file     下载的文件
     * @param response 响应体
     */
    private void setDownloadHeader(File file, HttpServletResponse response) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(file.length());
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(file.getName(), StandardCharsets.UTF_8)
                .build();
        httpHeaders.setContentDisposition(contentDisposition);
        httpHeaders.add(HttpHeaders.ACCEPT_RANGES, "bytes");
        httpHeaders.setLastModified(file.lastModified());
        httpHeaders.forEach((headerName, headerValueList) -> {
            for (String headerValue : headerValueList) {
                response.addHeader(headerName, headerValue);
            }
        });
    }

}
