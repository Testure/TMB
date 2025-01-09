package turing.tmb;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.KeyBindingComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.TMBEntrypoint;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.util.IKeybinds;
import turing.tmb.vanilla.VanillaPlugin;
import turniplabs.halplibe.helper.CommandHelper;
import turniplabs.halplibe.util.ClientStartEntrypoint;

import java.util.ArrayList;
import java.util.List;

public class TMB implements ModInitializer, ClientStartEntrypoint, TMBEntrypoint {
    public static final String MOD_ID = "tmb";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean shouldShowModName = true;
	public static boolean shouldReplaceGuidebook = true;
	protected static final TMBRuntime runtime = new TMBRuntime();
	protected static final List<ITMBPlugin> plugins = new ArrayList<>();

    @Override
    public void onInitialize() {
		CommandHelper.createCommand(new CommandReload("tmb"));
		gatherPlugins(false);
		if (FabricLoader.getInstance().isModLoaded("modnametooltip")) {
			shouldShowModName = false;
		}
	}

	@Override
	public void onGatherPlugins(boolean isReload) {
		registerPlugin(new VanillaPlugin());
		registerPlugin(new BaseTMBPlugin());
	}

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {
		OptionsCategory category = new OptionsCategory("gui.options.page.controls.category.tmb");
		category.withComponent(new KeyBindingComponent(((IKeybinds) Minecraft.getMinecraft(this).gameSettings).toomanyblocks$getKeyHideTMB()));
		OptionsPages.CONTROLS.withComponent(category);
		/*GameSettings settings = Minecraft.getMinecraft(this).gameSettings;
		settings.getAllOptions().add(((IKeybinds) settings).toomanyblocks$getIsTMBHidden());
		settings.getAllOptions().add(((IKeybinds) settings).toomanyblocks$getLastTMBSearch());*/
		loadTMB();
	}

	private static void loadTMB() {
		long time = System.currentTimeMillis();
		LOGGER.info("Loading TMB");
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
		long timeTook = System.currentTimeMillis() - time;
		LOGGER.info("TMB loaded in {}ms!", timeTook);
		runtime.isReady = true;
	}

	private static void clear() {
		plugins.clear();
		runtime.clear();
	}

	private static void gatherPlugins(boolean isReload) {
		FabricLoader.getInstance().getEntrypoints("gatherTMBPlugins", TMBEntrypoint.class).forEach(tmbEntrypoint -> {
			tmbEntrypoint.onGatherPlugins(isReload);
		});
	}

	public static void reloadTMB() {
		runtime.isReady = false;
		clear();
		gatherPlugins(true);
		loadTMB();
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
