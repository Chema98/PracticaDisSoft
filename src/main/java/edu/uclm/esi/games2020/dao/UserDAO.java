package edu.uclm.esi.games2020.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.games2020.model.User;

@Repository
public interface UserDAO extends CrudRepository<User, String> {

}
