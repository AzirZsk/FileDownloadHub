package io.github.azirzsk.filedownloadhub.controller;

import io.github.azirzsk.filedownloadhub.entity.FileItem;
import io.github.azirzsk.filedownloadhub.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author azir
 * @since 2025/04/07
 */
@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/list")
    @ResponseBody
    public List<? extends FileItem> list(@RequestParam(defaultValue = "") String path) {
        return fileService.list(path);
    }

    @GetMapping("/download")
    public void download(@RequestParam String path, HttpServletRequest request, HttpServletResponse response) {
        fileService.download(path, request, response);
    }
}
