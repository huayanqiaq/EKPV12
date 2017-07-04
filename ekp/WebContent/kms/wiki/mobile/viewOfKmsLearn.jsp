<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/sys/ui/jsp/common.jsp"%>
<%@ taglib uri="/WEB-INF/KmssConfig/sys/ui/template.tld" prefix="template"%>
<template:include ref="mobile.view" compatibleMode="true">
	<template:replace name="title">
		<c:out value="${ kmsWikiMainForm.docSubject }" />
	</template:replace>
	<template:replace name="head">
		<link rel="stylesheet" type="text/css"
			href="${ LUI_ContextPath}/kms/wiki/mobile/css/view.css" />
	</template:replace>
	<template:replace name="content">
		<div data-dojo-type="mui/view/DocScrollableView" id="scrollView">
			<div class="muiDocFrame">
	            <div class="muiDocSubject">
	            	<c:out value="${ kmsWikiMainForm.docSubject }" />
	            </div>
				<div class="muiDocInfo">
					<c:if test="${not empty publishTime }">
						<span>
							<c:out
								value="${publishTime }" />
						</span>
						&nbsp;&nbsp;&nbsp;&nbsp; 
					</c:if>
					<span>
						${lfn:message('sys-doc:sysDocBaseInfo.docAuthor') } ： <c:if
							test="${not empty kmsWikiMainForm.docAuthorId }">
							<c:out value="${ kmsWikiMainForm.docAuthorName }" />
						</c:if> <c:if test="${not empty kmsWikiMainForm.docAuthorId }">
							<c:out value="${ kmsWikiMainForm.outerAuthor }" />
						</c:if>
					</span> 
					&nbsp;&nbsp;&nbsp;&nbsp; 
					<c:if test="${kmsWikiMainForm.docStatus >= '30' }">
						<span>
							阅读量：
							<c:out
								value="${ kmsWikiMainForm.docReadCount }" />
						</span>
					</c:if>
				</div>
				<c:if
					test="${kmsWikiMainForm.fdDescription!=null && kmsWikiMainForm.fdDescription!='' }">
					<div class="muiDocSummary">
						<div class="muiDocSummarySign">
							摘要
						</div>
						<c:out value="${ kmsWikiMainForm.fdDescription }" />
					</div>
				</c:if>
			</div>
				
            <span class="muiDocContent">
				<div data-dojo-type="mui/panel/AccordionPanel" class="muiAccordionPanelWiki">
					<c:forEach items="${kmsWikiMainForm.fdCatelogList}" var="kmsWikiCatelogForm" varStatus="status">
						<div data-dojo-type="mui/panel/Content" data-dojo-props="title:'${kmsWikiCatelogForm.fdName}',icon:'mui-ul'">
							<div class="muiWikiCatalogContent" 
								 id="catalog_content_${kmsWikiCatelogForm.fdId}">
								${kmsWikiCatelogForm.docContent}
							</div>
						</div>
					</c:forEach>
					
					<div class="muiDocFrame">
				       	 <!-- 附件 -->
						<c:import url="/sys/attachment/mobile/import/view.jsp" charEncoding="UTF-8">
							<c:param name="formName" value="kmsWikiMainForm" />
							<c:param name="fdKey" value="attachment" />
						</c:import> 
					</div>
					<!--<c:if test="${kmsWikiMainForm.docStatus < '30' }">
						<div data-dojo-type="mui/panel/Content"
							data-dojo-props="title:'流程日志',icon:'mui-ul'">
							<c:import
								url="/sys/lbpmservice/mobile/lbpm_audit_note/import/view.jsp"
								charEncoding="UTF-8">
								<c:param name="fdModelId"
									value="${kmsWikiMainForm.fdId }"></c:param>
								<c:param name="fdModelName"
									value="com.landray.kmss.kms.wiki.model.KmsWikiMain"></c:param>
								<c:param name="formBeanName" value="kmsWikiMainForm"></c:param>
							</c:import>
						</div>
					</c:if>-->
					<div style="width: 100%;margin: 1rem 0rem;text-align:center">
				<c:choose>
				     <c:when test="${fdStatus=='1'}"> 
						<a class="btnCourse" href="javascript:void(0)" onclick="">已学习</a>
				     </c:when>
				     <c:when test="${fdStatus=='0'}">
						  <a class="btnCourse" id="learningButton" href="javascript:void(0)" onclick="changeStatus('${fdCatelogContentId}');">置为已学习</a>
						  <a class="btnCourse" id="learntButton"  href="javascript:void(0)" style="display: none;">已学习</a>
				     </c:when>
				   
				     <c:otherwise></c:otherwise>
			    </c:choose>
			</div>
				</div>
				<style>
				 .btnCourse{ display:inline-block; height:2rem; line-height:2rem; padding:0rem 0.6rem;  border:0.1rem solid #6fc13a; color:#6fc13a; font-size:1.4rem; border-radius:0.25rem;margin: 1rem auto;}
				</style>
				<script>
				 	require([ 'mui/rtf/RtfResize', 'dojo/query',
							'dojo/_base/array' ,'dojo/ready'], function(RtfResize,
							query, array, ready) {
							ready(function(){
								var arr = query('.muiWikiCatalogContent');
								array.forEach(arr, function(item) {
									new RtfResize({containerNode:item});
								});	
							});
					}); 

				 	 function changeStatus(fdCatelogContentId){
						 
							var url="<c:out value='${LUI_ContextPath}/kms/learn/kms_learn_main/kmsLearnMain.do?method=buildClassEnd'/>";
								url=url+"&fdCatelogContentId="+fdCatelogContentId;
					    	require(["dojo/request"],function(request){
					    		request.post(url).then(function(result){
					    			document.getElementById("learningButton").style.display="none";
										document.getElementById("learntButton").style.display="";
						    		});

						    	});
						    }
				 	   function gobackHome(){
				    	   require(["dojo/request"], function(request){
				  			 var fdCatelogContentId="${fdCatelogContentId}";
				  			 var url="<c:out value='${LUI_ContextPath}/kms/learn/kms_learn_main/kmsLearnMain.do?method=buildClassMainId'/>";
				  				url=url+"&fdCatelogContentId="+fdCatelogContentId;
				  				request.post(url).then(function(data){
				  					var curUrl = "<c:url value='/kms/learn/kms_learn_main/kmsLearnMain.do'/>?method=view&fdId="+data.replace(/\s+/g,"");
				  					window.location.href=curUrl;
				  		    		});
				  				
				  			 });
				           }
				</script>
            </span>
            
	        <c:if test="${kmsWikiMainForm.docStatus >= '30' }">
		        <ul data-dojo-type="mui/tabbar/TabBar" fixed="bottom">
					<!-- 返回 -->
					<li data-dojo-type="mui/tabbar/TabBarButton" onclick="gobackHome()" id="goBackHome" class="mblTabBarButton mblTabBarButtonHasIcon unselectable"  style="width: 50%; margin: 0px;">
					<div class="mblTabBarButtonIconArea" style="float: left;">
					<div class="mblTabBarButtonIconParent mblTabBarButtonIconParent1"><i title="" class="mui mui-back"></i></div>
					<div class="mblTabBarButtonIconParent mblTabBarButtonIconParent2"><i title="" class="mui mui-back"></i></div></div>
					<div class="mblTabBarButtonLabel"></div></li>
					<!-- 点评 
					<c:import url="/sys/evaluation/mobile/import/view.jsp" charEncoding="UTF-8">
						<c:param name="formName" value="kmsWikiMainForm"></c:param>
					</c:import>-->
					<!-- 推荐 
					<c:import url="/sys/introduce/mobile/import/view.jsp" charEncoding="UTF-8">
						<c:param name="formName" value="kmsWikiMainForm"></c:param>
					</c:import>-->
				    <!-- 收藏
					<c:import url="/sys/bookmark/mobile/import/view.jsp" charEncoding="UTF-8">
						<c:param name="fdSubject" value="${kmsWikiMainForm.docSubject}" />
						<c:param name="fdModelId" value="${kmsWikiMainForm.fdId}" />
						<c:param name="fdModelName" value="com.landray.kmss.kms.wiki.model.KmsWikiMain" />
					</c:import> -->
					<!-- 更多 -->
					<li data-dojo-type="mui/tabbar/TabBarButtonGroup" data-dojo-props="icon1:'mui mui-more'">
				    	<div data-dojo-type="mui/back/HomeButton"></div>
				    	<c:import url="/sys/relation/mobile/import/view.jsp" charEncoding="UTF-8">
				    		<c:param name="formName" value="kmsWikiMainForm"></c:param>
				    	</c:import>
				    </li>
				</ul>
	        </c:if>
	        
	       <!-- <c:if test="${kmsWikiMainForm.docStatus < '30' }">
				<template:include file="/sys/lbpmservice/mobile/import/bar.jsp" 
					docStatus="${kmsWikiMainForm.docStatus}" 
					editUrl="javascript:building();"
					formName="kmsWikiMainForm">
					<template:replace name="group">
						<template:super/>
						<c:import url="/sys/relation/mobile/import/view.jsp" charEncoding="UTF-8">
				    		<c:param name="formName" value="kmsWikiMainForm"/>
				    	</c:import>
					</template:replace>
				</template:include>
			</c:if> --> 
		</div>
		
		<c:import url="/sys/lbpmservice/mobile/import/view.jsp" charEncoding="UTF-8">
			<c:param name="formName" value="kmsWikiMainForm" />
			<c:param name="fdKey" value="mainDoc" />
			<c:param name="viewName" value="lbpmView" />
			<c:param name="backTo" value="scrollView" />
		</c:import>
		
	</template:replace>
</template:include>
