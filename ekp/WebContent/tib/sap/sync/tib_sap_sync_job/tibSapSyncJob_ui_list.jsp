<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="
	java.util.Date,
	com.landray.kmss.util.DateUtil,
	com.landray.kmss.util.ResourceUtil" %>
<%@ page import="com.landray.kmss.tib.sap.sync.model.TibSapSyncJob"%>
<%@ page import="com.landray.kmss.sys.quartz.scheduler.CronExpression"%>
<%@ include file="/sys/ui/jsp/common.jsp"%>

<list:data>
	<list:data-columns var="tibSapSyncJob" list="${queryPage.list }">
		<%--ID--%>
		<list:data-column property="fdId"></list:data-column>
		<%--标题--%>
		<list:data-column property="fdSubject" title="${ lfn:message('sys-quartz:sysQuartzJob.fdSubject') }" style="text-align:center" escape="false">
			<span class="com_subject">
			</span>
		</list:data-column>
		<list:data-column col="fdCronExpression" title="${ lfn:message('sys-quartz:sysQuartzJob.fdCronExpression') }" escape="false" style="text-align:center;">
			<c:import url="/tib/sap/sync/tib_sap_sync_job/tibSapSyncJob_showCronExpression.jsp" charEncoding="UTF-8">
				<c:param name="value" value="${tibSapSyncJob.fdCronExpression}" />
			</c:import>
		</list:data-column>
		<list:data-column col="fdLink" title="${ lfn:message('sys-quartz:sysQuartzJob.fdLink') }" escape="false" style="text-align:center;">
			<c:if test="${tibSapSyncJob.fdLink!=null && tibSapSyncJob.fdLink!=''}">
				<a href="<c:url value="${tibSapSyncJob.fdLink}" />" target="_blank">
					<bean:write name="tibSapSyncJob" property="fdUseExplain"/> 
				</a>
			</c:if>
		</list:data-column>
		<list:data-column col="fdRunType" title="${ lfn:message('sys-quartz:sysQuartzJob.fdRunType') }" escape="false" style="text-align:center;">
			<sunbor:enumsShow value="${tibSapSyncJob.fdRunType}" enumsType="sysQuartzJob_fdRunType" />
		</list:data-column>
		<list:data-column col="fdEnabled" title="${ lfn:message('sys-quartz:sysQuartzJob.fdEnabled') }">
			<sunbor:enumsShow value="${tibSapSyncJob.fdEnabled}" enumsType="common_yesno" />
		</list:data-column>
	</list:data-columns>

	<list:data-paging currentPage="${queryPage.pageno }"
		pageSize="${queryPage.rowsize }" totalSize="${queryPage.totalrows }">
	</list:data-paging>
</list:data>
