package org.fffd.l23o6.util.strategy.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;

public class AliPaymentStrategy extends PaymentStrategy{
    public String prepay(int mount) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient("\t\n" +
                "https://openapi-sandbox.dl.alipaydev.com/gateway.do","9021000122699447","MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCC20lYU+fmvv1N7BJeTM/b47uR/vzyglNIOvXSrkEEh1+/ES+mpu3pfHXsvq4Q6fMFIIrFgInCmg+MFXPw++Jm7z+te9BKhjfeREgm9DcvVNk2Eeprg0de92eHzF21NhT0iYmVMRWjoz2Rk2CcKVsfHMQ1fb2l08moAn0Qfyl6R0QT/uig7rHxypzzb/FBg09U+S+lY5dIFbtQH8+ctkPbX1hJiY6gi1HRHf04sYWo84BzUuH+3sUaqKL0VWCq+eCEPJuP5r02IvMRvuLpL5wfHiApe5BGvzAyUpXxxnD9sLe+IzS7XaFMWNwjjovZrijizkdFEBNJ1LsiWAynMLkPAgMBAAECggEBAIHl7yQofr9Xfpp4qy9DLt/IbK2cIzsVVlkQdnBCnLZDMC+6fsP/V5L9Aw8VpjOSGbVQPZGbbKVqS96yeRqS4IdwHpU3oGgqXpFc1QceTR3GK616EWvSsOm7zOFIZQl6+0hYs+QPCr0bUgYN98xtXVBAPSIghT91QuxuM/pJUUJYyidZNhGuB/6Im4bbr8sng4rIf5Ss2NdVYM52b/NTxdjUf0etsKyL5DUunc3/Yy23XSC5HgipP22AJOo9tGMyK+AWM2kf6qIMh/lLQqmCGaYgcGWDKLQ65UA58aPUXYpD1TVZcnk7Y5Dr3QbGyebjAgZawDdMuTVD48c42lRIY9ECgYEAw/67H9sfBlZfix/4+Zg07A9Z0widoC/8p+SFPEjcVQqIyCo1+BgIFkflftrYsY+4a8e+V9aJNtKlMNH+cNvq+jjQckFhzK0QsePljqROEXDhl1g7v8BmzpoPNBLF2wwVvZQYjcnsOuvFR4l6ZFiegpCzByaGHNPiM+YDLpbdYUcCgYEAqutG2xo3gDWnDgDYqtCtwAHXf0h4zTKGTae502LydIZq+LLSmNORGbrknG164yZJ+LAi0Z5D7zimIBt5eMx6g5R1vr3M1EpBjqoBZ0u2kBspQThVIzSo8NkuqDnemfyKEArLiAPlD/6i5U9ztJxo6W1OXjHC/ahs4R9oHYbvjfkCgYB1Zuv+LAqMiEaZFuDT2emxkYxzzhBlePk2NLvenaJlynaeuDI112dYuV5uO3db/UQyQ6bwBNGSpmFPOirYvdMmacvq9OE5cl2ywVmrtR4ScnSfbLn1pbGvhjcfMTOVf8qyEbkXjeHUdZBpxp8q5q6kdTrYSKpDMGSqQ9iaGsKnVwKBgCSuY8GICdtBKH4P3nAiYvevLYGKmypmqn4l/fKI7MHnSzPepOXZme1st71+nX38bKmrm31jOu8vOC/x1YsAT1bH2NE7yKbS3OjyQWTa0e5xddQic9sfZRZzddjyunxPv42b03x4YYOiQXF3MLzVSIq4Q7ZhdZicuelCpUSY/fZ5AoGAG7D7biRlIsJHmCqgB4NAzl+wV6xZrmQuQHYzQUFe7qfO8jLn/NrRZR6BCfAaHL2si3+wCjWf9uz7VsDokUlFCL2DlQVVDCyzgVju9jbKjLnGA8Jpe6SAPX9FR17aNyMkZPgOjq86/C8p+Uu73R9Zvn/ZPNp29806XAiNdCyCuns=",
                "json","GBK","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5fmAmiUS7H990UNEwvTwQpm7qhxIQT4xPQ/s7c80se0GtvEOTzZkZbZAWk5MtBizDD0r6QGiAz0hM13UC193UR0O6ulDtzF4zJcFD6Aa/RO/yC1jl5Ey9idN5zQWhUyjz6OSho+VQttbPPVes26CuCsJAhnFYg8GpUpCz+7gK1Rh9/ox+DM0pJ782Smyu5hQD+Jprw2P71IuKshHSLedPdGVsuwVRJDcyqjSR5Y9L89b61MP8AmlmFwmlSE0CPtkS3pIg+bt3K1qAB7ceUeA2OJEM8ZW/2QqXjtLnqoDX0tQ2wsO4behO2hnP8fD3IjEyL7KG6dg7YYQkAQDbaaZVQIDAQAB","RSA2");
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl("http://localhost:5173/pay");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "20210817010101003");
        bizContent.put("total_amount", mount);
        bizContent.put("subject", "测试商品");
        request.setBizContent(bizContent.toString());
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        return response.getMsg();
    }
}
