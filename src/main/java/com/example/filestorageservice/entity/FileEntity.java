package com.example.filestorageservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "file")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FileEntity {

    @Id
    @UuidGenerator
    @Column(name = "file_id")
    private String id;

    @Column(name = "file_name")
    private String originalName;

    @Column(name = "file_content_type")
    private String contentType;

    @Lob
    @Column(name = "file_content")
    private byte[] fileContent;

    @Column(name = "file_creation_date")
    private LocalDateTime uploadAt;

    @Column(name = "size")
    private Long size;

    @Column(name = "proc_instance_key")
    private String processId;

}

