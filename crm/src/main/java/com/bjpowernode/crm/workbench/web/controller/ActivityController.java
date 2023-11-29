package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DataUtils;
import com.bjpowernode.crm.commons.utils.HSSFUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.github.pagehelper.PageInfo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.TestConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityRemarkService activityRemarkService;
    @RequestMapping("/workbench/activity/index")
    public String index(HttpServletRequest request){
        //调用service层方法，查询所有在职用户
        List<User> userList=userService.queryAllUsers();
        request.setAttribute("userList",userList);
        return "workbench/activity/index";
    }
    @RequestMapping(value = "/workbench/activity/saveCreateActivity",method = RequestMethod.POST)
    @ResponseBody
    public Object saveCreateActivity(Activity activity, HttpSession session){
        User user=(User) session.getAttribute(Contants.SESSION_USER);
        //封装参数
        activity.setId(UUIDUtils.getUUID());//用工具类生成UUID
        activity.setCreateTime(DataUtils.formateDateTime(new Date()));//用工具类当前时间
        activity.setCreateBy(user.getId());//市场活动的创建者，登录的用户

        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，保存创建的市场活动
            int ret = activityService.saveCreateActivity(activity);

            if (ret > 0) {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("当前网络延迟，创建失败...");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("当前网络延迟，创建失败...");
        }
        return returnObject;
    }

    /**
     * 市场活动查询分页
     * @param name 市场活动名
     * @param owner  创建者
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageNo 每页开始记录的编号（待计算）
     * @param pageSize 每页行数
     * @return
     */
    @RequestMapping("/workbench/activity/queryActivityByConditionForPage")
    @ResponseBody
    public Object queryActivityByConditionForPage(String name,String owner,String startDate,String endDate,Integer pageNo,Integer pageSize){
        //封装参数
        Map<String,Object> map=new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        //调用service层方法，查询数据
        List<Activity> activityList=activityService.queryActivityByConditionForPage(map, pageNo, pageSize);
        //调用PageHelper分页插件处理分页逻辑
        PageInfo<Activity> activityPageInfo=new PageInfo<>(activityList);
        //根据查询结果，生成响应信息
        Map<String,Object> retMap=new HashMap<>();
        retMap.put("activityList",activityList);
        retMap.put("activityPageInfo",activityPageInfo);
        return retMap;
    }

    /**
     * 批量删除市场活动
     * @param id
     * @return
     */
    @RequestMapping("/workbench/activity/deleteActivityIds")
    @ResponseBody
    public Object deleteActivityIds(String[] id){
        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，删除市场活动
            int ret =activityService.deleteActivityByIds(id);
            if (ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnObject;
    }

    /**
     * 根据id查询市场活动
     * @param id
     * @return
     */
    @RequestMapping("/workbench/activity/queryActivityById")
    @ResponseBody
    public Object queryActivityById(String id){
        //调用service层方法，查询市场活动
        Activity activity= activityService.selectActivityById(id);
        //根据查询结果，返回响应信息
        return activity;
    }

    /**
     * 修改市场活动
     * @param activity
     * @param session
     * @return
     */
    @RequestMapping("/workbench/activity/saveEditActivity")
    @ResponseBody
    public Object saveEditActivity(Activity activity, HttpSession session){
       User user=(User)session.getAttribute(Contants.SESSION_USER);
        //封装参数
        activity.setEditTime(DataUtils.formateDateTime(new Date()));
        activity.setEditBy(user.getId());

        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，保存修改的市场活动
            int ret = activityService.saveEditActivity(activity);
            //判断修改是否成功
            if (ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }

    /**
     * 查找所有生成Excel文件
     * @return
     */
    @RequestMapping("/workbench/activity/queryAllActivitys")
    public void queryAllActivitys(HttpServletResponse response,String name,String owner,String startDate,String endDate,Integer pageNo,Integer pageSize) throws Exception {
        //封装参数
        Map<String,Object> map=new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        //调用service层方法，查询数据
        List<Activity> activityList=activityService.queryAllActivitys(map);
        //生成exel文件，并且把activityList写入到excel文件中
        HSSFWorkbook wb=new HSSFWorkbook();
        HSSFSheet sheet= wb.createSheet("市场活动列表");
        HSSFRow row=sheet.createRow(0);
        HSSFCell cell= row.createCell(0);
        cell.setCellValue("ID");
        cell=row.createCell(1);
        cell.setCellValue("所有者");
        cell=row.createCell(2);
        cell.setCellValue("名称");
        cell=row.createCell(3);
        cell.setCellValue("开始日期");
        cell=row.createCell(4);
        cell.setCellValue("结束日期");
        cell=row.createCell(5);
        cell.setCellValue("成本");
        cell=row.createCell(6);
        cell.setCellValue("描述");
        cell=row.createCell(7);
        cell.setCellValue("创建时间");
        cell=row.createCell(8);
        cell.setCellValue("创建者");
        cell=row.createCell(9);
        cell.setCellValue("修改时间");
        cell=row.createCell(10);
        cell.setCellValue("修改者");
        //判断是否为空决定是否遍历
        if (activityList!=null&&activityList.size()>0){
            // 遍历activityList，创建Excel数据行
            int rowNum = 1;
            for (Activity activity : activityList) {
                HSSFRow dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(activity.getId());
                dataRow.createCell(1).setCellValue(activity.getOwner());
                dataRow.createCell(2).setCellValue(activity.getName());
                dataRow.createCell(3).setCellValue(activity.getStartDate());
                dataRow.createCell(4).setCellValue(activity.getEndDate());
                dataRow.createCell(5).setCellValue(activity.getCost());
                dataRow.createCell(6).setCellValue(activity.getDescription());
                dataRow.createCell(7).setCellValue(activity.getCreateTime());
                dataRow.createCell(8).setCellValue(activity.getCreateBy());
                dataRow.createCell(9).setCellValue(activity.getEditTime());
                dataRow.createCell(10).setCellValue(activity.getEditBy());
            }
        }
        //当前程序的文件传输是从 服务器内存传到服务器磁盘，再从服务器磁盘传到客户内存，现在转变为内存传到内存改变（注释掉的代码）
        //根据wb对象生成exel文件
        /*OutputStream os = new FileOutputStream("E:\\activityList.xls");
        wb.write(os);//将文件写入服务器磁盘*/
        //关闭资源
        /*os.close();
        wb.close();*/

        //把生成的exel文件下载到客户端
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Content-Disposition","attachment;filename=mystudentList.xls");
        //获取输出流
        OutputStream out=response.getOutputStream();
        //读取excel文件(InputStream),把输出到浏览器（OutoutStream）
        /*InputStream is=new FileInputStream("E:\\activityList.xls");
        byte[] buff=new byte[256];
        int len=0;
        while ((len=is.read(buff))!=-1){
            out.write(buff,0,len);//从磁盘取出到客户端内存中
        }
        //关闭资源
        is.close();*/
        wb.write(out);

        wb.close();
        out.flush();
    }

    /**
     * 根据id查询所有生成Excel文件
     * @param session
     * @param response
     * @throws IOException
     */
    @RequestMapping("/workbench/activity/queryAllActivityById")
    public void queryAllActivityById(String[] id, HttpSession session, HttpServletResponse response) throws IOException {
        //调用service层方法，查询数据
        if (id!=null) {
            session.setAttribute("id", id);
        }else {
            //从session中获取id
            String[] ids= (String[]) session.getAttribute("id");
            //删除session当中的id
            session.removeAttribute("id");
            //获取数据
            List<Activity> activityList = activityService.queryAllActivityById(ids);
            //生成exel文件，并且把activityList写入到excel文件中
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("市场活动列表");
            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("ID");
            cell = row.createCell(1);
            cell.setCellValue("所有者");
            cell = row.createCell(2);
            cell.setCellValue("名称");
            cell = row.createCell(3);
            cell.setCellValue("开始日期");
            cell = row.createCell(4);
            cell.setCellValue("结束日期");
            cell = row.createCell(5);
            cell.setCellValue("成本");
            cell = row.createCell(6);
            cell.setCellValue("描述");
            cell = row.createCell(7);
            cell.setCellValue("创建时间");
            cell = row.createCell(8);
            cell.setCellValue("创建者");
            cell = row.createCell(9);
            cell.setCellValue("修改时间");
            cell = row.createCell(10);
            cell.setCellValue("修改者");
            //判断是否为空决定是否遍历
            if (activityList != null && activityList.size() > 0) {
                // 遍历activityList，创建Excel数据行
                int rowNum = 1;
                for (Activity activity : activityList) {
                    HSSFRow dataRow = sheet.createRow(rowNum++);
                    dataRow.createCell(0).setCellValue(activity.getId());
                    dataRow.createCell(1).setCellValue(activity.getOwner());
                    dataRow.createCell(2).setCellValue(activity.getName());
                    dataRow.createCell(3).setCellValue(activity.getStartDate());
                    dataRow.createCell(4).setCellValue(activity.getEndDate());
                    dataRow.createCell(5).setCellValue(activity.getCost());
                    dataRow.createCell(6).setCellValue(activity.getDescription());
                    dataRow.createCell(7).setCellValue(activity.getCreateTime());
                    dataRow.createCell(8).setCellValue(activity.getCreateBy());
                    dataRow.createCell(9).setCellValue(activity.getEditTime());
                    dataRow.createCell(10).setCellValue(activity.getEditBy());
                }
            }

            //把生成的exel文件下载到客户端
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=mystudentList.xls");
            //获取输出流
            OutputStream out = response.getOutputStream();
            wb.write(out);

            wb.close();
            out.flush();
        }
    }

    /**
     * 根据文件批量导入
     * @param activityFile
     * @return
     */
    @RequestMapping("/workbench/activity/importActivity")
    @ResponseBody
    public Object importActivity(MultipartFile activityFile, HttpSession session) throws Exception {
        ReturnObject returnObject = new ReturnObject();
        User user = (User)session.getAttribute(Contants.SESSION_USER);//获取当前用户
        try {
        //把excel文件写到磁盘目录中
        /*String originalFilename = activityFile.getOriginalFilename();//文件名
        File file = new File("E:\\轻音图片\\",originalFilename);
        activityFile.transferTo(file);*/

        //解析excel文件，获取文件中的数据，并且封装到activityList
        //根据excel文件生成HSSFWorkbook对象,封装了excel文件的所有信息
        //InputStream is = new FileInputStream("E:\\轻音图片\\"+originalFilename);


        //getInputStream()方法用于获取该文件的输入流。
        // 通过这个输入流，你可以读取文件中的数据。
        InputStream is = activityFile.getInputStream();
        HSSFWorkbook wb = new HSSFWorkbook(is);
        //根据wb获取HSSFSheet对象，封装了一页的所有信息
        HSSFSheet sheet = wb.getSheetAt(0);//页的下标，从零开始
        //根据sheet获取HSSFRow对象，封装了一行所有的信息
        HSSFRow row = null;
        HSSFCell cell = null;
        Activity activity = null;
        List<Activity> activityList = new ArrayList<>();
        //sheet.getLastRowNum()获取这一页最后一行的下标
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);//行的下标，下标从0开始(0是表头)，依次增加
            activity = new Activity();
            activity.setId(UUIDUtils.getUUID());//id不能使用用户给的数据
            activity.setOwner(user.getId());//使用当前用户作为创建者
            activity.setCreateTime(DataUtils.formateDateTime(new Date()));//使用当前时间作为创建时间
            activity.setCreateBy(user.getId());//使用当前用户作为创建者

            for (int j = 0; j < row.getLastCellNum(); j++) {//row.getLastCellNum()最后一列的下标+1
                //根据row获取HSSFCell对象，封装了一列的所有信息
                cell = row.getCell(j);
                //列的下标，下标从0开始，依次增加
                //获取列中的数据(表的数据顺序应当与用户预先约定：项目名称，开始日期，结束日期，成本，描述)
                String cellValue = HSSFUtils.getCellValueForStr(cell);
                if (j == 0) {
                    activity.setName(cellValue);
                } else if (j == 1) {
                    activity.setStartDate(cellValue);
                } else if (j == 2) {
                    activity.setEndDate(cellValue);
                } else if (j == 3) {
                    activity.setCost(cellValue);
                } else if (j == 4) {
                    activity.setDescription(cellValue);
                }
            }
            //每一行中所有列都封装到activity中，并保存到activityList中
            activityList.add(activity);
        }
        //调用service层方法，保存市场活动
        int ret = activityService.saveCreateActivityByList(activityList);

        returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        returnObject.setRetData(ret);
    }catch(Exception e) {
        e.printStackTrace();
        returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
        returnObject.setMessage("系统忙，请稍后重试");
    }
        return returnObject;
    }

    /**
     * 跳转到活动详情页面
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("/workbench/activity/detailActivity")
    public String detailActivity(String id,HttpServletRequest request){
        //调用service层方法，查询数据
        Activity activity = activityService.queryActivityForDetailById(id);
        List<ActivityRemark> remarkList = activityRemarkService.queryActivityRemarkForDetailByActivityId(id);
        //把数据保存到request中
        request.setAttribute("activity",activity);
        request.setAttribute("remarkList",remarkList);
        //请求转发
        return "workbench/activity/detail";
    }
}
