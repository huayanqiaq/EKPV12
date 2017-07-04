package com.landray.kmss.example.rules.forms;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import com.landray.kmss.common.forms.ExtendForm;
import com.landray.kmss.util.AutoArrayList;
import com.landray.kmss.util.AutoHashMap;

import com.landray.kmss.common.convertor.FormToModelPropertyMap;
import com.landray.kmss.common.convertor.FormConvertor_FormListToModelList;
import com.landray.kmss.common.convertor.FormConvertor_IDToModel;
import com.landray.kmss.common.convertor.FormConvertor_IDsToModelList;

import com.landray.kmss.example.rules.model.ExampleRulesCategory;
import com.landray.kmss.sys.attachment.forms.AttachmentDetailsForm;
import com.landray.kmss.sys.attachment.forms.IAttachmentForm;
import com.landray.kmss.sys.attachment.model.IAttachment;
import com.landray.kmss.sys.bookmark.interfaces.ISysBookmarkForm;
import com.landray.kmss.sys.organization.model.SysOrgElement;
import com.landray.kmss.example.rules.model.ExampleRulesMain;



/**
 * 最新案例 Form
 * 
 * @author 戴云
 * @version 1.0 2017-07-04
 */
public class ExampleRulesMainForm  extends ExtendForm implements IAttachmentForm {

	/**
	 * 标题
	 */
	private String docSubject;
	
	/**
	 * @return 标题
	 */
	public String getDocSubject() {
		return this.docSubject;
	}
	
	/**
	 * @param docSubject 标题
	 */
	public void setDocSubject(String docSubject) {
		this.docSubject = docSubject;
	}
	
	/**
	 * 创建时间
	 */
	private String docCreateTime;
	
	/**
	 * @return 创建时间
	 */
	public String getDocCreateTime() {
		return this.docCreateTime;
	}
	
	/**
	 * @param docCreateTime 创建时间
	 */
	public void setDocCreateTime(String docCreateTime) {
		this.docCreateTime = docCreateTime;
	}
	
	/**
	 * 案例类型
	 */
	private String fdWorkType;
	
	/**
	 * @return 案例类型
	 */
	public String getFdWorkType() {
		return this.fdWorkType;
	}
	
	/**
	 * @param fdWorkType 案例类型
	 */
	public void setFdWorkType(String fdWorkType) {
		this.fdWorkType = fdWorkType;
	}
	
	/**
	 * 发布时间
	 */
	private String docPublishTime;
	
	/**
	 * @return 发布时间
	 */
	public String getDocPublishTime() {
		return this.docPublishTime;
	}
	
	/**
	 * @param docPublishTime 发布时间
	 */
	public void setDocPublishTime(String docPublishTime) {
		this.docPublishTime = docPublishTime;
	}
	
	/**
	 * 内容
	 */
	private String docContent;
	
	/**
	 * @return 内容
	 */
	public String getDocContent() {
		return this.docContent;
	}
	
	/**
	 * @param docContent 内容
	 */
	public void setDocContent(String docContent) {
		this.docContent = docContent;
	}
	
	/**
	 * 通知方式
	 */
	private String fdNotifyType;
	
	/**
	 * @return 通知方式
	 */
	public String getFdNotifyType() {
		return this.fdNotifyType;
	}
	
	/**
	 * @param fdNotifyType 通知方式
	 */
	public void setFdNotifyType(String fdNotifyType) {
		this.fdNotifyType = fdNotifyType;
	}
	
	/**
	 * 文档状态
	 */
	private String docStatus;
	
	/**
	 * @return 文档状态
	 */
	public String getDocStatus() {
		return this.docStatus;
	}
	
	/**
	 * @param docStatus 文档状态
	 */
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	
	/**
	 * 所属分类的ID
	 */
	private String docCategoryId;
	
	/**
	 * @return 所属分类的ID
	 */
	public String getDocCategoryId() {
		return this.docCategoryId;
	}
	
	/**
	 * @param docCategoryId 所属分类的ID
	 */
	public void setDocCategoryId(String docCategoryId) {
		this.docCategoryId = docCategoryId;
	}
	
	/**
	 * 所属分类的名称
	 */
	private String docCategoryName;
	
	/**
	 * @return 所属分类的名称
	 */
	public String getDocCategoryName() {
		return this.docCategoryName;
	}
	
	/**
	 * @param docCategoryName 所属分类的名称
	 */
	public void setDocCategoryName(String docCategoryName) {
		this.docCategoryName = docCategoryName;
	}
	
	/**
	 * 创建者的ID
	 */
	private String docCreatorId;
	
	/**
	 * @return 创建者的ID
	 */
	public String getDocCreatorId() {
		return this.docCreatorId;
	}
	
	/**
	 * @param docCreatorId 创建者的ID
	 */
	public void setDocCreatorId(String docCreatorId) {
		this.docCreatorId = docCreatorId;
	}
	
