package com.example.BattleshipBack.Model;

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

    private HashMap<HashMap<Integer, Integer>, Integer> boardArray; // this will be the ship array for the player

    public HashMap<HashMap<Integer, Integer>, Integer> getAttackBoardArray() {
        return attackBoardArray;
    }

    public void setAttackBoardArray(HashMap<HashMap<Integer, Integer>, Integer> attackBoardArray) {
        this.attackBoardArray = attackBoardArray;
    }

    private HashMap<HashMap<Integer, Integer>, Integer> attackBoardArray;

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

    public void changeAtPosition(int row, int column, int value, boolean attack) {
        HashMap<Integer, Integer> someMap = new HashMap<>();
        someMap.put(row, column);
        if (attack) {
            this.attackBoardArray.put(someMap, value);
        } else {
            this.boardArray.put(someMap, value);
        }
    }

    public Integer getElementAt(int row, int column, boolean attack) {
        HashMap<Integer, Integer> someMap = new HashMap<>();
        someMap.put(row, column);
        if (attack) {
            return this.attackBoardArray.get(someMap);
        } else {
            return this.boardArray.get(someMap);
        }
    }
}
