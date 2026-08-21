package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bprflavorshub.bpr_flavors_hub.dto.FoodRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.FoodResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.Food;
import com.bprflavorshub.bpr_flavors_hub.entity.Restaurant;
import com.bprflavorshub.bpr_flavors_hub.repository.FoodRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.RestaurantRepository;
import com.bprflavorshub.bpr_flavors_hub.service.FoodService;

@Service
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;


    public FoodServiceImpl(FoodRepository foodRepository,
                           RestaurantRepository restaurantRepository) {

        this.foodRepository = foodRepository;
        this.restaurantRepository = restaurantRepository;
    }


    // ADD FOOD
    @Override
    public FoodResponse addFood(FoodRequest request) {

        Restaurant restaurant = restaurantRepository
                .findById(request.getRestaurantId())
                .orElseThrow(() -> 
                    new RuntimeException("Restaurant not found")
                );


        Food food = Food.builder()
                .foodName(request.getFoodName())
                .description(request.getDescription())
                .price(request.getPrice())
                .image(request.getImage())
                .category(request.getCategory())
                .available(request.getAvailable())
                .restaurant(restaurant)
                .build();


        return mapToResponse(foodRepository.save(food));
    }



    // GET ALL FOODS
    @Override
    public List<FoodResponse> getAllFoods() {

        return foodRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }



    // GET FOOD BY ID
    @Override
    public FoodResponse getFoodById(Long id) {

        Food food = foodRepository.findById(id)
                .orElseThrow(() -> 
                    new RuntimeException("Food not found")
                );


        return mapToResponse(food);
    }



    // GET FOODS BY RESTAURANT
    @Override
    public List<FoodResponse> getFoodsByRestaurant(Long restaurantId) {

        return foodRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }




    // UPDATE FOOD
    @Override
    public FoodResponse updateFood(Long id, FoodRequest request) {


        Food food = foodRepository.findById(id)
                .orElseThrow(() -> 
                    new RuntimeException("Food not found")
                );


        food.setFoodName(request.getFoodName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setImage(request.getImage());
        food.setCategory(request.getCategory());
        food.setAvailable(request.getAvailable());



        // Update restaurant only when restaurantId is provided
        if(request.getRestaurantId() != null) {

            Restaurant restaurant = restaurantRepository
                    .findById(request.getRestaurantId())
                    .orElseThrow(() ->
                        new RuntimeException("Restaurant not found")
                    );

            food.setRestaurant(restaurant);
        }


        Food updatedFood = foodRepository.save(food);


        return mapToResponse(updatedFood);
    }





    // DELETE FOOD
    @Override
    public void deleteFood(Long id) {


        if(!foodRepository.existsById(id)) {

            throw new RuntimeException("Food not found");
        }


        foodRepository.deleteById(id);
    }






    // ENTITY TO RESPONSE DTO
    private FoodResponse mapToResponse(Food food) {


        return FoodResponse.builder()

                .id(food.getId())

                .foodName(food.getFoodName())

                .description(food.getDescription())

                .price(food.getPrice())

                .image(food.getImage())

                .category(food.getCategory())

                .available(food.getAvailable())


                .restaurantId(
                    food.getRestaurant() != null ?
                    food.getRestaurant().getId() :
                    null
                )


                .restaurantName(
                    food.getRestaurant() != null ?
                    food.getRestaurant().getRestaurantName() :
                    null
                )


                .build();
    }

}