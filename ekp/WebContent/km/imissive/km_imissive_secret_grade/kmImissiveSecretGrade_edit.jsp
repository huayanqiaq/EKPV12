<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/resource/jsp/edit_top.jsp"%>
<html:form action="/km/imissive/km_imissive_secret_grade/kmImissiveSecretGrade.do" >
<div id="optBarDiv">
	<c:if test="${kmImissiveSecretGradeForm.method_GET=='edit'}">
		<input type=button value="<bean:message key="button.update"/>"
			onclick="Com_Submit(document.kmImissiveSecretGradeForm, 'update');">
	</c:if>
	<c:if test="${kmImissiveSecretGradeForm.method_GET=='add'}">
		<input type=button value="<bean:message key="button.save"/>"
			onclick="Com_Submit(document.kmImissiveSecretGradeForm, 'save');">
		<input type=button value="<bean:message key="button.saveadd"/>"
			onclick="Com_Submit(document.kmImissiveSecretGradeForm, 'saveadd');">
	</c:if>
	<input type="button" value="<bean:message key="button.close"/>" onclick="Com_CloseWindow();">
</div>

<p class="txttitle"><bean:message  bundle="km-imissive" key="table.kmImissiveSecretGrade"/></p>

<center>
<table class="tb_normal" width=95%>
		<html:hidden property="fdId"/>
	<tr>
		<td class="td_normal_title" width=15%>
			<bean:message  bundle="km-imissive" key="kmImissiveSecretGrade.fdName"/>
		</td><td width=35% colspan='3'>
			<html:text property="fdName" style="width:85%"/>
			<span class="txtstrong">*</span>
		</td>
	</tr>
	<tr>
		<td class="td_normal_title" width=15%>
			<bean:message  bundle="km-imissive" key="kmImissiveSecretGrade.fdOrder"/>
		</td><td width=35%>
			<html:text property="fdOrder" style="width:85%"/>
		</td>
		<td class="td_normal_title" width=15%>
			<bean:message  bundle="km-imissive" key="kmImissiveSecretGrade.fdIsAvailable"/>
		</td><td width=35%>
			<sunbor:enums property="fdIsAvailable" enumsType="common_yesno" elementType="radio" />
		</td>
	</tr>
</table>
</center>
<html:hidden property="method_GET"/>
</html:form>
<%@ include file="/resource/jsp/edit_down.jsp"%>