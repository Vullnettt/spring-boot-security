package com.spring.oauth2.springsecurity.oauth2.security;


import com.spring.oauth2.springsecurity.exception.BaseException;
import com.spring.oauth2.springsecurity.oauth2.OAuth2UserDetailFactory;
import com.spring.oauth2.springsecurity.oauth2.OAuth2UserDetails;
import com.spring.oauth2.springsecurity.role.RoleRepository;
import com.spring.oauth2.springsecurity.user.User;
import com.spring.oauth2.springsecurity.user.UserRepository;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserDetailService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomOAuth2UserDetailService(UserRepository userRepository,
                                         RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return checkingOAuth2User(userRequest, oAuth2User);
        }catch (AuthenticationException ae){
            throw ae;
        }catch (Exception e){
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User checkingOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserDetails oAuth2UserDetails = OAuth2UserDetailFactory.getOAuth2UserDetails(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        if (ObjectUtils.isEmpty(oAuth2UserDetails)) {
            throw new BaseException("400", "Can not found oauth2 user from properties");
        }

        Optional<User> user = userRepository.findByUsernameAndProviderId(
                oAuth2UserDetails.getEmail(),
                oAuth2UserRequest.getClientRegistration().getRegistrationId());

        User userDetail;
        if (user.isPresent()) {
            userDetail = user.get();
            if (!userDetail.getProviderId().equals(
                    oAuth2UserRequest.getClientRegistration().getRegistrationId())) {
                throw new BaseException("400", "Invalid site login with " + userDetail.getProviderId());
            }
            userDetail = updateOAuth2UserDetail(userDetail, oAuth2UserDetails);
        } else {
            userDetail = registerNewOAuth2UserDetail(oAuth2UserRequest, oAuth2UserDetails);
        }
        return new OAuth2UserDetailCustom(
                userDetail.getId(),
                userDetail.getUsername(),
                userDetail.getPassword(),
                userDetail.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList())
        );
    }
    public User registerNewOAuth2UserDetail(OAuth2UserRequest oAuth2UserRequest, OAuth2UserDetails oAuth2UserDetails){
        User user = new User();

        user.setUsername(oAuth2UserDetails.getEmail());

        user.setProviderId(oAuth2UserRequest.getClientRegistration().getRegistrationId());

        user.setEnabled(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);

        user.setRoles(new HashSet<>());
        user.getRoles().add(roleRepository.findByName("USER"));

        return userRepository.save(user);
    }

    public User updateOAuth2UserDetail(User user, OAuth2UserDetails oAuth2UserDetails){
        user.setUsername(oAuth2UserDetails.getEmail());
        return userRepository.save(user);
    }
}
