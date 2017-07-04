package com.landray.kmss.kms.multidoc.actions;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Query;
import org.hibernate.Session;

import com.landray.kmss.common.actions.DataAction;
import com.landray.kmss.common.dao.HQLInfo;
import com.landray.kmss.common.dao.HQLParameter;
import com.landray.kmss.common.dao.HQLWrapper;
import com.landray.kmss.common.dao.IBaseDao;
import com.landray.kmss.common.model.IBaseTreeModel;
import com.landray.kmss.common.service.IBaseService;
import com.landray.kmss.common.test.TimeCounter;
import com.landray.kmss.constant.BaseTreeConstant;
import com.landray.kmss.constant.SysAuthConstant;
import com.landray.kmss.constant.SysDocConstant;
import com.landray.kmss.kms.common.constant.KmsDocConstant;
import com.landray.kmss.kms.knowledge.model.KmsKnowledgeBaseDoc;
import com.landray.kmss.kms.knowledge.model.KmsKnowledgeCategory;
import com.landray.kmss.kms.knowledge.service.IKmsKnowledgeCategoryService;
import com.landray.kmss.kms.knowledge.util.KmsKnowledgeUtil;
import com.landray.kmss.kms.multidoc.model.KmsMultidocKnowledge;
import com.landray.kmss.kms.multidoc.service.IKmsMultidocKnowledgeService;
import com.landray.kmss.kms.multidoc.service.IKmsMultidocTemplateService;
import com.landray.kmss.sys.attachment.service.ISysAttMainCoreInnerService;
import com.landray.kmss.sys.authentication.filter.HQLFragment;
import com.landray.kmss.sys.config.model.SysConfigParameters;
import com.landray.kmss.sys.property.model.SysPropertyTemplate;
import com.landray.kmss.sys.property.util.SysPropertyCriteriaUtil;
import com.landray.kmss.sys.simplecategory.service.ISysSimpleCategoryService;
import com.landray.kmss.sys.workflow.interfaces.SysFlowUtil;
import com.landray.kmss.util.ArrayUtil;
import com.landray.kmss.util.CriteriaUtil;
import com.landray.kmss.util.CriteriaValue;
import com.landray.kmss.util.HQLUtil;
import com.landray.kmss.util.KmssMessages;
import com.landray.kmss.util.KmssReturnPage;
import com.landray.kmss.util.ModelUtil;
import com.landray.kmss.util.ResourceUtil;
import com.landray.kmss.util.SpringBeanUtil;
import com.landray.kmss.util.StringUtil;
import com.landray.kmss.util.UserUtil;
import com.sunbor.web.tag.Page;

import edu.emory.mathcs.backport.java.util.Arrays;

public class KmsMultidocKnowledgeIndexAction extends DataAction {

	protected void changeFindPageHQLInfo(HttpServletRequest request,
			HQLInfo hqlInfo) throws Exception {
		super.changeFindPageHQLInfo(request, hqlInfo);
		String dataType = request.getParameter("dataType");
		if ("pic".equals(dataType)) {
			request.setAttribute("loadImg", true);
		}
		hqlInfo
				.setModelName("com.landray.kmss.kms.multidoc.model.KmsMultidocKnowledge");
		hqlInfo.setWhereBlock(" kmsMultidocKnowledge.docIsNewVersion=1 ");
		CriteriaValue cv = new CriteriaValue(request);
		CriteriaUtil.buildHql(cv, hqlInfo, KmsMultidocKnowledge.class);
		if(!cv.containsKey("docStatus")) {
			ArrayList<String> statusList = new ArrayList<String>();
			statusList.add(KmsDocConstant.DOC_STATUS_PUBLISH);
			statusList.add(KmsDocConstant.DOC_STATUS_DISCARD);
			statusList.add(KmsDocConstant.DOC_STATUS_EXAMINE);
			statusList.add(KmsDocConstant.DOC_STATUS_EXPIRE);
			statusList.add(KmsDocConstant.DOC_STATUS_REFUSE);
			String recycleWhereBlcok = " kmsMultidocKnowledge.docStatus in(:statusAray)";
			hqlInfo.setWhereBlock(StringUtil.linkString(hqlInfo.getWhereBlock(), " and ", recycleWhereBlcok));
			hqlInfo.setParameter("statusAray", statusList);
		}
		String categoryId = request.getParameter("categoryId");

		String orderBy = hqlInfo.getOrderBy();
		if (StringUtil.isNotNull(orderBy)) {
			orderBy = "," + orderBy;
		} else {
			orderBy = "";
		}
		String driverClass = ResourceUtil
				.getKmssConfigString("hibernate.connection.driverClass");
		if (StringUtil.isNotNull(categoryId)) {
			KmsKnowledgeCategory template = (KmsKnowledgeCategory) getkmsKnowledgeCategoryServiceImp()
					.findByPrimaryKey(categoryId);

			// 找出当前类别属于第几级分类
			int level = 1;
			if (StringUtil.isNotNull(categoryId)) {
				level = getServiceImp(request).getLevelCount(template);
			}
			
			//oracle数据库字段数据为null的问题
			if (driverClass.equals("oracle.jdbc.driver.OracleDriver")) {
				if (level == 1) {// 当为一级分类时
					hqlInfo
							.setOrderBy("substr(nvl(fdSetTopLevel,0),length(nvl(fdSetTopLevel,0)),1) desc"
									+ orderBy);
				} else {
					hqlInfo.setOrderBy("substr(nvl(fdSetTopLevel,0),1," + level
							+ ") desc" + orderBy);
				}
			}else{
				if (level == 1) {// 当为一级分类时
					hqlInfo
							.setOrderBy("subString(fdSetTopLevel,length(fdSetTopLevel),1) desc"
									+ orderBy);
				} else {
					hqlInfo.setOrderBy("subString(fdSetTopLevel,1," + level
							+ ") desc" + orderBy);
				}
			}
		} else {
			//oracle数据库字段数据为null的问题
			if (driverClass.equals("oracle.jdbc.driver.OracleDriver")) {
				hqlInfo.setOrderBy(" nvl(docIsIndexTop,0) desc" + orderBy);
			}else{
				hqlInfo.setOrderBy("docIsIndexTop desc" + orderBy);
			}
		}

		if (StringUtil.isNotNull(categoryId)) {
			KmsKnowledgeCategory template = (KmsKnowledgeCategory) getkmsKnowledgeCategoryServiceImp()
					.findByPrimaryKey(categoryId);

			List<?> temps = getkmsKnowledgeCategoryServiceImp()
					.getAllChildCategory(template);
			List<String> idLists = new ArrayList<String>();
			for (int i = 0; i < temps.size(); i++) {
				KmsKnowledgeCategory cate = (KmsKnowledgeCategory) temps.get(i);
				if (cate.getSysPropertyTemplate() != null) {
					String fdId = cate.getSysPropertyTemplate().getFdId();
					if (!idLists.contains(fdId)) {
						idLists.add(fdId);
					}
				}
			}
			SysPropertyTemplate temp = template.getSysPropertyTemplate();
			if (temp == null)
				return;
			SysPropertyCriteriaUtil.buildHql(cv, hqlInfo, temp, idLists);
		}

	}

