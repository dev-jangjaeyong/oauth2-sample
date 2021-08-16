package com.big.auth;

import com.big.auth.user.model.LoginUser;
import com.big.auth.user.model.UserInfo;
import com.big.auth.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserInfo user = userRepository.findByUserId(username);
		if (user == null) {
			throw new UsernameNotFoundException("UsernameNotFound [" + username + "]");
		}
		LoginUser loginUser = createUser(user);
		return loginUser;
	}

	private LoginUser createUser(UserInfo user) {
		LoginUser loginUser = new LoginUser(user);
		if (loginUser.getUserType().equals("300")) {
			loginUser.setRoles(Arrays.asList("ROLE_EXT"));
		} else {
			loginUser.setRoles(Arrays.asList("ROLE_USER"));
		}
		return loginUser;
	}

}