	/**
	 * 创建者的名称
	 */
	private String docCreatorName;
	
	/**
	 * @return 创建者的名称
	 */
	public String getDocCreatorName() {
		return this.docCreatorName;
	}
	
	/**
	 * @param docCreatorName 创建者的名称
	 */
	public void setDocCreatorName(String docCreatorName) {
		this.docCreatorName = docCreatorName;
	}
	
	/**
	 * 通知人的ID列表
	 */
	private String fdNotifierIds;
	
	/**
	 * @return 通知人的ID列表
	 */
	public String getFdNotifierIds() {
		return this.fdNotifierIds;
	}
	
	/**
	 * @param fdNotifierIds 通知人的ID列表
	 */
	public void setFdNotifierIds(String fdNotifierIds) {
		this.fdNotifierIds = fdNotifierIds;
	}
	
	/**
	 * 通知人的名称列表
	 */
	private String fdNotifierNames;
	
	/**
	 * @return 通知人的名称列表
	 */
	public String getFdNotifierNames() {
		return this.fdNotifierNames;
	}
	
	/**
	 * @param fdNotifierNames 通知人的名称列表
	 */
	public void setFdNotifierNames(String fdNotifierNames) {
		this.fdNotifierNames = fdNotifierNames;
	}
	
	/**
	 * 可阅读者的ID列表
	 */
	private String authReaderIds;
	
	/**
	 * @return 可阅读者的ID列表
	 */
	public String getAuthReaderIds() {
		return this.authReaderIds;
	}
	
	/**
	 * @param authReaderIds 可阅读者的ID列表
	 */
	public void setAuthReaderIds(String authReaderIds) {
		this.authReaderIds = authReaderIds;
	}
	
	/**
	 * 可阅读者的名称列表
	 */
	private String authReaderNames;
	
	/**
	 * @return 可阅读者的名称列表
	 */
	public String getAuthReaderNames() {
		return this.authReaderNames;
	}
	
	/**
	 * @param authReaderNames 可阅读者的名称列表
	 */
	public void setAuthReaderNames(String authReaderNames) {
		this.authReaderNames = authReaderNames;
	}
	
	/**
	 * 可编辑者的ID列表
	 */
	private String authEditorIds;
	
	/**
	 * @return 可编辑者的ID列表
	 */
	public String getAuthEditorIds() {
		return this.authEditorIds;
	}
	
	/**
	 * @param authEditorIds 可编辑者的ID列表
	 */
	public void setAuthEditorIds(String authEditorIds) {
		this.authEditorIds = authEditorIds;
	}
	
	/**
	 * 可编辑者的名称列表
	 */
	private String authEditorNames;
	
	/**
	 * @return 可编辑者的名称列表
	 */
	public String getAuthEditorNames() {
		return this.authEditorNames;
	}
	
	/**
	 * @param authEditorNames 可编辑者的名称列表
	 */
	public void setAuthEditorNames(String authEditorNames) {
		this.authEditorNames = authEditorNames;
	}
	
	/**
	 * 其它可阅读者的ID列表
	 */
	private String authOtherReaderIds;
	
	/**
	 * @return 其它可阅读者的ID列表
	 */
	public String getAuthOtherReaderIds() {
		return this.authOtherReaderIds;
	}
	
	/**
	 * @param authOtherReaderIds 其它可阅读者的ID列表
	 */
	public void setAuthOtherReaderIds(String authOtherReaderIds) {
		this.authOtherReaderIds = authOtherReaderIds;
	}
	
	/**
	 * 其它可阅读者的名称列表
	 */
	private String authOtherReaderNames;
	
	/**
	 * @return 其它可阅读者的名称列表
	 */
	public String getAuthOtherReaderNames() {
		return this.authOtherReaderNames;
	}
	
	/**
	 * @param authOtherReaderNames 其它可阅读者的名称列表
	 */
	public void setAuthOtherReaderNames(String authOtherReaderNames) {
		this.authOtherReaderNames = authOtherReaderNames;
	}
	
	/**
	 * 所有可阅读者的ID列表
	 */
	private String authAllReaderIds;
	
	/**
	 * @return 所有可阅读者的ID列表
	 */
	public String getAuthAllReaderIds() {
		return this.authAllReaderIds;
	}
	
	/**
	 * @param authAllReaderIds 所有可阅读者的ID列表
	 */
	public void setAuthAllReaderIds(String authAllReaderIds) {
		this.authAllReaderIds = authAllReaderIds;
	}
	
	/**
	 * 所有可阅读者的名称列表
	 */
	private String authAllReaderNames;
	
	/**
	 * @return 所有可阅读者的名称列表
	 */
	public String getAuthAllReaderNames() {
		return this.authAllReaderNames;
	}
	
	/**
	 * @param authAllReaderNames 所有可阅读者的名称列表
	 */
	public void setAuthAllReaderNames(String authAllReaderNames) {
		this.authAllReaderNames = authAllReaderNames;
	}
	
