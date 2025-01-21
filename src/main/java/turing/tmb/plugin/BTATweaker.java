package turing.tmb.plugin;

import net.minecraft.core.util.collection.Pair;
import org.luaj.vm2.lib.LibFunction;
import turing.btatweaker.BTATweakerEntrypoint;
import turing.btatweaker.IBTATweaker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BTATweaker implements BTATweakerEntrypoint {
	public static final TMBLib lib = new TMBLib();

	@Override
	public void initPlugin(IBTATweaker registry) {
		registry.addModLibrary(lib);
	}

	public static void onReload() {
		List<Pair<LibFunction, Consumer<LibFunction>>> list = new ArrayList<>(TMBLib.scriptActions);
		TMBLib.scriptActions.clear();
		for (Pair<LibFunction, Consumer<LibFunction>> pair : list) {
			pair.getRight().accept(pair.getLeft());
		}
		list.clear();
	}
}
