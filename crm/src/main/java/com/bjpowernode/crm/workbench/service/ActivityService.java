package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    /**
     * 船检市场活动
     * @param activity
     * @return
     */
    int saveCreateActivity(Activity activity);

    /**
     * 活动分页查询
     *
     * @param map
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<Activity> queryActivityByConditionForPage(Map<String,Object> map, Integer pageNo, Integer pageSize);

    /**
     * 根据ids批量删除市场活动
     * @param ids
     * @return
     */
    int deleteActivityByIds(String[] ids);

    /**
     * 根据id查询市场活动
     * @param id
     * @return
     */
    Activity selectActivityById(String id);

    /**
     * 保存市场活动的修改
     * @param activity
     * @return
     */
    int saveEditActivity(Activity activity);

    /**
     * 查询所有的市场活动
     * @return
     */
    List<Activity> queryAllActivitys(Map<String,Object> map);

    /**
     * 根据ids查找市场活动
     * @param ids
     * @return
     */
    List<Activity> queryAllActivityById(String[] ids);
    int saveCreateActivityByList(List<Activity> activityList);
    Activity queryActivityForDetailById(String id);
}
