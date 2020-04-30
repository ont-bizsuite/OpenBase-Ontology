package com.ontology.mapper;

import com.ontology.entity.AsyncTx;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Component
public interface AsyncTxMapper extends Mapper<AsyncTx> {
    void insertTxList(@Param("action") String action, @Param("paramList") List<String> paramList);

    List<AsyncTx> getCacheTx(@Param("action") String action, @Param("state") Integer state, @Param("size") Integer size);
}
