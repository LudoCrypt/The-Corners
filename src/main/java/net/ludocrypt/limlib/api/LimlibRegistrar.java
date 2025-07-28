package net.ludocrypt.limlib.api;

public interface LimlibRegistrar {

	// Entrypoint key to be used inside quilt.mod.json
	public static String ENTRYPOINT_KEY = "limlib:registrar";

	void registerHooks();

}
