package com.lin.srb.core.mapper;

import com.lin.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author Helen
 * @since 2022-09-03
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    void updateAccount(
            @Param("bindCode") String bindCode,
            @Param("amount") BigDecimal amount,
            @Param("freezeAmount") BigDecimal freezeAmount);
}
