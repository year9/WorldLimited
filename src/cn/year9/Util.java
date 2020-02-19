package cn.year9;

import java.util.*;

public class Util {
    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * MAP对象转数组
     */
    public List<String> maptoarray(Map<String,Object> map,String key,String Path)
    {
        String str = map.get(key+Path).toString().replaceAll(" +","");// 去空格
        String strSub = map.get(key+Path).toString().substring(1,map.get(key+Path).toString().length()-1);
        String[] strArray = strSub.split(",");
        return Arrays.asList(strArray);
    }



}
