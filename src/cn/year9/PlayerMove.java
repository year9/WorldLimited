package cn.year9;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class PlayerMove implements Listener {
    private List<String> worldList;
    private Map<String,Object> map;
    private WorldLimiter worldLimiter;

    public PlayerMove(List<String> worldList,Map<String,Object> map,WorldLimiter worldLimiter) {
        this.worldList = worldList;
        this.map = map;
        this.worldLimiter = worldLimiter;
    }
    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e){
        PlayerChangedWorld pc = new PlayerChangedWorld(worldList,map,worldLimiter);
        Util util = new Util();
        new BukkitRunnable() {
            @Override
            public void run() {
                // 你想在这里安排什么
                try {
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
                    pc.checkEvent(e);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater(worldLimiter, 10);
    }
}
