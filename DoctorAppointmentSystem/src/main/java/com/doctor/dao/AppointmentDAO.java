package com.doctor.dao;

import com.doctor.entity.Appointment;
import com.doctor.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentDAO {
    public void saveAppointment(Appointment appointment) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(appointment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Appointment> getAllAppointments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Appointment", Appointment.class).list();
        }
    }

    public Appointment getAppointmentById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Appointment.class, id);
        }
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Appointment where doctor.id = :doctorId", Appointment.class)
                    .setParameter("doctorId", doctorId)
                    .list();
        }
    }

    public boolean isDoctorAvailable(Long doctorId, LocalDateTime dateTime) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Appointment WHERE doctor.id = :doctorId AND appointmentDateTime = :dateTime";
            List<Appointment> appointments = session.createQuery(hql, Appointment.class)
                .setParameter("doctorId", doctorId)
                .setParameter("dateTime", dateTime)
                .list();
            return appointments.isEmpty();
        }
    }

    public List<Appointment> getDoctorSchedule(Long doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Appointment WHERE doctor.id = :doctorId AND appointmentDateTime >= :now ORDER BY appointmentDateTime";
            return session.createQuery(hql, Appointment.class)
                .setParameter("doctorId", doctorId)
                .setParameter("now", LocalDateTime.now())
                .list();
        }
    }

    public void updateAppointmentStatus(Long appointmentId, String status) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Appointment appointment = session.get(Appointment.class, appointmentId);
            if (appointment != null) {
                appointment.setStatus(status);
                session.update(appointment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteAppointment(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Appointment appointment = session.get(Appointment.class, id);
            if (appointment != null) {
                session.delete(appointment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Appointment appointment = session.get(Appointment.class, appointmentId);
            if (appointment != null) {
                appointment.setAppointmentDateTime(newDateTime);
                session.update(appointment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Appointment> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Appointment WHERE appointmentDateTime BETWEEN :start AND :end";
            return session.createQuery(hql, Appointment.class)
                .setParameter("start", startDate)
                .setParameter("end", endDate)
                .list();
        }
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Appointment a WHERE a.patient.id = :patientId ORDER BY a.appointmentDateTime";
            return session.createQuery(hql, Appointment.class)
                .setParameter("patientId", patientId)
                .list();
        }
    }

    public List<Appointment> getTodayAppointments() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return searchByDateRange(startOfDay, endOfDay);
    }
}