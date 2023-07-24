package com.epam.esm.gift.model.exception;

public class ResourceNotFoundException extends RuntimeException{
    private final int resourceId;

    public ResourceNotFoundException(int resourceId) {
        this.resourceId = resourceId;
    }

    public long getResourceId() {
        return resourceId;
    }
}
