package turing.tmb.plugin;

import turing.btatweaker.BTATweakerEntrypoint;
import turing.btatweaker.IScriptPropertyHolder;
import turing.btatweaker.lua.LibGatherer;

public class BTATweaker implements BTATweakerEntrypoint {
	@Override
	public void addLibs(LibGatherer gatherer) {

	}

	@Override
	public void init(IScriptPropertyHolder registry) {
		registry.addModLibrary(new TMBLib());
	}
}
