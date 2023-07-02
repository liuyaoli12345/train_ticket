package org.fffd.l23o6.util.strategy.payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditStrtegy extends PaymentStrategy{
//    Map<Long , >
    Map<Long, Double> creditAccount = new HashMap<>();
//    List<Integer> base = new ArrayList<>();
    private CreditStrtegy(){
//        base.add(0);
//        base.add(1);
//        base.add(4);
//        base.add(18);
//        base.add(118);

        creditAccount.put(50000L, 0.0025);
        creditAccount.put(10000L, 0.002);
        creditAccount.put(3000L, 0.0015);
        creditAccount.put(1000L, 0.001);
        creditAccount.put(0L, 0D);
        //creditAccount.put(Long.MAX_VALUE, 0.003);
    }

    public Double creditPay(long credit, int price){
        double ret = 0;
        //int i = 0;
        long before = 0;
        for(Map.Entry<Long, Double> entry : creditAccount.entrySet()){
            Long key = entry.getKey();
            Double value = entry.getValue();
            if(credit > key){
                ret += value * (credit - key);
                credit = key;

                if(ret > price){
                    ret = price;
                    credit += (ret - price) / value;
                }
            }
            //i++;
        }

        return ret;
    }
}
