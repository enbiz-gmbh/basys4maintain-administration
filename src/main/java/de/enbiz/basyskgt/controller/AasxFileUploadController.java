package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.controller.storage.DbFile;
import de.enbiz.basyskgt.controller.storage.DbFileStorageService;
import de.enbiz.basyskgt.controller.storage.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modified version of <a href="https://spring.io/guides/gs/uploading-files/">Spring "uploading files" guide</a>
 */
@Controller
public class AasxFileUploadController {

    private final DbFileStorageService fileStorageService;

    @Autowired
    public AasxFileUploadController(DbFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> listUploadedFiles(Model model) throws IOException {
        List<String> result = fileStorageService.getAllFiles().map(DbFile::getName).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<DbFile> serveFile(@PathVariable String filename) {

        DbFile file = fileStorageService.getFile(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/files")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        fileStorageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
