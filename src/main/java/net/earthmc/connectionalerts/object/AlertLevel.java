package net.earthmc.connectionalerts.object;

public enum AlertLevel {
    NONE,
    FRIEND,
    TOWN,
    NATION,
    ALL;

    public static AlertLevel getAlertLevelByName(String name) {
        for (AlertLevel alertLevel : values()) {
            if (alertLevel.toString().equalsIgnoreCase(name)) return alertLevel;
        }

        return null;
    }
}
