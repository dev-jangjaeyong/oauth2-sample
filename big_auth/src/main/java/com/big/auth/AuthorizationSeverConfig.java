package com.big.auth;

import com.big.auth.user.model.LoginUser;
import com.big.auth.util.WebUtils;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.security.KeyPair;
import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
@EnableAuthorizationServer
//@AllArgsConstructor
public class AuthorizationSeverConfig extends AuthorizationServerConfigurerAdapter {
	@Value("${resouce.id:spring-boot-application}")
	private String resourceId;

	@Value("${access_token.validity_period:3600}")
	int accessTokenValiditySeconds = 3600;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	@Qualifier("prodDataSource")
	private DataSource dataSource;


    /*@Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }*/

	@Bean
	public TokenStore JdbcTokenStore() {
		return new JdbcTokenStore(dataSource);
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new CustomTokenEnhancer();
		converter.setSigningKey("secret");
		// TODO : 서비스에 따라 jks 파일 생성 해서 if 로 분기?
		/*KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("server.jks"), "passtwo".toCharArray())
				.getKeyPair("auth", "passone".toCharArray());
		converter.setKeyPair(keyPair);*/
		//converter.setAccessTokenConverter(new JwtConverter());
		return converter;
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenService() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(JdbcTokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		return defaultTokenServices;
	}


	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				.approvalStore(approvalStore())
				.tokenStore(JdbcTokenStore())
				.authenticationManager(authenticationManager)
				.accessTokenConverter(accessTokenConverter());
	}

	@Bean
	@Primary
	public JdbcClientDetailsService JdbcClientDetailsService(DataSource dataSource) {
		//this.dataSource = dataSource;
		return new JdbcClientDetailsService(dataSource);
	}

	@Bean
	public ApprovalStore approvalStore() {
		return new JdbcApprovalStore(dataSource);
	}


	@Autowired
	private ClientDetailsService clientDetailsService;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		//oauth_client_details 테이블에 등록된 사용자로 조회한다.
		clients
				.withClientDetails(clientDetailsService);

		/*
		clients.inMemory()
				.withClient("bar").secret("foo")
				.authorizedGrantTypes("password")
				.authorities("ROLE_USER")
				.scopes("read", "write")
				.resourceIds(resourceId)
				.accessTokenValiditySeconds(accessTokenValiditySeconds);
		*/

	}

	protected static class CustomTokenEnhancer extends JwtAccessTokenConverter {
		@Override
		public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

			LoginUser user = (LoginUser) authentication.getPrincipal();
			Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
					//<>(
					//accessToken.getAdditionalInformation());

			info.put("pid", user.getPid());
			info.put("userTyCd", user.getUserType());
			info.put("cellPhoneNo", user.getCellPhoneNo());

			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			WebUtils webUtils = new WebUtils();
			webUtils.setRequest(request);
			info.put("ip", webUtils.getClientIp());

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				System.out.println("::::::: host map : " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(webUtils.getRequestHeadersInMap()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
			customAccessToken.setAdditionalInformation(info);
			return super.enhance(customAccessToken, authentication);
		}
	}

	public static String getRequestRemoteAddr(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		return request.getRemoteAddr();
	}

}
