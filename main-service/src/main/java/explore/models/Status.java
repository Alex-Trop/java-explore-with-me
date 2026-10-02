package explore.models;

import explore.exceptions.IncorrectRequestError;

public enum Status {
    CONFIRMED,
    REJECTED,
    PENDING,
    CANCELED;

    public static Status of(String statusString) {
        for (Status status : values()) {
            if (statusString.equalsIgnoreCase(status.toString())) {
                return status;
            }
        }
        throw new IncorrectRequestError("Incorrect status value");
    }
}
