package com.yhjun.meetingreservation.service;

import com.yhjun.meetingreservation.domain.MeetingRoom;
import com.yhjun.meetingreservation.domain.Reservation;
import com.yhjun.meetingreservation.domain.User;
import com.yhjun.meetingreservation.dto.ReservationRequest;
import com.yhjun.meetingreservation.repository.MeetingRoomRepository;
import com.yhjun.meetingreservation.repository.ReservationRepository;
import com.yhjun.meetingreservation.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ReservationService.class)
class ReservationServiceTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private ReservationService reservationService;
    private User user;
    private MeetingRoom room;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(reservationRepository, userRepository, entityManager);
        user = userRepository.save(new User("양현준", "yhjun@example.com"));
        room = meetingRoomRepository.save(new MeetingRoom("회의실 A"));
    }

    @Test
    void reserve_success() {
        LocalDateTime start = LocalDateTime.of(2025, 5, 13, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 5, 13, 11, 0);
        ReservationRequest req = new ReservationRequest(user.getId(), room.getId(), start, end);

        Reservation saved = reservationService.reserve(req);

        assertNotNull(saved.getId());
        assertEquals(user.getId(), saved.getUser().getId());
        assertEquals(room.getId(), saved.getMeetingRoom().getId());
        assertEquals(start, saved.getStartTime());
        assertEquals(end, saved.getEndTime());
    }

    @Test
    void reserve_overlap_throws() {
        reservationService.reserve(new ReservationRequest(user.getId(), room.getId(), LocalDateTime.of(2025, 5, 13, 10, 0), LocalDateTime.of(2025, 5, 13, 11, 0)));

        ReservationRequest badReq = new ReservationRequest(user.getId(), room.getId(), LocalDateTime.of(2025, 5, 13, 10, 30), LocalDateTime.of(2025, 5, 13, 11, 30));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> reservationService.reserve(badReq));
        assertEquals("해당 시간에 이미 예약이 존재합니다.", ex.getMessage());
    }

    @Test
    void reserve_startAfterEnd_throws() {
        ReservationRequest req = new ReservationRequest(user.getId(), room.getId(), LocalDateTime.of(2025, 5, 13, 12, 0), LocalDateTime.of(2025, 5, 13, 11, 0));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> reservationService.reserve(req));
        assertEquals("예약 시작 시간은 종료 시간보다 빨라야 합니다.", ex.getMessage());
    }

    @Test
    void reserve_adjacentNoOverlap_success() {
        reservationService.reserve(new ReservationRequest(user.getId(), room.getId(), LocalDateTime.of(2025, 5, 13, 10, 0), LocalDateTime.of(2025, 5, 13, 11, 0)));

        Reservation saved = reservationService.reserve(new ReservationRequest(user.getId(), room.getId(), LocalDateTime.of(2025, 5, 13, 11, 0), LocalDateTime.of(2025, 5, 13, 12, 0)));
        assertNotNull(saved.getId());
    }
}
