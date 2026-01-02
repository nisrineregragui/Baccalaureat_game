package sockets;

import DAO.CategoryDAO;

public class RunServer {
    public static void main(String[] args) {
        CategoryDAO categoryDAO = new CategoryDAO();
        categoryDAO.initCategoriesIfEmpty(); 
        GameServer server = new GameServer(12345);
        server.startServer();

    }
}