package com.example.BattleshipBack.Controllers;

import com.example.BattleshipBack.Dao.BoardDao;
import com.example.BattleshipBack.Dao.PlayerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class SocketController {
    private static Logger logger = Logger.getAnonymousLogger();
    private Integer counter = 0;
    @Autowired
    private PlayerDao playerDao;

    @Autowired
    private BoardDao boardDao;

    public SocketController(PlayerDao playerDao, BoardDao boardDao) {
        this.playerDao = playerDao;
        this.boardDao = boardDao;
    }

    // update player 1 for attacking board and player 2 for attacking
    @MessageMapping("/sent-from-player1")
    @SendTo("/topic/player1")
    public String receiveAndSend(String fromClient) throws Exception {
        logger.log(Level.INFO, "received " + fromClient );
        return (fromClient);
    }

    @MessageMapping("/message-from-player1")
    @SendTo("/topic/player1-message")
    public String receivedMessageAndSend(String fromPlayer) throws Exception {
        logger.log(Level.INFO, "received message " + fromPlayer);
        return (fromPlayer);
    }

    @MessageMapping("/sent-from-player2")
    @SendTo("/topic/player2")
    public String sendNow(String fromClient) throws Exception {
        logger.log(Level.INFO, "received " + fromClient);
        return (fromClient);
    }

    @MessageMapping("/message-from-player2")
    @SendTo("/topic/player2-message")
    public String receivedMessageAndSendNow(String fromPlayer) throws Exception {
        logger.log(Level.INFO, "received message " + fromPlayer);
        return (fromPlayer);
    }

    @MessageMapping("/player1-win")
    @SendTo("/topic/player1-has-won")
    public String receivedWinSend(String fromPlayer) throws Exception {
        logger.log(Level.INFO, "received message " + fromPlayer);
        return (fromPlayer);
    }

    @MessageMapping("/player2-win")
    @SendTo("/topic/player2-has-won")
    public String receivedWinS(String fromPlayer) throws Exception {
        logger.log(Level.INFO, "received message " + fromPlayer);
        return (fromPlayer);
    }
}
