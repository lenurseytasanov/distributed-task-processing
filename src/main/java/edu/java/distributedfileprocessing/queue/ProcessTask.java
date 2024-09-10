package edu.java.distributedfileprocessing.queue;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ProcessTask implements Serializable {

    private Long reportId;

    private String fileId;

    private String userEmail;

}
