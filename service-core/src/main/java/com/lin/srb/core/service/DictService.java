package com.lin.srb.core.service;

import com.lin.srb.core.pojo.dto.ExcelDictDTO;
import com.lin.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author Helen
 * @since 2022-09-03
 */
public interface DictService extends IService<Dict> {
    // 导入数据
    void importData(InputStream inputStream);

    // 导出数据
    List<ExcelDictDTO> listDictData();

    List<Dict> listByParentId(Long parentId);

    List<Dict> findByDictCode(String dictCode);
    String getNameByParentDictCodeAndValue(String dictCode, Integer value);

}
