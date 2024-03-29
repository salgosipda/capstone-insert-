package kr.ac.hansung.controller;

import java.io.IOException;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kr.ac.hansung.model.Estate;
import kr.ac.hansung.service.EstateService;

@Controller
public class HomeController {

	@Autowired
	private EstateService estate;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) throws IOException, ParseException  {
		
		//DB에 매물 저장
		estate.insert(); 
		
		//DB에서 매물 가져오기 
		List<Estate> home=estate.getCurrent();
		
		//home.jsp 호출
		model.addAttribute("home",home);

		return "home";
	}

}
