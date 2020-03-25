package com.javainuse.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Setter
@Getter
@ToString
public class Register {
	
	private String username;
	private String password;
	private boolean is2FAEnabled;
	
}
