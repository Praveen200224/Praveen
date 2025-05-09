package com.doctor.dao;

import com.doctor.entity.Doctor;
import com.doctor.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class DoctorDAO {
    public void saveDoctor(Doctor doctor) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(doctor);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Doctor> getAllDoctors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Doctor", Doctor.class).list();
        }
    }

    public Doctor getDoctorById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Doctor.class, id);
        }
    }

    public void updateDoctor(Doctor doctor) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(doctor);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteDoctor(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Doctor doctor = session.get(Doctor.class, id);
            if (doctor != null) {
                session.delete(doctor);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Doctor> searchBySpecialization(String specialization) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Doctor WHERE specialization LIKE :spec";
            return session.createQuery(hql, Doctor.class)
                .setParameter("spec", "%" + specialization + "%")
                .list();
        }
    }

    public List<Doctor> searchByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Doctor WHERE name LIKE :name";
            return session.createQuery(hql, Doctor.class)
                .setParameter("name", "%" + name + "%")
                .list();
        }
    }

    public List<Doctor> searchByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Doctor WHERE email LIKE :email";
            return session.createQuery(hql, Doctor.class)
                .setParameter("email", "%" + email + "%")
                .list();
        }
    }
}