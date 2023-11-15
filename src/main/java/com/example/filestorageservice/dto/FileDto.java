package com.example.filestorageservice.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto {

        private String id;
        private String originalName;  //": "2020.jpg",
        private String encoding;  //": "7bit",
        private String mimetype;  //": "image/jpeg",
        private byte[] buffer;  //"
        private Long size;  //": 37058
        private LocalDateTime uploadAt;
}
