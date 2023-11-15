package com.example.filestorageservice.repository;

import com.example.filestorageservice.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;


@Repository
public interface FileRepository extends JpaRepository<FileEntity, String> {

    void removeAllByIdIsNotIn(Collection<String> ids);
}
