<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/resource/jsp/common.jsp"%>
<c:if test="${empty requestScope.sysRelationMainPrefix}">
	<%-- 系统模板中使用 --%>
	<c:import url="/resource/jsp/edit_top.jsp" charEncoding="UTF-8" />
	<form name="sysRelationMainForm" action="<c:url value='/sys/relation/sys_relation_main/sysRelationMain.do'/>" method="post">
		<div id="optBarDiv">
			<input type=button value="<bean:message key="button.update"/>"
				onclick="Com_Submit(document.sysRelationMainForm,'update','fdId');">
		</div>
</c:if>
<script type="text/javascript">
Com_IncludeFile("docutil.js|dialog.js|doclist.js", null, "js");
</script>
<center>
<table id="sysRelationZoneTop" class="tb_normal" style="border:0;" width=100%>
	<tr>
	<td align="right" style="border:0;">
		<input type="hidden" name="${requestScope.sysRelationMainPrefix}fdId" value="<c:out value='${sysRelationMainForm.fdId}' />" />
		<input type="hidden" name="${requestScope.sysRelationMainPrefix}fdKey" value="<c:out value='${fdKey}' />"/>
		<input type="hidden" name="${requestScope.sysRelationMainPrefix}fdModelName" value="<c:out value='${sysRelationMainForm.fdModelName}' />"/>
		<input type="hidden" name="${requestScope.sysRelationMainPrefix}fdModelId" value="<c:out value='${sysRelationMainForm.fdModelId}' />"/>
		<input type="hidden" name="${requestScope.sysRelationMainPrefix}fdParameter" value="<c:out value='${sysRelationMainForm.fdParameter}' />"/>
		<input type=button class="btnopt" value='<bean:message key="button.insert" />'
			onclick="addRelationEntry();">
		<c:if test="${not empty requestScope.sysRelationMainPrefix}">
			&nbsp;&nbsp;
			<input type=button class="btnopt" value='<bean:message bundle="sys-relation" key="button.insertFromTemplate" />' 
				onclick="importFromTemplate();">&nbsp;&nbsp;
			<input type=button class="btnopt" value="<bean:message bundle="sys-relation" key="button.preview" />" onclick="preview()">
		</c:if>
	</td>
	</tr>
