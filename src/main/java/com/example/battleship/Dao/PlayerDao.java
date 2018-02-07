package com.example.battleship.Dao;

import com.example.battleship.Model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PlayerDao extends CrudRepository<Player, Integer> {
    public List<Player> findAll();
    public Player findById(Integer id);
    public <S extends Player> S save(S Player);
    public void delete(Integer id);
}
