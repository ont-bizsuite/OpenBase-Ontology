package com.ontology.mapper;

import com.ontology.entity.DataAuth;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;


@Component
public interface DataAuthMapper extends Mapper<DataAuth> {
    void insertAuthId(@Param("authId") String authId, @Param("dataOntId") String dataOntId);
}
