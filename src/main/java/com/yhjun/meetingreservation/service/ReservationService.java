package com.yhjun.meetingreservation.service;

import com.yhjun.meetingreservation.domain.MeetingRoom;
import com.yhjun.meetingreservation.domain.Reservation;
import com.yhjun.meetingreservation.domain.User;
import com.yhjun.meetingreservation.dto.ReservationRequest;
import com.yhjun.meetingreservation.repository.ReservationRepository;
import com.yhjun.meetingreservation.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            EntityManager entityManager
    ) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public Reservation reserve(ReservationRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 비관적 락 걸어서 회의실 조회
        MeetingRoom room = entityManager.find(MeetingRoom.class, request.meetingRoomId(), LockModeType.PESSIMISTIC_WRITE);
        if (room == null) {
            throw new IllegalArgumentException("회의실 없음");
        }

        if (!request.startTime().isBefore(request.endTime())) {
            throw new IllegalArgumentException("예약 시작 시간은 종료 시간보다 빨라야 합니다.");
        }

        boolean isDuplicate = reservationRepository
                .existsByMeetingRoomAndStartTimeLessThanAndEndTimeGreaterThan(
                        room, request.endTime(), request.startTime()
                );

        if (isDuplicate) {
            throw new IllegalStateException("해당 시간에 이미 예약이 존재합니다.");
        }

        return reservationRepository.save(
                new Reservation(user, room, request.startTime(), request.endTime())
        );
    }

    @Transactional(readOnly = true)
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reservation> getByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return reservationRepository.findAllByUser(user);
    }
}
