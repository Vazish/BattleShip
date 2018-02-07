package com.example.battleship.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Board {
    @Id
    @Column(name = "board_id")
    private Integer id;

    @MapsId
    @OneToOne(mappedBy = "someBoard")
    @JoinColumn(name = "board_id")
    private Player player;

    private HashMap<HashMap<Integer, Integer>, Integer> boardArray;

    public HashMap<HashMap<Integer, Integer>, Integer> getBoardArray() {
        return boardArray;
    }

    public void setBoardArray(HashMap<HashMap<Integer, Integer>, Integer> boardArray) {
        this.boardArray = boardArray;
    }

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void changeAtPosition(int row, int column, int value) {
        HashMap<Integer, Integer> someMap = new HashMap<>();
        someMap.put(row, column);
        this.boardArray.put(someMap, value);
    }

    public Integer getElementAt(int row, int column) {
        HashMap<Integer, Integer> someMap = new HashMap<>();
        someMap.put(row, column);
        return this.boardArray.get(someMap);
    }
}
