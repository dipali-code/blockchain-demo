package com.blockchaindemo.repository;

import com.blockchaindemo.model.DeploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentHistoryRepo extends JpaRepository<DeploymentHistory,Long> {

    @Query("Select contractAddress from DeploymentHistory dh where id=(select max(id) from DeploymentHistory) ")
    public String getLastDeployedContract();
}
