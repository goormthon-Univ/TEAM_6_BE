package com.LikeCloud.LikeCloud.controller;

import com.LikeCloud.LikeCloud.domain.entity.User;
import com.LikeCloud.LikeCloud.dto.YearPlanRequestDto;
import com.LikeCloud.LikeCloud.repository.UserRepository;
import com.LikeCloud.LikeCloud.service.YearPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class YearPlanController {

    private final YearPlanService yearPlanService;

    private final UserRepository userRepository;

    @PostMapping("/YearPlans")
    public ResponseEntity<?> postYearPlan(@RequestHeader("userId") Integer userId, @RequestBody YearPlanRequestDto yearPlanRequestDto) {
        try {
            // UserRepository를 사용하여 실제로 존재하는 유저를 조회
            User user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new RuntimeException("해당 아이디의 유저를 찾을 수 없습니다."));

            yearPlanService.save(user.getId().intValue() ,yearPlanRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Year Plan이 성공적으로 저장되었습니다!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Year Plan 저장 중 오류 발생: " + e.getMessage());
        }
    }
}

