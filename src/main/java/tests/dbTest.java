package tests;

import DAO.CategoryDAO;

public class dbTest {
    public static void main(String[] args) {
        try {
            CategoryDAO dao = new CategoryDAO();

            dao.initCategoriesIfEmpty();

            System.out.println("✅ DATABASE CONNECTED SUCCESSFULLY");
        } catch (Exception e) {
            System.out.println("❌ DATABASE CONNECTION FAILED");
            e.printStackTrace();
        }
    }
}
