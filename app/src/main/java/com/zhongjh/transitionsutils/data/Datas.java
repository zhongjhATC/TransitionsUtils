package com.zhongjh.transitionsutils.data;

import java.util.ArrayList;

/**
 * 模拟的数据源
 *
 * @author zhongjh
 * @date 2022/2/22
 */
public class Datas {

    private volatile static Datas mInstance;

    private Datas() {
    }

    public static Datas getInstance() {
        if (mInstance == null) {
            synchronized (Datas.class) {
                if (mInstance == null) {
                    mInstance = new Datas();
                }
            }
        }
        return mInstance;
    }

    ArrayList<Data> dataArrayList = new ArrayList<>();

    public ArrayList<Data> getDatas() {
        if (dataArrayList.size() == 0) {
            dataArrayList.add(new Data(0, "https://t7.baidu.com/it/u=4198287529,2774471735&fm=193&f=GIF", "https://t7.baidu.com/it/u=4198287529,2774471735&fm=193&f=GIF"));
            dataArrayList.add(new Data(1, "https://t7.baidu.com/it/u=1956604245,3662848045&fm=193&f=GIF", "https://t7.baidu.com/it/u=1956604245,3662848045&fm=193&f=GIF"));
            dataArrayList.add(new Data(2, "https://t7.baidu.com/it/u=2529476510,3041785782&fm=193&f=GIF", "https://t7.baidu.com/it/u=2529476510,3041785782&fm=193&f=GIF"));
            dataArrayList.add(new Data(3, "https://t7.baidu.com/it/u=2511982910,2454873241&fm=193&f=GIF", "https://t7.baidu.com/it/u=2511982910,2454873241&fm=193&f=GIF"));
            dataArrayList.add(new Data(4, "https://t7.baidu.com/it/u=825057118,3516313570&fm=193&f=GIF", "https://t7.baidu.com/it/u=825057118,3516313570&fm=193&f=GIF"));
            dataArrayList.add(new Data(5, "https://t7.baidu.com/it/u=727460147,2222092211&fm=193&f=GIF", "https://t7.baidu.com/it/u=727460147,2222092211&fm=193&f=GIF"));
            dataArrayList.add(new Data(6, "https://t7.baidu.com/it/u=2397542458,3133539061&fm=193&f=GIF", "https://t7.baidu.com/it/u=2397542458,3133539061&fm=193&f=GIF"));
            dataArrayList.add(new Data(7, "https://t7.baidu.com/it/u=3569419905,626536365&fm=193&f=GIF", "https://t7.baidu.com/it/u=3569419905,626536365&fm=193&f=GIF"));
            dataArrayList.add(new Data(8, "https://t7.baidu.com/it/u=3779234486,1094031034&fm=193&f=GIF", "https://t7.baidu.com/it/u=3779234486,1094031034&fm=193&f=GIF"));
            dataArrayList.add(new Data(9, "https://t7.baidu.com/it/u=3911840071,2534614245&fm=193&f=GIF", "https://t7.baidu.com/it/u=3911840071,2534614245&fm=193&f=GIF"));
            dataArrayList.add(new Data(10, "https://t7.baidu.com/it/u=3908717,2002330211&fm=193&f=GIF", "https://t7.baidu.com/it/u=3908717,2002330211&fm=193&f=GIF"));
            dataArrayList.add(new Data(11, "https://t7.baidu.com/it/u=318887420,2894941323&fm=193&f=GIF", "https://t7.baidu.com/it/u=318887420,2894941323&fm=193&f=GIF"));
        }
        return dataArrayList;
    }

}
