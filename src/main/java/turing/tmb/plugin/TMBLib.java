package turing.tmb.plugin;

import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.collection.Pair;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.api.ModLibrary;
import turing.docs.Argument;
import turing.docs.Function;
import turing.docs.FunctionExample;
import turing.docs.Library;
import turing.tmb.*;
import turing.tmb.api.ingredient.ITypedIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Library(value = "mods.tmb", className = "Too Many Blocks")
public class TMBLib extends ModLibrary {
	public static final List<Pair<LibFunction, Consumer<LibFunction>>> scriptActions = new ArrayList<>();

	@Override
	public void setupLib(LuaTable t, LuaValue env) {
		t.set("hideIngredient", new HideIngredient());
		t.set("hideCategory", new HideCategory());
		t.set("addInfo", new AddInfo());
	}

	@Override
	public List<String> getAliases() {
		return Collections.singletonList(TMB.MOD_ID);
	}

	@Function(value = "hideCategory", arguments = @Argument(value = "string", name = "categoryName"), examples = @FunctionExample("guidebook.section.furnace"))
	protected static final class HideCategory extends OneArgFunction {
		@Override
		public LuaValue call(LuaValue arg) {
			String name = arg.checkjstring();
			TMB.getRuntime().getRecipeIndex().hideCategory(name);
			scriptActions.add(Pair.of(this, f -> f.call(arg)));
			return NIL;
		}
	}

	@Function(value = "hideIngredient", arguments = {@Argument(value = "string", name = "namespace"), @Argument(value = "string", name = "name")})
	protected static final class HideIngredient extends TwoArgFunction {
		@Override
		public LuaValue call(LuaValue arg, LuaValue arg2) {
			String namespace = arg.checkjstring();
			String name = arg2.checkjstring();
			TMB.getRuntime().getIngredientIndex().hideIngredient(new TypedIngredient<>(namespace, name, null, null));
			if (name.contains(".")) {
				TMB.getRuntime().getIngredientIndex().hideIngredient(new TypedIngredient<>(namespace, I18n.getInstance().translateKey(name), null, null));
				scriptActions.add(Pair.of(this, f -> f.call(arg, arg2)));
			}
			return NIL;
		}
	}

	@Function(value = "addInfo", arguments = {
		@Argument(value = "string", name = "namespace"),
		@Argument(value = "string", name = "name"),
		@Argument(value = "string", name = "infoText")
	}, examples = @FunctionExample({"\"minecraft\"", "\"Iron Ingot\"", "This is a cool item!!!"}))
	protected static final class AddInfo extends ThreeArgFunction {
		@Override
		public LuaValue call(LuaValue arg, LuaValue arg2, LuaValue arg3) {
			String namespace = arg.checkjstring();
			String name = arg2.checkjstring();
			String text = arg3.checkjstring();
			Optional<ITypedIngredient<Object>> ingredient = TMB.getRuntime().getIngredientIndex().getIngredient(namespace, name);
			ingredient.ifPresent(i -> {
				TMB.getRuntime().getRecipeIndex().registerRecipe(BaseTMBPlugin.infoCategory, new IngredientInfo(i, text, false), InfoRecipeTranslator::new);
				scriptActions.add(Pair.of(this, f -> f.call(arg, arg2, arg3)));
			});
			return NIL;
		}
	}
}
