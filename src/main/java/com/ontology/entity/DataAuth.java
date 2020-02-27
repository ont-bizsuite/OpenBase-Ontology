package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_data_auth")
@Data
public class DataAuth {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String dataId;

    private String version;
    private String dataOntId;
    private String authId;
    private String downloadUrl;
    private Integer state;

}
