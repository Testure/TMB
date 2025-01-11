package turing.tmb.plugin;

import net.minecraft.core.lang.I18n;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.api.ModLibrary;
import turing.tmb.TMB;
import turing.tmb.TypedIngredient;

import java.util.Collections;
import java.util.List;

public class TMBLib extends ModLibrary {
	@Override
	public void setupLib(LuaTable t, LuaValue env) {
		t.set("hideIngredient", new HideIngredient());
		t.set("hideCategory", new HideCategory());
	}

	@Override
	public List<String> getValidNames() {
		return Collections.singletonList(TMB.MOD_ID);
	}

	protected static final class HideCategory extends OneArgFunction {
		@Override
		public LuaValue call(LuaValue arg) {
			String name = arg.checkjstring();
			TMB.getRuntime().getRecipeIndex().hideCategory(name);
			return NIL;
		}
	}

	protected static final class HideIngredient extends TwoArgFunction {
		@Override
		public LuaValue call(LuaValue arg, LuaValue arg2) {
			String namespace = arg.checkjstring();
			String name = arg2.checkjstring();
			TMB.getRuntime().getIngredientIndex().hideIngredient(new TypedIngredient<>(namespace, name, null, null));
			if (name.contains(".")) {
				TMB.getRuntime().getIngredientIndex().hideIngredient(new TypedIngredient<>(namespace, I18n.getInstance().translateKey(name), null, null));
			}
			return NIL;
		}
	}
}
