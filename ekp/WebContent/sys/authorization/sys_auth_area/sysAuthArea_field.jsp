<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/kmss.tld" prefix="kmss"%>
<%@ taglib uri="/WEB-INF/kmss-xform.tld" prefix="xform"%>
<%@page import="com.landray.kmss.sys.authorization.constant.ISysAuthConstant"%>	

<% if(ISysAuthConstant.IS_AREA_ENABLED) { %> 
<tr>	
    <td class="td_normal_title" width="15%">
        <bean:message key="sysAuthArea.authArea" bundle="sys-authorization" />
	</td>
	<td colspan="3">
		<input type="hidden" name="authAreaId" value="${param.id}"> 
		<xform:text style="width:98%" property="authAreaName" showStatus="view" />	
	</td>	
</tr>
<% } %>