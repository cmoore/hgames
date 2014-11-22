
package io.ivy.hgames;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.canarymod.Canary;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.Chunk;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.*;
import net.canarymod.api.world.blocks.properties.*;
import net.canarymod.api.world.position.Position;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.plugin.PluginListener;
import net.canarymod.api.entity.living.humanoid.Player;

public class HGamesListener implements PluginListener {
	
	private List<Chest> chests;

	@HookHandler
	public void onBlockRightClickHook(BlockRightClickHook hook) {
		if (hook.getPlayer().getName().equals("hydo")) {
			if (hook.getPlayer().getWorld().getFqName().equals("hgames_NORMAL")) {
          Block block = hook.getBlockClicked();
          if (block.getType().equals(BlockType.SignPost)) {
              Sign sign = (Sign) block.getTileEntity();
              if (sign.getTextOnLine(0).equals("rescan")) {
                  scan_for_chests(hook.getPlayer().getWorld(), hook.getPlayer());
				
                  /*
                  ItemFactory factory = Canary.factory().getItemFactory();
                  chests.forEach(new Consumer<Chest>() {
                          @Override
                          public void accept(Chest chest) {
                              chest.clearInventory();
                              chest.addItem(factory.newItem(ItemType.IronSword));
                              chest.update();
                          }
                      });
                  */
              }
          }
			}
		}
	}
	
    private void scan_for_chests(World world, Player me) {
      List<Chunk> chunks = world.getLoadedChunks();
      me.notice("Chunks: ".concat(String.valueOf(chunks.size())));
      chunks.forEach(new Consumer<Chunk>() {
              @Override
              public void accept(Chunk chunk) {
                  if (chunk.getTileEntityMap().size() > 0) {
                      chunk.getTileEntityMap().forEach(new BiConsumer<Position,TileEntity>() {
                              @Override
                              public void accept(Position position, TileEntity tileentity) {
                                  if (tileentity.getBlock().getType().equals(BlockType.Chest)) {
                                      Chest chest = (Chest) tileentity;
                                      chests.add(chest);
                                  }
                              }
                          });
                  }
              }
          });
      
      // chunks.forEach(new Consumer<Chunk>() {
      //         @Override
      //         public void accept(Chunk chunk) {
      //             chunk.getTileEntityMap().forEach(new BiConsumer<Position,TileEntity>() {
      //                     @Override
      //                     public void accept(Position position, TileEntity tileentity) {
      //                         if (tileentity.getBlock().getType().equals(BlockType.Chest)) {
      //                             Chest chest = (Chest) tileentity;
      //                             if (!chests.contains(chest)) {
      //                                 chests.add(chest);
      //                             }
      //                         }
      //                     }
      //                 });
      //         }
      //     });
	}
}

