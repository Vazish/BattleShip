package com.example.battleship.Controller;

import com.example.battleship.Dao.BoardDao;
import com.example.battleship.Dao.PlayerDao;
import com.example.battleship.Model.Board;
import com.example.battleship.Model.Player;
import com.example.battleship.Service.BoardService;
import com.example.battleship.Service.PlayerService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class PlayerController {
    public PlayerDao playerDao;
    public BoardDao boardDao;

    public PlayerController(PlayerDao playerDao, BoardDao boardDao) {
        this.playerDao = playerDao;
        this.boardDao = boardDao;
    }

    @RequestMapping("/createGame")
    public PlayerService createGame(@RequestParam(value = "firstPlayer", required = false, defaultValue = "Player 1") String player1, @RequestParam(name = "secondPlayer", required = false, defaultValue = "Player 2") String player2) {
        PlayerService playerService = new PlayerService();
        BoardService boardService = new BoardService();

        try {
            List<Player> players = new ArrayList<>();
            List<Board> boards = new ArrayList<>();

            Player playerOne = new Player();
            playerOne.setName(player1);
            Board board1 = new Board();
            Board board2 = new Board();

            HashMap<HashMap<Integer, Integer>, Integer> finalMap1 = new HashMap<>();
            HashMap<HashMap<Integer, Integer>, Integer> finalMap2 = new HashMap<>();


            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    HashMap<Integer, Integer> first = new HashMap<>();
                    HashMap<Integer, Integer> second = new HashMap<>();
                    first.put(i, j);
                    second.put(i, j);
                    finalMap1.put(first, 0);
                    finalMap2.put(second, 0);

                }
            }

            board1.setBoardArray(finalMap1);
            playerOne.setSomeBoard(board1);

            Player playerTwo = new Player();
            playerTwo.setName(player2);

            board2.setBoardArray(finalMap2);
            playerTwo.setSomeBoard(board2);

            players.add(playerDao.save(playerOne));
            players.add(playerDao.save(playerTwo));

            playerService.setPlayers(players);
            playerService.setSuccess(true);

        }catch (Exception e) {
            playerService.setErrorMessage(e.getMessage());
            playerService.setSuccess(false);
        }
        return playerService;
    }

    @RequestMapping("/getPlayer")
    public Board getPlayer(@RequestParam(value = "id", required = false) int id) {
        HashMap<HashMap<Integer, Integer>, Integer> finalMap1 = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                HashMap<Integer, Integer> first = new HashMap<>();
                HashMap<Integer, Integer> second = new HashMap<>();
                first.put(i, j);
                second.put(i, j);
                finalMap1.put(first, 0);

            }
        }

        Board newBoard = new Board();
        newBoard.setBoardArray(finalMap1);
        newBoard.changeAtPosition(0, 0, 555);
        return newBoard;
    }

    @RequestMapping("/placeShips")
    public Board placeShip(@RequestParam(value = "playerId") int playerId, @RequestParam(value = "shipType") String shipType, @RequestParam(value = "row") int row, @RequestParam(value = "column") int column, @RequestParam(value = "rowOrColumn") String rowOrColumn){
        Player player = playerDao.findById(playerId);
        Board board = player.getSomeBoard();

        if (shipType.equals("carrier")) {
            if (rowOrColumn.equals("row")) {
                for (int i = 0; i < 5; i++) {
                    board.changeAtPosition(row, column + i, 1);
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    board.changeAtPosition(row + i, column, 1);
                }
            }
        } else if (shipType == "battleship") {
            if (rowOrColumn.equals("row")) {
                for (int i = 0; i < 4; i++) {
                    board.changeAtPosition(row, column + i, 1);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    board.changeAtPosition(row + i, column, 1);
                }
            }

        } else if (shipType == "submarine") {
            if (rowOrColumn.equals("row")) {
                for (int i = 0; i < 3; i++) {
                    board.changeAtPosition(row, column + i, 1);
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    board.changeAtPosition(row + i, column, 1);
                }
            }

        } else if (shipType == "Destroyer") {
            if (rowOrColumn.equals("row")) {
                for (int i = 0; i < 2; i++) {
                    board.changeAtPosition(row, column + i, 1);
                }
            } else {
                for (int i = 0; i < 2; i++) {
                    board.changeAtPosition(row + i, column, 1);
                }
            }

        } else if (shipType == "patrol") {
            if (rowOrColumn.equals("row")) {
                for (int i = 0; i < 1; i++) {
                    board.changeAtPosition(row, column + i, 1);
                }
            } else {
                for (int i = 0; i < 1; i++) {
                    board.changeAtPosition(row + i, column, 1);
                }
            }

        }
        player.setSomeBoard(board);
        boardDao.save(board);

        return player.getSomeBoard();
    }

    @RequestMapping("/attack")
    public Boolean attack(@RequestParam(value = "attackingPlayerId") int idAttacking, @RequestParam(value = "attackedPlayerId") int idAttacked, @RequestParam(value = "row") int row, @RequestParam(value = "column") int column)  {
        Player attackedPlayer = playerDao.findById(idAttacked);
        Board attackedPlayerBoard = attackedPlayer.getSomeBoard();

        int element = attackedPlayerBoard.getElementAt(row, column);

        if (element == 0) {
            return false;
        } else {
            attackedPlayerBoard.changeAtPosition(row, column, -1);
            attackedPlayer.setSomeBoard(attackedPlayerBoard);
            boardDao.save(attackedPlayerBoard);
        }
        return true;
    }

    @RequestMapping("/checkWinning")
    public Boolean checkWinning(@RequestParam(value = "playerId") int id) {
        Player player = playerDao.findById(id);
        Board board = player.getSomeBoard();

        Integer sum = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                sum += board.getElementAt(i, j);
            }
        }

        return (sum == -15 ? true : false);
    }
}
