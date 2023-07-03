package org.fffd.l23o6.util.strategy;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.HashMap;
import java.util.Map;

public class paymentUtil
{
    /*依据现行的铁路票价标准,铁路票价的基价率为0.05861元/km.200km以上里程的票价执行递远递减,
     *200-500km的部分九折,500-1000km的部分八折,1000-1500km的部分七折,
     *1500-2500km的部分六折,2500km以上的部分五折.
     *这里我们采用简化的办法:以经过的站数为计价单位，高铁阶梯为40(<5),30(<10),15(>10),普快阶梯为25(<5),15(<10),10(>10)
     * 在此基础上四挡坐席分别*1.5,1.2,1.0,0.8,0.6
     */
    public static Integer genPrice(int cmd, int length, int type)
    {
        final Map<Integer, Integer> pricePerStation = new HashMap<>();
        Integer[] base;
        // 设置电价表
        Integer price = 0;
        if (cmd == 1)
        {
            base = new Integer[]{0, 200, 350};
            pricePerStation.put(5, 40);
            pricePerStation.put(10, 30);
            pricePerStation.put(99999, 15);
        } else
        {
            base = new Integer[]{0, 125, 200};
            pricePerStation.put(5, 25);
            pricePerStation.put(10, 15);
            pricePerStation.put(99999, 10);
        }
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : pricePerStation.entrySet())
        {
            int threshold = entry.getKey();
            int rate = entry.getValue();

            if (length <= threshold)
            {
                price = base[i] + (threshold - length) * rate;
                break;
            }
            i++;
        }
        double[] grade = {1.5,1.2,1.0,0.8,0.6};
        return (int)(price*grade[type]);
    }
}
