package turing.tmb;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.vanilla.VanillaPlugin;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.ArrayList;
import java.util.List;

public class TMB implements ModInitializer, RecipeEntrypoint, ClientStartEntrypoint {
    public static final String MOD_ID = "tmb";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean shouldShowModName = true;
	public static boolean shouldReplaceGuidebook = true;
	protected static final TMBRuntime runtime = new TMBRuntime();
	protected static final List<ITMBPlugin> plugins = new ArrayList<>();

    @Override
    public void onInitialize() {
		registerPlugin(new BaseTMBPlugin());
		registerPlugin(new VanillaPlugin());
		if (FabricLoader.getInstance().isModLoaded("modnametooltip")) {
			shouldShowModName = false;
		}
    }

	@Override
	public void onRecipesReady() {
		long time = System.currentTimeMillis();
		LOGGER.info("Loading plugins");
		for (ITMBPlugin plugin : plugins) {
			plugin.registerExtraScreens(runtime.getGuiHelper());
			plugin.registerIngredientTypes(runtime);
			plugin.registerRecipeCategories(runtime);
		}
		runtime.getRecipeIndex().loadLists();
		for (ITMBPlugin plugin : plugins) {
			plugin.registerIngredients(runtime);
			plugin.registerRecipeCatalysts(runtime);
			plugin.registerRecipes(runtime);
		}
		runtime.index.gatherIngredients();
		LOGGER.info("TMB loaded in {}ms!", System.currentTimeMillis() - time);
		runtime.isReady = true;
	}

	@Override
	public void initNamespaces() {

	}

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {

	}

	public static void registerPlugin(ITMBPlugin plugin) {
		plugins.add(plugin);
	}

	public static ITMBRuntime getRuntime() {
		if (!runtime.isReady) {
			throw new IllegalStateException("Attempt to get runtime before it is ready!");
		}
		return runtime;
	}
}
