package com.youzi.autoAnn.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.youzi.autoAnn.entity.User;
import com.youzi.autoAnn.util.DoclectAnnUtil;
import com.youzi.autoAnn.util.DocletUtil;


/**
 * 核心控制器
 * @author Administrator
 *
 */
@Controller
public class MainController {

	
	/**
	 * this is PPY
	 * @param user
	 * @return
	 */
	@GetMapping("/index")
	public String index(@RequestParam User user) {
		return "index";
	}
	
	
	/**
	 * 获取全部的接口
	 * @return
	 */
	@GetMapping("/full")
	public String getFullAnnotaion(ModelMap modelMap) {
		Map<String, Map<String, Map<String, Object>>> fullinterface=DoclectAnnUtil.values;
		Map<String,Map<String,Object>> controller=fullinterface.get("controller");
		modelMap.addAttribute("userMap", controller);
		return "index";
	}
	
	/**
	 * 获取指定Controller的接口
	 * @param controllerName
	 * @return
	 */
	@GetMapping("/getByName")
	public String getAnnotionByConName(@RequestParam(name="controllerName",required=true)String controllerName) {
		return "";
	}
}
