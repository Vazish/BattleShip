package com.example.battleship.Controller;

import com.example.battleship.Model.Board;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BoardController {
    @RequestMapping("/getBoard")
    public Board getBoard() {
        Board someBoard = new Board();

        HashMap<HashMap<Integer, Integer>, Integer> finalMap = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                HashMap<Integer, Integer> someMap = new HashMap<>();
                someMap.put(i, j);
                finalMap.put(someMap, 0);
            }
        }

        someBoard.setBoardArray(finalMap);
        return someBoard;
    }

    @RequestMapping("/changeBoard")
    public Board changeBoard() {
        Board someBoard = new Board();
        HashMap<HashMap<Integer, Integer>, Integer> finalMap = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                HashMap<Integer, Integer> someMap = new HashMap<>();
                someMap.put(i, j);
                finalMap.put(someMap, 0);
            }
        }

        someBoard.setBoardArray(finalMap);
        someBoard.changeAtPosition(1, 1, 3);
        return someBoard;
    }
}
