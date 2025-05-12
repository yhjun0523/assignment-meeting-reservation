package com.yhjun.meetingreservation.repository;

import com.yhjun.meetingreservation.domain.MeetingRoom;
import com.yhjun.meetingreservation.domain.Reservation;
import com.yhjun.meetingreservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByMeetingRoomAndStartTimeLessThanAndEndTimeGreaterThan(
            MeetingRoom meetingRoom, LocalDateTime endTime, LocalDateTime startTime
    );

    List<Reservation> findByMeetingRoom(MeetingRoom room);

    List<Reservation> findAllByUser(User user);
}