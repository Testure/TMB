package turing.tmb.plugin;

import turing.btatweaker.BTATweakerEntrypoint;
import turing.btatweaker.IBTATweaker;

public class BTATweaker implements BTATweakerEntrypoint {
	@Override
	public void initPlugin(IBTATweaker registry) {
		registry.addModLibrary(new TMBLib());
	}
}
