package DAO;

import com.example.java_project.database.HibernateUtil;
import models.Category;
import org.hibernate.Session;

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
}
