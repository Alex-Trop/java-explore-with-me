package dto;

import exceptions.NotFoundError;

import static exceptions.ErrorDetails.APP_NOT_FOUND_ERROR;

public enum App {
    EWM_MAIN_SERVICE("ewm-main-service");

    private final String appName;

    App(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return this.appName;
    }

    public static App of(String appName) {
        for (App app : values()) {
            if (app.toString().equalsIgnoreCase(appName)) {
                return app;
            }
        }
        throw new NotFoundError(APP_NOT_FOUND_ERROR);
    }
}