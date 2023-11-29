<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%><!--动态获取当前协议，服务器ip地址，服务器端口号-->
<html>
<head>
	<base href="<%=basePath%>"><!--所有路径开端以此开始-->
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<!--分页插件css-->
<link href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<!--分页插件js-->
<script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

<script type="text/javascript">

	$(function() {
		//给创建添加单击事件
		$('#createActivityBtn').click(function () {
			//初始化工作
			//重置表单,清空上次的数据
			$("#createActivityForm").get(0).reset();
			//弹出创建市场活动模态窗口
			$("#createActivityModal").modal("show");
		});

		//给创建市场活动的保存按钮添加单击事件
		$("#saveCreateActivityBtn").click(function () {
			//收集参数
			var owner = $("#create-marketActivityOwner").val();
			var name = $.trim($("#create-marketActivityName").val());
			var startDate = $("#create-startDate").val();
			var endDate = $("#create-endDate").val();
			var cost = $("#create-cost").val();
			var description = $("#create-describe").val();
			//表单验证
			if (owner == "") {
				alert("所有者不能为空")
				return;
			}
			if (name == "") {
				alert("名称不能为空")
				return;
				;
			}
			//比较日期大小
			if (startDate != "" && endDate != "") {
				//使用字符串的大小代替日期的大小
				if (endDate < startDate) {
					alert("结束日期不能比开始日期小");
					return;
				}
			}
			//成本,使用正则(非负整数)
			var reExp = /^(([1-9]\d*)|0)$/;
			if (!reExp.test(cost)) {
				alert("成本只能是非负整数");
				return;
			}
			//发送请求,
			$.ajax({
				url: "workbench/activity/saveCreateActivity",
				data: {
					owner: owner,
					name: name,
					startDate: startDate,
					endDate: endDate,
					cost: cost,
					description: description
				},
				type: 'post',
				dataType: 'json',
				success: function (data) {
					if (data.code == "1") {
						//关闭模态窗口
						$("#createActivityModal").modal("hide");
						//刷新市场活动列，显示第一页数据，保持每页显示条数不变
						queryActivityByConditionForPage(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
					} else {
						//提示信息
						alert(data.message);
						//模态窗口不关闭
					}
				}
			})
		});

		//当容器加载完成后，对容器调用日历插件函数获取时间
		$(".mydate").datetimepicker({
			language: 'zh-CN',//语言
			format: 'yyyy-mm-dd',//日期的格式
			minView: 'month',//可以选择的最小视图
			initialDate: new Date(),//初始化显示日期
			autoclose: true,//设置选择完日期或者时间之后，是否自动关闭日历
			todayBtn: true,//设置是否显示”今天“按钮，默认false
			clearBtn: true,//设置是否显示”清空“按钮，默认false
		});

		//当市场活动主页面加载完成，所有的数据的第一页以及所有数据的总条数
		queryActivityByConditionForPage(1, 10);

		//给查询按钮添加单击事件
		$("#queryActivityBtn").click(function () {
			queryActivityByConditionForPage(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
		});

		//给全选按钮添加单击事件
		$("#chckAll").click(function () {
			$("#tBody input[type='checkbox']").prop("checked", this.checked);
		});

		//当点击列表中的选择按钮，判断所有选择的按钮是否与所有的按钮数量相等
		$("#tBody").on("click", "input[type='checkbox']", function () {
			if ($("#tBody input[type='checkbox']").size() == $("#tBody input[type='checkbox']:checked").size()) {
				$("#chckAll").prop("checked", true);
			} else {
				$("#chckAll").prop("checked", false);
			}
		})

		//给删除按钮添加单击事件
		$("#deleteActivityBtn").click(function () {
			//收集参数
			//获取列表中所有被选中的checkbox
			var chekkedIds = $("#tBody input[type='checkbox']:checked");
			if (chekkedIds.size() == 0) {
				alert("请选择要删除的市场活动");
				return;
			}

			//删除提醒
			if (window.confirm("确定删除吗")) {
				//整理id数据(将数据整理成xx=xx&xx=xx&...格式，方便发送)
				var ids = '';
				$.each(chekkedIds, function () {
					ids += "id=" + this.value + "&";
				});
				ids = ids.substr(0, ids.length - 1);
				//发送删除请求
				$.ajax({
					url: "workbench/activity/deleteActivityIds",
					data: ids,
					type: 'post',
					dataType: 'json',
					success: function (data) {
						if (data.code == "1") {
							//删除成功，刷新市场活动列表
							queryActivityByConditionForPage(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
						} else {
							//提示信息
							alert(data.message)
						}
					}
				})
			}
		})

		//给修改按钮添加单击事件
		$("#editActivityBtn").click(function () {
			//收集参数
			//获取列表中被选中的checkbox
			var chekkedIds = $("#tBody input[type='checkbox']:checked");
			if (chekkedIds.size() == 0) {
				alert("请选择要修改的市场活动")
				return;
			}
			if (chekkedIds.size() > 1) {
				alert("每次只能修改一条数据")
				return;
			}
			//var id=chekkedIds.val();
			//var id=chekkedIds.get(0).value;
			var id = chekkedIds[0].value;
			//发送请求
			$.ajax({
				url: 'workbench/activity/queryActivityById',
				data: {id: id},
				type: 'post',
				dataType: 'json',
				success: function (data) {
					//把市场活动的信息显示在修改的模态窗口上
					$("#edit-id").val(data.id);
					$("#edit-marketActivityOwner").val(data.owner);
					$("#edit-marketActivityName").val(data.name);
					$("#edit-startTime").val(data.startDate);
					$("#edit-endTime").val(data.endDate);
					$("#edit-cost").val(data.cost);
					$("#edit-description").val(data.description);
					//弹出模态窗口
					$("#editActivityModal").modal("show");
				}
			})
		})

		//给修改后的更新按钮添加单击事件
		$("#saveEditActivityBtn").click(function () {
			//获取元素
			var id = $("#edit-id").val();
			var owner = $("#edit-marketActivityOwner").val();
			var name = $.trim($("#edit-marketActivityName").val());
			var startDate = $("#edit-startTime").val();
			var endDate = $("#edit-endTime").val();
			var cost = $.trim($("#edit-cost").val());
			var description = $.trim($("#edit-description").val());
			//表单验证
			if (owner == "") {
				alert("所有者不能为空")
				return;
			}
			if (name == "") {
				alert("名称不能为空")
				return;
				;
			}
			//比较日期大小
			if (startDate != "" && endDate != "") {
				//使用字符串的大小代替日期的大小
				if (endDate < startDate) {
					alert("结束日期不能比开始日期小");
					return;
				}
			}
			//成本,使用正则(非负整数)
			var reExp = /^(([1-9]\d*)|0)$/;
			if (!reExp.test(cost)) {
				alert("成本只能是非负整数");
				return;
			}

			//发送Ajax请求
			$.ajax({
				url: 'workbench/activity/saveEditActivity',
				data: {
					id: id,
					owner: owner,
					name: name,
					startDate: startDate,
					endDate: endDate,
					cost: cost,
					description: description
				},
				type: 'post',
				dataType: 'json',
				success: function (data) {
					if (data.code == '1') {
						//关闭模态窗口
						$("#editActivityModal").modal('hide');
						//刷新市场活动列表，保存页面条数和页号都不变
						queryActivityByConditionForPage($("#demo_pag1").bs_pagination('getOption', 'currentPage'), $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
					} else {
						//提示信息
						alert(data.message);
						//模态窗口不关闭”show“打开
					}
				}
			})
		})

		//给批量导出添加单击事件
		$("#exportActivityAllBtn").click(function () {
			//发送同步请求
			window.location.href = "workbench/activity/queryAllActivitys";
		})

		/**
		 * 选择导出单击事件
		 */
		$("#exportActivityXzBtn").click(function () {
			//收集参数
			//所有被选择的checkbox
			var chekkedIds = $("#tBody input[type='checkbox']:checked");
			//判断个数
			if (chekkedIds.size() == 0) {
				alert("请选择要导出的数据")
				return;
			}
			//整理id数据(将数据整理成xx=xx&xx=xx&...格式，方便发送)
			var ids = '';
			$.each(chekkedIds, function () {
				ids += "id=" + this.value + "&";
			});
			ids = ids.substr(0, ids.length - 1);
			console.log(ids)
			//方法一：通过发送window.location.href发送get同步请求(缺点：get请求对传值有字符限制)
			//window.location.href="workbench/activity/queryAllActivityById?"+ids;
			//方法二：通过ajax将值传给后端，后端再返回一个url到前端，前端再通过window.location.href发送请求导出数据
			//该方法可以解决window.location.href的get请求字符限制，但是ajax的返回值只能是字符串，所以只起到一个post请求传id的作用
			$.ajax({
				url: "workbench/activity/queryAllActivityById",
				type: "post",
				data: ids,
				dataType: "json",
				success: function () {
					console.log("执行成功")
					window.location.href="workbench/activity/queryAllActivityById"
				},
				error:function (){
					console.log("执行失败")
					window.location.href="workbench/activity/queryAllActivityById"
				}
			});
		});

		/**
		 * 导入市场活动按钮事件
		 */
		$("#importActivityBtn").click(function () {
			//收集参数
			//直接.val只能获取到在隐藏目录下的路径名 C:\fakepath\autocomplete.sql
			var activityFile = $("#activityFile").val();
			console.log(activityFile)
			//获取文件后缀，判断是不是xls文件
			//substr(从第几个开始，到第几个结束)
			//lastIndexOf()返回最后一个出现的位置 闭区间，加一才能不包括 .
			//toLocaleLowerCase()转成小写
			var suffix=activityFile.substr(activityFile.lastIndexOf(".")+1).toLocaleLowerCase()
			console.log(suffix)
			if (suffix!="xls"){
				alert("只支持xls文件");
				return;
			}
			//通过获取activityFile的DOM对象，获取文件
			var activityFile = $("#activityFile")[0].files[0];
			//判断文件大小是否符合标准
			if (activityFile.size > 1024 * 1024 * 5){
				alert("文件最大不能超过5MB")
				return;
			}

			//FormData是ajax提供的接口，可以模拟键值对向后台提交参数；
			//FormData最大的优势是不仅可以提交文本数据，还能提供二进制数据
			var formData = new FormData();
			//一个formData可以配置多个参数
			formData.append("activityFile", activityFile);

			//发送请求
			$.ajax({
				url:"workbench/activity/importActivity",
				data:formData,
				type:"post",
				dataType:"json",
				processData:false,//ajax向后台提交参数前，是否把参数统一转换成字符串
				contentType:false,//ajax向后台提交参数之前，是否把所有的参数统一按urlencoded编码
				success:function (data) {
					if (data.code == '1'){
						alert("成功导入"+data.retData+"条数据");
						//关闭模态窗口
						$("#importActivityModal").modal('hide');
						//刷新市场活动列表，保存页面条数不变
						queryActivityByConditionForPage(1, $("#demo_pag1").bs_pagination('getOption', 'rowsPerPage'));
					}else {
						alert(data.message);
					}
				}
			})
		});
	});
	/**
	 * 获取市场活动列表数据
	 * @param pageNo 当页第一条的序号
	 * @param pageSize 每页数据
	 */
	function queryActivityByConditionForPage(pageNo, pageSize) {
		//收集参数
		var name = $("#query-name").val();
		var owner = $("#query-owner").val();
		var startDate = $("#query-startDate").val();
		var endDate = $("#query-endDate").val();
		//发送请求
		$.ajax({
			url: "workbench/activity/queryActivityByConditionForPage",
			data: {
				name: name,
				owner: owner,
				startDate: startDate,
				endDate: endDate,
				pageNo: pageNo,
				pageSize: pageSize
			},
			type: 'post',
			dataType: 'json',
			success: function (data) {
				//显示市场活动的列表
				//遍历activityList，拼接所以行数据
				var htmlStr = "";
				$.each(data.activityList, function (index, obj) {
					htmlStr += "<tr class='active'>";
					htmlStr += "	<td><input type='checkbox' value='" + obj.id + "'/></td>";
					htmlStr += "	<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/activity/detailActivity?id="+obj.id+"'\">" + obj.name + "</a></td>";
					htmlStr += "	<td>" + obj.owner + "</td>";
					htmlStr += "	<td>" + obj.startDate + "</td>";
					htmlStr += "	<td>" + obj.endDate + "</td>";
					htmlStr += "</tr>"
				});
				//显示
				$("#tBody").html(htmlStr);

				//取消全选按钮
				$("#chckAll").prop("checked", false);

				//对容器调用bs_pagination工具函数，显示翻页信息
				$("#demo_pag1").bs_pagination({
					currentPage: data.activityPageInfo.pageNum,//当前页号，相当于pageNo

					rowsPerPage: data.activityPageInfo.pageSize,//每页显示条数，相当于pageSize
					totalRows: data.activityPageInfo.total,//总条数
					totalPages: data.activityPageInfo.pages,//总页数，必填参数

					visiblePageLinks: 5,//最多可以显示的卡片数

					showGoToPage: true,//是否显示“跳转到”部分，默认true
					showRowsPerPage: true,//是否显示“每页显示条数”部分
					showRowsInfo: true,//是否显示记录的信息，默认true--显示

					//用户每次切换页号，都自动触发本函数
					//每次返回切换页号之后的pageNo和pageSize
					onChangePage: function (event, pageObj) {
						//console.log(pageObj);
						//currentPage当前页面的页号
						//rowsPerPage每页显示条数
						queryActivityByConditionForPage(pageObj.currentPage, pageObj.rowsPerPage)
					}
				});
			}
		})
	}

</script>
</head>
<body>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">

					<form id="createActivityForm" class="form-horizontal" role="form">

						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">
									<c:forEach items="${userList}" var="u">
										<option value="${u.id}">${u.name}</option>
									</c:forEach>
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>

						<div class="form-group">
							<label for="create-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" name="mydate" id="create-startDate" readonly>
							</div>
							<label for="create-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" name="mydate" id="create-endDate" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>

					</form>

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveCreateActivityBtn">保存</button>
				</div>
			</div>
		</div>
	</div>

	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">

					<form class="form-horizontal" role="form">
						<!--设置id隐藏域用来接收后端传来的id：不显示-->
						<input type="hidden" id="edit-id">
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">
								  <c:forEach items="${userList}" var="u">
									  <option value="${u.id}">${u.name}</option>
								  </c:forEach>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName" value="发传单">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" id="edit-startTime" name="mydate" readonly>
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" id="edit-endTime" name="mydate" readonly>
							</div>
						</div>

						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>

						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
							</div>
						</div>

					</form>

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveEditActivityBtn">更新</button>
				</div>
			</div>
		</div>
	</div>

	<!-- 导入市场活动的模态窗口 -->
    <div class="modal fade" id="importActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
                </div>
                <div class="modal-body" style="height: 350px;">
                    <div style="position: relative;top: 20px; left: 50px;">
                        请选择要上传的文件：<small style="color: gray;">[仅支持.xls或.xlsx格式]</small>
                    </div>
                    <div style="position: relative;top: 40px; left: 50px;">
                        <input type="file" id="activityFile">
                    </div>
                    <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;" >
                        <h3>重要提示</h3>
                        <ul>
                            <li>操作仅针对Excel，仅支持后缀名为XLS/XLSX的文件。</li>
                            <li>给定文件的第一行将视为字段名。</li>
                            <li>请确认您的文件大小不超过5MB。</li>
                            <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                            <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                            <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                            <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
                </div>
            </div>
        </div>
    </div>


	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">

			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="query-name">
				    </div>
				  </div>

				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="query-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control mydate" type="text" id="query-startDate" name="mydate" readonly/>
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control mydate" type="text" id="query-endDate" name="mydate" readonly>
				    </div>
				  </div>

				  <button type="button" class="btn btn-default" id="queryActivityBtn">查询</button>

				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="createActivityBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editActivityBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger"  id="deleteActivityBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				<div class="btn-group" style="position: relative; top: 18%;">
                    <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal" ><span class="glyphicon glyphicon-import"></span> 上传列表数据（导入）</button>
                    <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（批量导出）</button>
                    <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（选择导出）</button>
                </div>
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="chckAll"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="tBody">
                        </tr>
					</tbody>
				</table>
				<!--分页导航-->
				<div id="demo_pag1"></div>
			</div>

		</div>

	</div>
</body>
</html>
