package com.workshop.service;

import com.workshop.dao.UserDao;
import com.workshop.model.Role;
import com.workshop.model.User;
import com.workshop.util.SecurityUtil;

public class AuthService {
    private static AuthService instance;
    private final UserDao userDao = new UserDao();
    private User currentUser;

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && user.isActive()) {
            if (SecurityUtil.checkPassword(password, user.getPasswordHash())) {
                this.currentUser = user;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public boolean hasRole(Role... allowedRoles) {
        if (currentUser == null) return false;
        for (Role r : allowedRoles) {
            if (currentUser.getRole() == r) return true;
        }
        return false;
    }

    public boolean isSupervisor() {
        return hasRole(Role.WORKSHOP_SUPERVISOR);
    }

    public boolean isClerk() {
        return hasRole(Role.INVENTORY_CLERK);
    }

    public boolean isTechnician() {
        return hasRole(Role.SERVICE_TECHNICIAN);
    }
}
