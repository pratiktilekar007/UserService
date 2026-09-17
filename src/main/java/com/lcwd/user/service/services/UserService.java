package com.lcwd.user.service.services;

import com.lcwd.user.service.entities.User;
import java.util.List;

public interface UserService {

    //create User
    User saveUser (User user);

    //get All user
    List<User> getAllUser();

    //get single user
    User getSingleUser(String userId);


}
