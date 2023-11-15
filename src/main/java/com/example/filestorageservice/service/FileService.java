package com.example.filestorageservice.service;

import com.example.filestorageservice.dto.FileDto;


public interface FileService {

    FileDto getById(String id);

    Object createFiles(Object payload);

    void remove(String id);

    void updateFileProcessId(String id, String processId);
}
