package com.yhjun.meetingreservation;

import com.yhjun.meetingreservation.domain.MeetingRoom;
import com.yhjun.meetingreservation.domain.User;
import com.yhjun.meetingreservation.dto.ReservationRequest;
import com.yhjun.meetingreservation.repository.MeetingRoomRepository;
import com.yhjun.meetingreservation.repository.ReservationRepository;
import com.yhjun.meetingreservation.repository.UserRepository;
import com.yhjun.meetingreservation.service.ReservationService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MeetingRoomRepository meetingRoomRepository;

    @Autowired
    EntityManager em;

    @Test
    void shouldHandleConcurrentReservationAttempts() throws InterruptedException {
        User user1 = userRepository.save(new User("user1", "user1@example.com"));
        User user2 = userRepository.save(new User("user2", "user2@example.com"));
        MeetingRoom room = meetingRoomRepository.save(new MeetingRoom("회의실 A"));

        LocalDateTime start = LocalDateTime.of(2025, 5, 14, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 5, 14, 11, 0);

        int threadCount = 2;
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        Runnable reserveTask1 = () -> {
            try {
                reservationService.reserve(new ReservationRequest(user1.getId(), room.getId(), start, end));
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                latch.countDown();
            }
        };

        Runnable reserveTask2 = () -> {
            try {
                reservationService.reserve(new ReservationRequest(user2.getId(), room.getId(), start, end));
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                latch.countDown();
            }
        };

        new Thread(reserveTask1).start();
        new Thread(reserveTask2).start();

        latch.await();

        assertEquals(1, reservationRepository.findAll().size(), "Only one reservation should succeed");
        assertEquals(1, exceptions.size(), "One reservation should fail due to conflict");
        assertInstanceOf(IllegalStateException.class, exceptions.get(0));
    }
}