	/**
	 * 拼接分类HQL，支持辅分类
	 */
	protected String buildCategoryHQL(HQLInfo hqlInfo,
			IBaseTreeModel treeModel, String tableName) {
		String whereBlock;
		if (StringUtil.isNull(treeModel.getFdHierarchyId())) {
			whereBlock = tableName + "." + getParentProperty()
					+ ".fdId=:_treeFdId";
			hqlInfo.setParameter("_treeFdId", treeModel.getFdId());
		} else {
			whereBlock = tableName + "." + getParentProperty()
					+ ".fdHierarchyId like :_treeHierarchyId or " + tableName
					+ " in (select elements(knowledgeCategory.knowledges) "
					+ "from KmsKnowledgeCategory knowledgeCategory where "
					+ "knowledgeCategory.fdHierarchyId like :_treeHierarchyId)";

			hqlInfo.setParameter("_treeHierarchyId", treeModel
					.getFdHierarchyId()
					+ "%");
		}
		return "(" + whereBlock + ")";
	}

	public ActionForward getSysAttList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		KmssMessages messages = new KmssMessages();
		try {
			String s_pageno = request.getParameter("pageno");
			String s_rowsize = request.getParameter("rowsize");
			String orderby = request.getParameter("orderby");
			String ordertype = request.getParameter("ordertype");
			String parentId = request.getParameter("categoryId");
			String s_IsShowAll = request.getParameter("isShowAll");
			String excepteIds = request.getParameter("excepteIds");
			boolean isShowAll = true;
			if (StringUtil.isNotNull(s_IsShowAll)
					&& s_IsShowAll.equals("false"))
				isShowAll = false;
			boolean isReserve = false;
			if (ordertype != null && ordertype.equalsIgnoreCase("down")) {
				isReserve = true;
			}
			int pageno = 0;
			int rowsize = SysConfigParameters.getRowSize();
			if (s_pageno != null && s_pageno.length() > 0) {
				pageno = Integer.parseInt(s_pageno);
			}
			if (s_rowsize != null && s_rowsize.length() > 0) {
				rowsize = Integer.parseInt(s_rowsize);
			}
			if (isReserve)
				orderby += " desc";
			HQLInfo hqlAtt = new HQLInfo();
			// hqlAtt.setOrderBy(orderby);
			hqlAtt.setPageNo(pageno);
			hqlAtt.setRowSize(rowsize);
			changeFindPageHQLInfo(request, hqlAtt);
			String whereBlock = hqlAtt.getWhereBlock();
			if (!StringUtil.isNull(parentId)) {
				if (StringUtil.isNull(whereBlock))
					whereBlock = "";
				else
					whereBlock = "(" + whereBlock + ") and ";
				String tableName = ModelUtil.getModelTableName(getServiceImp(
						request).getModelName());
				if (isShowAll) {
					IBaseTreeModel treeModel = (IBaseTreeModel) getSysSimpleCategoryService()
							.findByPrimaryKey(parentId);

					whereBlock += buildCategoryHQL(hqlAtt, treeModel, tableName);
				} else {
					whereBlock += tableName + "." + getParentProperty()
							+ ".fdId=:_treeParentId";
					hqlAtt.setParameter("_treeParentId", parentId);
				}
				if (StringUtil.isNotNull(excepteIds)) {
					whereBlock += " and "
							+ HQLUtil.buildLogicIN(tableName + ".fdId not",
									ArrayUtil.convertArrayToList(excepteIds
											.split("\\s*[;,]\\s*")));
				}
			}

			// 文件格式筛选处理
			Iterator<Entry<String, String[]>> iterator = new CriteriaValue(
					request).entrySet().iterator();
			List<String> fileTypeList = new ArrayList<String>();
			String allFileType = "";
			while (iterator.hasNext()) {
				Entry<String, String[]> a = iterator.next();
				String key = a.getKey();
				String[] values = a.getValue();

				if ("fileType".equals(key)) {
					List valueList = Arrays.asList(values);
					List fileType = Arrays
							.asList(new Object[] { "doc", "ppt", "pdf",
									"excel", "pic", "sound", "video", "others" });
					if (ArrayUtil.isListIntersect(Arrays.asList(values),
							fileType)) {

						allFileType = KmsKnowledgeUtil.getFileTypeHql(
								valueList, fileTypeList, allFileType);
					}
				}
			}

			String __joinBlock = "com.landray.kmss.kms.multidoc.model.KmsMultidocKnowledge kmsMultidocKnowledge";
			__joinBlock += StringUtil.isNotNull(hqlAtt.getJoinBlock()) ? hqlAtt
					.getJoinBlock() : "";
			// 经过筛选器筛选后的文档hql（已权限处理）
			HQLWrapper _docHqlWrapper = getServiceImp(request).getDocHql(
					whereBlock, __joinBlock, request);
			String _docHql = _docHqlWrapper.getHql();
			List<HQLParameter> _docHqlPara = _docHqlWrapper.getParameterList();
			
			if(StringUtil.isNotNull(orderby)){
				if(orderby.trim().startsWith("kmsMultidocKnowledge."))
					hqlAtt.setOrderBy(orderby);
				else hqlAtt.setOrderBy(" kmsMultidocKnowledge."+orderby);
			}else{
				hqlAtt.setOrderBy("");
			}
			hqlAtt
					.setFromBlock("com.landray.kmss.sys.attachment.model.SysAttMain sysAttMain");
			hqlAtt
					.setSelectBlock("sysAttMain.fdId,sysAttMain.fdCreatorId,sysAttMain.fdSize,sysAttMain.docCreateTime,sysAttMain.fdFileName,kmsMultidocKnowledge.fdId,kmsMultidocKnowledge.docSubject");
			hqlAtt
					.setModelName("com.landray.kmss.sys.attachment.model.SysAttMain");
			hqlAtt
					.setJoinBlock(",com.landray.kmss.kms.multidoc.model.KmsMultidocKnowledge kmsMultidocKnowledge");
			String where;
			if (StringUtil.isNotNull(allFileType)) {
				where = " and sysAttMain.fdContentType " + allFileType
						+ " and sysAttMain.fdModelId in (" + _docHql + ")";
			} else {
				where = " and sysAttMain.fdModelId in (" + _docHql + ")";
			}
			hqlAtt
					.setWhereBlock("sysAttMain.fdKey!='spic' and sysAttMain.fdModelId=kmsMultidocKnowledge.fdId"
							+ where);

			Page page = new Page();
			Boolean hqlGetCount = false;
			Query query = null;
			HQLWrapper hqlWrap = null;
			int total = hqlAtt.getRowSize();
			if (hqlAtt.isGetCount()) {
				TimeCounter.logCurrentTime("Dao-findPage-count", true,
						getClass());
				hqlGetCount = true;
				hqlWrap = getHQL(hqlAtt, hqlGetCount, allFileType, hqlAtt
						.getWhereBlock());
				query = getServiceImp(request).getBaseDao()
						.getHibernateSession().createQuery(hqlWrap.getHql());
				HQLUtil.setParameters(query, _docHqlPara);
				HQLUtil.setParameters(query, hqlWrap.getParameterList());

				total = ((Long) query.iterate().next()).intValue();
				TimeCounter.logCurrentTime("Dao-findPage-count", false,
						getClass());
			}
			TimeCounter.logCurrentTime("Dao-findPage-list", true, getClass());
			if (total > 0) {
				hqlGetCount = false;
				// Oracle的排序列若出现重复值，那排序的结果可能不准确，为了避免该现象，若出现了排序列，则强制在最后加上按fdId排序
				String order = hqlAtt.getOrderBy();
				if (StringUtil.isNotNull(order)) {
					Pattern p = Pattern.compile(",\\s*"
							+ "kmsMultidocKnowledge"
							+ "\\.fdId\\s*|,\\s*fdId\\s*");
					if (!p.matcher("," + order).find()) {
						hqlAtt.setOrderBy(order + "," + "kmsMultidocKnowledge"
								+ ".fdId desc");
					}
				}
				page = new Page();
				page.setRowsize(hqlAtt.getRowSize());
				page.setPageno(hqlAtt.getPageNo());
				page.setTotalrows(total);
				page.excecute();
				hqlWrap = getHQL(hqlAtt, hqlGetCount, allFileType, hqlAtt
						.getWhereBlock());

				Query q = getServiceImp(request).getBaseDao()
						.getHibernateSession().createQuery(hqlWrap.getHql());
				HQLUtil.setParameters(q, _docHqlPara);
				HQLUtil.setParameters(q, hqlWrap.getParameterList());
				q.setFirstResult(page.getStart());
				q.setMaxResults(page.getRowsize());
				page.setList(q.list());

			}

			request.setAttribute("attPage", page);
		} catch (Exception e) {
			messages.addError(e);
		}
		if (messages.hasError()) {
			KmssReturnPage.getInstance(request).addMessages(messages)
					.addButton(KmssReturnPage.BUTTON_CLOSE).save(request);
			return mapping.findForward("failure");
		} else {
			return getActionForward("attList", mapping, form, request, response);
		}
	}

	private static final AtomicLong atomicLong = new AtomicLong(0);

	private static long getNextIndex() {
		atomicLong.compareAndSet(10000, 0); // 如果累加到了10000，重置为0
		return atomicLong.getAndIncrement();
	}

	private static String replaceTempName(String srcName, String fromName,
			String toName) {
		return srcName.replaceAll("(^|\\W)" + fromName + "(\\.|\\W)", "$1"
				+ toName + "$2");
	}

	public HQLWrapper getHQL(HQLInfo hqlInfo, Boolean hqlGetCount,
			String allFileType, String extendWhereBlock) {
		StringBuffer hql = new StringBuffer();
		if (hqlGetCount) {
			hql.append("select count(distinct " + hqlInfo.getModelTable()
					+ ".fdId) ");
			hql.append("from " + hqlInfo.getModelName() + " "
					+ hqlInfo.getModelTable() + " ");
		} else {

			hql
					.append("select "
							+ "sysAttMain.fdId,sysAttMain.fdCreatorId,sysAttMain.fdSize,sysAttMain.docCreateTime,"
							+ "sysAttMain.fdFileName,kmsMultidocKnowledge.fdId,kmsMultidocKnowledge.docSubject"
							+ " ");
			String andSet = " ";
			if (StringUtil.isNotNull(allFileType)) {
				andSet = " and sysAttMain.fdContentType " + allFileType + " ";
			}

			hql
					.append("from com.landray.kmss.sys.attachment.model.SysAttMain sysAttMain,"
							+ "com.landray.kmss.kms.multidoc.model.KmsMultidocKnowledge kmsMultidocKnowledge "
							+ "where sysAttMain.fdModelId=kmsMultidocKnowledge.fdId"
							+ andSet);
			// + " ");
			hql.append(replaceTempName(HQLUtil.getAutoFetchInfo(hqlInfo),
					hqlInfo.getModelTable(), "sysAttMain"));
			hql.append("and " + "sysAttMain" + ".fdId in (");
			hql.append("select " + hqlInfo.getModelTable() + ".fdId ");
			if (StringUtil.isNull(hqlInfo.getFromBlock()))
				hql.append("from " + hqlInfo.getModelName() + " "
						+ hqlInfo.getModelTable() + " ");
			else
				hql.append("from " + hqlInfo.getFromBlock() + " ");
		}
		if (!StringUtil.isNull(hqlInfo.getJoinBlock()))
			hql.append(hqlInfo.getJoinBlock() + " ");

		if (!StringUtil.isNull(extendWhereBlock))
			hql.append("where " + extendWhereBlock);
		if (!hqlGetCount) {
			hql.append(")");
			if (!StringUtil.isNull(hqlInfo.getOrderBy()))
				hql.append(" order by "
						+ replaceTempName(hqlInfo.getOrderBy(), hqlInfo
								.getModelTable(), "sysAttMain"));
		}
		return new HQLWrapper(hql.toString(), hqlInfo.getParameterList());
	}

	protected ISysAttMainCoreInnerService sysAttMainService;

	public ISysAttMainCoreInnerService getSysAttMainService() {
		if (sysAttMainService == null) {
			sysAttMainService = (ISysAttMainCoreInnerService) SpringBeanUtil
					.getBean("sysAttMainService");
		}
		return sysAttMainService;
	}

	private IKmsKnowledgeCategoryService kmsKnowledgeCategoryService;

	protected IKmsKnowledgeCategoryService getkmsKnowledgeCategoryServiceImp() {
		if (kmsKnowledgeCategoryService == null)
			kmsKnowledgeCategoryService = (IKmsKnowledgeCategoryService) getBean("kmsKnowledgeCategoryService");
		return kmsKnowledgeCategoryService;
	}

	@Override
	protected String getParentProperty() {
		return "docCategory";
	}

	public ISysSimpleCategoryService getSysSimpleCategoryService() {
		return (ISysSimpleCategoryService) getkmsKnowledgeCategoryServiceImp();
	}

	private IKmsMultidocKnowledgeService kmsMultidocKnowledgeService;

	protected IKmsMultidocKnowledgeService getServiceImp(
			HttpServletRequest request) {
		if (kmsMultidocKnowledgeService == null)
			kmsMultidocKnowledgeService = (IKmsMultidocKnowledgeService) getBean("kmsMultidocKnowledgeService");
		return kmsMultidocKnowledgeService;
	}

	public ActionForward mydoc(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		KmssMessages messages = new KmssMessages();
		try {
			String s_pageno = request.getParameter("pageno");
			String s_rowsize = request.getParameter("rowsize");
			String orderby = request.getParameter("orderby");
			String ordertype = request.getParameter("ordertype");
			String docCreatorId = request.getParameter("fdOrgId");
			boolean isReserve = false;
			if (ordertype != null && ordertype.equalsIgnoreCase("down")) {
				isReserve = true;
			}
			int pageno = 0;
			int rowsize = SysConfigParameters.getRowSize();
			if (s_pageno != null && s_pageno.length() > 0) {
				pageno = Integer.parseInt(s_pageno);
			}
			if (s_rowsize != null && s_rowsize.length() > 0) {
				rowsize = Integer.parseInt(s_rowsize);
			}
			if (isReserve)
				orderby += " desc";
			HQLInfo hqlInfo = new HQLInfo();
			hqlInfo.setOrderBy(orderby);
			hqlInfo.setPageNo(pageno);
			hqlInfo.setRowSize(rowsize);
			String whereBlock = "";

			whereBlock += " (kmsMultidocKnowledge.docCreator.fdId = :fdPersonId and kmsMultidocKnowledge.docIsNewVersion = :isNew) ";
			hqlInfo.setParameter("fdPersonId", docCreatorId);
			hqlInfo.setParameter("isNew", true);
			hqlInfo.setWhereBlock(whereBlock);

			String[] restatus = request.getParameterValues("q.status");

			if (restatus != null) {
				if (!"all".equals(restatus[0])) {
					whereBlock += " and (kmsMultidocKnowledge.docStatus = :docStatus)";
					hqlInfo.setParameter("docStatus", restatus[0]);
				}
			}

			hqlInfo.setWhereBlock(whereBlock);
			Page page = getServiceImp(request).findPage(hqlInfo);
			request.setAttribute("queryPage", page);
		} catch (Exception e) {
			messages.addError(e);
		}
		if (messages.hasError()) {
			KmssReturnPage.getInstance(request).addMessages(messages)
					.addButton(KmssReturnPage.BUTTON_CLOSE).save(request);
			return mapping.findForward("failure");
		} else {
			return getActionForward("listChildren", mapping, form, request,
					response);
		}
	}

	public ActionForward myReview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		KmssMessages messages = new KmssMessages();
		try {
			String s_pageno = request.getParameter("pageno");
			String s_rowsize = request.getParameter("rowsize");
			String orderby = request.getParameter("orderby");
			String ordertype = request.getParameter("ordertype");
			String docCreatorId = request.getParameter("fdOrgId");
			boolean isReserve = false;
			if (ordertype != null && ordertype.equalsIgnoreCase("down")) {
				isReserve = true;
			}
			int pageno = 0;
			int rowsize = SysConfigParameters.getRowSize();
			if (s_pageno != null && s_pageno.length() > 0) {
				pageno = Integer.parseInt(s_pageno);
			}
			if (s_rowsize != null && s_rowsize.length() > 0) {
				rowsize = Integer.parseInt(s_rowsize);
			}
			if (isReserve)
				orderby += " desc";

			HQLInfo hqlInfo = new HQLInfo();
			hqlInfo.setOrderBy(orderby);
			hqlInfo.setPageNo(pageno);
			hqlInfo.setRowSize(rowsize);

			changeFindPageHQLInfo(request, hqlInfo);
			String shortName = ModelUtil
					.getModelTableName(KmsMultidocKnowledge.class.getName());

			String[] flowStatus = request.getParameterValues("q.review");
			// 已审
			if (flowStatus == null || "1".equals(flowStatus[0])) {
				// 通过个人Id获得该人的已审文档
				buildLimitBlockForPersonApproved(shortName, hqlInfo,
						docCreatorId);
				hqlInfo.setCheckParam(SysAuthConstant.CheckType.AllCheck,
						SysAuthConstant.AllCheck.NO);
			}
			// 未审
			if (flowStatus != null && "0".equals(flowStatus[0])) {
				buildLimitBlockForPersonApproval(shortName, hqlInfo,
						docCreatorId);
				hqlInfo.setCheckParam(SysAuthConstant.CheckType.AllCheck,
						SysAuthConstant.AllCheck.NO);
			}

			Page page = getServiceImp(request).findPage(hqlInfo);
			request.setAttribute("queryPage", page);
		} catch (Exception e) {
			messages.addError(e);
		}
		if (messages.hasError()) {
			KmssReturnPage.getInstance(request).addMessages(messages)
					.addButton(KmssReturnPage.BUTTON_CLOSE).save(request);
			return mapping.findForward("failure");
		} else {
			return getActionForward("listChildren", mapping, form, request,
					response);
		}
	}

	public ActionForward myEvaluate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		KmssMessages messages = new KmssMessages();
		try {
			String s_pageno = request.getParameter("pageno");
			String s_rowsize = request.getParameter("rowsize");
			String orderby = request.getParameter("orderby");
			String ordertype = request.getParameter("ordertype");
			String docCreatorId = request.getParameter("fdOrgId");
			boolean isReserve = false;
			if (ordertype != null && ordertype.equalsIgnoreCase("down")) {
				isReserve = true;
			}
			int pageno = 0;
			int rowsize = SysConfigParameters.getRowSize();
			if (s_pageno != null && s_pageno.length() > 0) {
				pageno = Integer.parseInt(s_pageno);
			}
			if (s_rowsize != null && s_rowsize.length() > 0) {
				rowsize = Integer.parseInt(s_rowsize);
			}
			if (isReserve)
				orderby += " desc";

			HQLInfo hqlInfo = new HQLInfo();
			hqlInfo.setOrderBy(orderby);
			hqlInfo.setPageNo(pageno);
			hqlInfo.setRowSize(rowsize);

			StringBuffer hqlBuffer = new StringBuffer();
			hqlBuffer.append("kmsMultidocKnowledge.fdId in ");
			// 拼接子查询
			hqlBuffer
					.append("(select distinct sysEvaluationMain.fdModelId from ");
			hqlBuffer
					.append(" com.landray.kmss.sys.evaluation.model.SysEvaluationMain sysEvaluationMain ");
			hqlBuffer
					.append("where sysEvaluationMain.fdModelName = :fdModelName ");
			hqlBuffer
					.append("and sysEvaluationMain.fdEvaluator.fdId = :fdEvaluatorId)");

			hqlInfo.setWhereBlock(hqlBuffer.toString());
			hqlInfo.setParameter("fdModelName",
					"com.landray.kmss.kms.multidoc.model.KmsMultidocKnowledge");
			hqlInfo.setParameter("fdEvaluatorId", docCreatorId);

			Page page = getServiceImp(request).findPage(hqlInfo);
			request.setAttribute("queryPage", page);
		} catch (Exception e) {
			messages.addError(e);
		}
		if (messages.hasError()) {
			KmssReturnPage.getInstance(request).addMessages(messages)
					.addButton(KmssReturnPage.BUTTON_CLOSE).save(request);
			return mapping.findForward("failure");
		} else {
			return getActionForward("listChildren", mapping, form, request,
					response);
		}
	}

	public ActionForward myIntroduce(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		KmssMessages messages = new KmssMessages();
		try {
			String s_pageno = request.getParameter("pageno");
			String s_rowsize = request.getParameter("rowsize");
			String orderby = request.getParameter("orderby");
			String ordertype = request.getParameter("ordertype");
			String docCreatorId = request.getParameter("fdOrgId");
			boolean isReserve = false;
			if (ordertype != null && ordertype.equalsIgnoreCase("down")) {
				isReserve = true;
			}
			int pageno = 0;
			int rowsize = SysConfigParameters.getRowSize();
			if (s_pageno != null && s_pageno.length() > 0) {
				pageno = Integer.parseInt(s_pageno);
			}
			if (s_rowsize != null && s_rowsize.length() > 0) {
				rowsize = Integer.parseInt(s_rowsize);
			}
			if (isReserve)
				orderby += " desc";

			HQLInfo hqlInfo = new HQLInfo();
			hqlInfo.setOrderBy(orderby);
			hqlInfo.setPageNo(pageno);
			hqlInfo.setRowSize(rowsize);

			StringBuffer hqlBuffer = new StringBuffer();
			hqlBuffer.append("kmsMultidocKnowledge.fdId in ");
			// 拼接子查询
			hqlBuffer
					.append("(select distinct sysIntroduceMain.fdModelId from ");
			hqlBuffer
					.append(" com.landray.kmss.sys.introduce.model.SysIntroduceMain sysIntroduceMain ");
			hqlBuffer
					.append("where sysIntroduceMain.fdModelName = :fdModelName ");
			hqlBuffer
					.append("and sysIntroduceMain.fdIntroducer.fdId = :fdIntroducerId)");

			hqlInfo.setWhereBlock(hqlBuffer.toString());
			hqlInfo.setParameter("fdModelName",
					"com.landray.kmss.kms.multidoc.model.KmsMultidocKnowledge");
			hqlInfo.setParameter("fdIntroducerId", docCreatorId);

			Page page = getServiceImp(request).findPage(hqlInfo);
			request.setAttribute("queryPage", page);
		} catch (Exception e) {
			messages.addError(e);
		}
		if (messages.hasError()) {
			KmssReturnPage.getInstance(request).addMessages(messages)
					.addButton(KmssReturnPage.BUTTON_CLOSE).save(request);
			return mapping.findForward("failure");
		} else {
			return getActionForward("listChildren", mapping, form, request,
					response);
		}
	}

	public static HQLInfo buildLimitBlockForPersonApproved(String tableName,
			HQLInfo hqlInfo, String docCreatorId) {
		// join片段
		StringBuffer buff = new StringBuffer();
		if (StringUtil.isNotNull(hqlInfo.getJoinBlock())) {
			buff.append(hqlInfo.getJoinBlock());
		}
		buff.append(",LbpmHistoryWorkitem workitem ");
		hqlInfo.setJoinBlock(buff.toString());
		// 去重
		hqlInfo.setDistinctType(HQLInfo.DISTINCT_YES);
		// where片段
		HQLFragment hqlFragment = buildWhereBlockForPersonApproved(tableName,
				hqlInfo.getWhereBlock(), docCreatorId);
		hqlInfo.setAuthCheckType(HQLInfo.AUTH_CHECK_NONE);
		hqlInfo.setDistinctType(HQLInfo.DISTINCT_YES);

		hqlInfo.setWhereBlock(hqlFragment.getWhereBlock());
		hqlInfo.setParameter(hqlFragment.getParameterList());
		return hqlInfo;
	}

	private static HQLFragment buildWhereBlockForPersonApproved(
			String tableName, String whereBlock, String docCreatorId) {
		HQLFragment hqlFragment = new HQLFragment();
		// where片段
		StringBuffer buff = new StringBuffer(tableName);
		buff
				.append(".fdId = workitem.fdProcess.fdId and workitem.fdHandler.fdId in (:handlerIds)");
		buff.append(" and workitem.fdActivityType != 'draftWorkitem'");
		if (StringUtil.isNotNull(whereBlock)) {
			buff.append(" and (").append(whereBlock).append(")");
		}
		hqlFragment.setWhereBlock(buff.toString());
		// 参数集
		hqlFragment.setParameter(new HQLParameter("handlerIds", docCreatorId));
		return hqlFragment;
	}

	public static HQLInfo buildLimitBlockForPersonApproval(String tableName,
			HQLInfo hqlInfo, String docCreatorId) {
		// join片段
		StringBuffer buff = new StringBuffer();
		if (StringUtil.isNotNull(hqlInfo.getJoinBlock())) {
			buff.append(hqlInfo.getJoinBlock());
		}
		buff.append(",LbpmExpecterLog log ");
		hqlInfo.setJoinBlock(buff.toString());
		// where片段
		HQLFragment hqlFragment = buildWhereBlockForPersonApproval(tableName,
				hqlInfo.getWhereBlock(), docCreatorId);
		hqlInfo.setAuthCheckType(HQLInfo.AUTH_CHECK_NONE);
		hqlInfo.setDistinctType(HQLInfo.DISTINCT_YES);

		hqlInfo.setWhereBlock(hqlFragment.getWhereBlock());
		hqlInfo.setParameter(hqlFragment.getParameterList());
		return hqlInfo;
	}

	private static HQLFragment buildWhereBlockForPersonApproval(
			String tableName, String whereBlock, String docCreatorId) {
		HQLFragment hqlFragment = new HQLFragment();
		// where片段
		StringBuffer buff = new StringBuffer(tableName);
		buff.append(".fdId = log.fdProcessId");
		buff.append(" and log.fdHandler.fdId in (:flowIds)");
		buff.append(" and log.fdTaskType != 'draftWorkitem'");
		buff.append(" and log.fdIsActive = 1");
		if (StringUtil.isNotNull(whereBlock)) {
			buff.append(" and (").append(whereBlock).append(")");
		}
		hqlFragment.setWhereBlock(buff.toString());
		// 参数集
		hqlFragment.setParameter(new HQLParameter("flowIds", docCreatorId));
		return hqlFragment;
	}

	// 置顶
	public static String setTop_index = "setTop_index",
			setTop_firstCate = "setTop_firstCate",
			setTop_currentCate = "setTop_currentCate";

	public ActionForward setTop(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		KmssMessages messages = new KmssMessages();
		response.setContentType("application/jsonp");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();

		String docIds = request.getParameter("docIds");
		String fdSetTopReason = request.getParameter("fdSetTopReason");
		String topLevel = request.getParameter("fdSetTopLevel");
		boolean docIsIndexTop = false;
		try {
			// 如进行首页置顶，要判断是否有权限
			String url = "/kms/multidoc/kms_multidoc_knowledge/kmsMultidocKnowledge.do?method=setTop&local=index";
			if (topLevel.equals(setTop_index)
					&& !UserUtil.checkAuthentication(url, "get")) {
				json.element("hasRight", false);
			} else {
				json.element("hasRight", true);

				String fdSetTopLevel = null;
				String fdTopCategoryId = "";// 文档可置顶的所有分类集合
				if (topLevel.equals(setTop_index)) {// 首页置顶
					docIsIndexTop = true;
				} else if (topLevel.equals(setTop_firstCate)) {// 一级目录置顶
					fdSetTopLevel = "1";
				}

				String[] ids = docIds.split(",");
				if (ids.length > 0) {
					for (int i = 0; i < ids.length; i++) {
						KmsMultidocKnowledge model = (KmsMultidocKnowledge) getServiceImp(
								request).findByPrimaryKey(ids[i]);
						KmsKnowledgeCategory category = model.getDocCategory();

						if (topLevel.equals(setTop_currentCate)) {// 当前目录置顶
							// 根据分类拼出所对应的fdSetTopLevel排序码
							int levelCount = getServiceImp(request)
									.getLevelCount(category);
							if (levelCount == 1) {
								fdSetTopLevel = getServiceImp(request)
										.getFdSetTopLevel(category, "2");
							} else {
								fdSetTopLevel = getServiceImp(request)
										.getFdSetTopLevel(category, "1");
							}
							fdTopCategoryId = getTopCateId(setTop_currentCate,
									category, model);
						} else if (topLevel.equals(setTop_firstCate)) {// 一级目录置顶
							fdSetTopLevel = getServiceImp(request)
									.getFdSetTopLevel(category, "2");

							fdTopCategoryId = getTopCateId(setTop_firstCate,
									category, model);
						}
						if (model != null) {
							model.setFdTopCategoryId(fdTopCategoryId);
							if (docIsIndexTop) {
								model.setDocIsIndexTop(docIsIndexTop);
							} else {
								model.setDocIsIndexTop(null);
							}
							model.setFdSetTopReason(fdSetTopReason);
							model.setFdSetTopTime(new Date());
							model.setFdSetTopLevel(fdSetTopLevel);
							updateSetTop(model);
						}
					}
				}
			}
			out.println(json.toString(1));
		} catch (Exception e) {
			messages.addError(e);
			e.printStackTrace();
		}
		if (messages.hasError()) {
			response.sendError(505);
			out.println(messages);
			return null;
		} else {
			return null;
		}
	}
	
	public void updateSetTop(KmsMultidocKnowledge kmsMultidocKnowledge) throws Exception{
		IBaseDao baseDao = (IBaseDao) SpringBeanUtil
							.getBean("KmssBaseDao");
		Session session = baseDao.getHibernateSession();
		Query query = session.createQuery(
									"update "
											+ KmsKnowledgeBaseDoc.class.getName()
											+ " kmsKnowledgeBaseDoc set kmsKnowledgeBaseDoc.fdTopCategoryId = :fdTopCategoryId,"
											+ " kmsKnowledgeBaseDoc.docIsIndexTop = :docIsIndexTop, kmsKnowledgeBaseDoc.fdSetTopReason = :fdSetTopReason,"
											+ " kmsKnowledgeBaseDoc.fdSetTopTime = :fdSetTopTime, kmsKnowledgeBaseDoc.fdSetTopLevel = :fdSetTopLevel "
											+ " where kmsKnowledgeBaseDoc.fdId = :fdId");
		query.setParameter("fdTopCategoryId", kmsMultidocKnowledge.getFdTopCategoryId());
		query.setParameter("docIsIndexTop", kmsMultidocKnowledge.getDocIsIndexTop());
		query.setParameter("fdSetTopReason", kmsMultidocKnowledge.getFdSetTopReason());
		query.setParameter("fdSetTopTime", kmsMultidocKnowledge.getFdSetTopTime());
		query.setParameter("fdSetTopLevel", kmsMultidocKnowledge.getFdSetTopLevel());
		query.setParameter("fdId", kmsMultidocKnowledge.getFdId());
		query.executeUpdate();
	}
	
	// 获取文档所应置顶显示的所有类别(包括辅分类)
	public String getTopCateId(String location, KmsKnowledgeCategory temp,
			KmsMultidocKnowledge model) throws Exception {
		StringBuffer categoryIds = new StringBuffer();
		if ("setTop_firstCate".equals(location)) {
			String hierarchyId = temp.getFdHierarchyId();
			categoryIds.append(hierarchyId);
		} else if ("setTop_currentCate".equals(location)) {
			categoryIds.append(BaseTreeConstant.HIERARCHY_ID_SPLIT
					+ temp.getFdId() + BaseTreeConstant.HIERARCHY_ID_SPLIT);
		}
		// 辅分类
		List<KmsKnowledgeCategory> docSecondCategories = model
				.getDocSecondCategories();
		if (docSecondCategories != null && !docSecondCategories.isEmpty()) {
			for (KmsKnowledgeCategory cates : docSecondCategories) {
				String cateId = cates.getFdId();
				categoryIds
						.append(cateId + BaseTreeConstant.HIERARCHY_ID_SPLIT);
			}
		}
		return categoryIds.toString();
	}

	// 取消置顶
	public ActionForward cancelTop(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		KmssMessages messages = new KmssMessages();
		response.setContentType("application/jsonp");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();

		String docIds = request.getParameter("docIds");
		if (StringUtil.isNotNull(docIds)) {
			// String templateId = request.getParameter("templateId");
			// 判断知识文档是否已首页置顶
			boolean isTopIndex = isTopIndex(docIds, request, response);
			try {
				// 判断是否有权限
				String url = "/kms/multidoc/kms_multidoc_knowledge/kmsMultidocKnowledge.do?method=cancelTop&local=index";
				if (isTopIndex == true
						&& !UserUtil.checkAuthentication(url, "get")) {
					json.element("hasRight", false);
				} else {
					String[] ids = docIds.split(",");
					if (ids.length > 0) {
						for (int i = 0; i < ids.length; i++) {
							KmsMultidocKnowledge model = (KmsMultidocKnowledge) getServiceImp(
									request).findByPrimaryKey(ids[i]);
							if (model != null) {
								model.setFdSetTopTime(null);
								model.setFdSetTopLevel(null);
								model.setFdSetTopReason(null);
								model.setFdTopCategoryId(null);
								model.setDocIsIndexTop(null);
								getServiceImp(request).update(model);
							}
						}
					}
					json.element("hasRight", true);
				}
				out.println(json.toString(1));
			} catch (Exception e) {
				messages.addError(e);
				e.printStackTrace();
			}
		}
		if (messages.hasError()) {
			response.sendError(505);
			out.println(messages);
			return null;
		} else {
			return null;
		}
	}

	// 判断知识文档是否已首页置顶
	public boolean isTopIndex(String docIds, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String[] ids = docIds.split(",");
		boolean isIndex = false;

		if (ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				if (StringUtil.isNotNull(docIds)) {
					KmsMultidocKnowledge model = (KmsMultidocKnowledge) getServiceImp(
							request).findByPrimaryKey(ids[i]);
					if (model.getDocIsIndexTop() != null
							&& model.getDocIsIndexTop()) {
						isIndex = model.getDocIsIndexTop();
					}
				}
			}
		}
		return isIndex;
	}

	protected IKmsMultidocTemplateService kmsMultidocTemplateService;

	protected IBaseService getTreeServiceImp() {
		if (kmsMultidocTemplateService == null)
			kmsMultidocTemplateService = (IKmsMultidocTemplateService) getBean("kmsMultidocTemplateService");
		return kmsMultidocTemplateService;
	}

	/**
	 * 个人中心数据
	 */

	public ActionForward listPerson(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		KmssMessages messages = new KmssMessages();
		try {
			String s_pageno = request.getParameter("pageno");
			String s_rowsize = request.getParameter("rowsize");
			String orderby = request.getParameter("orderby");
			String ordertype = request.getParameter("ordertype");
			String parentId = request.getParameter("categoryId");
			String s_IsShowAll = request.getParameter("isShowAll");
			String excepteIds = request.getParameter("excepteIds");
			String mydoc = request.getParameter("q.mydoc");
			boolean isShowAll = true;
			if (StringUtil.isNotNull(s_IsShowAll)
					&& s_IsShowAll.equals("false"))
				isShowAll = false;
			boolean isReserve = false;
			if (ordertype != null && ordertype.equalsIgnoreCase("down")) {
				isReserve = true;
			}
			int pageno = 0;
			int rowsize = SysConfigParameters.getRowSize();
			if (s_pageno != null && s_pageno.length() > 0) {
				pageno = Integer.parseInt(s_pageno);
			}
			if (s_rowsize != null && s_rowsize.length() > 0) {
				rowsize = Integer.parseInt(s_rowsize);
			}
			if (isReserve)
				orderby += " desc";
			HQLInfo hqlInfo = new HQLInfo();
			hqlInfo.setOrderBy(orderby);
			hqlInfo.setPageNo(pageno);
			hqlInfo.setRowSize(rowsize);
			// changeFindPageHQLInfo(request, hqlInfo);
			String whereBlock = "1=1 ";
			hqlInfo.setWhereBlock(whereBlock);
			if (!StringUtil.isNull(parentId)) {
				if (StringUtil.isNull(whereBlock))
					whereBlock = "";
				else
					whereBlock = "(" + whereBlock + ") and ";
				String tableName = ModelUtil.getModelTableName(getServiceImp(
						request).getModelName());
				if (isShowAll) {
					IBaseTreeModel treeModel = (IBaseTreeModel) getSysSimpleCategoryService()
							.findByPrimaryKey(parentId);

					if (StringUtil.isNull(treeModel.getFdHierarchyId())) {
						whereBlock += tableName + "." + getParentProperty()
								+ ".fdId=:_treeFdId";
						hqlInfo.setParameter("_treeFdId", treeModel.getFdId());
					} else {
						whereBlock += tableName + "." + getParentProperty()
								+ ".fdHierarchyId like :_treeHierarchyId";
						hqlInfo.setParameter("_treeHierarchyId", treeModel
								.getFdHierarchyId()
								+ "%");
					}
				} else {
					whereBlock += tableName + "." + getParentProperty()
							+ ".fdId=:_treeParentId";
					hqlInfo.setParameter("_treeParentId", parentId);
				}
				if (StringUtil.isNotNull(excepteIds)) {
					whereBlock += " and "
							+ HQLUtil.buildLogicIN(tableName + ".fdId not",
									ArrayUtil.convertArrayToList(excepteIds
											.split("\\s*[;,]\\s*")));
				}
			}
			if (StringUtil.isNotNull(mydoc)) {
				whereBlock = _mydoc(request, hqlInfo);
			}
			// hqlInfo.setWhereBlock(whereBlock);
			Page page = getServiceImp(request).findPage(hqlInfo);
			request.setAttribute("queryPage", page);
		} catch (Exception e) {
			messages.addError(e);
		}
		if (messages.hasError()) {
			KmssReturnPage.getInstance(request).addMessages(messages)
					.addButton(KmssReturnPage.BUTTON_CLOSE).save(request);
			return getActionForward("failure", mapping, form, request, response);
		} else {
			return getActionForward("listChildren", mapping, form, request,
					response);
		}
	}

	public String _mydoc(HttpServletRequest request, HQLInfo hqlInfo) {
		String whereBlock = hqlInfo.getWhereBlock();
		String mydoc = request.getParameter("q.mydoc");
		String userId = request.getParameter("userId");
		String personType = request.getParameter("personType");
		// 判断查的是谁的数据
		String fdPersonId = UserUtil.getUser().getFdId();
		if (StringUtil.isNotNull(userId)) {
			fdPersonId = userId;
		}
		// 若是ta的，则只要显示最新版本和发布状态下的
		if ("other".equals(personType)) {
			whereBlock += " and kmsMultidocKnowledge.docStatus =:docStatus and kmsMultidocKnowledge.docIsNewVersion =:docIsNewVersion";
			hqlInfo
					.setParameter("docStatus",
							SysDocConstant.DOC_STATUS_PUBLISH);
			hqlInfo.setParameter("docIsNewVersion", true);
			hqlInfo.setWhereBlock(whereBlock);
		}
		if (StringUtil.isNotNull(mydoc)) {
			// 我创建的
			if ("myCreate".equals(mydoc)) {
				String[] status = request.getParameterValues("q.status");
				whereBlock += " and  (kmsMultidocKnowledge.docCreator.fdId  =:fdPersonId) ";
				hqlInfo.setParameter("fdPersonId", fdPersonId);
				if (status != null && status.length > 0) {
					List<String> idList = ArrayUtil.convertArrayToList(status);
					whereBlock += " and  (kmsMultidocKnowledge.docStatus in (:docStatus)) ";
					hqlInfo.setParameter("docStatus", idList);
				}
				hqlInfo.setWhereBlock(whereBlock);
			}
			// 我原创的
			else if ("myOriginal".equals(mydoc)) {
				whereBlock += " and  (kmsMultidocKnowledge.docAuthor.fdId = :fdPersonId and kmsMultidocKnowledge.docIsNewVersion = :isNew) ";
				hqlInfo.setParameter("fdPersonId", fdPersonId);
				hqlInfo.setParameter("isNew", true);
				hqlInfo.setWhereBlock(whereBlock);
			}
			// 我已审批
			else if ("myApproved".equals(mydoc)) {
				String shortName = ModelUtil
						.getModelTableName(KmsMultidocKnowledge.class.getName());
				SysFlowUtil.buildLimitBlockForMyApproved(shortName, hqlInfo);
				hqlInfo.setCheckParam(SysAuthConstant.CheckType.AllCheck,
						SysAuthConstant.AllCheck.NO);
				whereBlock = hqlInfo.getWhereBlock();
				String[] status = request.getParameterValues("q._status");
				if (status != null && status.length > 0) {
					List<String> idList = ArrayUtil.convertArrayToList(status);
					whereBlock += " and  (kmsMultidocKnowledge.docStatus in (:docStatus)) ";
					hqlInfo.setParameter("docStatus", idList);
				}
				hqlInfo.setWhereBlock(whereBlock);
			}
			// 待我审批
			else if ("myApproval".equals(mydoc)) {
				String shortName = ModelUtil
						.getModelTableName(KmsMultidocKnowledge.class.getName());
				SysFlowUtil.buildLimitBlockForMyApproval(shortName, hqlInfo);
				hqlInfo.setCheckParam(SysAuthConstant.CheckType.AllCheck,
						SysAuthConstant.AllCheck.NO);
			}
			// 我的推荐
			else if ("myIntro".equals(mydoc)) {
				StringBuffer hqlBuffer = new StringBuffer();
				hqlBuffer.append(" and kmsMultidocKnowledge.fdId in ");
				// 拼接子查询
				hqlBuffer
						.append("(select distinct sysIntroduceMain.fdModelId from ");
				hqlBuffer
						.append(" com.landray.kmss.sys.introduce.model.SysIntroduceMain  sysIntroduceMain ");
				hqlBuffer
						.append("where sysIntroduceMain.fdModelName = :fdModelName ");
				hqlBuffer
						.append("and sysIntroduceMain.fdIntroducer.fdId = :fdIntroducerId)");

				hqlInfo
						.setParameter("fdModelName",
								"com.landray.kmss.kms.multidoc.model.KmsMultidocKnowledge");
				hqlInfo.setParameter("fdIntroducerId", fdPersonId);
				whereBlock += hqlBuffer.toString();
				hqlInfo.setWhereBlock(whereBlock);
			}
			// 推荐给我的
			else if ("myIntroTo".equals(mydoc)) {
				StringBuffer hqlBuffer = new StringBuffer();
				hqlBuffer.append(" and kmsMultidocKnowledge.fdId in ");
				// 拼接子查询
				hqlBuffer
						.append("(select distinct sysIntroduceMain.fdModelId from ");
				hqlBuffer
						.append(" com.landray.kmss.sys.introduce.model.SysIntroduceMain  sysIntroduceMain ");

				hqlBuffer
						.append(" where sysIntroduceMain.hbmIntroduceGoalList.fdId =:fdUserId ");
				hqlBuffer
						.append(" and sysIntroduceMain.fdModelName = :fdModelName )");

				hqlInfo
						.setParameter("fdModelName",
								"com.landray.kmss.kms.multidoc.model.KmsMultidocKnowledge");
				hqlInfo.setParameter("fdUserId", fdPersonId);
				whereBlock += hqlBuffer.toString();
				hqlInfo.setWhereBlock(whereBlock);
			}
			// 我的点评
			else if ("myEva".equals(mydoc)) {
				StringBuffer hqlBuffer = new StringBuffer();
				hqlBuffer.append(" and kmsMultidocKnowledge.fdId in ");
				// 拼接子查询
				hqlBuffer
						.append("(select distinct sysEvaluationMain.fdModelId from ");
				hqlBuffer
						.append(" com.landray.kmss.sys.evaluation.model.SysEvaluationMain sysEvaluationMain ");
				hqlBuffer
						.append(" where sysEvaluationMain.fdModelName = :fdModelName ");
				hqlBuffer
						.append("and sysEvaluationMain.fdEvaluator.fdId = :fdEvaluatorId)");
				hqlInfo
						.setParameter("fdModelName",
								"com.landray.kmss.kms.multidoc.model.KmsMultidocKnowledge");
				hqlInfo.setParameter("fdEvaluatorId", fdPersonId);
				whereBlock += hqlBuffer.toString();
				hqlInfo.setWhereBlock(whereBlock);
			}
		}
		return whereBlock;
	}

	@Override
	protected IBaseService getCategoryServiceImp(HttpServletRequest request) {
		// TODO 自动生成的方法存根
		return getkmsKnowledgeCategoryServiceImp();
	}
}
