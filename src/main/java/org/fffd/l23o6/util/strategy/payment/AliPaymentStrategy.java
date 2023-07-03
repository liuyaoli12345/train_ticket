package org.fffd.l23o6.util.strategy.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;


public class AliPaymentStrategy extends PaymentStrategy{

    public void pay(int mount) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(
                AliPaymentConst.URL,
                AliPaymentConst.APPID,
                AliPaymentConst.PRIVATE_KEY,
                "json",
                "utf-8",
                AliPaymentConst.ALIPAY_PUBLIC_KEY,
                "RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
//        request.setNotifyUrl("");
//        request.setReturnUrl("");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", System.currentTimeMillis()+"");
        bizContent.put("total_amount", mount);
        bizContent.put("subject", "测试商品");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if(response.isSuccess()){
            System.out.println(response.getBody());
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
    }
}