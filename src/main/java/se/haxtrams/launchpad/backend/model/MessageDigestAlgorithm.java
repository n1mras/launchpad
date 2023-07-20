package se.haxtrams.launchpad.backend.model;

public enum MessageDigestAlgorithm {
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    MD5("MD5");

    private String value;

    MessageDigestAlgorithm(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
