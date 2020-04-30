package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_async_tx")
@Data
public class AsyncTx {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    private String action;
    private String param;
    private Integer state;
    private String txHash;
    private Date createTime;
}
