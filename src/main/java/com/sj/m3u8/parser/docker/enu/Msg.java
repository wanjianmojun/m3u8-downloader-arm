package com.sj.m3u8.parser.docker.enu;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Msg {
	EXCEPTION_QYWX_TOKEN_GET_FAILURE("exception.qywx.token.get.failure", "企业微信令牌获取失败"),
	EXCEPTION_QYWX_TOKEN_FAILURE("exception.qywx.token.failure", "企业微信令牌授权失败"),
	EXCEPTION_ORDER_OR_NAME_MUST_VAL("exception.order.or.name.must.val", "排序或名称必须输入其中一个值"),
	EXCEPTION_ORDER_OR_NAME_MUST_VALID("exception.order.or.name.must.valid", "排序或名称在工作簿中无效"),
	EXCEPTION_FIELD_NOT_ANN("exception.field.not.ann", "字段没有匹配的注解"),
	EXCEPTION_CONTACT_ADMIN("exception.contact.admin", "请联系管理员开通邮件撤回功能(提交系统BUG/建议单)"),
	MSG_QYWX_LOGON_SUCCEED("msg.qywx.logon.succeed", "企业微信登录成功"), MSG_QYWX_NO_CODE("msg.qywx.no.code", "无效的认证代码"),
	MSG_QYWX_TOKEN_ERROR("msg.qywx.token.error", "企业微信令牌获取错误"), MSG_QYWX_SSO_ERROR("msg.qywx.sso.error", "获取用户授权信息错误"),
	MSG_QYWX_NO_ACCOUNT("msg.qywx.no.acccount", "账号未开通,请联系管理员"),
	MSG_USER_NAME_NOT_FOUND("msg.user.name.not.found", "用户名不存在"),
	MSG_LOGIN_NOT_ALLOW("msg.login.not.allow", "请使用企业微信扫码登录"), MSG_PASSWORD_ERROR("msg.logon.succeed", "密码错误"),
	MSG_NO_RESULT("msg.no.result", "未查询到数据"), MSG_SEARCH_ERROR("msg.search.error", "查询出错"),
	NO_RECORD_TYPE("no.record.type", "无记录类型记录"), MSG_INSERT_ERROR("msg.search.error", "插入出错"),
	MSG_SERVICE_ERROR("msg.server.error", "内部服务错误"), MSG_IS_DELETED("msg.is.deleted", "单据已删除"),
	MSG_PARAMS_LOSS("msg.params.loss", "参数缺失"), MSG_NOT_PASS_DELETED("msg.not.pass.deleted", "只有未提交的单据才允许删除"),
	MSG_NO_APPROVAL_SETP("msg.no.approval.step", "未设置批准过程"), MSG_NO_API_FIELD("msg.no.api.field", "未传输API字段"),
	MSG_APPROVAL_IS_END("msg.approval.is.end", "流程已结束"), MSG_APPROVAL_IS_SUMBITED("msg.approval.is.sumbited", "流程已提交"),
	MSG_APPROVAL_IS_NOT_BACK("msg.approval.is.not.back", "此节点无法退回"),
	MSG_APPROVAL_IS_NOT_BEFORE_BACK("msg.approval.is.not.before.back", "无前置节点可调回"),
	MSG_APPROVAL_IS_NOT_AUTH_BACK("msg.approval.is.not.auth.back", "需要调回的节点不在权限范围内"),
	MSG_APPROVAL_IS_NOT_NEED_BACK("msg.approval.is.not.need.back", "无需调回"),
	MSG_APPROVAL_IS_NOT_CALLBACK("msg.approval.is.not.back", "此节点无法调回"),
	MSG_APPROVAL_IS_NOT_RETURN("msg.approval.is.not.return", "只有单据创建者可以调回"),
	MSG_APPROVAL_IS_NOT_USER("msg.approval.is.not.back", "节点未设置审批人"),
	MSG_APPROVAL_IS_LOCKED("msg.approval.is.locked", "单据锁定中,请稍后再试"),
	MSG_APPROVAL_IS_NOT_AUTH("msg.approval.is.not.auth", "无权限操作此节点"),
	MSG_APPROVAL_IS_MOVED("msg.approval.is.moved", "审批节点已转移"),
	MSG_APPROVAL_IS_REPEAT("msg.approval.is.repeat", "请重新从待审批页面进入此单据进行审批"),
	MSG_APPROVAL_IS_NOT_WASID("msg.approval.is.not.wasid", "审批节点数据未知"),
	MSG_APPROVAL_IS_NOT_SUPPORT_BACK("msg.approval.is.not.support.back", "此节点无法退回"),
	EXCEL_HAS_PASSWORD("excel.has.password", "excel文件含有密码"), FILE_FORMAT_ERROR("file.format,error", "文件格式错误"),
	MSG_QUESTION_NUMBER_IS_NOT_ENOUTH("msg.question.number.is.not.enouth", "题库数量不足"),
	MSG_EXAM_ACCOUNT_OVER("msg.exam.account.over", "考试次数超出限制(2次)\n学习进度已重置"),
	MSG_NO_COURSE_NUMBER("msg.no.course.number", "课程编号未填写"), CUSTOMER_CODE_REPEAT("msg.customer.code.repeat", "客户编码重复"),
	CUSTOMER_NAME_REPEAT("msg.customer.name.repeat", "客户名称重复"), CUSTOMER_NAME_NULL("msg.customer.name.null", "客户名称为空"),
	MODEL_ID_ERROR("model.id.error", "模板不匹配"), MSG_REF_FAILURE("msg.ref.failure", "客户关联失败,请刷新页面后重试");

	private String key;
	private String value;

	public Msg append(String content) {
		if (StrUtil.isNotBlank(content))
			value = value.concat(content);
		return this;
	}

}
