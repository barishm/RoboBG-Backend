package com.robobg.service;

import com.robobg.dtos.UserDTO.UserIdUsernameRoleDTO;
import com.robobg.dtos.UserDTO.UserUsernameEmailDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    void setRole(UserIdUsernameRoleDTO userIdUsernameRoleDTO);
    List<UserIdUsernameRoleDTO> getAll();

    List<UserUsernameEmailDTO> getAllModerators();


}
