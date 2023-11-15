package com.example.filestorageservice.controller;

import com.example.filestorageservice.dto.FileDto;
import com.example.filestorageservice.service.FileService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Object upload(@RequestParam("file") Object payload) {
        return fileService.createFiles(payload);
    }

    @GetMapping(value = "/{id}")
    public Object getById(@PathVariable("id") String id, @PathParam("download") String download) {
        FileDto fileDto = fileService.getById(id);
        String originalName = fileDto.getOriginalName();
        if (Boolean.parseBoolean(download)) {
            return fileDto;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", originalName))
                .header(HttpHeaders.CONTENT_LENGTH, fileDto.getSize().toString())
                .contentType(MediaType.parseMediaType(fileDto.getMimetype()))
                .body(fileDto.getBuffer());
    }

    @PatchMapping("/{id}/process/{processId}")
    public void patchFileProcessId(@PathVariable("id") String id, @PathVariable("processId") String processId) {
        fileService.updateFileProcessId(id, processId);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        fileService.remove(id);
    }
}
