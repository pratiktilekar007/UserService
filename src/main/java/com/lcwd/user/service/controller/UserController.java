package com.lcwd.user.service.controller;

import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.services.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
      User user1 =  userService.saveUser(user);

      return  ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    int retryCount=1;

    @GetMapping("/{userId}")
    //@CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallback")
    //@Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelFallback")
    @RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<User> getSingleUser(@PathVariable  String userId){


        //System.out.println("retryCount : " + retryCount );

        retryCount++;

        User singleUser = userService.getSingleUser(userId);
        return  ResponseEntity.ok(singleUser);
    }

    //creating fall back method for circuitbreaker



    public ResponseEntity<User> ratingHotelFallback(String userId,Exception ex){

        System.out.println("Fallback is executed because service is down " + ex.getMessage());


        User user = User.builder().email("test@gmail.com").name("pratik").about("this is about ")
                .userId(userId)
                .build();

        return new ResponseEntity<>(user,HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){

        List<User> allUser = userService.getAllUser();
        return ResponseEntity.ok(allUser);
    }
}
