package com.example.battleship.Model;


import javax.annotation.Generated;
import javax.persistence.*;

@Entity
public class Player {
    @Id
    @GeneratedValue
    @Column(name = "player_id")
    private Integer id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private Board someBoard = new Board();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Board getSomeBoard() {
        return someBoard;
    }

    public void setSomeBoard(Board someBoard) {
        this.someBoard = someBoard;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
