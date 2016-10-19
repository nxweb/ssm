package com.FangBianMian.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.FangBianMian.constant.Common;
import com.FangBianMian.constant.LoginStatus;
import com.FangBianMian.domain.Member;
import com.FangBianMian.response.JsonResWrapper;
import com.FangBianMian.service.IMemberService;
import com.FangBianMian.utils.DataValidation;
import com.FangBianMian.utils.EasyuiDatagrid;

@RequestMapping("/admin/member")
@Controller
public class MemberController {

	@Autowired
	private IMemberService memberService;
	
	/**
	 * 充值
	 * @return
	 */
	@RequestMapping("/charge")
	@ResponseBody
	public JsonResWrapper charge(@RequestParam(required=false) String username,
								 @RequestParam(required=false) Float price){
		JsonResWrapper jrw = new JsonResWrapper();
		if(StringUtils.isBlank(username) || price==null){
			jrw.setFlag(false);
			jrw.setMessage("操作失败，请求参数缺失");
		}
		
		Member m = new Member();
		m.setUsername(username);
		m.setBalance(m.getBalance()+price);
		memberService.updateMember(m);
		
		return jrw;
	}
	
	/**
	 * 确认登录
	 * @return
	 */
	@RequestMapping("/confirmLogin")
	@ResponseBody
	public JsonResWrapper confirmLogin(HttpServletRequest request,
									   @RequestParam(required=false) String username,
									   @RequestParam(required=false) Integer flag){
		JsonResWrapper jrw = new JsonResWrapper();
		if(flag==null || StringUtils.isBlank(username)){
			jrw.setFlag(false);
			jrw.setMessage("操作失败，请求参数缺失");
		}
		
		Member m = new Member();
		m.setUsername(username);
		m.setStatus(flag);
		memberService.updateMember(m);
		
		if(flag==LoginStatus.FIALD){ 
			request.getSession().setAttribute(Common.MEMBER_SESSION, null);
		}
		
		return jrw;
	}
	
	/**
	 * 用户管理
	 * @return
	 */
	@RequestMapping("/list")
	public String list(){
		return "pages/member/list";
	}
	
	/**
	 * 用户列表数据
	 * @return
	 */
	@RequestMapping("/listData")
	@ResponseBody
	public EasyuiDatagrid<Member> listData(@RequestParam(required=false) Integer page,
											@RequestParam(required=false) Integer rows,
											@RequestParam(required=false) String name,
											@RequestParam(required=false) Integer status){
		EasyuiDatagrid<Member> ed = new EasyuiDatagrid<Member>();
		Map<String,Object> param = new HashMap<String,Object>();

		if(page!=null && rows!=null){
			param.put("rows", rows);
			param.put("page", ((page-1)*rows));
		}
		if(!StringUtils.isBlank(name)){
			param.put("name", name);
		}
		if(status!=null && status!=-1){
			param.put("status", status);
		}
		
		List<Member> ms = DataValidation.isEmpty(memberService.queryMembersByParam(param));
		int total = memberService.queryMembersByParamTotal(param);
		
		ed.setRows(ms);
		ed.setTotal(total);
		return ed;
	}
}
