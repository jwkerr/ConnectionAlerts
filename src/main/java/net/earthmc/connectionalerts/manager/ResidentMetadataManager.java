package net.earthmc.connectionalerts.manager;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import net.earthmc.connectionalerts.object.AlertLevel;

public class ResidentMetadataManager {
    private final String alertLevelKey = "connectionalerts_alert_level";

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
}
