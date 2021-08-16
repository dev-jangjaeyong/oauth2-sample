package com.big.auth.user.repository;

import com.big.auth.user.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserInfo, Long> {
	UserInfo findByUserId(String userId);
}
