
package io.ivy.hgames;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.canarymod.Canary;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.world.Chunk;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.*;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.position.Position;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.hook.player.PlayerMoveHook;
import net.canarymod.hook.player.PlayerRespawnedHook;
import net.canarymod.plugin.PluginListener;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.config.Configuration;

public class HGamesListener implements PluginListener {
	
	private List<Chest> chests = new ArrayList<Chest>();
	private Boolean is_running = false;
	private List<Player> players = new ArrayList<Player>();
	
	@HookHandler
	public void onPlayerRespawnedHook(PlayerRespawnedHook hook) {
		if (in_world(hook.getPlayer().getWorld())) {
		
			if (in_world(hook.getPlayer().getWorld())) {
				hook.getPlayer().getInventory().clearContents();
				String[] game_status = {"/title @a title {text:\"" +
						hook.getPlayer().getDisplayName() + " just joined.\",color:red}" };
			
				String[] sub_status = {"/title @a subtitle {text:\"Waiting for more...\",color:red}"};
				hook.getPlayer().executeCommand(game_status);
				hook.getPlayer().executeCommand(sub_status);
			}
		}
	}

	@HookHandler
	public void onBlockRightClickHook(BlockRightClickHook hook) {
		if (in_world(hook.getPlayer().getWorld())) {
			if (hook.getBlockClicked().getType().equals(BlockType.SignPost)) {
				Sign sign = (Sign) hook.getBlockClicked().getTileEntity();
				World world = hook.getPlayer().getWorld();
				if (sign.getTextOnLine(0).equals("restart"))
					game_starting(world);
				if (sign.getTextOnLine(0).equals("chests"))
					fill_chests(world);
				if (sign.getTextOnLine(0).equals("kick"))
					kick_player(hook.getPlayer());
				if (sign.getTextOnLine(0).equals("cage"))
					make_cage(world);
				if (sign.getTextOnLine(0).equals("freedom"))
					remove_cage(world);
			}
		}
	}
	
	private boolean in_world(World world) {
		return world.getFqName().equals("hgames_NORMAL");
	}
	
	private void kick_player(Player player) {
		String[] game_in_progress = {"/title @p title {text:\"A game is in progress.\"}"};
		player.executeCommand(game_in_progress);
		Canary.warps().getWarp("castle").warp(player);
	}
	
	private void game_starting(World world) {
		String[] game_starting = { "/title @p title {text:\"Game Starting Soon\"}" };
		String[] game_starting_sub = { "/title @p subtitle {text:\"omg get ready omg omg\"}"};
		
		List<Player> plist = world.getPlayerList();
		plist.forEach(new Consumer<Player>() {
			@Override
			public void accept(Player p) {
				p.teleportTo(world.getSpawnLocation());
				p.executeCommand(game_starting);
				p.executeCommand(game_starting_sub);
			}
		});
	}

	private void setwool(Block block, BlockType type) {
		block.setType(type);
		block.update();
	}
	
	private void make_cage(World world) {
		draw_cage(world, BlockType.Glass);
	}
	private void remove_cage(World world) {
		draw_cage(world, BlockType.Air);
		draw_box(world.getBlockAt(world.getSpawnLocation()), -1, BlockType.Grass);
	}
	private void draw_cage(World world, BlockType type) {
		Location worldspawn = world.getSpawnLocation();
		Block refblock = world.getBlockAt(worldspawn);

		for (int i = 0; i <= 3; i++) {
			draw_box(refblock, i, type);
		}
	}

	private void draw_box(Block reference, int y, BlockType type) {
		setwool(reference.getRelative( 2, y,  0), type);
		setwool(reference.getRelative( 0, y,  2), type);
		setwool(reference.getRelative( 2, y,  2), type);
		
		setwool(reference.getRelative(-2, y,  0), type);
		setwool(reference.getRelative( 0, y, -2), type);
		setwool(reference.getRelative(-2, y, -2), type);
		
		setwool(reference.getRelative( 2, y, -2), type);
		setwool(reference.getRelative(-2, y,  2), type);
/*
		setwool(reference.getRelative( 1, y,  0));
		setwool(reference.getRelative( 0, y,  1));
		setwool(reference.getRelative( 1, y,  1));
		
		setwool(reference.getRelative(-1, y,  0));
		setwool(reference.getRelative( 0, y, -1));
		setwool(reference.getRelative(-1, y, -1));
		
		setwool(reference.getRelative( 1, y, -1));
		setwool(reference.getRelative(-1, y,  1));
*/
		setwool(reference.getRelative( 2, y,  1), type);
		setwool(reference.getRelative( 1, y,  2), type);
		setwool(reference.getRelative( 2, y, -1), type);
		setwool(reference.getRelative(-1, y,  2), type);
		
		setwool(reference.getRelative(-2, y, -2), type);
		setwool(reference.getRelative(-2, y,  1), type);
		setwool(reference.getRelative(-2, y, -1), type);
		setwool(reference.getRelative(-2, y,  0), type);
		
		setwool(reference.getRelative( 1, y, -2), type);
		setwool(reference.getRelative( 0, y, -2), type);
		setwool(reference.getRelative(-1, y, -2), type);
		
	}
	private void fill_chests(World world) {
		scan_for_chests(world);
		ItemFactory factory = Canary.factory().getItemFactory();
		chests.forEach(new Consumer<Chest>() {
			@Override
			public void accept(Chest chest) {
				chest.clearInventory();
				
				/*
				Item potion = factory.newItem(ItemType.Potion);								
				PotionFactory potion_factory = Canary.factory().getPotionFactory();
				PotionEffect potion_effect = potion_factory.newPotionEffect(PotionEffectType.HEAL, 0, 1);
				PotionItemHelper.setCustomPotionEffects(potion, potion_effect);
				chest.addItem(potion);
				*/
				
				chest.addItem(factory.newItem(ItemType.IronSword));
				chest.addItem(factory.newItem(ItemType.IronHelmet));
				chest.addItem(factory.newItem(ItemType.IronLeggings));
				chest.addItem(factory.newItem(ItemType.IronChestplate));
				chest.addItem(factory.newItem(ItemType.IronBoots));
				chest.addItem(factory.newItem(ItemType.CookedChicken, 1));
				chest.addItem(factory.newItem(ItemType.EnderPearl));
				
				chest.addItem(factory.newItem(ItemType.Bow));
				chest.addItem(factory.newItem(ItemType.Arrow));
				
				chest.update();
			}
		});

	}
	
	private void reset_everyone(World world) {
		world.getPlayerList().forEach(new Consumer<Player>() {
			@Override
			public void accept(Player player) {
				player.getInventory().clearContents();
			}
		});
	}
	
    private void scan_for_chests(World world) {
      List<Chunk> chunks = world.getLoadedChunks();
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
    }
}

