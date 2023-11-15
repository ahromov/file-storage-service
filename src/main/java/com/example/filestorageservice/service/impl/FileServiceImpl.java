package com.example.filestorageservice.service.impl;

import com.example.filestorageservice.dto.FileDto;
import com.example.filestorageservice.entity.FileEntity;
import com.example.filestorageservice.repository.FileRepository;
import com.example.filestorageservice.service.FileService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileServiceImpl implements FileService {

    @PersistenceContext
    private EntityManager entityManager;

    private final FileRepository fileRepository;

    @Override
    public FileDto getById(String id) {
        FileEntity fileEntity = fileRepository.findById(id).orElseThrow();
        FileDto build = FileDto.builder()
                .id(fileEntity.getId())
                .originalName(fileEntity.getOriginalName())
                .mimetype(fileEntity.getContentType())
                .size(fileEntity.getSize())
                .buffer(fileEntity.getFileContent())
                .uploadAt(fileEntity.getUploadAt())
                .build();
        log.debug("{}", build.getBuffer());
        return build;
    }

    @Override
    public Object createFiles(Object payload) {
        if (payload instanceof List<?>) {
            return creates((List<MultipartFile>) payload);
        }
        return Optional.of(payload)
                .map(object -> getFileDto((MultipartFile) object))
                .map(this::createFile)
                .orElseThrow();
    }

    @Override
    public void remove(String id) {
        FileEntity fileEntity = fileRepository.findById(id).orElseThrow();
        fileRepository.delete(fileEntity);
    }

    @Override
    public void updateFileProcessId(String id, String processId) {
        FileEntity fileEntity = fileRepository.findById(id).orElseThrow();
        fileEntity.setProcessId(processId);
        fileRepository.save(fileEntity);
    }

    public List<FileDto> creates(List<MultipartFile> files) {
        return files.stream()
                .map(this::getFileDto)
                .map(this::createFile)
                .toList();
    }

    private FileDto getFileDto(MultipartFile file) {
        try {
            return FileDto.builder()
                    .originalName(file.getOriginalFilename())
                    .mimetype(file.getContentType())
                    .size(file.getSize())
                    .uploadAt(LocalDateTime.now())
                    .buffer(file.getBytes())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileDto createFile(FileDto fileDto) {
        FileEntity fileEntity = FileEntity.builder()
                .originalName(fileDto.getOriginalName())
                .contentType(fileDto.getMimetype())
                .fileContent(fileDto.getBuffer())
                .uploadAt(fileDto.getUploadAt())
                .size(fileDto.getSize())
                .build();
        fileRepository.save(fileEntity);
        fileDto.setId(fileEntity.getId());
        fileDto.setUploadAt(fileDto.getUploadAt());
        return fileDto;
    }

    @Scheduled(cron = "${scheduling.cron}")
    void removeAllOrphanFiles() {
        log.info("Start scheduler: {}", LocalDateTime.now());
        List<String> resultList = entityManager.createNativeQuery("select a.photo_id from public.application a", String.class)
                .getResultList();
        List<String> resultList1 = entityManager.createNativeQuery("select a.file_id from public.id_card a", String.class)
                .getResultList();
        List<String> resultList2 = entityManager.createNativeQuery("select a.file_id from public.order a", String.class)
                .getResultList();
        List<String> resultList3 = entityManager.createNativeQuery("select a.file_id from public.other a", String.class)
                .getResultList();
        List<String> resultList4 = entityManager.createNativeQuery("select a.file_id from public.passport a", String.class)
                .getResultList();
        List<String> resultList5 = entityManager.createNativeQuery("select a.file_id from public.tax a", String.class)
                .getResultList();
        List<String> resultList6 = entityManager.createNativeQuery("select f.file_id from public.file f where f.proc_instance_key IS NOT NULL ")
                .getResultList();
        Result result = new Result(resultList, resultList1, resultList2, resultList3, resultList4, resultList5, resultList6);

        List<String> stringsId= new ArrayList<>();
        stringsId.addAll(result.resultList());
        stringsId.addAll(result.resultList1());
        stringsId.addAll(result.resultList2());
        stringsId.addAll(result.resultList3());
        stringsId.addAll(result.resultList4());
        stringsId.addAll(result.resultList5());
        stringsId.addAll(result.resultList6());

        stringsId = stringsId.stream().filter(Objects::nonNull).toList();

        log.debug("resultList: {}", stringsId);
        if (stringsId == null || stringsId.isEmpty())
            fileRepository.deleteAll();
        else if (!stringsId.isEmpty())
            fileRepository.removeAllByIdIsNotIn(stringsId);
    }

    private record Result(
            List<String> resultList,
            List<String> resultList1,
            List<String> resultList2,
            List<String> resultList3,
            List<String> resultList4,
            List<String> resultList5,
            List<String> resultList6
    ) {
    }

}
