package com.example.BattleshipBack.Dao;

import com.example.BattleshipBack.Model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PlayerDao extends CrudRepository<Player, Integer>{

}
