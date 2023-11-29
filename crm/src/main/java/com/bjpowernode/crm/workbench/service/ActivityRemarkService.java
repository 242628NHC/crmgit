package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkService {
    /**
     * 根据市场活动Id查询活动备注
     * @return
     */
    List<ActivityRemark> queryActivityRemarkForDetailByActivityId(String activityId);

    /**
     * 添加市场活动备注
     * @param activityRemark
     * @return
     */
    int saveCreateActivityRemark(ActivityRemark activityRemark);

    /**
     * 根据id删除市场活动备注
     * @param id
     * @return
     */
    int deleteActivityRemarkById(String id);

    /**
     * 修改市场活动备注
     * @param activityRemark
     * @return
     */
    int updateActivityRemark(ActivityRemark activityRemark);
}