</table>
<%-- 尽量不要在id为sysRelationZone的table中增删行 --%>
<table id="sysRelationZone" class="tb_normal" width=100%>
	<tr align="center">
		<td class="td_normal_title" width="35%">
			<bean:message bundle="sys-relation" key="sysRelationEntry.fdName" />
		</td>
		<td class="td_normal_title" width="35%">
			<bean:message bundle="sys-relation" key="sysRelationEntry.fdType" />
		</td>
		<td class="td_normal_title" width="30%">
			<bean:message bundle="sys-relation" key="button.operation" />
		</td>
	</tr>
	<c:set var="entryPrefixIndex" value="${requestScope.sysRelationMainPrefix}sysRelationEntryFormList[!{index}]." />
	<tr KMSS_IsReferRow="1" style="display:none">
		<td>
			<input name="${entryPrefixIndex}fdModuleName" class="inputsgl" style="width: 90%" />
			<span class="txtstrong">*</span>
			<input type="hidden" name="${entryPrefixIndex}fdId" />
			<input type="hidden" name="${entryPrefixIndex}fdType" />
			<input type="hidden" name="${entryPrefixIndex}fdModuleModelName" />
			<input type="hidden" name="${entryPrefixIndex}fdOrderBy" />
			<input type="hidden" name="${entryPrefixIndex}fdOrderByName" />
			<input type="hidden" name="${entryPrefixIndex}fdPageSize" />
			<input type="hidden" name="${entryPrefixIndex}fdRelationProperty" />
			<input type="hidden" name="${entryPrefixIndex}fdParameter" />
			<%-- 说明：为了避免动态表格刷新，条件的字段提交的时候在拼装  --%>
			<input type="hidden" name="${entryPrefixIndex}fdKeyWord" />
			<input type="hidden" name="${entryPrefixIndex}docCreatorId" />
			<input type="hidden" name="${entryPrefixIndex}docCreatorName" />
			<input type="hidden" name="${entryPrefixIndex}fdFromCreateTime" />
			<input type="hidden" name="${entryPrefixIndex}fdToCreateTime" />
			<input type="hidden" name="${entryPrefixIndex}fdSearchScope" />
			<input type="hidden" name="${entryPrefixIndex}fdOtherUrl" />
		</td>
		<td align="center">
			&nbsp;
		</td>
		<td align="center">
			<a href="#" onclick="editRelationEntry();" style="text-decoration: none"><img src="${KMSS_Parameter_StylePath}icons/edit.gif" border="0" /></a>
			<a href="#" onclick="deleteRelationEntry();" style="text-decoration: none">&nbsp;<img src="${KMSS_Parameter_StylePath}icons/delete.gif" border="0" /></a>
			<a href="#" onclick="DocList_MoveRow(-1);" style="text-decoration: none">&nbsp;<img src="${KMSS_Parameter_StylePath}icons/up.gif" border="0" /></a>
			<a href="#" onclick="DocList_MoveRow(1);" style="text-decoration: none">&nbsp;<img src="${KMSS_Parameter_StylePath}icons/down.gif" border="0" /></a>
		</td>
	</tr>
	<c:forEach items="${sysRelationMainForm.sysRelationEntryFormList}" varStatus="vstatus" var="sysRelationEntryForm">
		<c:set var="entryPrefix" value="${requestScope.sysRelationMainPrefix}sysRelationEntryFormList[${vstatus.index}]." />
		<tr KMSS_IsContentRow="1">
			<td>
				<input name="${entryPrefix}fdModuleName" value="<c:out value='${sysRelationEntryForm.fdModuleName}' />" class="inputsgl" style="width: 90%" />
				<span class="txtstrong">*</span>
				<input type="hidden" name="${entryPrefix}fdId" value="<c:out value='${sysRelationEntryForm.fdId}' />" />
				<input type="hidden" name="${entryPrefix}fdType" value="<c:out value='${sysRelationEntryForm.fdType}' />" />
				<input type="hidden" name="${entryPrefix}fdModuleModelName" value="<c:out value='${sysRelationEntryForm.fdModuleModelName}' />" />
				<input type="hidden" name="${entryPrefix}fdOrderBy" value="<c:out value='${sysRelationEntryForm.fdOrderBy}' />" />
				<input type="hidden" name="${entryPrefix}fdOrderByName" value="<c:out value='${sysRelationEntryForm.fdOrderByName}' />" />
				<input type="hidden" name="${entryPrefix}fdPageSize" value="<c:out value='${sysRelationEntryForm.fdPageSize}' />" />
				<input type="hidden" name="${entryPrefix}fdRelationProperty" value="<c:out value='${sysRelationEntryForm.fdRelationProperty}' />" />
				<input type="hidden" name="${entryPrefix}fdParameter" value="<c:out value='${sysRelationEntryForm.fdParameter}' />" />
				<%-- 说明：为了避免动态表格刷新，条件的字段提交的时候在拼装  --%>
				<input type="hidden" name="${entryPrefix}fdKeyWord" value="<c:out value='${sysRelationEntryForm.fdKeyWord}' />" />
				<input type="hidden" name="${entryPrefix}docCreatorId" value="<c:out value='${sysRelationEntryForm.docCreatorId}' />" />
				<input type="hidden" name="${entryPrefix}docCreatorName" value="<c:out value='${sysRelationEntryForm.docCreatorName}' />" />
				<input type="hidden" name="${entryPrefix}fdFromCreateTime" value="<c:out value='${sysRelationEntryForm.fdFromCreateTime}' />" />
				<input type="hidden" name="${entryPrefix}fdToCreateTime" value="<c:out value='${sysRelationEntryForm.fdToCreateTime}' />" />
				<input type="hidden" name="${entryPrefix}fdSearchScope" value="<c:out value='${sysRelationEntryForm.fdSearchScope}' />" />
				<input type="hidden" name="${entryPrefix}fdOtherUrl" value="<c:out value='${sysRelationEntryForm.fdOtherUrl}' />" />
			</td>
			<td align="center">
				<sunbor:enumsShow enumsType="sysRelationEntry_fdType" value="${sysRelationEntryForm.fdType}"></sunbor:enumsShow>
			</td>
			<td align="center">
				<a href="#" onclick="editRelationEntry();"><img src="${KMSS_Parameter_StylePath}icons/edit.gif" border="0" /></a>
				<a href="#" onclick="deleteRelationEntry();"><img src="${KMSS_Parameter_StylePath}icons/delete.gif" border="0" /></a>
				<a href="#" onclick="DocList_MoveRow(-1);"><img src="${KMSS_Parameter_StylePath}icons/up.gif" border="0" /></a>
				<a href="#" onclick="DocList_MoveRow(1);"><img src="${KMSS_Parameter_StylePath}icons/down.gif" border="0" /></a>
			</td>
		</tr>
	</c:forEach>
</table>
<table id="sysRelationZoneFooter" class="tb_normal" style="border-top: 0px;" width=100%>
	<tr>
		<td style="border-top: 0px;"><font color="red"><bean:message bundle="sys-relation" key="sysRelationEntry.fdNote" /></font></td>
	</tr>
</table>
</center>
<%@ include file="sysRelationMain_edit_script.jsp"%>
<c:if test="${empty requestScope.sysRelationMainPrefix}">
	<%-- 系统模板中使用 --%>
	</form>
	<c:import url="/resource/jsp/edit_down.jsp" charEncoding="UTF-8" />
</c:if>