package com.example.kuangjia.util;

import org.springframework.web.servlet.view.InternalResourceView;

import java.io.File;
import java.util.Locale;

public class HtmlResourceView extends InternalResourceView {
	
	
	     public boolean checkResource(Locale locale) {  
	      File file = new File(this.getServletContext().getRealPath("/") + getUrl());  
	      return file.exists();// 判断该页面是否存在  
	     }  
	

}
