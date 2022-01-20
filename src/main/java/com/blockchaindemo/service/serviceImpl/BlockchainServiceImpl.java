package com.blockchaindemo.service.serviceImpl;

import com.blockchaindemo.model.DeploymentHistory;
import com.blockchaindemo.repository.DeploymentHistoryRepo;
import com.blockchaindemo.service.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.crypto.Credentials;
import org.web3j.model.User;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.Date;

@Service
public class BlockchainServiceImpl implements BlockchainService {

    @Value("${chain.addresses.rpc}")
    private String rpcPortAddress;

    @Value("${chain.addresses.ws}")
    private String webSocketUrl;

    @Value("${chain.account.private-key}")
    private String privateKey;

    @Value("${chain.gas-price}")
    private long gasPrice;

    @Value("${chain.gas-limit}")
    private long gasLimit;

    @Autowired
    private Web3j web3j;

    @Autowired
    private DeploymentHistoryRepo deploymentHistoryRepo;

    public BlockchainServiceImpl() {
    }

    @Override
    public String deployContract() throws Exception {
        UserServiceImpl.user= User.deploy( web3j, Credentials.create(privateKey),
                new StaticGasProvider(BigInteger.valueOf(gasPrice), BigInteger.valueOf(gasLimit))).send();
        String contractAddress=UserServiceImpl.user.getContractAddress();
        saveContractAddress(contractAddress);
        subscriptionToEvent(contractAddress);
        return contractAddress;
    }


    public String loadContract(String contractAddress) {
        UserServiceImpl.user=User.load(contractAddress,web3j, Credentials.create(privateKey),
                new StaticGasProvider(BigInteger.valueOf(gasPrice), BigInteger.valueOf(gasLimit)));
        subscriptionToEvent(contractAddress);
       return  "CONTRACT LOADED";
    }


    public void subscriptionToEvent(String contractAddress) {
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST,contractAddress );

        String encodedEventSignature = EventEncoder.encode(User.USEREVENT_EVENT);
        filter.addSingleTopic(encodedEventSignature);

        // subscribe to events

        web3j.ethLogFlowable(filter).subscribe(event -> {
            System.out.println("Event received"+event);
            //log.info("event={}",event);
        }, error -> {
            System.out.println("Error: " + error);
        });
    }

    public String saveContractAddress(String contractAddress){

        DeploymentHistory deploymentHistory=new DeploymentHistory();
        deploymentHistory.setContractAddress(contractAddress);
        deploymentHistory.setCreatedAt(new Date());
        deploymentHistoryRepo.save(deploymentHistory);

        return "ADDED SUCCESSFULLY";
    }

    @Override
    public String getLastDeployedContract(){
        String address =deploymentHistoryRepo.getLastDeployedContract();
        System.out.println("Last deployed address="+address);
        return address;
    }
}
