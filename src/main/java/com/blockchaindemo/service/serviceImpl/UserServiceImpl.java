package com.blockchaindemo.service.serviceImpl;

import com.blockchaindemo.service.BlockchainService;
import com.blockchaindemo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.model.User;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Optional;

@Slf4j
//@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
   private Web3j web3j;
    @Autowired
    private BlockchainService blockchainService;

   public static User user;


    public UserServiceImpl() {
    }

    @Override
    public String setUserDetails(String name, Integer age) throws Exception {
        if(user==null){
            String lastAddress=blockchainService.getLastDeployedContract();
            if(lastAddress==null || lastAddress.isEmpty()){
                return "Contract is not yet deployed.";
            }
            else{
                blockchainService.loadContract(lastAddress);
                System.out.println("Contract loaded from address: "+lastAddress);
            }
        }
        TransactionReceipt transactionReceiptData = user.set(name, new BigInteger(String.valueOf(age))).sendAsync().get();
        return "Success";
    }


}


