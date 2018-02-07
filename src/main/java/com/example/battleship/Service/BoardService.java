package com.example.battleship.Service;

import com.example.battleship.Model.Board;

import java.util.List;

public class BoardService {
    private List<Board> board;
    private String errorMessage;
    private Boolean success;

    public List<Board> getBoard() {
        return board;
    }

    public void setBoard(List<Board> board) {
        this.board = board;
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
