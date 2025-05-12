package com.yhjun.meetingreservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhjun.meetingreservation.domain.MeetingRoom;
import com.yhjun.meetingreservation.domain.User;
import com.yhjun.meetingreservation.dto.ReservationRequest;
import com.yhjun.meetingreservation.repository.MeetingRoomRepository;
import com.yhjun.meetingreservation.repository.ReservationRepository;
import com.yhjun.meetingreservation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private User user;
    private MeetingRoom room;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        userRepository.deleteAll();
        meetingRoomRepository.deleteAll();
        user = userRepository.save(new User("양현준", "yhjun@example.com"));
        room = meetingRoomRepository.save(new MeetingRoom("회의실 A"));
    }

    @Test
    void createReservation_success() throws Exception {
        ReservationRequest req = new ReservationRequest(user.getId(), room.getId(), LocalDateTime.of(2025, 5, 13, 10, 0), LocalDateTime.of(2025, 5, 13, 11, 0));
        mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.user.id").value(user.getId())).andExpect(jsonPath("$.meetingRoom.id").value(room.getId()));
    }

    @Test
    void createReservation_invalidTimeSlot_returns400() throws Exception {
        // 1) DTO 객체 생성
        ReservationRequest badRequest = new ReservationRequest(user.getId(), room.getId(), LocalDateTime.of(2025, 5, 13, 10, 15), LocalDateTime.of(2025, 5, 13, 11, 15));

        // 2) 객체 → JSON 문자열 변환
        String badJson = objectMapper.writeValueAsString(badRequest);

        // 3) MockMvc 요청
        mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON).content(badJson)).andExpect(status().isBadRequest());  // 400만 검증
    }
}
