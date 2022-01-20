package com.blockchaindemo.model;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentHistory {

    @Id
    @GeneratedValue
    @Column(name="id")
    private Long id;

    @Column(name="contract_address")
    private String contractAddress;

    @Column(name="created_at")
    private Date createdAt;
}
