package DAO;

import com.example.java_project.database.HibernateUtil;
import models.Word;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class WordDAO {
    //methode to check if the word exists in the DB
        public boolean exists(String word, int category_id) {
            try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            String requete="Select count(w) from Word w where w.value = :val and w.category.id = :catId";
            Query<Long> query=session.createQuery(requete, Long.class);
            query.setParameter("val", word.toLowerCase().trim());
            query.setParameter("catId", category_id);

            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


// methode to save a word in DB
public void save(Word word)
        {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.persist(word);
                transaction.commit();
            }
            catch (Exception e) {
                if (transaction != null) transaction.rollback();
                e.printStackTrace();
            }
        }
}
