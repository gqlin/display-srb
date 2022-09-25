package com.lin.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lin.srb.base.constant.RedisConstant;
import com.lin.srb.core.enums.BorrowerStatusEnum;
import com.lin.srb.core.enums.IntegralEnum;
import com.lin.srb.core.mapper.BorrowerAttachMapper;
import com.lin.srb.core.mapper.UserInfoMapper;
import com.lin.srb.core.mapper.UserIntegralMapper;
import com.lin.srb.core.pojo.entity.Borrower;
import com.lin.srb.core.mapper.BorrowerMapper;
import com.lin.srb.core.pojo.entity.BorrowerAttach;
import com.lin.srb.core.pojo.entity.UserInfo;
import com.lin.srb.core.pojo.entity.UserIntegral;
import com.lin.srb.core.pojo.vo.BorrowerApprovalVO;
import com.lin.srb.core.pojo.vo.BorrowerAttachVO;
import com.lin.srb.core.pojo.vo.BorrowerDetailVO;
import com.lin.srb.core.pojo.vo.BorrowerVO;
import com.lin.srb.core.service.BorrowerAttachService;
import com.lin.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin.srb.core.service.DictService;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2022-09-03
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {
    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        //保存借款人信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());//认证中
        baseMapper.insert(borrower);
        //保存附件
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
            //存入数据库的附件图片，其url存入redis集合RedisConstant.ATTACH_DB_RESOURCES中
            redisTemplate.opsForSet().add(RedisConstant.ATTACH_DB_RESOURCES, borrowerAttach.getImageUrl());
        });

        //更新会员状态，更新为认证中
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.select("status").eq("user_id", userId);
        List<Object> objects = baseMapper.selectObjs(borrowerQueryWrapper);
        if (objects.size() == 0) {
            //借款人顺未提交信息
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        Integer status = (Integer) objects.get(0);
        return status;
    }

    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isEmpty(keyword)) {
            return baseMapper.selectPage(pageParam, null);
        }
        borrowerQueryWrapper.like("name", keyword)
                .or().like("id_card", keyword)
                .or().like("mobile", keyword)
                .orderByDesc("id");
        return baseMapper.selectPage(pageParam, borrowerQueryWrapper);
    }

    @Resource
    private DictService dictService;
    @Resource
    private BorrowerAttachService borrowerAttachService;

    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {

        //获取借款人信息
        Borrower borrower = baseMapper.selectById(id);

        //以下步骤,从数据库对应的Borrower转换到页面的BorrowerDetailVO
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();

        //填充两种对象一致的属性：
        BeanUtils.copyProperties(borrower, borrowerDetailVO);

        //填充BorrowerDetailVO有特殊要求的属性：
        //婚否
        borrowerDetailVO.setMarry(borrower.getMarry() ? "是" : "否");

        //性别
        borrowerDetailVO.setSex(borrower.getSex() == 1 ? "男" : "女");

        //计算下拉列表选中内容
        String education = dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation());
        String industry = dictService.getNameByParentDictCodeAndValue("moneyUse", borrower.getIndustry());
        String income = dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource());
        String contactsRelation = dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation());

        //设置下拉列表选中内容
        borrowerDetailVO.setEducation(education);
        borrowerDetailVO.setIndustry(industry);
        borrowerDetailVO.setIncome(income);
        borrowerDetailVO.setReturnSource(returnSource);
        borrowerDetailVO.setContactsRelation(contactsRelation);

        //审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        borrowerDetailVO.setStatus(status);

        //获取附件VO列表
        List<BorrowerAttachVO> borrowerAttachVOList = borrowerAttachService.selectBorrowerAttachVOList(id);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);
        return borrowerDetailVO;
    }

    @Resource
    private UserIntegralMapper userIntegralMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        //获取借款额度申请id
        Long borrowerId = borrowerApprovalVO.getBorrowerId();

        //获取借款额度申请对象
        Borrower borrower = baseMapper.selectById(borrowerId);

        //设置审核状态
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);

        //获取用户id
        Long userId = borrower.getUserId();

        //获取用户对象
        UserInfo userInfo = userInfoMapper.selectById(userId);

        ////设置user_integral表中该用户的一个积分来源——"基本信息积分"
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);

        //创建用户的时候的原始积分(userInfo.integral)+基本信息积分=当前拥有的积分（curIntegral）
        int curIntegral = userInfo.getIntegral() + borrowerApprovalVO.getInfoIntegral();

        //身份证审核通过，加上身份证积分
        if (borrowerApprovalVO.getIsIdCardOk()) {
            //加上身份证积分
            curIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();

            //设置user_integral表中该用户的一个积分来源——"身份证"
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        //房产审核通过，加上房产积分
        if (borrowerApprovalVO.getIsHouseOk()) {
            //加上房产积分
            curIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();

            //设置user_integral表中该用户的一个积分来源——"房产"
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        //车辆审核通过，加上车辆积分
        if (borrowerApprovalVO.getIsCarOk()) {
            //加上车辆积分
            curIntegral += IntegralEnum.BORROWER_CAR.getIntegral();

            //设置user_integral表中该用户的一个积分来源——"车辆"
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        //更新user_info表中的总积分
        userInfo.setIntegral(curIntegral);
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());//修改审核状态
        userInfoMapper.updateById(userInfo);//执行更新
    }
}
