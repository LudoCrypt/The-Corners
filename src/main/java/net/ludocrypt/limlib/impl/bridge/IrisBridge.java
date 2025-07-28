package net.ludocrypt.limlib.impl.bridge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.quiltmc.loader.api.QuiltLoader;

public class IrisBridge {

	public static final boolean IRIS_LOADED = QuiltLoader.isModLoaded("iris");

	public static boolean areShadersInUse() {

		if (IRIS_LOADED) {

			try {
				Class<?> irisApi = Class.forName("net.coderbot.iris.apiimpl.IrisApiV0Impl");
				Field irisInstance = irisApi.getField("INSTANCE");
				Method isShaderInUse = irisApi.getMethod("isShaderPackInUse", new Class[0]);
				Object areThey = isShaderInUse.invoke(irisInstance.get(null), new Object[0]);

				if (areThey instanceof Boolean depends) {
					return depends;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

		return false;
	}

}
