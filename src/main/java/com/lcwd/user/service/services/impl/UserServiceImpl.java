package com.lcwd.user.service.services.impl;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exceptions.ResourceNotFoundException;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final HotelService hotelService;

    UserServiceImpl(UserRepository userRepository, RestTemplate restTemplate,
                    HotelService hotelService)
    {
        this.userRepository=userRepository;
        this.restTemplate=restTemplate;
        this.hotelService=hotelService;
    }

    @Override
    public User saveUser(User user) {

        String id = UUID.randomUUID().toString();
        user.setUserId(id);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getSingleUser(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with given id is not found on server !! : " + userId));

        // Fix 1: Use parameterized array to avoid type erasure issues
        Rating[] ratingsArray = restTemplate.getForObject(
                "http://RATINGSERVICE/ratings/users/" + user.getUserId(),
                Rating[].class
        );

        List<Rating> ratingList = new ArrayList<>();

        if (ratingsArray != null) {
            ratingList = Arrays.stream(ratingsArray).map(rating -> {
                // Fix 2: Fetch the hotel data dynamically for each rating
               /* ResponseEntity<Hotel> forEntity = restTemplate.getForEntity(
                        "http://HOTELSERVICE/hotels/" + rating.getHotelId(),
                        Hotel.class
                ); */

                //calling from feign client
                Hotel hotel = hotelService.getHotel(rating.getHotelId());
                rating.setHotel(hotel);
                return rating;
            }).collect(Collectors.toList());
        }

// Fix 3: Assign the fully populated list to the user
        user.setRatings(ratingList);

        return user;


    }
}
