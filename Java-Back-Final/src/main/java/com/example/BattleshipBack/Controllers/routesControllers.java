package com.example.BattleshipBack.Controllers;

import com.example.BattleshipBack.Dao.BoardDao;
import com.example.BattleshipBack.Dao.PlayerDao;
import com.example.BattleshipBack.Model.Board;
import com.example.BattleshipBack.Model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
public class routesControllers {
    private Boolean firstPlayerRegistered = false;
    private static Logger logger = Logger.getAnonymousLogger();
    private Boolean secondPlayerRegistered = false;
    private Boolean toggle = true;

    @Autowired
    private PlayerDao playerDao;

    @Autowired
    private BoardDao boardDao;

    public routesControllers(PlayerDao playerDao, BoardDao boardDao) {
        this.playerDao = playerDao;
        this.boardDao = boardDao;
    }

    @RequestMapping("/is-there-registered-player")
    public Boolean isThereYo() {

        if (firstPlayerRegistered && secondPlayerRegistered) {
                this.toggle = !this.toggle;
                return this.toggle;
        } else {
            Board playerBoard = randomizeBoard();
            this.firstPlayerRegistered = true;

            // Create first player
            Player player1 = new Player();
            player1.setName("Java");

            // Create Board and assign to player
            player1.setSomeBoard(playerBoard);
            boardDao.save(playerBoard);

            // Save Player and assigned board to database and return true for frontEnd response
            playerDao.save(player1);
            logger.log(Level.INFO, "Player 1 has been registered");

            Board playerBoard2 = randomizeBoard();

            // Create player
            Player player2 = new Player();
            player2.setName("Javascript");

            // Create Board and assign to player
            player2.setSomeBoard(playerBoard2);
            boardDao.save(playerBoard2);

            // Save Player and assigned board to database and return true for frontEnd response
            playerDao.save(player2);
            logger.log(Level.INFO, "Player 2 has been registered");
            this.secondPlayerRegistered = true;

            return true;
        }
    }

    @RequestMapping("/get-player-board")
    public Board getBoard(@RequestParam(value = "player") String name) {
        if (name.equals("player1")) {
            return playerDao.findOne(2).getSomeBoard();
        } else {
            return playerDao.findOne(4).getSomeBoard();
        }
    }

    public Board createEmptyBoard() {
        Board temp = new Board();
        HashMap<HashMap<Integer, Integer>, Integer> finalMap1 = new HashMap<>();
        HashMap<HashMap<Integer, Integer>, Integer> finalMap2 = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                HashMap<Integer, Integer> someMap = new HashMap<>();
                someMap.put(i, j);
                finalMap1.put(someMap, 0);
                finalMap2.put(someMap, 0);
            }
        }
        temp.setBoardArray(finalMap1);
        temp.setAttackBoardArray(finalMap2);
        return temp;
    }

    public Board randomizeBoard() {
        Board B = createEmptyBoard();

        Random rowOrCol = new Random();

        int configFive = rowOrCol.nextInt(2);

        int configFour = rowOrCol.nextInt(4);
        int configThree = rowOrCol.nextInt(4);
        int configTwo = rowOrCol.nextInt(4);
        int configOne = rowOrCol.nextInt(12);

        if (configFive == 0) { // row
            for(int i = 1; i < 6; i++) {
                B.changeAtPosition(1,i, 1, false);
            }
        } else { // column
            for (int i = 1; i < 6; i++) {
                B.changeAtPosition(i, 1, 1, false);
            }
        }

        if (configFive >= 1 && configFour == 0) { // row
            for(int i = 5; i < 9; i++) {
                B.changeAtPosition(8,i, 1, false);
            }
        } else { // column
            for (int i = 5; i < 9; i++) {
                B.changeAtPosition(i, 8, 1, false);
            }
        }

        if (configFour >= 1 && configThree == 0) { // row
            for(int i = 2; i < 5; i++) {
                B.changeAtPosition(7,i, 1, false);
            }
        } else { // column
            for (int i = 2; i < 5; i++) {
                B.changeAtPosition(i, 7, 1, false);
            }
        }

        if (configThree >= 1 && configTwo == 0) { // row
            for(int i = 3; i < 5; i++) {
                B.changeAtPosition(5,i, 1, false);
            }
        } else { // column
            for (int i = 3; i < 5; i++) {
                B.changeAtPosition(i, 5, 1, false);
            }
        }

        if (configOne <= 3) {
            B.changeAtPosition(0, 5, 1, false);
        } else if (configOne <= 6) {
            B.changeAtPosition(6, 0, 1, false);
        } else if (configOne <= 9) {
            B.changeAtPosition(9, 3, 1, false);
        } else {
            B.changeAtPosition(5, 9, 1, false);
        }

        return B;
    }


}
