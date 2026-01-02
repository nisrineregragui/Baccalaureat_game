package DAO;

import com.example.java_project.database.HibernateUtil;
import models.Category;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CategoryDAO {

    public Category getCategory(int category_id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(Category.class, category_id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Category> getCategories() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Category", Category.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ NEW METHOD
    public void initCategoriesIfEmpty() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Long count = session
                    .createQuery("SELECT COUNT(c) FROM Category c", Long.class)
                    .uniqueResult();

            if (count == 0) {
                System.out.println("Initialisation des catégories...");

                session.persist(new Category("Ville"));
                session.persist(new Category("Pays"));
                session.persist(new Category("Animal"));
                session.persist(new Category("Métier"));
                session.persist(new Category("Fruit"));

                System.out.println("Catégories insérées");
            } else {
                System.out.println(" Catégories déjà existantes");
            }

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
