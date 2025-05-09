package com.doctor.dao;

import com.doctor.entity.Patient;
import com.doctor.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class PatientDAO {
    public void savePatient(Patient patient) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(patient);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Patient> getAllPatients() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Patient", Patient.class).list();
        }
    }

    public Patient getPatientById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Patient.class, id);
        }
    }

    public void updatePatient(Patient patient) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(patient);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deletePatient(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Patient patient = session.get(Patient.class, id);
            if (patient != null) {
                session.delete(patient);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Patient> searchByNameOrEmail(String searchTerm) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Patient WHERE name LIKE :term OR email LIKE :term";
            return session.createQuery(hql, Patient.class)
                .setParameter("term", "%" + searchTerm + "%")
                .list();
        }
    }

    public List<Patient> searchByAddress(String address) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Patient WHERE address LIKE :address";
            return session.createQuery(hql, Patient.class)
                .setParameter("address", "%" + address + "%")
                .list();
        }
    }

    public List<Patient> searchByPhone(String phoneNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Patient WHERE phoneNumber LIKE :phone";
            return session.createQuery(hql, Patient.class)
                .setParameter("phone", "%" + phoneNumber + "%")
                .list();
        }
    }
}