package com.lcwd.user.service;

import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.external.services.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private RatingService ratingService;

    @Test
    void CreateRating(){

        Rating rating = Rating.builder().ratings(11).hotelId("13271eba-14c2-446d-ae2d-231d933f8bf7").
                userId("d368d791-0cc1-4732-9f49-a42ee0fc2121").remark("pratiks rating").build();
        Rating rating1 = ratingService.createRating(rating);

        System.out.println(rating1);
    }

}
