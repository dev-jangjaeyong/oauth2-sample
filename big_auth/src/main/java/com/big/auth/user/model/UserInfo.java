package com.big.auth.user.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author jang jea young (wman11@bizinfogroup.co.kr)
 * @since 2018-08-09
 */
@Entity
//@Table(name = "USER_INFO", schema = "big", catalog = "big")
@Table(name = "USER_INFO")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class UserInfo {

    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sq_generator")
    //@SequenceGenerator(name="user_sq_generator", sequenceName = "sq_user", allocationSize=1)
    @Column(name = "USER_NO")
    private Long userNo;

    @Column(name = "PID")
    private String pid;

    @Column(name = "USER_TY_CD")
    private String userTyCd;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_PASSWORD")
    private String userPassword;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "BIRTHDAY")
    private LocalDateTime birthDay;

    @Column(name = "GENDER_CODE")
    private String genderCode;

    @Column(name = "CELL_PHONE_NO")
    private String cellPhoneNo;

    @Column(name = "EXTN_PHONE_NO")
    private String extnPhoneNo;

    @Column(name="ZIP_CODE")
    private String zipCode;

    @Column(name="ADDR")
    private String addr;

    @Column(name="ADDR_DETAIL")
    private String addrDetail;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "USER_POINT")
    private Long userPoint;
}
