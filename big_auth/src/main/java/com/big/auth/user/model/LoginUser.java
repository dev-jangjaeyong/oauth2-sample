package com.big.auth.user.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class LoginUser implements UserDetails {

	private static final long serialVersionUID = 6396079419309274853L;
	private Long id;
	private String username;
	private String password;
	private String userType;
	private String pid;
	private String cellPhoneNo;
	private List<String> roles;

	public LoginUser() {
	}

	public LoginUser(UserInfo user) {
		this.id = user.getUserNo();
		this.username =  user.getUserId();
		this.password = user.getUserPassword();
		this.userType = user.getUserTyCd();
		this.pid = user.getPid();
		this.cellPhoneNo = user.getCellPhoneNo();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return true;
	}



}
