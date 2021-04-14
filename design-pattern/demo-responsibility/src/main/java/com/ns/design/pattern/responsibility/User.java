package com.ns.design.pattern.responsibility;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ns
 * @date 2021/4/7  9:37
 */
@Data
@AllArgsConstructor
public class User {

	private String username;
	
	private String password;
	
	private String captcha;

}
