package turing.tmb.mixin;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.core.world.save.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.tmb.RecipeIngredient;
import turing.tmb.TMB;
import turing.tmb.api.ingredient.ITypedIngredient;
import turing.tmb.api.recipe.IRecipeCategory;
import turing.tmb.api.recipe.IRecipeTranslator;
import turing.tmb.api.recipe.RecipeIngredientRole;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = LevelData.class,remap = false)
public class LevelDataMixin {

	@Inject(method = "updateTagCompound", at = @At("HEAD"))
	private void updateTagCompound(CompoundTag levelTag, CompoundTag playerTag, CallbackInfo ci) {
		CompoundTag favourites = new CompoundTag();
		List<ITypedIngredient<?>> iTypedIngredients = TMB.getRuntime().getFavourites();
		for (int j = 0; j < iTypedIngredients.size(); j++) {
			ITypedIngredient<?> favourite = iTypedIngredients.get(j);
			CompoundTag favouriteTag = new CompoundTag();
			favouriteTag.putString("namespace", favourite.getNamespace());
			favouriteTag.putString("uid", favourite.getUid());
			favourites.put(String.valueOf(j), favouriteTag);
		}
		levelTag.putCompound("Favourites", favourites);

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
		levelTag.putCompound("DefaultRecipes", defaultRecipes);
	}


	@Inject(method = "readFromCompoundTag", at = @At("HEAD"))
	private void readFromCompoundTag(CompoundTag tag, CallbackInfo ci) {
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
	}
}
