package com.robobg.service;

import com.robobg.entity.dtos.UserDTO.UserIdUsernameRoleDTO;
import com.robobg.entity.dtos.UserDTO.UserUsernameEmailDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    void setRole(UserIdUsernameRoleDTO userIdUsernameRoleDTO);
    List<UserIdUsernameRoleDTO> getAll();

    List<UserUsernameEmailDTO> getAllModerators();


}
