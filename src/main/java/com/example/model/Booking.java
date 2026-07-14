package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private User student;

    @Column(nullable = false)
    private LocalDateTime bookedAt = LocalDateTime.now();

    public Booking() {}

    public Booking(Reservation reservation, User student) {
        this.reservation = reservation;
        this.student = student;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }
}