	/**
	 * 其它可编辑者的ID列表
	 */
	private String authOtherEditorIds;
	
	/**
	 * @return 其它可编辑者的ID列表
	 */
	public String getAuthOtherEditorIds() {
		return this.authOtherEditorIds;
	}
	
	/**
	 * @param authOtherEditorIds 其它可编辑者的ID列表
	 */
	public void setAuthOtherEditorIds(String authOtherEditorIds) {
		this.authOtherEditorIds = authOtherEditorIds;
	}
	
	/**
	 * 其它可编辑者的名称列表
	 */
	private String authOtherEditorNames;
	
	/**
	 * @return 其它可编辑者的名称列表
	 */
	public String getAuthOtherEditorNames() {
		return this.authOtherEditorNames;
	}
	
	/**
	 * @param authOtherEditorNames 其它可编辑者的名称列表
	 */
	public void setAuthOtherEditorNames(String authOtherEditorNames) {
		this.authOtherEditorNames = authOtherEditorNames;
	}
	
	/**
	 * 所有可编辑者的ID列表
	 */
	private String authAllEditorIds;
	
	/**
	 * @return 所有可编辑者的ID列表
	 */
	public String getAuthAllEditorIds() {
		return this.authAllEditorIds;
	}
	
	/**
	 * @param authAllEditorIds 所有可编辑者的ID列表
	 */
	public void setAuthAllEditorIds(String authAllEditorIds) {
		this.authAllEditorIds = authAllEditorIds;
	}
	
	/**
	 * 所有可编辑者的名称列表
	 */
	private String authAllEditorNames;
	
	/**
	 * @return 所有可编辑者的名称列表
	 */
	public String getAuthAllEditorNames() {
		return this.authAllEditorNames;
	}
	
	/**
	 * @param authAllEditorNames 所有可编辑者的名称列表
	 */
	public void setAuthAllEditorNames(String authAllEditorNames) {
		this.authAllEditorNames = authAllEditorNames;
	}
	
	//机制开始 
	//机制结束

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		docSubject = null;
		docCreateTime = null;
		fdWorkType = null;
		docPublishTime = null;
		docContent = null;
		fdNotifyType = null;
		docStatus = null;
		docCategoryId = null;
		docCategoryName = null;
		docCreatorId = null;
		docCreatorName = null;
		fdNotifierIds = null;
		fdNotifierNames = null;
		authReaderIds = null;
		authReaderNames = null;
		authEditorIds = null;
		authEditorNames = null;
		authOtherReaderIds = null;
		authOtherReaderNames = null;
		authAllReaderIds = null;
		authAllReaderNames = null;
		authOtherEditorIds = null;
		authOtherEditorNames = null;
		authAllEditorIds = null;
		authAllEditorNames = null;
		
 
		super.reset(mapping, request);
	}

	public Class<ExampleRulesMain> getModelClass() {
		return ExampleRulesMain.class;
	}
	
	private static FormToModelPropertyMap toModelPropertyMap;

	public FormToModelPropertyMap getToModelPropertyMap() {
		if (toModelPropertyMap == null) {
			toModelPropertyMap = new FormToModelPropertyMap();
			toModelPropertyMap.putAll(super.getToModelPropertyMap());
			toModelPropertyMap.put("docCategoryId",
					new FormConvertor_IDToModel("docCategory",
						ExampleRulesCategory.class));
			toModelPropertyMap.put("docCreatorId",
					new FormConvertor_IDToModel("docCreator",
						SysOrgElement.class));
			toModelPropertyMap.put("fdNotifierIds", new FormConvertor_IDsToModelList(
					"fdNotifiers", SysOrgElement.class));
			toModelPropertyMap.put("authReaderIds", new FormConvertor_IDsToModelList(
					"authReaders", SysOrgElement.class));
			toModelPropertyMap.put("authEditorIds", new FormConvertor_IDsToModelList(
					"authEditors", SysOrgElement.class));
			toModelPropertyMap.put("authOtherReaderIds", new FormConvertor_IDsToModelList(
					"authOtherReaders", SysOrgElement.class));
			toModelPropertyMap.put("authAllReaderIds", new FormConvertor_IDsToModelList(
					"authAllReaders", SysOrgElement.class));
			toModelPropertyMap.put("authOtherEditorIds", new FormConvertor_IDsToModelList(
					"authOtherEditors", SysOrgElement.class));
			toModelPropertyMap.put("authAllEditorIds", new FormConvertor_IDsToModelList(
					"authAllEditors", SysOrgElement.class));
		}
		return toModelPropertyMap;
	}
	
	/**
     * 附件实现
     */
	AutoHashMap autoHashMap = new AutoHashMap(AttachmentDetailsForm.class);
	public AutoHashMap getAttachmentForms() {
		return autoHashMap;
	}
}
