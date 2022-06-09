package com.thp.project.vintud.dao;

import com.thp.project.vintud.entity.impl.UserImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<UserImpl, Integer> {
    UserImpl findUserImplByPseudo(String pseudo);
}
