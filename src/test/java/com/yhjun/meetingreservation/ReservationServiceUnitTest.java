package com.yhjun.meetingreservation;

import com.yhjun.meetingreservation.domain.MeetingRoom;
import com.yhjun.meetingreservation.domain.Reservation;
import com.yhjun.meetingreservation.domain.User;
import com.yhjun.meetingreservation.dto.ReservationRequest;
import com.yhjun.meetingreservation.repository.MeetingRoomRepository;
import com.yhjun.meetingreservation.repository.ReservationRepository;
import com.yhjun.meetingreservation.repository.UserRepository;
import com.yhjun.meetingreservation.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceUnitTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    private User mockUser;
    private MeetingRoom mockRoom;

    @BeforeEach
    void setup() {
        mockUser = new User("user1", "user1@example.com");
        mockUser.setId(1L);
        mockRoom = new MeetingRoom("Room A");
        mockRoom.setId(1L);
    }

    @Test
    void shouldThrowExceptionIfStartTimeIsAfterEndTime() {
        // given
        ReservationRequest request = new ReservationRequest(
                1L, 1L,
                LocalDateTime.of(2025, 5, 14, 12, 0),
                LocalDateTime.of(2025, 5, 14, 10, 0) // 종료 < 시작
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));

        // when + then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.reserve(request)
        );

        assertEquals("예약 시작 시간은 종료 시간보다 빨라야 합니다.", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionIfDuplicateReservationExists() {
        // given
        LocalDateTime start = LocalDateTime.of(2025, 5, 14, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 5, 14, 11, 0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));
        when(reservationRepository.existsByMeetingRoomAndStartTimeLessThanAndEndTimeGreaterThan(mockRoom, end, start))
                .thenReturn(true); // 중복 존재

        ReservationRequest request = new ReservationRequest(1L, 1L, start, end);

        // when + then
        assertThrows(IllegalStateException.class, () -> reservationService.reserve(request));
    }

    @Test
    void shouldReserveSuccessfullyIfNoConflict() {
        // given
        LocalDateTime start = LocalDateTime.of(2025, 5, 14, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 5, 14, 11, 0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(meetingRoomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));
        when(reservationRepository.existsByMeetingRoomAndStartTimeLessThanAndEndTimeGreaterThan(mockRoom, end, start))
                .thenReturn(false);

        Reservation savedReservation = new Reservation(mockUser, mockRoom, start, end);
        savedReservation.setId(100L);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationRequest request = new ReservationRequest(1L, 1L, start, end);

        // when
        Reservation result = reservationService.reserve(request);

        // then
        assertNotNull(result);
        assertEquals(100L, result.getId());
    }
}
