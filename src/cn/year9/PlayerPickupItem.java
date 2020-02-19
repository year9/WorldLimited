package cn.year9;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class PlayerPickupItem implements Listener {
    private List<String> worldList;
    private Map<String,Object> map;
    private WorldLimiter worldLimiter;

    public PlayerPickupItem(List<String> worldList, Map<String,Object> map, WorldLimiter worldLimiter) {
        this.worldList = worldList;
        this.map = map;
        this.worldLimiter = worldLimiter;
    }
    @EventHandler
    public void  PlayerPickupItemEvent(PlayerPickupItemEvent e)
    {
        Util util = new Util();
        try {
            List<String> ItemList = util.maptoarray(map, e.getPlayer().getWorld().getName(), ".prohibited-items");
            for (String world : worldList)
            {
                if (e.getPlayer().getLocation().getWorld().getName().equals(world))
                {
                    for (String id : ItemList)
                    {
                        if (e.getItem().getItemStack().getType().getId() == Integer.parseInt(id.replace(" ","")))
                        {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    // 你想在这里安排什么
                                    e.setCancelled(true);
                                    e.getPlayer().chat("/spawn");
                                    e.getPlayer().sendMessage("§4[世界限制]您拾取的 " + e.getItem().getItemStack().getType().name() + " 是该世界所限制携带的!");
                                }
                            }.runTaskLater(worldLimiter, 10);
                        }
                    }

                }
            }
        }catch (NullPointerException exp)
        {

        }


    }
}
