package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityMapper activityMapper;

    /**
     * 创建市场活动
     * @param activity
     * @return
     */
    @Override
    public int saveCreateActivity(Activity activity) {
        return activityMapper.insertActivity(activity);
    }

    /**
     * 活动分页查询
     *
     * @param map
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<Activity> queryActivityByConditionForPage(Map<String, Object> map, Integer pageNo, Integer pageSize) {
        //调用PageHelper插件的startPage是分页查询的工具方法，
        //pageNo 页码
        //pageSize 每页显示条数
        PageHelper.startPage(pageNo, pageSize);
        return activityMapper.selectActivityByConditionForPage(map);
    }


    /**
     * 根据ids批量删除市场活动
     * @param ids id数组
     * @return
     */
    @Override
    public int deleteActivityByIds(String[] ids) {
        return activityMapper.deleteActivityByIds(ids);
    }

    /**
     * 根据id查询市场活动
     * @param id
     * @return
     */
    @Override
    public Activity selectActivityById(String id) {
        return activityMapper.selectActivityById(id);
    }

    /**
     * 编辑市场活动
     * @param activity
     * @return
     */
    @Override
    public int saveEditActivity(Activity activity) {
        return activityMapper.updateActivity(activity);
    }

    /**
     * 查询所有的市场活动
     * @return
     */
    @Override
    public List<Activity> queryAllActivitys(Map<String,Object> map) {
        return activityMapper.selectActivityByConditionForPage(map);
    }

    /**
     * 根据ids批量查询市场活动
     * @param ids
     * @return
     */
    @Override
    public List<Activity> queryAllActivityById(String[] ids) {
        return activityMapper.selectAllActivityById(ids);
    }

    /**
     * 批量创建市场活动
     * @param activityList
     * @return
     */
    @Override
    public int saveCreateActivityByList(List<Activity> activityList) {
        return activityMapper.insertActivityByList(activityList);
    }

    /**
     * 查询市场活动详情
     * @return
     */
    @Override
    public Activity queryActivityForDetailById(String id) {
        return activityMapper.selectActivityForDetailById(id);
    }
}
