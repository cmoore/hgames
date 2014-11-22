
package io.ivy.hgames;

import net.canarymod.Canary;
import net.canarymod.plugin.Plugin;

public class HGames extends Plugin {

    @Override
    public boolean enable() {
        Canary.hooks().registerListener(new HGamesListener(), this);
        return true;
    }

    @Override
    public void disable() {
    }
}
