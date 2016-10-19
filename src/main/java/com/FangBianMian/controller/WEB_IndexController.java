package com.FangBianMian.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.FangBianMian.constant.Common;
import com.FangBianMian.constant.LoginStatus;
import com.FangBianMian.dao.IBaseDao;
import com.FangBianMian.domain.Member;
import com.FangBianMian.domain.Product;
import com.FangBianMian.domain.ProductCategory;
import com.FangBianMian.domain.User;
import com.FangBianMian.service.IMemberService;
import com.FangBianMian.service.IProductService;
import com.FangBianMian.utils.DataValidation;

/**
 * 商城首页
 * @author Administrator
 *
 */
@RequestMapping("/web/index")
@Controller
public class WEB_IndexController {

	@Autowired
	private IProductService productService;
	@Autowired
	private IBaseDao baseDao;
	@Autowired
	private IMemberService memberService;
	
	/**
	 * 发送验证码
	 * @param model
	 * @return
	 */
	@RequestMapping("/sendVerifyCode")
	@ResponseBody
	public Map<String,Object> sendVerifyCode(@RequestParam(required=false) String phone){
		Map<String,Object> map = new HashMap<String, Object>();
		if(!DataValidation.isMobile(phone)){ //验证失败
			map.put("flag", true);
			map.put("msg", "发送失败");
		}else{ //发送短信
			
		}
		return map;
	}
	
	/**
	 * 登录页面
	 * @param model
	 * @return
	 */
	@RequestMapping("/login")
	public String login(){
		return "web/login";
	}
	
	/**
	 * 首页
	 * @param model
	 * @return
	 */
	@RequestMapping("/main")
	public String main(){
		return "web/main";
	}
	
	/**
	 * 上
	 * @param model
	 * @return
	 */
	@RequestMapping("/top")
	public String top(Model model, HttpServletRequest request){
		//获取登录用户
		Member user = (Member)request.getSession().getAttribute(Common.MEMBER_SESSION);
		if(user!=null){
			user = memberService.queryMemberByUsername(user.getUsername());
			//如果session中的用户状态不是登录成功，则设为null
			if(user.getStatus()!=LoginStatus.SUCCESS){
				user = null;
			}
		}
		
		//获取商品分类列表
		List<ProductCategory> pcs = DataValidation.isEmpty(baseDao.queryProductCategory());
		
		model.addAttribute("pcs", pcs);
		model.addAttribute("user", user);
		return "web/header";
	}

	/**
	 * 中
	 * @param model
	 * @return
	 */
	@RequestMapping("/center")
	public String center(Model model, @RequestParam(required=false) Integer category_id,
						@RequestParam(required=false) String name){
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("page", 0);
		param.put("rows", 12);
		param.put("status", true);
		if(category_id!=null){
			param.put("category_id", category_id);
			model.addAttribute("category_id", category_id);
		}
		if(!StringUtils.isBlank(name)){
			param.put("name", name);
		}
		//查询所有商品
		List<Product> ps = productService.queryProductsByParam(param);
		
		model.addAttribute("ps", ps);
		return "web/list";
	}

	/**
	 * 下
	 * @param model
	 * @return
	 */
	@RequestMapping("/bottom")
	public String left(){
		return "web/footer";
	}

	/**
	 * 首页
	 * @param model
	 * @return
	 */
	@RequestMapping("/index")
	public String index(Model model,
						@RequestParam(required=false) Boolean hot,
						@RequestParam(required=false) Integer category_id){
		Map<String, Object> hot_param = new HashMap<String, Object>();
		hot_param.put("page", 0);
		hot_param.put("rows", 5);
		hot_param.put("hot", true); //是否是热门商品
		hot_param.put("status", true); //是否上架
		//查询热门商品
		List<Product> hot_ps = productService.queryProductsByParam(hot_param);

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("page", 0);
		param.put("rows", 12);
		param.put("status", true);
		if(hot!=null){
			param.put("hot", hot);
			model.addAttribute("hot", hot);
		}
		if(category_id!=null){
			param.put("category_id", category_id);
			model.addAttribute("category_id", category_id);
		}
		//查询所有商品
		List<Product> ps = productService.queryProductsByParam(param);
		
		//获取商品分类列表
		List<ProductCategory> pcs = DataValidation.isEmpty(baseDao.queryProductCategory());
		
		model.addAttribute("ps", ps);
		model.addAttribute("hot_ps", hot_ps);
		model.addAttribute("pcs", pcs);
		return "kaquan/index";
	}
	
	/**
	 * 判断用户是否登录
	 * @return
	 */
	@RequestMapping("/logged")
	@ResponseBody
	public Map<String,Object> logged(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		User user = (User) request.getSession().getAttribute(Common.USER_SESSION);
		map.put("flag", user!=null);
		return map;
	}
}
