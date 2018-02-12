package com.example.BattleshipBack.Dao;

import com.example.BattleshipBack.Model.Board;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BoardDao extends CrudRepository<Board, Integer>{

}
