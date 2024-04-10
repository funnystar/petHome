package com.example.mapper;

import com.example.entity.Room;
import com.example.entity.Room;

import java.util.List;

/**
 * 操作room相关数据接口
*/
public interface RoomMapper {

    /**
      * 新增
    */
    int insert(Room room);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Room room);

    /**
      * 根据ID查询
    */
    Room selectById(Integer id);

    /**
      * 查询所有
    */
    List<Room> selectAll(Room room);

}