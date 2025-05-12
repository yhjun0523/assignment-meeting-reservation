package com.yhjun.meetingreservation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private MeetingRoom meetingRoom;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Reservation(User user, MeetingRoom room, LocalDateTime startTime, LocalDateTime endTime) {
        this.user = user;
        this.meetingRoom = room;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}