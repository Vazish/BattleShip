package com.example.battleship.Service;

import com.example.battleship.Model.Player;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    private List<Player>  players;
    private String errorMessage;
    private Boolean success;

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}


