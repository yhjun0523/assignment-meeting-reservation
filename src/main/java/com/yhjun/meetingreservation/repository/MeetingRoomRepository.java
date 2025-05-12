package com.yhjun.meetingreservation.repository;

import com.yhjun.meetingreservation.domain.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
}