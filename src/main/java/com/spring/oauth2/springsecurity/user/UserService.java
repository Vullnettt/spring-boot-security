package com.spring.oauth2.springsecurity.user;


import com.spring.oauth2.springsecurity.response.BaseResponse;

public interface UserService {

    BaseResponse registerAccount(UserDto userDTO);
}
