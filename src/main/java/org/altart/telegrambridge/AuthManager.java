package org.altart.telegrambridge;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AuthManager {
    private final List<String> loggedPlayers = new ArrayList<>();

    public boolean isLogged(Player player) {
        String nickname = player.getName();
        return loggedPlayers.contains(nickname);
    }

    public void login(String nickname) {
        if (!loggedPlayers.contains(nickname)) {
            loggedPlayers.add(nickname);
        }
    }

    public void logout(String nickname) {
        loggedPlayers.remove(nickname);
    }
}
