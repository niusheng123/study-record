package com.ns.springframework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 模板引擎
 * @author ns
 * @date 2021/4/19  18:28
 */
public class NsView {
	
	private File viewFile;

	public NsView(File viewFile) {
		this.viewFile = viewFile;
	}
	
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse resp)  throws Exception{
		RandomAccessFile ra = new RandomAccessFile(viewFile, "r");
		String line = null;
		if ((line = ra.readLine()) != null) {
			line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
//			Pattern pattern = Pattern.compile("￥\\{[^\\]}+\\}", Pattern.CASE_INSENSITIVE);
		}
	}
}
