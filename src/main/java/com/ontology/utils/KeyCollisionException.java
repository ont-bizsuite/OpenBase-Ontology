package com.ontology.utils;

public class KeyCollisionException extends RuntimeException {
    private int id;
    private String sid;

    public KeyCollisionException() {
        super();
    }

    public KeyCollisionException(int id) {
        super();
        this.id = id;
        this.sid = "";
    }

    public KeyCollisionException(String sid) {
        super();
        this.id = -1;
        this.sid = sid;
    }

    public int getId() {
        return id;
    }

    public String getSid() {
        return sid;
    }

    @Override
    public String toString() {
        return String.format("Key collision on generation, id: #%d, sid: #%s", id, sid);
    }
}
