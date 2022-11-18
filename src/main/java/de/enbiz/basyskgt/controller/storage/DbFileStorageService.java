package de.enbiz.basyskgt.controller.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@Service
public class DbFileStorageService {

    @Autowired
    private DbFileRepository dbFileRepository;

    public DbFile store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        DbFile dbFile = new DbFile(fileName, file.getContentType(), file.getBytes());

        return dbFileRepository.save(dbFile);
    }

    public DbFile getFile(String id) throws NoSuchElementException {
        return dbFileRepository.findById(id).orElseThrow();
    }

    public Iterable<DbFile> getAllFilesMetaData() {
        Iterable<DbFile> files = dbFileRepository.findAll();
        files.forEach(dbFile -> dbFile.setData(null));
        return files;
    }
}


