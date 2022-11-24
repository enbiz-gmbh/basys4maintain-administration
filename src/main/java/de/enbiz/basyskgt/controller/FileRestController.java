package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.controller.storage.DbFile;
import de.enbiz.basyskgt.controller.storage.DbFileStorageService;
import de.enbiz.basyskgt.controller.storage.StorageFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

/**
 * Modified version of <a href="https://spring.io/guides/gs/uploading-files/">Spring "uploading files" guide</a>
 */
@Controller
public class FileRestController {
    private final DbFileStorageService fileStorageService;
    Logger log = LoggerFactory.getLogger(FileRestController.class);

    @Autowired
    public FileRestController(DbFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/api/files")
    public ResponseEntity<Iterable<DbFile>> listUploadedFiles(Model model) throws IOException {
        Iterable<DbFile> result = fileStorageService.getAllFilesMetaData();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/files/{fileId:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> serveFile(@PathVariable String fileId) {
        DbFile file;
        try {
            file = fileStorageService.getFile(fileId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getName() + "\"").body(file.getData());
    }

    @PostMapping("/api/files")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws URISyntaxException {
        DbFile createdFile;
        try {
            createdFile = fileStorageService.store(file);
        } catch (IOException e) {
            log.info(String.format("An exception has occurred during a post request to /files:\n%s", e));
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return ResponseEntity.created(new URI(baseUrl + "/api/files/" + createdFile.getName())).build();
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
