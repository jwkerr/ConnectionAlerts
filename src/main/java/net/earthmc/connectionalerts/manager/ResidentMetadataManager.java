package net.earthmc.connectionalerts.manager;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import net.earthmc.connectionalerts.object.AlertLevel;

public class ResidentMetadataManager {
    private final String alertLevelKey = "connectionalerts_alert_level";
    private final String shouldAlertForFriendsKey = "connectionalerts_should_alert_for_friends";
    private final String shouldAlertForPartyKey = "connectionalerts_should_alert_for_party";

    public void setResidentAlertLevel(Resident resident, AlertLevel alertLevel) {
        if (!resident.hasMeta(alertLevelKey))
            resident.addMetaData(new StringDataField(alertLevelKey, null));

        StringDataField sdf = (StringDataField) resident.getMetadata(alertLevelKey);
        if (sdf == null) return;

        sdf.setValue(alertLevel.toString());
        resident.addMetaData(sdf);
    }

    public AlertLevel getResidentAlertLevel(Resident resident) {
        if (resident == null) return null;

        StringDataField sdf = (StringDataField) resident.getMetadata(alertLevelKey);
        if (sdf == null) return AlertLevel.NONE;

        return AlertLevel.getAlertLevelByName(sdf.getValue());
    }

    public void setShouldAlertForFriends(Resident resident, boolean value) {
        if (!resident.hasMeta(shouldAlertForFriendsKey))
            resident.addMetaData(new BooleanDataField(shouldAlertForFriendsKey, null));

        BooleanDataField bdf = (BooleanDataField) resident.getMetadata(shouldAlertForFriendsKey);
        if (bdf == null) return;

        bdf.setValue(value);
        resident.addMetaData(bdf);
    }

    public boolean getShouldAlertForFriends(Resident resident) {
        if (resident == null) return false;

        BooleanDataField bdf = (BooleanDataField) resident.getMetadata(shouldAlertForFriendsKey);
        if (bdf == null) return false;

        return bdf.getValue();
    }

    public void setShouldAlertForParty(Resident resident, boolean value) {
        if (!resident.hasMeta(shouldAlertForPartyKey))
            resident.addMetaData(new BooleanDataField(shouldAlertForPartyKey, null));

        BooleanDataField bdf = (BooleanDataField) resident.getMetadata(shouldAlertForPartyKey);
        if (bdf == null) return;

        bdf.setValue(value);
        resident.addMetaData(bdf);
    }

    public boolean getShouldAlertForParty(Resident resident) {
        if (resident == null) return false;

        BooleanDataField bdf = (BooleanDataField) resident.getMetadata(shouldAlertForPartyKey);
        if (bdf == null) return false;
        return bdf.getValue();
    }
}
