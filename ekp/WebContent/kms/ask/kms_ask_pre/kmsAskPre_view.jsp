<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/kmss-xform.tld" prefix="xform"%>
<%@ include file="/resource/jsp/view_top.jsp"%>
<script>
	function confirmDelete(msg){
	var del = confirm("<bean:message key="page.comfirmDelete"/>");
	return del;
}
</script>
<div id="optBarDiv">
	<kmss:auth requestURL="/kms/ask/kms_ask_pre/kmsAskPre.do?method=edit&fdId=${param.fdId}" requestMethod="GET">
		<input type="button"
			value="<bean:message key="button.edit"/>"
			onclick="Com_OpenWindow('kmsAskPre.do?method=edit&fdId=${param.fdId}','_self');">
	</kmss:auth>
	<kmss:auth requestURL="/kms/ask/kms_ask_pre/kmsAskPre.do?method=delete&fdId=${param.fdId}" requestMethod="GET">
		<input type="button"
			value="<bean:message key="button.delete"/>"
			onclick="if(!confirmDelete())return;Com_OpenWindow('kmsAskPre.do?method=delete&fdId=${param.fdId}','_self');">
	</kmss:auth>
	<input type="button"
		value="<bean:message key="button.close"/>"
		onclick="Com_CloseWindow();">
</div>

<p class="txttitle"><bean:message bundle="kms-ask" key="table.kmsAskPre"/></p>

<center>
<table class="tb_normal" width=95%>
	<tr>
		<td class="td_normal_title" width=15%>
			<bean:message bundle="kms-ask" key="kmsAskPre.docAlterTime"/>
		</td><td width="35%">
			<xform:datetime property="docAlterTime" />
		</td>
		<td class="td_normal_title" width=15%>
			<bean:message bundle="kms-ask" key="kmsAskPre.docContent"/>
		</td><td width="35%">
			<xform:rtf property="docContent" />
		</td>
	</tr>
</table>
</center>
<%@ include file="/resource/jsp/view_down.jsp"%>