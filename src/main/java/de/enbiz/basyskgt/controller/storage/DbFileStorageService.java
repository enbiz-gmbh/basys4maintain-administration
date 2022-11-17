package de.enbiz.basyskgt.controller.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DbFileStorageService {

    @Autowired
    private DbFileRepository dbFileRepository;

    public DbFile store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        DbFile dbFile = new DbFile(fileName, file.getContentType(), file.getBytes());

        return dbFileRepository.save(dbFile);
    }

    public DbFile getFile(String id) {
        return dbFileRepository.findById(id).get();
    }

    public List<String> getAllFileNames() {
        return dbFileRepository.findFileNames();
    }
}


