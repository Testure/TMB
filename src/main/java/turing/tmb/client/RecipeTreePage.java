package turing.tmb.client;

import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.achievement.Achievement;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import turing.tmb.RecipeIngredient;
import turing.tmb.RecipeTreeIngredient;
import turing.tmb.TMB;
import turing.tmb.api.drawable.IIngredientList;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.recipe.IRecipeTranslator;
import turing.tmb.api.recipe.RecipeIngredientRole;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class RecipeTreePage {
    protected final Map<RecipeIngredient, RecipeTreeIngredient> entryMap = new HashMap<>();
	protected final List<RecipeTreeIngredient> ingredientList = new ArrayList<>();

	protected final String id;
	protected final RecipeIngredient ingredient;
	protected final RecipeTreeIngredient root;

	protected int currentX = 1;
	protected int currentY = 1;

	protected final List<RecipeIngredient> allRequired = new ArrayList<>();
	protected final List<String> usedRecipes = new ArrayList<>();

    public @NotNull String getName() {
		if (ingredient.ingredient.getItemStack().isPresent()) {
			return ingredient.ingredient.getItemStack().get().getDisplayName();
		}
		return "";
	}

    public @NotNull String getDescription() {
		return "";
	}

	public RecipeTreePage(@NotNull RecipeIngredient rootIngredient){
		this.ingredient = rootIngredient;
		this.root = new RecipeTreeIngredient(rootIngredient, currentX, currentY);
		addIngredient(rootIngredient, currentX, currentY);

		this.id = rootIngredient.recipe.toString();

		currentX += 1;
		addRecipe(rootIngredient, true);
		usedRecipes.clear();
		addRequirements(rootIngredient, true);

		int j = currentY + 2;
		int k = 0;
		for (RecipeIngredient recipeIngredient : allRequired) {
			addIngredient(recipeIngredient, k+1, j);
			k += 2;
			if (k % 10 == 0) {
				j++;
				k = 0;
			}
		}

	}

	public void addRequirements(RecipeIngredient result, boolean root){
		List<IIngredientList> ingredients = new ArrayList<>();
		AtomicReference<IRecipeCategory<? super IRecipeTranslator<?>>> currentCategory = new AtomicReference<>();
		AtomicReference<IRecipeTranslator<?>> currentRecipe = new AtomicReference<>();
		if(root){
			result.category.getIngredients(result.recipe, result.category.getRecipeLayout(), null, ingredients);
			currentCategory.set(result.category);
			currentRecipe.set(result.recipe);
		} else {
			Optional<Map.Entry<RecipeIngredient, IRecipeTranslator<?>>> optional = TMB.getRuntime().getDefaultRecipes().entrySet().stream().filter(E -> E.getKey().ingredient.matches(result.ingredient.getIngredient())).findFirst();
			optional.ifPresent(entry -> {
				entry.getKey().category.getIngredients(entry.getValue(), entry.getKey().category.getRecipeLayout(), null, ingredients);
				currentCategory.set(entry.getKey().category);
				currentRecipe.set(entry.getValue());
			});
			if (!optional.isPresent()) {
				return;
			}
		}
		if(usedRecipes.contains(currentRecipe.get().getOriginal().toString())){
			return;
		}
		usedRecipes.add(currentRecipe.get().getOriginal().toString());

		for (int i = 0; i < ingredients.size(); i++) {
			IIngredientList list = ingredients.get(i);
			if (list.getSize() <= 0) continue;
			ITypedIngredient<?> typedIngredient = list.getIngredients().get(0);
			RecipeIngredient ingredient = new RecipeIngredient(typedIngredient, null, null, RecipeIngredientRole.INPUT);
			if (currentCategory.get().getRecipeLayout().getSlots().get(i).getRole() != RecipeIngredientRole.INPUT) continue;
			if(allRequired
				.stream()
				.anyMatch(it ->
					it.ingredient.matches(typedIngredient.getIngredient())
				)
			){
				RecipeIngredient alreadyAdded = allRequired.stream().filter(it -> it.ingredient.matches(typedIngredient.getIngredient())).collect(Collectors.toList()).get(0);
				alreadyAdded.ingredient.addAmount(typedIngredient.getAmount());
				addRequirements(ingredient,false);
				continue;
			}
			addRequirements(ingredient,false);
			allRequired.add(ingredient);
		}
		usedRecipes.remove(currentRecipe.get().getOriginal().toString());

	}

	public void addRecipe(RecipeIngredient result, boolean root){
		int x = currentX;
		int y = currentY;
		List<IIngredientList> ingredients = new ArrayList<>();

		AtomicReference<IRecipeCategory<? super IRecipeTranslator<?>>> currentCategory = new AtomicReference<>();
		AtomicReference<IRecipeTranslator<?>> currentRecipe = new AtomicReference<>();

		List<RecipeIngredient> alreadyUsed = new ArrayList<>();

		if(root){
			result.category.getIngredients(result.recipe, result.category.getRecipeLayout(), null, ingredients);
			currentCategory.set(result.category);
			currentRecipe.set(result.recipe);
			currentX += 1;
		} else {
			Optional<Map.Entry<RecipeIngredient, IRecipeTranslator<?>>> optional = TMB.getRuntime().getDefaultRecipes().entrySet().stream().filter(E -> E.getKey().ingredient.matches(result.ingredient.getIngredient())).findFirst();
			optional.ifPresent(entry -> {
				entry.getKey().category.getIngredients(entry.getValue(), entry.getKey().category.getRecipeLayout(), null, ingredients);
				currentCategory.set(entry.getKey().category);
				currentRecipe.set(entry.getValue());
				currentX += 1;
			});
			if (!optional.isPresent()) {
				return;
			}
		}

		TMB.LOGGER.info(result.recipe.getOriginal().toString());
		TMB.LOGGER.info(currentRecipe.get().getOriginal().toString());

		if(usedRecipes.contains(currentRecipe.get().getOriginal().toString())){
			return;
		}
		usedRecipes.add(currentRecipe.get().getOriginal().toString());

		int uniqueIngredients = 0;
		for (int i = 0; i < ingredients.size(); i++) {
			IIngredientList list = ingredients.get(i);
			if (list.getSize() <= 0) continue;
			ITypedIngredient<?> typedIngredient = list.getIngredients().get(0);
			if (currentCategory.get().getRecipeLayout().getSlots().get(i).getRole() != RecipeIngredientRole.INPUT) continue;
			if(alreadyUsed
				.stream()
				.anyMatch(it ->
					it.ingredient
						.matches(typedIngredient.getIngredient())
				)
			){
				RecipeIngredient alreadyAdded = alreadyUsed.stream().filter(it -> it.ingredient.matches(typedIngredient.getIngredient())).collect(Collectors.toList()).get(0);
				alreadyAdded.ingredient.addAmount(typedIngredient.getAmount());
				continue;
			}
			RecipeIngredient ingredient = new RecipeIngredient(typedIngredient, currentRecipe.get(), currentCategory.get(), RecipeIngredientRole.INPUT);
			if(currentCategory.get().getRecipeLayout().getSlots().stream().filter(it->it.getRole() == RecipeIngredientRole.INPUT).count() > 0){
				currentY++;
			}
			addIngredient(ingredient, x, currentY);
			addRecipe(ingredient, false);
			alreadyUsed.add(ingredient);
			currentX = x+1;
			uniqueIngredients++;
		}
		usedRecipes.remove(currentRecipe.get().getOriginal().toString());
		//if(ingredients.size() > 1){

		//}
	}

    public void addIngredient(@NotNull RecipeIngredient ingredient, int x, int y) {
        RecipeTreeIngredient entry = new RecipeTreeIngredient(ingredient, x,y);
		ingredientList.add(entry);
        entryMap.put(ingredient, entry);
    }

	/*public void loadQuests(List<Quest> quests){
		questList.clear();
		entryMap.clear();
		for (Quest quest : quests) {
			if(quest.getPage() == this){
				questList.add(quest);
				entryMap.put(quest.getTemplate(), quest);
			}
		}
	}

	public void reset(){
		Set<QuestTemplate> quests = new HashSet<>(entryMap.keySet());
		questList.clear();
		entryMap.clear();
		for (QuestTemplate quest : quests) {
			addQuest(quest);
		}
		for (Quest quest : getQuests()) {
			quest.setupPrerequisites();
		}
	}*/

    public @Nullable IconCoordinate getBackgroundTile(ScreenRecipeTree screen, int layer, Random random, int tileX, int tileY) {
		return getTextureFromBlock(Blocks.DIRT);
	}

	public void postProcessBackground(ScreenRecipeTree screen, Random random, ScreenRecipeTree.BGLayer layerCache, int orgX, int orgY) {

	}

	public ItemStack getIcon() {
		return ingredient.ingredient.getItemStack().orElse(null);
	}

	public @NotNull Set<RecipeIngredient> getIngredients() {
		return entryMap.keySet();
	}

    public @NotNull List<RecipeTreeIngredient> getTreeIngredients() {
        return ingredientList;
    }

    public @Nullable RecipeTreeIngredient getTreeIngredient(RecipeIngredient ingredient) {
        return entryMap.get(ingredient);
    }

    public double getCompletionFraction() {
        /*int completed = 0;
        for (Quest q : questList) {
            if (q.isCompleted()) {
                completed++;
            }
        }
        return completed / (double) questList.size();*/
		return 0;
    }

	public int backgroundLayers() {
		return 1;
	}

	public int backgroundColor() {
		return 0;
	}

	public boolean hasIngredient(RecipeIngredient ingredient) {
        return entryMap.containsKey(ingredient);
    }

	public RecipeTreeIngredient getTreeRoot() {
		return root;
	}

	public IconCoordinate drawIngredientBackground(RecipeIngredient ingredient) {
		if(ingredient == root.ingredient){
			return TextureRegistry.getTexture(Achievement.TYPE_SPECIAL.texture);
		}
		return TextureRegistry.getTexture(Achievement.TYPE_NORMAL.texture);
	}

	public int lineColorLocked(boolean isHovered) {
		return 0x808080;
	}

	public int lineColorUnlocked(boolean isHovered) {
		return 0x00ff00;
	}

	public int lineColorCanUnlock(boolean isHovered) {
		return 0x707070;
	}

	public static IconCoordinate getTextureFromBlock(Block<?> block) {
        return BlockModelDispatcher.getInstance().getDispatch(block).getBlockTextureFromSideAndMetadata(Side.TOP, 0);
    }

	public String getId() {
		return id;
	}
}
