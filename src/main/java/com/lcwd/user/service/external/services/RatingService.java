package com.lcwd.user.service.external.services;

import com.lcwd.user.service.entities.Rating;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@FeignClient(name = "RATINGSERVICE")
public interface RatingService {

    //GET
    @GetMapping("/ratings")
    List<Rating> getAllRating();

    //POST
    @PostMapping("/ratings")
    Rating createRating(@RequestBody Rating rating);


}
