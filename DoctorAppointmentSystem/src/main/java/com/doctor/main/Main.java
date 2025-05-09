package com.doctor.main;

import com.doctor.dao.*;
import com.doctor.entity.*;
import com.doctor.util.ReportGenerator;
import com.doctor.util.ExportUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        DoctorDAO doctorDAO = new DoctorDAO();
        PatientDAO patientDAO = new PatientDAO();
        AppointmentDAO appointmentDAO = new AppointmentDAO();

        // Create and save a doctor
        Doctor doctor = new Doctor();
        doctor.setName("Dr. Sarah Smith");
        doctor.setSpecialization("Pediatrics");
        doctor.setEmail("sarah.smith@hospital.com");
        doctor.setPhoneNumber("555-0123");
        doctorDAO.saveDoctor(doctor);
        doctor = doctorDAO.getDoctorById(1L);

        Doctor doctor2 = new Doctor();
        doctor2.setName("Dr. James Wilson");
        doctor2.setSpecialization("Cardiology");
        doctor2.setEmail("james.wilson@hospital.com");
        doctor2.setPhoneNumber("555-4567");
        doctorDAO.saveDoctor(doctor2);
        doctor2 = doctorDAO.getDoctorById(2L);

        // Create and save patients
        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setEmail("john.doe@email.com");
        patient.setPhoneNumber("555-8901");
        patient.setAddress("123 Main St");
        patientDAO.savePatient(patient);
        patient = patientDAO.getPatientById(1L);

        Patient patient2 = new Patient();
        patient2.setName("Jane Smith");
        patient2.setEmail("jane.smith@email.com");
        patient2.setPhoneNumber("555-2345");
        patient2.setAddress("456 Oak Ave");
        patientDAO.savePatient(patient2);
        patient2 = patientDAO.getPatientById(2L);

        // Create appointments
        Appointment appointment1 = new Appointment();
        appointment1.setDoctor(doctor);
        appointment1.setPatient(patient);
        appointment1.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
        appointment1.setStatus("SCHEDULED");
        appointmentDAO.saveAppointment(appointment1);
        appointment1 = appointmentDAO.getAppointmentById(1L);

        Appointment appointment2 = new Appointment();
        appointment2.setDoctor(doctor2);
        appointment2.setPatient(patient2);
        appointment2.setAppointmentDateTime(LocalDateTime.now().plusDays(2));
        appointment2.setStatus("SCHEDULED");
        appointmentDAO.saveAppointment(appointment2);
        appointment2 = appointmentDAO.getAppointmentById(2L);

        // Test Search Features
        System.out.println("\n=== Testing Search Features ===");
        System.out.println("\nSearching doctors by specialization:");
        List<Doctor> cardiologists = doctorDAO.searchBySpecialization("Cardiology");
        cardiologists.forEach(System.out::println);
        
        System.out.println("\nSearching patients by name or email:");
        List<Patient> smithPatients = patientDAO.searchByNameOrEmail("Smith");
        smithPatients.forEach(System.out::println);
        
        System.out.println("\nSearching patients by address:");
        List<Patient> oakPatients = patientDAO.searchByAddress("Oak");
        oakPatients.forEach(System.out::println);

        // Test Advanced Appointment Features
        System.out.println("\n=== Testing Advanced Appointment Features ===");
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        System.out.println("\nAppointments in next 7 days:");
        List<Appointment> weekAppointments = appointmentDAO.searchByDateRange(startDate, endDate);
        weekAppointments.forEach(System.out::println);

        System.out.println("\nToday's Appointments:");
        List<Appointment> todayAppointments = appointmentDAO.getTodayAppointments();
        todayAppointments.forEach(System.out::println);

        System.out.println("\nPatient Appointment History:");
        List<Appointment> patientHistory = appointmentDAO.getPatientAppointments(patient.getId());
        patientHistory.forEach(System.out::println);

        // Test Report Generation
        System.out.println("\n=== Testing Report Generation ===");
        ReportGenerator reportGenerator = new ReportGenerator(appointmentDAO);
        reportGenerator.generateDailyReport(LocalDateTime.now());
        reportGenerator.generateWeeklyReport(LocalDateTime.now());
        reportGenerator.generateMonthlyReport(LocalDateTime.now());
        reportGenerator.generatePatientHistory(patient.getId());

        // Test Export Features
        System.out.println("\n=== Testing Export Features ===");
        // Create reports directory if it doesn't exist
        new File("reports").mkdirs();
        
        List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
        ExportUtil.exportToPDF(allAppointments, "reports\\appointments_report.pdf");
        System.out.println("PDF Report generated successfully");
        ExportUtil.exportToExcel(allAppointments, "reports\\appointments_report.xlsx");
        System.out.println("Excel Report generated successfully");

        // Test Appointment Updates
        System.out.println("\n=== Testing Appointment Updates ===");
        System.out.println("\nRescheduling appointment:");
        appointmentDAO.rescheduleAppointment(appointment1.getId(), LocalDateTime.now().plusDays(5));
        System.out.println("Updated appointment: " + appointmentDAO.getAppointmentById(appointment1.getId()));

        System.out.println("\nCancelling appointment:");
        appointmentDAO.updateAppointmentStatus(appointment2.getId(), "CANCELLED");
        System.out.println("Cancelled appointment: " + appointmentDAO.getAppointmentById(appointment2.getId()));

        // Test Payment Processing
        System.out.println("\n=== Testing Payment Processing ===");
        Payment payment1 = new Payment();
        payment1.setAppointment(appointment1);
        payment1.setAmount(new BigDecimal("100.00"));
        payment1.setPaymentMethod("CREDIT_CARD");
        payment1.setStatus("PENDING");
        payment1.setPaymentDate(LocalDateTime.now());
        payment1.setTransactionId("TXN" + System.currentTimeMillis());

        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.savePayment(payment1);
        System.out.println("Payment processed: " + payment1.getTransactionId());

        // Test payment status update
        System.out.println("\nUpdating payment status:");
        paymentDAO.updatePaymentStatus(payment1.getId(), "COMPLETED");
        System.out.println("Payment status updated");

        // Test Payment Report
        System.out.println("\n=== Testing Payment Report ===");
        LocalDateTime reportStartDate = LocalDateTime.now().minusDays(30);
        LocalDateTime reportEndDate = LocalDateTime.now().plusDays(1);
        reportGenerator.generatePaymentReport(reportStartDate, reportEndDate);

        // View payment history
        System.out.println("\nPayment history for appointment:");
        List<Payment> appointmentPayments = paymentDAO.getPaymentsByAppointment(appointment1.getId());
        appointmentPayments.forEach(System.out::println);
    }
}