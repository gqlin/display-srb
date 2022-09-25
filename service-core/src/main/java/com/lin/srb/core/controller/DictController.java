package com.lin.srb.core.controller;


import com.lin.common.result.R;
import com.lin.srb.core.pojo.entity.Dict;
import com.lin.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2022-09-03
 */
@RestController
@Api(tags = "数字字典")
@RequestMapping("/api/core/dict")
@Slf4j
public class DictController {

    @Resource
    private DictService dictService;

    @GetMapping("/findByDictCode/{dictCode}")
    @ApiOperation("根据dictCode获取下级节点")
    public R findByDictCode(@ApiParam(value = "节点编码",required = true)
                            @PathVariable String dictCode){
        List<Dict> list = dictService.findByDictCode(dictCode);
        return R.ok().data("dictList", list);
    }
}

