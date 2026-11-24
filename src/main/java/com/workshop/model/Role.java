package com.workshop.model;

public enum Role {
    WORKSHOP_SUPERVISOR("Workshop Supervisor / Admin"),
    INVENTORY_CLERK("Inventory Clerk"),
    SERVICE_TECHNICIAN("Service Technician");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
