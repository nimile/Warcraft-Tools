package de.haevn.ui.widgets.settings;

import de.haevn.abstraction.IController;
import de.haevn.abstraction.IModel;
import de.haevn.abstraction.IView;
import de.haevn.api.DiscordApi;
import de.haevn.utils.AlertUtils;
import de.haevn.utils.NetworkUtils;
import de.haevn.utils.PropertyHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SettingsController implements IController {

    private final StringProperty webhookLog = new SimpleStringProperty();

    @Override
    public void link(IView view, IModel model) {
        if (!(view instanceof SettingsView settingsView)) {
            throw new IllegalArgumentException("View is not of type SettingsView");
        }

        settingsView.getWebhookLogProperty().bindBidirectional(webhookLog);
        settingsView.addOnWebhookLogAction(e -> onWebhookLogUpdate());
        PropertyHandler.getInstance("config").getOptional(DiscordApi.LOG_KEY).ifPresent(webhookLog::set);

    }

    private void onWebhookLogUpdate() {
        if (NetworkUtils.isUrl(webhookLog.get()) || webhookLog.get().isEmpty()) {
            PropertyHandler.getInstance("config").set(DiscordApi.LOG_KEY, webhookLog.get());
            PropertyHandler.getInstance("config").set(DiscordApi.RECORDING_KEY, webhookLog.get());
            PropertyHandler.getInstance("config").set(DiscordApi.PLAYER_LOOKUP_KEY, webhookLog.get());
        } else {
            AlertUtils.showError("Invalid URL", "The URL you entered is not valid.");
        }
    }
}
