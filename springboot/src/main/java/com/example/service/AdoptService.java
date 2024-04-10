package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.AdoptStatusEnum;
import com.example.common.enums.AnimalStatusEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Adopt;
import com.example.entity.Animal;
import com.example.mapper.AdoptMapper;
import com.example.mapper.AnimalMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 领养信息表业务处理
 **/
@Service
public class AdoptService {

    @Resource
    private AdoptMapper adoptMapper;
    @Resource
    private AnimalMapper animalMapper;

    /**
     * 新增
     */
    public void add(Adopt adopt) {
        adopt.setTime(DateUtil.today());
        adopt.setStatus(AdoptStatusEnum.ADOPT_YES.status);
        adoptMapper.insert(adopt);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        adoptMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            adoptMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Adopt adopt) {
        if(AnimalStatusEnum.ADOPT_CANCEL.status.equals(adopt.getStatus())) {
            // 已归还需要把宠物信息的状态同步为待领养
            Animal animal = animalMapper.selectById(adopt.getAnimalId());
            // 设置宠物状态为待领养
            animal.setStatus(AnimalStatusEnum.ADOPT_NO.status);
            animalMapper.updateById(animal);
        }
        adoptMapper.updateById(adopt);
    }

    /**
     * 根据ID查询
     */
    public Adopt selectById(Integer id) {
        return adoptMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Adopt> selectAll(Adopt adopt) {
        return adoptMapper.selectAll(adopt);
    }


    /**
     * 查询宠物信息
     */
    public List<Animal> selectChange(){
        // 获取id列表
        List<Adopt> adopts = adoptMapper.selectChange();
        // 创建一个Animal类型列表存储宠物信息
        List<Animal> list = new ArrayList<>();
        // 遍历adopt类型id列表
        for (Adopt adopt : adopts) {
            Animal animal = animalMapper.selectById(adopt.getAnimalId());
            // 如果查询到的对象非空就加入Animal表中
            if (ObjectUtil.isNotEmpty(animal)) {
                list.add(animal);
            }
        }
        return list;
    }

    /**
     * 分页查询
     */
    public PageInfo<Adopt> selectPage(Adopt adopt, Integer pageNum, Integer pageSize) {
        // 获取当前的登录用户
        Account currentUser = TokenUtils.getCurrentUser();
        // 如果是用户则获取用户id并设置用户查询条件
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            adopt.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Adopt> list = adoptMapper.selectAll(adopt);
        return PageInfo.of(list);
    }

}