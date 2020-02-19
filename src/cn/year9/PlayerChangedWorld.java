package cn.year9;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.SimpleFormatter;

public class PlayerChangedWorld implements Listener {

    private List<String> worldList;
    private Map<String,Object> map;
    private WorldLimiter worldLimiter;

    public PlayerChangedWorld(List<String> worldList,Map<String,Object> map,WorldLimiter worldLimiter) {
        this.worldList = worldList;
        this.map = map;
        this.worldLimiter = worldLimiter;
    }

    @EventHandler
    public void ChangedWorld(PlayerChangedWorldEvent e) throws ParseException {
        Util util = new Util();
        try {
            List<String> ItemList = util.maptoarray(map, e.getPlayer().getWorld().getName(), ".prohibited-items");
            ItemStack[] itemStacks = e.getPlayer().getInventory().getContents();
            for (ItemStack is : itemStacks) {
                if (is != null) {
                   for (int i=0;i<ItemList.size();i++)
                   {
                       if (Integer.parseInt(ItemList.get(i).replace(" ","")) == is.getTypeId())
                       {
                           new BukkitRunnable() {
                               @Override
                               public void run() {
                                   // 你想在这里安排什么
                                   e.getPlayer().chat("/spawn");
                                   e.getPlayer().sendMessage("§4[世界限制]您不可携带 " + is.getType().name()+ " 进入该世界!");
                               }
                           }.runTaskLater(worldLimiter, 10);
                       }
                   }
                }
            }
        }catch (NullPointerException exp)
        {

        }

        checkEvent(e); //世界判断
    }

    public void checkEvent(PlayerChangedWorldEvent e) throws ParseException {
        for (String key:worldList)
        {
            if (e.getPlayer().getLocation().getWorld().getName().equals(key)) {
                e.getPlayer().sendMessage("§2[世界限制]您前往的世界 " + map.get(key + ".world-alias") + " §2将可能受到限制!");
                e.getPlayer().sendMessage("§2+开放时间段如下+");
                e.getPlayer().sendMessage("§2" + map.get(key + ".allow-enter-interval"));
                /*转换时间到数组*/

                Util util = new Util();
                List<Boolean> selarray = new ArrayList<>();
                String format = "HH:mm";
                Date nowTime = new SimpleDateFormat(format).parse(new SimpleDateFormat(format).format(new Date().getTime()));
                List<String> rowIdList = util.maptoarray(map, key, ".allow-enter-interval");
                for (String time : rowIdList) {
                    String[] timeFG = time.split("-");
                    Date startTime = new SimpleDateFormat(format).parse(timeFG[0]);
                    Date endTime = new SimpleDateFormat(format).parse(timeFG[1]);
                   // System.out.println(Util.isEffectiveDate(nowTime, startTime, endTime));
                    if (Util.isEffectiveDate(nowTime, startTime, endTime))
                    {
                        selarray.add(true);
                    }else{
                        selarray.add(false);
                    }
                }
                //System.out.println(selarray);
                boolean flag=false;
                for (int i=0;i<selarray.size();i++)
                {
                    if (selarray.get(i))
                    {
                     flag=true;
                     break;
                    }
                }
               // System.out.println(flag);
                if (!flag)
                {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // 你想在这里安排什么
                            e.getPlayer().chat("/spawn");
                            e.getPlayer().sendMessage("§4[世界限制]当前世界不在开放时间内,无法前往!");
                        }
                    }.runTaskLater(worldLimiter, 10);
                }
            }
        }
    }

    public void checkEvent(PlayerLoginEvent e) throws ParseException {
        for (String key:worldList)
        {
            if (e.getPlayer().getLocation().getWorld().getName().equals(key)) {
                e.getPlayer().sendMessage("§2[世界限制]您当前的世界 " + map.get(key + ".world-alias") + " §2将可能受到限制!");
                e.getPlayer().sendMessage("§2+开放时间段如下+");
                e.getPlayer().sendMessage("§2" + map.get(key + ".allow-enter-interval"));
                /*转换时间到数组*/
                Util util = new Util();
                List<Boolean> selarray = new ArrayList<>();
                String format = "HH:mm";
                Date nowTime = null;


                nowTime = new SimpleDateFormat(format).parse(new SimpleDateFormat(format).format(new Date().getTime()));
                List<String> rowIdList = util.maptoarray(map, key, ".allow-enter-interval");
                for (String time : rowIdList) {
                    String[] timeFG = time.split("-");
                    Date startTime = new SimpleDateFormat(format).parse(timeFG[0]);
                    Date endTime = new SimpleDateFormat(format).parse(timeFG[1]);
                    //System.out.println(Util.isEffectiveDate(nowTime, startTime, endTime));
                    if (Util.isEffectiveDate(nowTime, startTime, endTime))
                    {
                        selarray.add(true);
                    }else{
                        selarray.add(false);
                    }
                }
                //System.out.println(selarray);
                boolean flag = false;
                for (int i=0;i<selarray.size();i++)
                {
                    if (selarray.get(i))
                    {
                        flag=true;
                        break;
                    }
                }

                //System.out.println(flag);
                if (!flag)
                {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // 你想在这里安排什么
                            e.getPlayer().chat("/spawn");
                            e.getPlayer().sendMessage("§4[世界限制]当前世界不在开放时间内,无法前往!");
                        }
                    }.runTaskLater(worldLimiter, 10);
                }
            }
        }
    }


    public void checkEvent(PlayerMoveEvent e) throws ParseException {
        for (String key:worldList)
        {
            if (e.getPlayer().getLocation().getWorld().getName().equals(key)) {
                /*转换时间到数组*/
                Util util = new Util();
                List<Boolean> selarray = new ArrayList<>();
                String format = "HH:mm";
                Date nowTime = null;


                nowTime = new SimpleDateFormat(format).parse(new SimpleDateFormat(format).format(new Date().getTime()));
                List<String> rowIdList = util.maptoarray(map, key, ".allow-enter-interval");
                for (String time : rowIdList) {
                    String[] timeFG = time.split("-");
                    Date startTime = new SimpleDateFormat(format).parse(timeFG[0]);
                    Date endTime = new SimpleDateFormat(format).parse(timeFG[1]);
                    //System.out.println(Util.isEffectiveDate(nowTime, startTime, endTime));
                    if (Util.isEffectiveDate(nowTime, startTime, endTime))
                    {
                        selarray.add(true);
                    }else{
                        selarray.add(false);
                    }
                }
                //System.out.println(selarray);
                boolean flag = false;
                for (int i=0;i<selarray.size();i++)
                {
                    if (selarray.get(i))
                    {
                        flag=true;
                        break;
                    }
                }

                //System.out.println(flag);
                if (!flag)
                {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // 你想在这里安排什么
                            e.getPlayer().chat("/spawn");
                            e.getPlayer().sendMessage("§4[世界限制]当前世界不在开放时间内,无法前往!");
                        }
                    }.runTaskLater(worldLimiter, 10);
                }
            }
        }
    }
}
