<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/sys/ui/jsp/common.jsp"%>
<template:include file="/tib/common/tib_ui_list.jsp">
	<template:replace name="title">${ lfn:message('tib-common:module.tib.manage') }</template:replace>
	<template:replace name="path">
		<ui:menu layout="sys.ui.menu.nav" style="height:40px;line-height:40px;" id="categoryId">
			<ui:menu-item text="${ lfn:message('home.home') }" icon="lui_icon_s_home" href="/" target="_self">
			</ui:menu-item>
			<ui:menu-item text="${ lfn:message('tib-common:module.tib.common') }" href="/tib/common/" target="_self">
			</ui:menu-item>
			<ui:menu-item text="${ lfn:message('tib-common-mapping:module.tib.common.mapping') }" href="/tib/common/mapping/" target="_self">
			</ui:menu-item>
			<ui:menu-item text="${ lfn:message('tib-common-mapping:table.tibCommonMappingModule') }" href="/tib/common/mapping/" target="_self">
			</ui:menu-item>
		</ui:menu>
	</template:replace>
	
	<%-- 右边框内容 --%>
	<template:replace name="content">
		<%-- 筛选器 --%>
		<list:criteria expand="true">
			<list:cri-ref key="fdModuleName" ref="criterion.sys.docSubject">
			</list:cri-ref>
			<list:cri-auto modelName="com.landray.kmss.tib.common.mapping.model.TibCommonMappingModule" property="fdUse"/>
		</list:criteria>
		
		<%-- 显示列表按钮行 --%>
		<div class="lui_list_operation">
			<table width="100%">
				<tr>
					<td align="right">
						<ui:toolbar>
							<kmss:auth requestURL="/tib/common/mapping/tib_common_mapping_module/tibCommonMappingModule.do?method=add">
								<ui:button text="${lfn:message('button.add')}" onclick="parent.addDoc('${LUI_ContextPath}/tib/common/mapping/tib_common_mapping_module/tibCommonMappingModule.do?method=add')" order="2" ></ui:button>
							</kmss:auth>
							<kmss:auth requestURL="/tib/common/mapping/tib_common_mapping_module/tibCommonMappingModule.do?method=deleteall">
								<ui:button text="${lfn:message('button.delete')}" order="4" onclick="delDoc('${LUI_ContextPath}/tib/common/mapping/tib_common_mapping_module/tibCommonMappingModule.do?method=deleteall')"></ui:button>
							</kmss:auth>
						</ui:toolbar>						
					</td>
				</tr>
			</table>
		</div>
		<ui:fixed elem=".lui_list_operation"></ui:fixed>
		<list:listview id="listview">
			<ui:source type="AjaxJson">
				{url:'/tib/common/mapping/tib_common_mapping_module/tibCommonMappingModuleIndex.do?method=list'}
			</ui:source>
			<%--列表形式--%>
			<list:colTable isDefault="false" layout="sys.ui.listview.columntable" 
				rowHref="/tib/common/mapping/tib_common_mapping_module/tibCommonMappingModule.do?method=view&fdId=!{fdId}" name="columntable">
				<list:col-checkbox></list:col-checkbox>
				<list:col-serial></list:col-serial>
				<list:col-auto props=""></list:col-auto>
			</list:colTable>   
		</list:listview> 
		<script type="text/javascript">
		seajs.use(['lui/jquery', 'lui/util/str', 'lui/dialog','lui/topic'], function($, strutil, dialog , topic) {
		 	//删除
	 		window.delDoc = function(url){
	 			var values = [];
				$("input[name='List_Selected']:checked").each(function(){
					values.push($(this).val());
				});
				if(values.length==0){
					dialog.alert('<bean:message key="page.noSelect"/>');
					return;
				}
				dialog.confirm('<bean:message key="page.comfirmDelete"/>',function(value){
					if(value==true){
						window.del_load = dialog.loading();
						$.post('<c:url value="'+ url +'"/>',
								$.param({"List_Selected":values},true),delCallback,'json');
					}
				});
			};
			window.delCallback = function(data){
				if(window.del_load!=null) {
					window.del_load.hide();
				}
				if(data!=null && data.status==true){
					topic.publish("list.refresh");
					dialog.success('<bean:message key="return.optSuccess" />');
				}else{
					dialog.failure('<bean:message key="return.optFailure" />');
				}
			};
	 	});
		</script>
		<br>
	 	<list:paging></list:paging>
	</template:replace>
</template:include>