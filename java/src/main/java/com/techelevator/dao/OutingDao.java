package com.techelevator.dao;

import com.techelevator.model.Outing;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface OutingDao {
    public void createOuting(Outing outing);
//    public List<Outing> getOutingsByUserId(int UserId);
    public Outing getOutingByOutingId(int outingId);
//    public Outing updateOutingWithVotes(Outing outingToUpdate);
}
