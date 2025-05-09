package com.doctor.dao;

import com.doctor.entity.Payment;
import com.doctor.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    public void savePayment(Payment payment) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(payment);
            session.flush();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackException) {
                    rollbackException.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                }
            }
        }
    }

    public Payment getPaymentById(Long id) {
        Session session = null;
        Payment payment = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            payment = session.get(Payment.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                }
            }
        }
        return payment;
    }

    public List<Payment> getPaymentsByAppointment(Long appointmentId) {
        Session session = null;
        List<Payment> payments = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "FROM Payment WHERE appointment.id = :appointmentId";
            payments = session.createQuery(hql, Payment.class)
                    .setParameter("appointmentId", appointmentId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                }
            }
        }
        return payments;
    }

    public void updatePaymentStatus(Long paymentId, String status) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Payment payment = session.get(Payment.class, paymentId);
            if (payment != null) {
                payment.setStatus(status);
                session.merge(payment);
                session.flush();
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackException) {
                    rollbackException.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception closeException) {
                    closeException.printStackTrace();
                }
            }
        }
    }
}