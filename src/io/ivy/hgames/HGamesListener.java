
package io.ivy.hgames;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.canarymod.api.world.Chunk;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.Chest;
import net.canarymod.api.world.blocks.TileEntity;
import net.canarymod.api.world.position.Position;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.plugin.PluginListener;

public class HGamesListener implements PluginListener {
	
	private List<Chest> chests;

	@HookHandler
	public void onBlockRightClickHook(BlockRightClickHook hook) {
		if (hook.getPlayer().getName().equals("hydo")) {
			if (hook.getPlayer().getWorld().getFqName().equals("hgames_NORMAL")) {
				scan_for_chests(hook.getPlayer().getWorld());
			}
		}
	}
	
	private void scan_for_chests(World world) {
		world.getLoadedChunks().forEach(new Consumer<Chunk>() {
			@Override
			public void accept(Chunk chunk) {
				chunk.getTileEntityMap().forEach(new BiConsumer<Position,TileEntity>() {
					@Override
					public void accept(Position position, TileEntity tileentity) {
						if (tileentity.getBlock().getType().equals(BlockType.Chest)) {
							Chest chest = (Chest) tileentity.getBlock().getTileEntity();
							if (!chests.contains(chest)) {
								chests.add(chest);
							}
						}
					}
				});
			}
		});
	}
}

