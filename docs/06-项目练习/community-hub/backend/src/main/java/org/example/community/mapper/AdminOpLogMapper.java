package org.example.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.community.entity.AdminOpLog;

@Mapper
public interface AdminOpLogMapper extends BaseMapper<AdminOpLog> {
}
