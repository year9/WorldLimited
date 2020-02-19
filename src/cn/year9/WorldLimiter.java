package cn.year9;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class WorldLimiter extends JavaPlugin {

    Map<String,Object> map = null;
    List<Integer> addItemList = new ArrayList<>();
    List<String> list = new ArrayList<String>();
    boolean ifflag  = false;

    @Override
    public void onEnable() {
    //git
        saveDefaultConfig();

        map = YamlConfiguration.loadConfiguration(new Config(this).getConfigFile()).getValues(true);

        //载入世界
        for (String key: map.keySet())
        {
            if (!key.contains("."))
            {
                list.add(key);
            }
        }

        getServer().getPluginManager().registerEvents(new PlayerLogin(list,map,this),this);
        getServer().getPluginManager().registerEvents(new PlayerChangedWorld(list,map,this),this);
        getServer().getPluginManager().registerEvents(new PlayerPickupItem(list,map,this),this);
        getServer().getPluginManager().registerEvents(new PlayerMove(list,map,this),this);


        getServer().getLogger().info("=================================");
        getServer().getLogger().info("[世界限制]世界限制插件正在被启动....");
        getServer().getLogger().info("[世界限制]作者: year9");
        getServer().getLogger().info("=================================");

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        Util util = new Util();
        List<Boolean> selarray = new ArrayList<>();
        String format = "HH:mm";

        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                List<String> openworld = new ArrayList<>();
                for (String worldName : list)
                {
                    List<String> rowIdList = util.maptoarray(map, worldName, ".allow-enter-interval");
                    for (String time : rowIdList)
                    {
                        String[] timeFG = time.split("-");
                        Date startTime = null;
                        try {
                            startTime = new SimpleDateFormat(format).parse(timeFG[0]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date endTime = null;
                        try {
                            endTime = new SimpleDateFormat(format).parse(timeFG[1]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Date nowTime = null;
                        try {
                            nowTime = nowTime = new SimpleDateFormat(format).parse(new SimpleDateFormat(format).format(new Date().getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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
                    if (flag)
                    {
                        openworld.add(worldName);
                        selarray.clear();
                    }
                }
                for (String opworld : openworld)
                {
                    getServer().broadcastMessage("§2[世界提示]各位玩家,世界 [" + map.get(opworld+".world-alias") + "] 目前正在该时间段开放，请火速前往！");
                }
                openworld.clear();
            }
        }, 0L, 1000L);
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("=================================");
        getServer().getLogger().info("[世界限制]世界限制插件正在被卸载....");
        getServer().getLogger().info("[世界限制]已经安全卸载");
        getServer().getLogger().info("=================================");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            sender.sendMessage("§c[世界限制]传递了太多的参数!");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage("§c[世界限制]传递了太少的参数!");
            return false;
        }
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            // do something
            if (!player.isOp())
            {
                sender.sendMessage("§c[世界限制]您没有权限使用该命令!");
                return true;
            }
            if (command.getName().equalsIgnoreCase("wd"))
            {
                switch (args[0])
                {
                    case "add":
                        if (player.getItemInHand().getType() == Material.AIR)
                        {
                            sender.sendMessage("§4[世界限制]手上没有持有物品，无法添加!");
                        }else{
                            addItemList.add(player.getItemInHand().getType().getId());
                            sender.sendMessage("§2[世界限制]添加物品ID: "+String.valueOf(player.getItemInHand().getType().getId())+" 成功，若需保存输入/wd save 若想重新添加输入 /wd clear!");
                        }
                        break;
                    case "save":
                        if (addItemList.size() <= 0)
                        {
                            sender.sendMessage("§4[世界限制]当前列表为空,请先用/wd add 增加物品 然后在使用/wd save 确定所添加内容!");
                            return true;
                        }

                        List<Boolean> selarray = new ArrayList<>();
                        for (String world : list )
                        {
                            //System.out.println(world);
                            //System.out.println("p"+player.getWorld().getName());
                            //System.out.println(world.equalsIgnoreCase(player.getWorld().getName()));
                            selarray.add(world.equalsIgnoreCase(player.getWorld().getName()));
                        }

                        boolean flag=false;
                        for (int i=0;i<selarray.size();i++)
                        {
                            if (selarray.get(i))
                            {
                                flag=true;
                                break;
                            }
                        }

                        if (!flag)
                        {
                            sender.sendMessage("§4[世界限制]您所在的世界不存在限制，若需限制请先在配置中配置!");
                            return true;
                        }

                        sender.sendMessage("§b================§2[世界限制]§b==================");
                        sender.sendMessage("§b您将要添加的物品列表和世界如下:");
                        sender.sendMessage("§b" + String.valueOf(addItemList.toString()));
                        sender.sendMessage("§b所在添加世界:"+player.getWorld().getName());
                        sender.sendMessage("§b若确认添加到列表，请输入 §a/wd qsave §b保存");
                        sender.sendMessage("§b============================================");
                        ifflag = true;
                        break;
                    case "qsave":

                        if (addItemList.size() <= 0 || !ifflag)
                        {
                            sender.sendMessage("§4[世界限制]没有使用/wd save命令，请先用/wd save 确定所添加内容");
                            return true;
                        }
                        Util util = new Util();
                        List<String> ItemList = util.maptoarray(map, player.getPlayer().getWorld().getName(), ".prohibited-items");
                        List<String> savelist = new ArrayList<>();


                        for (String id:ItemList)
                        {
                            for (Integer item:addItemList)
                            {
                                if (item != Integer.parseInt(id.replace(" ","")))
                                {
                                    savelist.add(String.valueOf(item));
                                }
                            }
                            savelist.add(id);
                        }
                        //去重复
                        savelist= savelist.stream().distinct().collect(Collectors.toList());

                        System.out.println(savelist.toString());
                        System.out.println(addItemList.toString());
                        FileConfiguration fileConfiguration =  YamlConfiguration.loadConfiguration(new Config(this).getConfigFile());
                        fileConfiguration.set(player.getWorld().getName()+".prohibited-items",savelist);
                        try {
                            fileConfiguration.save(new Config(this).getConfigFile());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        addItemList.clear();
                        ifflag = false;
                        sender.sendMessage("§2[世界限制]添加限制物品到世界 "+player.getWorld().getName()+" 成功!");
                        break;
                    case "clear":
                        addItemList.clear();
                        sender.sendMessage("§2[世界限制]已经清空待添加列表");
                        break;
                    case "list":
                        Util util1 = new Util();
                        try {
                            List<String> banlist = util1.maptoarray(map, player.getPlayer().getWorld().getName(), ".prohibited-items");
                            List<String> time = util1.maptoarray(map, player.getPlayer().getWorld().getName(), ".allow-enter-interval");
                            sender.sendMessage("§b================§2[世界限制]§b==================");
                            sender.sendMessage("§b这个世界限制的物品和时间列表如下:");
                            sender.sendMessage("§b限制物品:" + banlist.toString());
                            sender.sendMessage("§b限制时间:" + time.toString());
                            sender.sendMessage("§b所在添加世界:" + player.getWorld().getName());
                            sender.sendMessage("§b修改删除请在配置文件中操作，增加请使用/wd add 添加");
                            sender.sendMessage("§b============================================");
                        }catch (NullPointerException exp) {
                            sender.sendMessage("§b================§2[世界限制]§b==================");
                            sender.sendMessage("§b这个世界限制没有任何限制");
                            sender.sendMessage("§b============================================");
                        }
                        break;
                    case "reload":
                        sender.sendMessage("§2[世界限制]配置文件已加载入内存，请重启服务器生效!");
                        break;
                    default:
                        sender.sendMessage("§2[世界限制]指令未知!");
                        break;
                }
            }
        }else {
            sender.sendMessage("§2[世界限制]命令只能由玩家进行发出!");
        }


      return true;
    }
}
