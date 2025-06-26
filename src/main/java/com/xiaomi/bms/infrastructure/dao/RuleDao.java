package com.xiaomi.bms.infrastructure.dao;

import com.xiaomi.bms.domain.model.Rule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RuleDao {
    Rule getRuleById(int id);
    void insertRule(Rule rule);
}