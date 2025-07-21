package turing.tmb;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import net.minecraft.client.gui.options.components.KeyBindingComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.option.GameSettings;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turing.tmb.api.ITMBPlugin;
import turing.tmb.api.TMBEntrypoint;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.recipe.IRecipeTranslator;
import turing.tmb.api.recipe.RecipeIngredientRole;
import turing.tmb.api.runtime.ITMBRuntime;
import turing.tmb.plugin.BTATweaker;
import turing.tmb.util.IKeybinds;
import turing.tmb.vanilla.VanillaPlugin;
import turniplabs.halplibe.util.ClientStartEntrypoint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TMB implements ModInitializer, ClientStartEntrypoint, TMBEntrypoint {
    public static final String MOD_ID = "tmb";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean shouldShowModName = true;
	public static boolean shouldReplaceGuidebook = true;
	protected static final TMBRuntime runtime = new TMBRuntime();
	protected static final List<ITMBPlugin> plugins = new ArrayList<>();

    @Override
    public void onInitialize() {
		CommandManager.registerCommand(new CommandReload());
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

	public static void onClientStart() {
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		OptionsCategory category = new OptionsCategory("gui.options.page.controls.category.tmb");
		category.withComponent(new KeyBindingComponent(((IKeybinds) settings).toomanyblocks$getKeyHideTMB()));
		category.withComponent(new KeyBindingComponent(((IKeybinds) settings).toomanyblocks$getKeyAddFavourite()));
		category.withComponent(new KeyBindingComponent(((IKeybinds) settings).toomanyblocks$getKeySetDefaultRecipe()));
		category.withComponent(new KeyBindingComponent(((IKeybinds) settings).toomanyblocks$getKeyShowRecipeTree()));
		OptionsPages.CONTROLS.withComponent(category);
		OptionsCategory category1 = new OptionsCategory("gui.options.page.general.category.tmb");
		category1.withComponent(new BooleanOptionComponent(((IKeybinds) settings).toomanyblocks$getIsRecipeViewEnabled()));
		OptionsPages.GENERAL.withComponent(category1);
		settings.getAllOptions().add(((IKeybinds) settings).toomanyblocks$getIsTMBHidden());
		settings.getAllOptions().add(((IKeybinds) settings).toomanyblocks$getLastTMBSearch());
		settings.getAllOptions().add(((IKeybinds) settings).toomanyblocks$getIsRecipeViewEnabled());
		loadTMB();
		runtime.index.gatherIngredients();
	}

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {
		loadData();
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
		runtime.isReady = true;
		long timeTook = System.currentTimeMillis() - time;
		LOGGER.info("TMB loaded in {}ms!", timeTook);
	}

	public static void saveData() {
		File dataFolder = new File(Minecraft.getMinecraft().getMinecraftDir()+"/config/tmb/");
		dataFolder.mkdirs();

		File dataFile = new File(Minecraft.getMinecraft().getMinecraftDir()+"/config/tmb/", "data.dat");

		CompoundTag dataTag = new CompoundTag();

		CompoundTag favourites = new CompoundTag();
		List<ITypedIngredient<?>> iTypedIngredients = TMB.getRuntime().getFavourites();
		for (int j = 0; j < iTypedIngredients.size(); j++) {
			ITypedIngredient<?> favourite = iTypedIngredients.get(j);
			CompoundTag favouriteTag = new CompoundTag();
			favouriteTag.putString("namespace", favourite.getNamespace());
			favouriteTag.putString("uid", favourite.getUid());
			favourites.put(String.valueOf(j), favouriteTag);
		}
		dataTag.putCompound("Favourites", favourites);

		CompoundTag defaultRecipes = new CompoundTag();
		int i = 0;
		for (Map.Entry<RecipeIngredient, IRecipeTranslator<?>> entry : TMB.getRuntime().getDefaultRecipes().entrySet()) {
			RecipeIngredient key = entry.getKey();
			IRecipeTranslator<?> value = entry.getValue();
			CompoundTag defaultRecipeTag = new CompoundTag();
			CompoundTag ingredientTag = new CompoundTag();
			CompoundTag categoryTag = new CompoundTag();

			defaultRecipeTag.putString("recipe", value.getOriginal().toString());
			ingredientTag.putString("namespace", key.ingredient.getNamespace());
			ingredientTag.putString("uid", key.ingredient.getUid());
			defaultRecipeTag.put("ingredient", ingredientTag);
			categoryTag.putString("namespace", key.category.getNamespace());
			categoryTag.putString("name", key.category.getName());
			defaultRecipeTag.put("category", categoryTag);
			defaultRecipes.put(String.valueOf(i), defaultRecipeTag);
			i++;
		}
		dataTag.putCompound("DefaultRecipes", defaultRecipes);

		try {
			if(!dataFile.exists()) {
				if (!dataFile.createNewFile()) {
					return;
				}
			}
			NbtIo.writeCompressed(dataTag, Files.newOutputStream(dataFile.toPath()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void loadData() {

		File dataFolder = new File(Minecraft.getMinecraft().getMinecraftDir()+"/config/tmb/");
		dataFolder.mkdirs();

		File dataFile = new File(Minecraft.getMinecraft().getMinecraftDir()+"/config/tmb/", "data.dat");
		try {
			if(!dataFile.exists()) {
				return;
			}
			CompoundTag tag = NbtIo.readCompressed(Files.newInputStream(dataFile.toPath()));

			TMB.getRuntime().getFavourites().clear();
			for (Tag<?> compoundTag : tag.getCompound("Favourites").getValues()) {
				CompoundTag favouriteTag = (CompoundTag) compoundTag;
				TMB.getRuntime().getIngredientIndex()
					.getIngredient(favouriteTag.getString("namespace"), favouriteTag.getString("uid"))
					.ifPresent(TMB.getRuntime().getFavourites()::add);
			}

			for (Tag<?> compoundTag : tag.getCompound("DefaultRecipes").getValues()) {
				CompoundTag defaultRecipeTag = (CompoundTag) compoundTag;
				CompoundTag ingredientTag = defaultRecipeTag.getCompound("ingredient");
				CompoundTag categoryTag = defaultRecipeTag.getCompound("category");
				String recipeId = defaultRecipeTag.getString("recipe");

				Optional<IRecipeCategory<?>> category = TMB.getRuntime().getRecipeIndex().getAllCategories().stream()
					.filter(it ->
						it.getName().equals(categoryTag.getString("name"))
							&& it.getNamespace().equals(categoryTag.getString("namespace"))).findFirst();

				Optional<IRecipeTranslator<?>> recipe = category
					.flatMap(it -> TMB.getRuntime().getRecipeIndex().getRecipeLists().get(it).stream()
						.filter(it2 -> it2.getOriginal().toString().equals(recipeId)).findFirst());

				Optional<ITypedIngredient<Object>> ingredient = recipe
					.flatMap(it -> TMB.getRuntime().getIngredientIndex()
						.getIngredient(ingredientTag.getString("namespace"), ingredientTag.getString("uid")));

				ingredient
					.ifPresent(it -> {
						RecipeIngredient recipeIngredient = new RecipeIngredient(it, recipe.get(), category.get(), RecipeIngredientRole.OUTPUT);
						TMB.getRuntime().getDefaultRecipes().put(recipeIngredient, recipe.get());
					});
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
		runtime.index.gatherIngredients();
		if (FabricLoader.getInstance().isModLoaded("btatweaker")) {
			BTATweaker.onReload();
		}
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
