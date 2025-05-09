package com.doctor.util;

import com.doctor.dao.AppointmentDAO;
import com.doctor.entity.Appointment;
import com.doctor.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;

public class ReportGenerator {
    private AppointmentDAO appointmentDAO;

    public ReportGenerator(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    public void generateDailyReport(LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<Appointment> appointments = appointmentDAO.searchByDateRange(startOfDay, endOfDay);

        System.out.println("\n=== Daily Appointment Report for " + date.toLocalDate() + " ===");
        System.out.println("Total Appointments: " + appointments.size());
        printAppointmentStatistics(appointments);
    }

    public void generateWeeklyReport(LocalDateTime date) {
        LocalDateTime startOfWeek = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
        List<Appointment> appointments = appointmentDAO.searchByDateRange(startOfWeek, endOfWeek);

        System.out.println("\n=== Weekly Appointment Report ===");
        System.out.println("Week Starting: " + startOfWeek.toLocalDate());
        System.out.println("Total Appointments: " + appointments.size());
        printAppointmentStatistics(appointments);
    }

    public void generateMonthlyReport(LocalDateTime date) {
        LocalDateTime startOfMonth = date.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        List<Appointment> appointments = appointmentDAO.searchByDateRange(startOfMonth, endOfMonth);

        System.out.println("\n=== Monthly Appointment Report ===");
        System.out.println("Month: " + startOfMonth.getMonth() + " " + startOfMonth.getYear());
        System.out.println("Total Appointments: " + appointments.size());
        printAppointmentStatistics(appointments);
    }

    private void printAppointmentStatistics(List<Appointment> appointments) {
        Map<String, Long> statusStats = appointments.stream()
            .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));
        
        System.out.println("\nStatus Statistics:");
        statusStats.forEach((status, count) -> 
            System.out.println(status + ": " + count));

        Map<String, Long> doctorStats = appointments.stream()
            .collect(Collectors.groupingBy(apt -> apt.getDoctor().getName(), Collectors.counting()));
        
        System.out.println("\nAppointments per Doctor:");
        doctorStats.forEach((doctorName, count) -> 
            System.out.println(doctorName + ": " + count));
    }

 // ... existing code ...

    public void generatePaymentReport(LocalDateTime startDate, LocalDateTime endDate) {
        System.out.println("\n=== Payment Report from " + startDate.toLocalDate() + " to " + endDate.toLocalDate() + " ===");
        
        String hql = "SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Payment> payments = session.createQuery(hql, Payment.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .list();

            BigDecimal totalAmount = BigDecimal.ZERO;
            Map<String, Integer> methodCount = new HashMap<>();
            Map<String, Integer> statusCount = new HashMap<>();

            System.out.println("\nPayment Details:");
            System.out.println("----------------");
            for (Payment payment : payments) {
                System.out.printf("Transaction ID: %s\n", payment.getTransactionId());
                System.out.printf("Amount: $%.2f\n", payment.getAmount());
                System.out.printf("Method: %s\n", payment.getPaymentMethod());
                System.out.printf("Status: %s\n", payment.getStatus());
                System.out.printf("Date: %s\n", payment.getPaymentDate());
                System.out.println("----------------");

                totalAmount = totalAmount.add(payment.getAmount());
                methodCount.merge(payment.getPaymentMethod(), 1, Integer::sum);
                statusCount.merge(payment.getStatus(), 1, Integer::sum);
            }

            System.out.println("\nSummary:");
            System.out.printf("Total Payments: %d\n", payments.size());
            System.out.printf("Total Amount: $%.2f\n", totalAmount);
            
            System.out.println("\nPayment Methods:");
            methodCount.forEach((method, count) -> 
                System.out.printf("%s: %d payments\n", method, count));

            System.out.println("\nPayment Status:");
            statusCount.forEach((status, count) -> 
                System.out.printf("%s: %d payments\n", status, count));
        }
    }

    // ... existing code ...
    
    public void generatePatientHistory(Long patientId) {
        List<Appointment> appointments = appointmentDAO.getPatientAppointments(patientId);

        if (!appointments.isEmpty()) {
            System.out.println("\n=== Patient Visit History ===");
            System.out.println("Patient: " + appointments.get(0).getPatient().getName());
            System.out.println("Total Visits: " + appointments.size());
            
            appointments.forEach(apt -> {
                System.out.println("\nVisit Date: " + apt.getAppointmentDateTime());
                System.out.println("Doctor: " + apt.getDoctor().getName());
                System.out.println("Status: " + apt.getStatus());
            });
        } else {
            System.out.println("\nNo appointments found for patient ID: " + patientId);
        }
    }
}