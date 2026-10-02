package explore.models;

import explore.exceptions.IncorrectRequestError;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static State of(String stateString) {
        for (State state : values()) {
            if (stateString.equalsIgnoreCase(state.toString())) {
                return state;
            }
        }
        throw new IncorrectRequestError("Incorrect state value");
    }
}
