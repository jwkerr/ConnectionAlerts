package net.earthmc.connectionalerts.manager;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import net.earthmc.connectionalerts.object.AlertLevel;

public class ResidentMetadataManager {

    private static ResidentMetadataManager instance;

    private static final String ALERT_LEVEL_KEY = "connectionalerts_alert_level";
    private static final String SHOULD_ALERT_FOR_FRIENDS_KEY = "connectionalerts_should_alert_for_friends";
    private static final String SHOULD_ALERT_FOR_PARTY_KEY = "connectionalerts_should_alert_for_party";

    private ResidentMetadataManager() {}

    public static ResidentMetadataManager getInstance() {
        if (instance == null) instance = new ResidentMetadataManager();
        return instance;
    }

    public void setResidentAlertLevel(Resident resident, AlertLevel alertLevel) {
        if (!resident.hasMeta(ALERT_LEVEL_KEY))
            resident.addMetaData(new StringDataField(ALERT_LEVEL_KEY, null));

        StringDataField sdf = (StringDataField) resident.getMetadata(ALERT_LEVEL_KEY);
        if (sdf == null) return;

        sdf.setValue(alertLevel.toString());
        resident.addMetaData(sdf);
    }

    public AlertLevel getResidentAlertLevel(Resident resident) {
        if (resident == null) return null;

        StringDataField sdf = (StringDataField) resident.getMetadata(ALERT_LEVEL_KEY);
        if (sdf == null) return AlertLevel.NONE;

        return AlertLevel.getAlertLevelByName(sdf.getValue());
    }

    public void setShouldAlertForFriends(Resident resident, boolean value) {
        if (!resident.hasMeta(SHOULD_ALERT_FOR_FRIENDS_KEY))
            resident.addMetaData(new BooleanDataField(SHOULD_ALERT_FOR_FRIENDS_KEY, null));

        BooleanDataField bdf = (BooleanDataField) resident.getMetadata(SHOULD_ALERT_FOR_FRIENDS_KEY);
        if (bdf == null) return;

        bdf.setValue(value);
        resident.addMetaData(bdf);
    }

    public boolean getShouldAlertForFriends(Resident resident) {
        if (resident == null) return false;

        BooleanDataField bdf = (BooleanDataField) resident.getMetadata(SHOULD_ALERT_FOR_FRIENDS_KEY);
        if (bdf == null) return false;

        return bdf.getValue();
    }

    public void setShouldAlertForParty(Resident resident, boolean value) {
        if (!resident.hasMeta(SHOULD_ALERT_FOR_PARTY_KEY))
            resident.addMetaData(new BooleanDataField(SHOULD_ALERT_FOR_PARTY_KEY, null));

        BooleanDataField bdf = (BooleanDataField) resident.getMetadata(SHOULD_ALERT_FOR_PARTY_KEY);
        if (bdf == null) return;

        bdf.setValue(value);
        resident.addMetaData(bdf);
    }

    public boolean getShouldAlertForParty(Resident resident) {
        if (resident == null) return false;

        BooleanDataField bdf = (BooleanDataField) resident.getMetadata(SHOULD_ALERT_FOR_PARTY_KEY);
        if (bdf == null) return false;
        return bdf.getValue();
    }
}
