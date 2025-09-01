package org.example.fileuploadanddownload.resource;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileResource {

    //define a location
    public static final String DIRECTORY = System.getProperty("user.home") + "/Downloads/uploads/" ;

    //Define a method to upload files
    /*@PostMapping("/upload")
    public ResponseEntity<List<String> > uploadFiles(@RequestParam("files") List<MultipartFile> multipartFiles) {
        List<String> filenames = new ArrayList<>();
        for(MultipartFile file : multipartFiles) {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            //Path fileStorage = Paths.get(DIRECTORY, filename).toAbsolutePath().normalize();
            Path filePath = Paths.get(DIRECTORY).toAbsolutePath().normalize().resolve(filename);
            try {
                Files.copy(
                        file.getInputStream(),
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                );
                filenames.add(filename);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok().body(filenames);
    }*/

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") List<MultipartFile> multipartFiles) {
        List<String> filenames = new ArrayList<>();

        try {
            Path storageDirectory = Paths.get(DIRECTORY).toAbsolutePath().normalize();

            // ensure the directory exists
            if (!Files.exists(storageDirectory)) {
                Files.createDirectories(storageDirectory);
            }

            for (MultipartFile file : multipartFiles) {
                String filename = StringUtils.cleanPath(file.getOriginalFilename());
                Path filePath = storageDirectory.resolve(filename);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                filenames.add(filename);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not store file. Error: " + e.getMessage(), e);
        }

        return ResponseEntity.ok().body(filenames);
    }


    //Define a method to download files
    /*@GetMapping("download/{filename}")
    public ResponseEntity<UrlResource> downloadFiles(@PathVariable("filename") String filename) throws IOException {
        Path filePath = Paths.get(DIRECTORY, filename).toAbsolutePath().normalize().resolve(filename);
        if(!Files.exists(filePath)){
            throw new FileNotFoundException(filename + " was not found on the server");
        }
        UrlResource resource = new UrlResource(filePath.toUri());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", filename);
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                .headers(httpHeaders).body(resource);


    }*/

    @GetMapping("download/{filename}")
    public ResponseEntity<UrlResource> downloadFiles(@PathVariable("filename") String filename) throws IOException {
        Path filePath = Paths.get(DIRECTORY, filename).toAbsolutePath().normalize();
        if(!Files.exists(filePath)){
            throw new FileNotFoundException(filename + " was not found on the server");
        }
        UrlResource resource = new UrlResource(filePath.toUri());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", filename);
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                .headers(httpHeaders).body(resource);
    }

}
